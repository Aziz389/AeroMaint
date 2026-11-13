package com.aeromaint.dto;

import com.aeromaint.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;

/** Body of PATCH /api/tickets/{id}/status. */
public record StatusChangeRequest(@NotNull TicketStatus status) {}
