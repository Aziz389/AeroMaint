package com.aeromaint.dto;

import com.aeromaint.entity.TicketStatus;

import java.util.List;

/**
 * Everything the dashboard shows, in one response.
 * The small records below are filled directly by JPQL "SELECT new ..." queries in TicketRepository.
 */
public record DashboardResponse(
        long openTickets,                       // OPEN + ASSIGNED + IN_PROGRESS
        Double averageResolutionHours,          // created_at -> resolved_at, null if nothing resolved yet
        List<StatusCount> ticketsByStatus,
        List<EquipmentFailureCount> mostFailingEquipment,
        List<TechnicianWorkload> technicianWorkload
) {
    public record StatusCount(TicketStatus status, long count) {}

    /** Number of fault tickets (reported by a person, not by the scheduler) per equipment. */
    public record EquipmentFailureCount(Long equipmentId, String equipmentName, long faultCount) {}

    /** Tickets currently ASSIGNED or IN_PROGRESS per active technician. */
    public record TechnicianWorkload(Long technicianId, String fullName, long activeTickets) {}
}
