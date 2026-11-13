package com.aeromaint.service;

import com.aeromaint.entity.MaintenancePlan;
import com.aeromaint.entity.Ticket;
import com.aeromaint.entity.TicketPriority;
import com.aeromaint.entity.TicketStatus;
import com.aeromaint.repository.MaintenancePlanRepository;
import com.aeromaint.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Preventive maintenance job.
 * Runs shortly after startup and then every hour (configurable). For every plan whose next_due date
 * is today or past, it creates one OPEN ticket (reporter = null, meaning "created by the system")
 * and moves next_due forward by interval_days until it is in the future.
 */
@Component
public class PreventiveMaintenanceScheduler {

    private static final Logger log = LoggerFactory.getLogger(PreventiveMaintenanceScheduler.class);

    private final MaintenancePlanRepository planRepository;
    private final TicketRepository ticketRepository;
    private final AuditService auditService;

    public PreventiveMaintenanceScheduler(MaintenancePlanRepository planRepository,
                                          TicketRepository ticketRepository,
                                          AuditService auditService) {
        this.planRepository = planRepository;
        this.ticketRepository = ticketRepository;
        this.auditService = auditService;
    }

    @Scheduled(initialDelayString = "${aeromaint.scheduler.initial-delay-ms}",
               fixedDelayString = "${aeromaint.scheduler.interval-ms}")
    @Transactional
    public void createDueMaintenanceTickets() {
        LocalDate today = LocalDate.now();
        List<MaintenancePlan> duePlans = planRepository.findDueOnOrBefore(today);

        for (MaintenancePlan plan : duePlans) {
            Ticket ticket = new Ticket();
            ticket.setEquipment(plan.getEquipment());
            ticket.setReporter(null);
            ticket.setTitle("Preventive maintenance: " + plan.getEquipment().getName());
            ticket.setDescription(plan.getDescription() + " (due " + plan.getNextDue() + ")");
            ticket.setPriority(TicketPriority.MEDIUM);
            ticket.setStatus(TicketStatus.OPEN);
            ticketRepository.save(ticket);

            // If the server was down for a while, skip the missed periods: one ticket, next date in the future.
            LocalDate next = plan.getNextDue();
            while (!next.isAfter(today)) {
                next = next.plusDays(plan.getIntervalDays());
            }
            plan.setNextDue(next);

            auditService.log(null, "PREVENTIVE_TICKET_CREATED", "TICKET", ticket.getId(),
                    "Plan " + plan.getId() + ", next due " + next);
        }

        if (!duePlans.isEmpty()) {
            log.info("Preventive maintenance: created {} ticket(s).", duePlans.size());
        }
    }
}
