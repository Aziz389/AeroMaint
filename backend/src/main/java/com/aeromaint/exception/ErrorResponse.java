package com.aeromaint.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The single JSON format for every error the API returns.
 * fieldErrors is only filled for validation errors, e.g. {"title": "must not be blank"}.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {}
