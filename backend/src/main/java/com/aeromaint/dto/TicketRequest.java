package com.aeromaint.dto;

import com.aeromaint.entity.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Body of POST and PUT /api/tickets (SUPERVISOR). Status and assignee have their own endpoints. */
public record TicketRequest(
        @NotNull Long equipmentId,
        @NotBlank @Size(max = 150) String title,
        @Size(max = 2000) String description,
        @NotNull TicketPriority priority
) {}
