package com.aeromaint.controller;

import com.aeromaint.dto.LoginRequest;
import com.aeromaint.dto.LoginResponse;
import com.aeromaint.dto.UserResponse;
import com.aeromaint.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /** Public. Checks username + password and returns a JWT. Wrong credentials -> 401. */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /** Any logged-in user. Returns who the token belongs to (used by the Angular app). */
    @GetMapping("/me")
    public UserResponse me(Principal principal) {
        return userService.getByUsername(principal.getName());
    }
}
