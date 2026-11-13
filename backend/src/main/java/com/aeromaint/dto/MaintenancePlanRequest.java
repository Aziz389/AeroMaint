package com.aeromaint.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Body of POST and PUT /api/maintenance-plans (SUPERVISOR). */
public record MaintenancePlanRequest(
        @NotNull Long equipmentId,
        @NotNull @Min(1) @Max(3650) Integer intervalDays,
        @NotNull LocalDate nextDue,
        @NotBlank @Size(max = 255) String description
) {}
