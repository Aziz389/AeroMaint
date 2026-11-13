package com.aeromaint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body of POST /api/tickets/{id}/comments. */
public record CommentRequest(@NotBlank @Size(max = 2000) String content) {}
