package com.aeromaint.dto;

import com.aeromaint.entity.Ticket;
import com.aeromaint.entity.TicketPriority;
import com.aeromaint.entity.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketPriority priority,
        TicketStatus status,
        Long equipmentId,
        String equipmentName,
        String zoneName,
        UserSummary reporter,
        UserSummary assignee,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {
    public static TicketResponse from(Ticket t) {
        return new TicketResponse(t.getId(), t.getTitle(), t.getDescription(), t.getPriority(), t.getStatus(),
                t.getEquipment().getId(), t.getEquipment().getName(), t.getEquipment().getZone().getName(),
                UserSummary.from(t.getReporter()), UserSummary.from(t.getAssignee()),
                t.getCreatedAt(), t.getResolvedAt());
    }
}
