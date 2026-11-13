package com.aeromaint.repository;

import com.aeromaint.entity.AuditLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /** The 200 most recent entries. @EntityGraph loads the user in the same query (no N+1). */
    @EntityGraph(attributePaths = "user")
    List<AuditLog> findTop200ByOrderByCreatedAtDescIdDesc();
}
