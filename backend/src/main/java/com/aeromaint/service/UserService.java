package com.aeromaint.service;

import com.aeromaint.dto.*;
import com.aeromaint.entity.Role;
import com.aeromaint.entity.User;
import com.aeromaint.exception.BusinessRuleException;
import com.aeromaint.exception.NotFoundException;
import com.aeromaint.repository.UserRepository;
import com.aeromaint.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Business logic for login and user management. Controllers stay thin and call this class. */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Spring Security loads the user, compares the BCrypt hash and checks the account is active.
        // On failure it throws an AuthenticationException, which GlobalExceptionHandler turns into 401
        // with the same message for "unknown user" and "wrong password" (do not reveal which usernames exist).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = findUser(request.username());
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        auditService.log(user, "LOGIN", "USER", user.getId(), null);
        return new LoginResponse(token, jwtService.getExpirationMs(), UserResponse.from(user));
    }

    public UserResponse getByUsername(String username) {
        return UserResponse.from(findUser(username));
    }

    public List<UserResponse> list(Role role) {
        List<User> users = (role == null) ? userRepository.findAll() : userRepository.findByRole(role);
        return users.stream().map(UserResponse::from).toList();
    }

    public UserResponse getById(Long id) {
        return UserResponse.from(findUser(id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessRuleException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        userRepository.save(user);
        auditService.log("USER_CREATED", "USER", user.getId(), user.getUsername() + " as " + user.getRole());
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findUser(id);

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already exists");
        }

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setActive(request.active());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        auditService.log("USER_UPDATED", "USER", user.getId(),
                user.getUsername() + ": role=" + user.getRole() + ", active=" + user.isActive());
        return UserResponse.from(user); // saved automatically at the end of the transaction
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
