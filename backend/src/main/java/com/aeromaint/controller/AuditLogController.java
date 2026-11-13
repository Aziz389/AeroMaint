package com.aeromaint.controller;

import com.aeromaint.dto.AuditLogResponse;
import com.aeromaint.repository.AuditLogRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Read-only audit log, ADMIN only. Nobody can edit or delete audit entries through the API. */
@RestController
@RequestMapping("/api/audit-log")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<AuditLogResponse> latest() {
        return auditLogRepository.findTop200ByOrderByCreatedAtDescIdDesc()
                .stream().map(AuditLogResponse::from).toList();
    }
}
