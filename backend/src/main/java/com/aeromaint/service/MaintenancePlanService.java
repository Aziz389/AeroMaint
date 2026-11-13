package com.aeromaint.service;

import com.aeromaint.dto.MaintenancePlanRequest;
import com.aeromaint.dto.MaintenancePlanResponse;
import com.aeromaint.entity.MaintenancePlan;
import com.aeromaint.exception.NotFoundException;
import com.aeromaint.repository.MaintenancePlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD for preventive maintenance plans. The ticket creation itself is in PreventiveMaintenanceScheduler. */
@Service
@Transactional(readOnly = true)
public class MaintenancePlanService {

    private final MaintenancePlanRepository planRepository;
    private final EquipmentService equipmentService;
    private final AuditService auditService;

    public MaintenancePlanService(MaintenancePlanRepository planRepository,
                                  EquipmentService equipmentService,
                                  AuditService auditService) {
        this.planRepository = planRepository;
        this.equipmentService = equipmentService;
        this.auditService = auditService;
    }

    public List<MaintenancePlanResponse> list() {
        return planRepository.findAllWithEquipment().stream().map(MaintenancePlanResponse::from).toList();
    }

    public MaintenancePlanResponse get(Long id) {
        return MaintenancePlanResponse.from(findPlan(id));
    }

    @Transactional
    public MaintenancePlanResponse create(MaintenancePlanRequest request) {
        MaintenancePlan plan = new MaintenancePlan();
        apply(plan, request);
        planRepository.save(plan);
        auditService.log("PLAN_CREATED", "MAINTENANCE_PLAN", plan.getId(), describe(plan));
        return MaintenancePlanResponse.from(plan);
    }

    @Transactional
    public MaintenancePlanResponse update(Long id, MaintenancePlanRequest request) {
        MaintenancePlan plan = findPlan(id);
        apply(plan, request);
        auditService.log("PLAN_UPDATED", "MAINTENANCE_PLAN", id, describe(plan));
        return MaintenancePlanResponse.from(plan);
    }

    @Transactional
    public void delete(Long id) {
        MaintenancePlan plan = findPlan(id);
        planRepository.delete(plan);
        auditService.log("PLAN_DELETED", "MAINTENANCE_PLAN", id, describe(plan));
    }

    private void apply(MaintenancePlan plan, MaintenancePlanRequest request) {
        plan.setEquipment(equipmentService.findEquipment(request.equipmentId()));
        plan.setIntervalDays(request.intervalDays());
        plan.setNextDue(request.nextDue());
        plan.setDescription(request.description());
    }

    private String describe(MaintenancePlan plan) {
        return plan.getEquipment().getName() + ": every " + plan.getIntervalDays() + " days, next " + plan.getNextDue();
    }

    private MaintenancePlan findPlan(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Maintenance plan " + id + " not found"));
    }
}
