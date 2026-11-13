package com.aeromaint.dto;

import com.aeromaint.entity.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Body of POST and PUT /api/equipment (ADMIN only). */
public record EquipmentRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 50) String type,
        @NotNull Long zoneId,
        @NotNull EquipmentStatus status,
        @NotNull @PastOrPresent LocalDate installedAt
) {}
