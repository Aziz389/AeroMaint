package com.aeromaint.dto;

/** Returned after a successful login. The client sends the token in the Authorization header. */
public record LoginResponse(
        String token,
        long expiresInMs,
        UserResponse user
) {}
