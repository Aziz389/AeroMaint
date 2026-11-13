package com.aeromaint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body of POST and PUT /api/zones (ADMIN only). */
public record ZoneRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description
) {}
