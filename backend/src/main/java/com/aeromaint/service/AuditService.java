package com.aeromaint.service;

import com.aeromaint.entity.AuditLog;
import com.aeromaint.entity.User;
import com.aeromaint.repository.AuditLogRepository;
import com.aeromaint.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Writes the audit log: who did what, on which object, and when.
 * Services call log(...) inside their own @Transactional method, so the audit entry is saved
 * in the same transaction: if the action fails and is rolled back, its audit entry is too.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    /** Logs an action done by the user of the current request (null user when there is none, e.g. the scheduler). */
    public void log(String action, String entityType, Long entityId, String details) {
        log(currentUser(), action, entityType, entityId, details);
    }

    public void log(User user, String action, String entityType, Long entityId, String details) {
        AuditLog entry = new AuditLog();
        entry.setUser(user);
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetails(details == null || details.length() <= 500 ? details : details.substring(0, 500));
        auditLogRepository.save(entry);
    }

    /** The logged-in user, read from the SecurityContext filled by JwtAuthenticationFilter. */
    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }
}
