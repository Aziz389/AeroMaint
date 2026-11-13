package com.aeromaint.dto;

import com.aeromaint.entity.Equipment;
import com.aeromaint.entity.EquipmentStatus;

import java.time.LocalDate;

public record EquipmentResponse(
        Long id,
        String name,
        String type,
        Long zoneId,
        String zoneName,
        EquipmentStatus status,
        LocalDate installedAt
) {
    public static EquipmentResponse from(Equipment e) {
        return new EquipmentResponse(e.getId(), e.getName(), e.getType(),
                e.getZone().getId(), e.getZone().getName(), e.getStatus(), e.getInstalledAt());
    }
}
