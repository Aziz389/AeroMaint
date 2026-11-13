package com.aeromaint.repository;

import com.aeromaint.entity.MaintenancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long> {

    List<MaintenancePlan> findByEquipmentIdOrderByNextDue(Long equipmentId);

    boolean existsByEquipmentId(Long equipmentId);

    @Query("SELECT p FROM MaintenancePlan p JOIN FETCH p.equipment ORDER BY p.nextDue")
    List<MaintenancePlan> findAllWithEquipment();

    /** Plans whose next due date is today or already past: the scheduler creates a ticket for each. */
    @Query("SELECT p FROM MaintenancePlan p JOIN FETCH p.equipment WHERE p.nextDue <= :date")
    List<MaintenancePlan> findDueOnOrBefore(@Param("date") LocalDate date);
}
