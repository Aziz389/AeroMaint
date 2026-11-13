package com.aeromaint.dto;

import com.aeromaint.entity.AuditLog;

import java.time.LocalDateTime;

/** user is null when the action was done by the system (maintenance scheduler). */
public record AuditLogResponse(
        Long id,
        UserSummary user,
        String action,
        String entityType,
        Long entityId,
        String details,
        LocalDateTime createdAt
) {
    public static AuditLogResponse from(AuditLog a) {
        return new AuditLogResponse(a.getId(), UserSummary.from(a.getUser()), a.getAction(), a.getEntityType(),
                a.getEntityId(), a.getDetails(), a.getCreatedAt());
    }
}
