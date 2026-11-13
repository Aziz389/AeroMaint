package com.aeromaint.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/** A recurring preventive maintenance task, e.g. "inspect escalator every 30 days". */
@Entity
@Table(name = "maintenance_plans")
public class MaintenancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays;

    /** Date when the next maintenance ticket must be created. */
    @Column(name = "next_due", nullable = false)
    private LocalDate nextDue;

    @Column(nullable = false, length = 255)
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }

    public LocalDate getNextDue() { return nextDue; }
    public void setNextDue(LocalDate nextDue) { this.nextDue = nextDue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
