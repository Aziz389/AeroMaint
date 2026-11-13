package com.aeromaint.dto;

import com.aeromaint.entity.User;

/** Short user info embedded in tickets and comments. */
public record UserSummary(Long id, String username, String fullName) {

    /** Returns null for a null user (e.g. ticket not assigned yet, or created by the scheduler). */
    public static UserSummary from(User user) {
        return user == null ? null : new UserSummary(user.getId(), user.getUsername(), user.getFullName());
    }
}
