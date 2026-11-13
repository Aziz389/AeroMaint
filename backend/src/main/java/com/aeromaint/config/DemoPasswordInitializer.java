package com.aeromaint.config;

import com.aeromaint.entity.User;
import com.aeromaint.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The seed data (V2 migration) creates demo users with password_hash = 'NOT_SET',
 * so no password is ever stored in Git.
 * On startup, if DEMO_PASSWORD is set, this gives those users that password (BCrypt-hashed).
 * It only touches users still marked NOT_SET, so it never overwrites a real password.
 */
@Component
public class DemoPasswordInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoPasswordInitializer.class);
    private static final String NOT_SET = "NOT_SET";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String demoPassword;

    public DemoPasswordInitializer(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   @Value("${aeromaint.demo-password}") String demoPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.demoPassword = demoPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<User> lockedUsers = userRepository.findByPasswordHash(NOT_SET);
        if (lockedUsers.isEmpty()) {
            return;
        }
        if (demoPassword == null || demoPassword.isBlank()) {
            log.warn("{} demo users have no password. Set DEMO_PASSWORD to enable them.", lockedUsers.size());
            return;
        }
        String hash = passwordEncoder.encode(demoPassword);
        lockedUsers.forEach(user -> user.setPasswordHash(hash));
        userRepository.saveAll(lockedUsers);
        log.info("Demo password set for {} users.", lockedUsers.size());
    }
}
