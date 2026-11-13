package com.aeromaint.controller;

import com.aeromaint.dto.MaintenancePlanRequest;
import com.aeromaint.dto.MaintenancePlanResponse;
import com.aeromaint.service.MaintenancePlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Preventive maintenance plans: SUPERVISOR manages them, ADMIN can read them. */
@RestController
@RequestMapping("/api/maintenance-plans")
public class MaintenancePlanController {

    private final MaintenancePlanService planService;

    public MaintenancePlanController(MaintenancePlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public List<MaintenancePlanResponse> list() {
        return planService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public MaintenancePlanResponse get(@PathVariable Long id) {
        return planService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SUPERVISOR')")
    public MaintenancePlanResponse create(@Valid @RequestBody MaintenancePlanRequest request) {
        return planService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public MaintenancePlanResponse update(@PathVariable Long id, @Valid @RequestBody MaintenancePlanRequest request) {
        return planService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SUPERVISOR')")
    public void delete(@PathVariable Long id) {
        planService.delete(id);
    }
}
