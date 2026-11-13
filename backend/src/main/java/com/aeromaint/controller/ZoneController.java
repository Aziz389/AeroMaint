package com.aeromaint.controller;

import com.aeromaint.dto.ZoneRequest;
import com.aeromaint.dto.ZoneResponse;
import com.aeromaint.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Zones: every logged-in user can read them; only ADMIN can change them. */
@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    public List<ZoneResponse> list() {
        return zoneService.list();
    }

    @GetMapping("/{id}")
    public ZoneResponse get(@PathVariable Long id) {
        return zoneService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ZoneResponse create(@Valid @RequestBody ZoneRequest request) {
        return zoneService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ZoneResponse update(@PathVariable Long id, @Valid @RequestBody ZoneRequest request) {
        return zoneService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        zoneService.delete(id);
    }
}
