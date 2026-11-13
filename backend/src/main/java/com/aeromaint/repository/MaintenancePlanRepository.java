package com.aeromaint.repository;

import com.aeromaint.entity.MaintenancePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long> {

    List<MaintenancePlan> findByEquipmentIdOrderByNextDue(Long equipmentId);

    boolean existsByEquipmentId(Long equipmentId);
}
