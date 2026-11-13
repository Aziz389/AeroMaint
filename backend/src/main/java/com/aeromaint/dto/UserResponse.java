package com.aeromaint.dto;

import com.aeromaint.entity.Role;
import com.aeromaint.entity.User;

import java.time.LocalDateTime;

/** What the API returns for a user. Never includes the password hash. */
public record UserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        Role role,
        boolean active,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(),
                user.getRole(), user.isActive(), user.getCreatedAt());
    }
}
