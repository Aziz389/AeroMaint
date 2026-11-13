package com.aeromaint.repository;

import com.aeromaint.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    /** JOIN FETCH loads each equipment's zone in the same query (avoids the N+1 query problem). */
    @Query("SELECT e FROM Equipment e JOIN FETCH e.zone ORDER BY e.name")
    List<Equipment> findAllWithZone();

    boolean existsByZoneId(Long zoneId);
}
