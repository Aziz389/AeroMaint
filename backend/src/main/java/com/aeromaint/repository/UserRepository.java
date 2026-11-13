package com.aeromaint.repository;

import com.aeromaint.entity.Role;
import com.aeromaint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Spring Data generates the SQL for these methods from their names. */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByPasswordHash(String passwordHash);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
