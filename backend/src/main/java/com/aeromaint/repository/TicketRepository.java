package com.aeromaint.repository;

import com.aeromaint.dto.DashboardResponse;
import com.aeromaint.entity.Ticket;
import com.aeromaint.entity.TicketPriority;
import com.aeromaint.entity.TicketStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Ticket list with optional filters: a null parameter means "do not filter on this".
     * JOIN FETCH loads equipment, zone, reporter and assignee in the same SQL query.
     */
    @Query("""
            SELECT t FROM Ticket t
            JOIN FETCH t.equipment e
            JOIN FETCH e.zone
            LEFT JOIN FETCH t.reporter
            LEFT JOIN FETCH t.assignee a
            WHERE (:status IS NULL OR t.status = :status)
              AND (:priority IS NULL OR t.priority = :priority)
              AND (:equipmentId IS NULL OR e.id = :equipmentId)
              AND (:assigneeId IS NULL OR a.id = :assigneeId)
            ORDER BY t.createdAt DESC
            """)
    List<Ticket> search(@Param("status") TicketStatus status,
                        @Param("priority") TicketPriority priority,
                        @Param("equipmentId") Long equipmentId,
                        @Param("assigneeId") Long assigneeId);

    @Query("""
            SELECT t FROM Ticket t
            JOIN FETCH t.equipment e
            JOIN FETCH e.zone
            LEFT JOIN FETCH t.reporter
            LEFT JOIN FETCH t.assignee
            WHERE e.id = :equipmentId
            ORDER BY t.createdAt DESC
            """)
    List<Ticket> findByEquipmentIdWithDetails(@Param("equipmentId") Long equipmentId);

    boolean existsByEquipmentId(Long equipmentId);

    // ---------- Dashboard statistics (computed by the database, not in Java) ----------

    long countByStatusIn(Collection<TicketStatus> statuses);

    /** Native SQL because JPQL has no portable date difference; TIMESTAMPDIFF works on MySQL and MariaDB. */
    @Query(value = "SELECT AVG(TIMESTAMPDIFF(MINUTE, created_at, resolved_at)) FROM tickets WHERE resolved_at IS NOT NULL",
           nativeQuery = true)
    Double averageResolutionMinutes();

    @Query("SELECT new com.aeromaint.dto.DashboardResponse$StatusCount(t.status, COUNT(t)) FROM Ticket t GROUP BY t.status")
    List<DashboardResponse.StatusCount> countByStatus();

    /** Fault tickets only: preventive tickets (reporter IS NULL) are planned work, not failures. */
    @Query("""
            SELECT new com.aeromaint.dto.DashboardResponse$EquipmentFailureCount(e.id, e.name, COUNT(t))
            FROM Ticket t JOIN t.equipment e
            WHERE t.reporter IS NOT NULL
            GROUP BY e.id, e.name
            ORDER BY COUNT(t) DESC, e.name
            """)
    List<DashboardResponse.EquipmentFailureCount> mostFailingEquipment(Pageable limit);

    /** LEFT JOIN so that technicians with zero active tickets still appear (with 0). */
    @Query("""
            SELECT new com.aeromaint.dto.DashboardResponse$TechnicianWorkload(u.id, u.fullName, COUNT(t))
            FROM User u
            LEFT JOIN Ticket t ON t.assignee = u AND t.status IN :activeStatuses
            WHERE u.role = com.aeromaint.entity.Role.TECHNICIAN AND u.active = true
            GROUP BY u.id, u.fullName
            ORDER BY COUNT(t) DESC, u.fullName
            """)
    List<DashboardResponse.TechnicianWorkload> technicianWorkload(
            @Param("activeStatuses") Collection<TicketStatus> activeStatuses);
}
