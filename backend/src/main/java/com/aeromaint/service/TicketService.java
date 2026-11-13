package com.aeromaint.service;

import com.aeromaint.dto.*;
import com.aeromaint.entity.*;
import com.aeromaint.exception.BusinessRuleException;
import com.aeromaint.exception.NotFoundException;
import com.aeromaint.repository.TicketCommentRepository;
import com.aeromaint.repository.TicketRepository;
import com.aeromaint.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ticket business rules.
 * Role checks ("is this a SUPERVISOR?") are on the controller with @PreAuthorize.
 * Checks that depend on the data ("is this ticket assigned to this technician?") are here.
 */
@Service
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EquipmentService equipmentService;
    private final AuditService auditService;

    public TicketService(TicketRepository ticketRepository,
                         TicketCommentRepository commentRepository,
                         UserRepository userRepository,
                         EquipmentService equipmentService,
                         AuditService auditService) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.equipmentService = equipmentService;
        this.auditService = auditService;
    }

    // ---------- Read ----------

    /** A technician only ever sees tickets assigned to them, whatever filter they send. */
    public List<TicketResponse> search(TicketStatus status, TicketPriority priority,
                                       Long equipmentId, Long assigneeId, String username) {
        User current = currentUser(username);
        if (current.getRole() == Role.TECHNICIAN) {
            assigneeId = current.getId();
        }
        return ticketRepository.search(status, priority, equipmentId, assigneeId)
                .stream().map(TicketResponse::from).toList();
    }

    public TicketResponse get(Long id, String username) {
        Ticket ticket = findTicket(id);
        checkCanAccess(ticket, currentUser(username));
        return TicketResponse.from(ticket);
    }

    // ---------- Create / update / delete (SUPERVISOR) ----------

    @Transactional
    public TicketResponse create(TicketRequest request, String username) {
        Ticket ticket = new Ticket();
        ticket.setEquipment(equipmentService.findEquipment(request.equipmentId()));
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());
        ticket.setReporter(currentUser(username));
        ticket.setStatus(TicketStatus.OPEN);
        ticketRepository.save(ticket);
        auditService.log("TICKET_CREATED", "TICKET", ticket.getId(), ticket.getTitle());
        return TicketResponse.from(ticket);
    }

    @Transactional
    public TicketResponse update(Long id, TicketRequest request) {
        Ticket ticket = findTicket(id);
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BusinessRuleException("A closed ticket cannot be edited");
        }
        ticket.setEquipment(equipmentService.findEquipment(request.equipmentId()));
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());
        auditService.log("TICKET_UPDATED", "TICKET", id, ticket.getTitle());
        return TicketResponse.from(ticket);
    }

    /** Only tickets nobody has started on can be deleted; the rest is maintenance history. */
    @Transactional
    public void delete(Long id) {
        Ticket ticket = findTicket(id);
        if (ticket.getStatus() != TicketStatus.OPEN) {
            throw new BusinessRuleException("Only OPEN tickets can be deleted");
        }
        ticketRepository.delete(ticket);
        auditService.log("TICKET_DELETED", "TICKET", id, ticket.getTitle());
    }

    // ---------- Workflow ----------

    /** OPEN -> ASSIGNED, or re-assign while still ASSIGNED. The assignee must be an active technician. */
    @Transactional
    public TicketResponse assign(Long id, Long assigneeId) {
        Ticket ticket = findTicket(id);
        if (ticket.getStatus() != TicketStatus.OPEN && ticket.getStatus() != TicketStatus.ASSIGNED) {
            throw new BusinessRuleException("Only OPEN or ASSIGNED tickets can be (re)assigned");
        }
        User technician = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NotFoundException("User " + assigneeId + " not found"));
        if (technician.getRole() != Role.TECHNICIAN || !technician.isActive()) {
            throw new BusinessRuleException("Tickets can only be assigned to an active technician");
        }
        ticket.setAssignee(technician);
        ticket.setStatus(TicketStatus.ASSIGNED);
        auditService.log("TICKET_ASSIGNED", "TICKET", id, "Assigned to " + technician.getUsername());
        return TicketResponse.from(ticket);
    }

    /**
     * Moves a ticket one step forward in OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED.
     * - ASSIGNED is only reachable through assign(), because it needs a technician.
     * - A technician works on their own tickets but cannot CLOSE them: a supervisor confirms the fix.
     */
    @Transactional
    public TicketResponse changeStatus(Long id, TicketStatus newStatus, String username) {
        Ticket ticket = findTicket(id);
        User current = currentUser(username);
        checkCanAccess(ticket, current);

        if (newStatus == TicketStatus.ASSIGNED) {
            throw new BusinessRuleException("Use the assign endpoint to assign a ticket");
        }
        if (!ticket.getStatus().canTransitionTo(newStatus)) {
            throw new BusinessRuleException(
                    "Invalid status transition: " + ticket.getStatus() + " -> " + newStatus);
        }
        if (newStatus == TicketStatus.CLOSED && current.getRole() == Role.TECHNICIAN) {
            throw new AccessDeniedException("Only a supervisor can close a ticket");
        }

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(newStatus);
        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        auditService.log(current, "TICKET_STATUS_CHANGED", "TICKET", id, oldStatus + " -> " + newStatus);
        return TicketResponse.from(ticket);
    }

    // ---------- Comments ----------

    public List<CommentResponse> listComments(Long ticketId, String username) {
        Ticket ticket = findTicket(ticketId);
        checkCanAccess(ticket, currentUser(username));
        return commentRepository.findByTicketIdWithAuthor(ticketId)
                .stream().map(CommentResponse::from).toList();
    }

    @Transactional
    public CommentResponse addComment(Long ticketId, CommentRequest request, String username) {
        Ticket ticket = findTicket(ticketId);
        User current = currentUser(username);
        checkCanAccess(ticket, current);
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BusinessRuleException("Cannot comment on a closed ticket");
        }
        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setAuthor(current);
        comment.setContent(request.content());
        return CommentResponse.from(commentRepository.save(comment));
    }

    // ---------- Helpers ----------

    /** Technicians may only touch tickets assigned to them. Supervisors and admins see all tickets. */
    private void checkCanAccess(Ticket ticket, User user) {
        if (user.getRole() == Role.TECHNICIAN
                && (ticket.getAssignee() == null || !ticket.getAssignee().getId().equals(user.getId()))) {
            throw new AccessDeniedException("This ticket is not assigned to you");
        }
    }

    private Ticket findTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket " + id + " not found"));
    }

    private User currentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
