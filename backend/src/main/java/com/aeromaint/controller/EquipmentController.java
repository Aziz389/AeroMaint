package com.aeromaint.controller;

import com.aeromaint.dto.EquipmentHistoryResponse;
import com.aeromaint.dto.EquipmentRequest;
import com.aeromaint.dto.EquipmentResponse;
import com.aeromaint.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Equipment register: every logged-in user can read it; only ADMIN can change it. */
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<EquipmentResponse> list() {
        return equipmentService.list();
    }

    @GetMapping("/{id}")
    public EquipmentResponse get(@PathVariable Long id) {
        return equipmentService.get(id);
    }

    /** All tickets (faults and preventive maintenance) and maintenance plans for this equipment. */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public EquipmentHistoryResponse history(@PathVariable Long id) {
        return equipmentService.history(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public EquipmentResponse create(@Valid @RequestBody EquipmentRequest request) {
        return equipmentService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EquipmentResponse update(@PathVariable Long id, @Valid @RequestBody EquipmentRequest request) {
        return equipmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        equipmentService.delete(id);
    }
}
