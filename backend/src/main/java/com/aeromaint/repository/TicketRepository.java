package com.aeromaint.repository;

import com.aeromaint.entity.Ticket;
import com.aeromaint.entity.TicketPriority;
import com.aeromaint.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
