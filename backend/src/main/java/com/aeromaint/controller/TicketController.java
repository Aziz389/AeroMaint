package com.aeromaint.controller;

import com.aeromaint.dto.*;
import com.aeromaint.entity.TicketPriority;
import com.aeromaint.entity.TicketStatus;
import com.aeromaint.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Tickets.
 * - SUPERVISOR: creates, edits, deletes (OPEN only), assigns and closes tickets.
 * - TECHNICIAN: sees only their tickets, moves them to IN_PROGRESS / RESOLVED, adds comments.
 * - ADMIN: read-only access to tickets.
 * Principal = the logged-in user, set by JwtAuthenticationFilter.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /** GET /api/tickets?status=OPEN&priority=HIGH&equipmentId=3&assigneeId=4 (all filters optional) */
    @GetMapping
    public List<TicketResponse> search(@RequestParam(required = false) TicketStatus status,
                                       @RequestParam(required = false) TicketPriority priority,
                                       @RequestParam(required = false) Long equipmentId,
                                       @RequestParam(required = false) Long assigneeId,
                                       Principal principal) {
        return ticketService.search(status, priority, equipmentId, assigneeId, principal.getName());
    }

    @GetMapping("/{id}")
    public TicketResponse get(@PathVariable Long id, Principal principal) {
        return ticketService.get(id, principal.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SUPERVISOR')")
    public TicketResponse create(@Valid @RequestBody TicketRequest request, Principal principal) {
        return ticketService.create(request, principal.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public TicketResponse update(@PathVariable Long id, @Valid @RequestBody TicketRequest request) {
        return ticketService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SUPERVISOR')")
    public void delete(@PathVariable Long id) {
        ticketService.delete(id);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public TicketResponse assign(@PathVariable Long id, @Valid @RequestBody AssignRequest request) {
        return ticketService.assign(id, request.assigneeId());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'SUPERVISOR')")
    public TicketResponse changeStatus(@PathVariable Long id, @Valid @RequestBody StatusChangeRequest request,
                                       Principal principal) {
        return ticketService.changeStatus(id, request.status(), principal.getName());
    }

    @GetMapping("/{id}/comments")
    public List<CommentResponse> listComments(@PathVariable Long id, Principal principal) {
        return ticketService.listComments(id, principal.getName());
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'SUPERVISOR')")
    public CommentResponse addComment(@PathVariable Long id, @Valid @RequestBody CommentRequest request,
                                      Principal principal) {
        return ticketService.addComment(id, request, principal.getName());
    }
}
