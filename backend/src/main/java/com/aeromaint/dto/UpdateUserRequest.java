package com.aeromaint.dto;

import com.aeromaint.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Body of PUT /api/users/{id} (ADMIN only).
 * password is optional: leave it null to keep the current one.
 * Users are deactivated (active = false) rather than deleted, so their ticket history stays intact.
 */
public record UpdateUserRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Email @Size(max = 150) String email,
        @NotNull Role role,
        @NotNull Boolean active,
        @Size(min = 8, max = 72) String password
) {}
