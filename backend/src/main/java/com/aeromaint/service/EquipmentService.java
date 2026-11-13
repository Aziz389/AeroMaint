package com.aeromaint.service;

import com.aeromaint.dto.*;
import com.aeromaint.entity.Equipment;
import com.aeromaint.exception.BusinessRuleException;
import com.aeromaint.exception.NotFoundException;
import com.aeromaint.repository.EquipmentRepository;
import com.aeromaint.repository.MaintenancePlanRepository;
import com.aeromaint.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Equipment register. @Transactional(readOnly = true) on the class keeps a database session open
 * while we map entities to DTOs, so LAZY relations (zone, assignee...) can still be loaded.
 */
@Service
@Transactional(readOnly = true)
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final TicketRepository ticketRepository;
    private final MaintenancePlanRepository planRepository;
    private final ZoneService zoneService;
    private final AuditService auditService;

    public EquipmentService(EquipmentRepository equipmentRepository,
                            TicketRepository ticketRepository,
                            MaintenancePlanRepository planRepository,
                            ZoneService zoneService,
                            AuditService auditService) {
        this.equipmentRepository = equipmentRepository;
        this.ticketRepository = ticketRepository;
        this.planRepository = planRepository;
        this.zoneService = zoneService;
        this.auditService = auditService;
    }

    public List<EquipmentResponse> list() {
        return equipmentRepository.findAllWithZone().stream().map(EquipmentResponse::from).toList();
    }

    public EquipmentResponse get(Long id) {
        return EquipmentResponse.from(findEquipment(id));
    }

    public EquipmentHistoryResponse history(Long id) {
        Equipment equipment = findEquipment(id);
        List<TicketResponse> tickets = ticketRepository.findByEquipmentIdWithDetails(id)
                .stream().map(TicketResponse::from).toList();
        List<MaintenancePlanResponse> plans = planRepository.findByEquipmentIdOrderByNextDue(id)
                .stream().map(MaintenancePlanResponse::from).toList();
        return new EquipmentHistoryResponse(EquipmentResponse.from(equipment), tickets, plans);
    }

    @Transactional
    public EquipmentResponse create(EquipmentRequest request) {
        Equipment equipment = new Equipment();
        apply(equipment, request);
        equipmentRepository.save(equipment);
        auditService.log("EQUIPMENT_CREATED", "EQUIPMENT", equipment.getId(), equipment.getName());
        return EquipmentResponse.from(equipment);
    }

    @Transactional
    public EquipmentResponse update(Long id, EquipmentRequest request) {
        Equipment equipment = findEquipment(id);
        apply(equipment, request);
        auditService.log("EQUIPMENT_UPDATED", "EQUIPMENT", id, equipment.getName() + ": status=" + equipment.getStatus());
        return EquipmentResponse.from(equipment);
    }

    /** Equipment with tickets or plans is part of the history and cannot be deleted (set it OUT_OF_SERVICE instead). */
    @Transactional
    public void delete(Long id) {
        Equipment equipment = findEquipment(id);
        if (ticketRepository.existsByEquipmentId(id) || planRepository.existsByEquipmentId(id)) {
            throw new BusinessRuleException("Equipment has tickets or maintenance plans; set it OUT_OF_SERVICE instead");
        }
        equipmentRepository.delete(equipment);
        auditService.log("EQUIPMENT_DELETED", "EQUIPMENT", id, equipment.getName());
    }

    private void apply(Equipment equipment, EquipmentRequest request) {
        equipment.setName(request.name());
        equipment.setType(request.type());
        equipment.setZone(zoneService.findZone(request.zoneId()));
        equipment.setStatus(request.status());
        equipment.setInstalledAt(request.installedAt());
    }

    Equipment findEquipment(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipment " + id + " not found"));
    }
}
