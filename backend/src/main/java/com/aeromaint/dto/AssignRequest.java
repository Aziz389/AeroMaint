package com.aeromaint.dto;

import jakarta.validation.constraints.NotNull;

/** Body of PUT /api/tickets/{id}/assign (SUPERVISOR). */
public record AssignRequest(@NotNull Long assigneeId) {}
