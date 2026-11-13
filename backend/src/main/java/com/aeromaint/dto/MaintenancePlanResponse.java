package com.aeromaint.dto;

import com.aeromaint.entity.MaintenancePlan;

import java.time.LocalDate;

public record MaintenancePlanResponse(
        Long id,
        Long equipmentId,
        String equipmentName,
        int intervalDays,
        LocalDate nextDue,
        String description
) {
    public static MaintenancePlanResponse from(MaintenancePlan p) {
        return new MaintenancePlanResponse(p.getId(), p.getEquipment().getId(), p.getEquipment().getName(),
                p.getIntervalDays(), p.getNextDue(), p.getDescription());
    }
}
