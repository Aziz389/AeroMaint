package com.aeromaint.service;

import com.aeromaint.dto.ZoneRequest;
import com.aeromaint.dto.ZoneResponse;
import com.aeromaint.entity.Zone;
import com.aeromaint.exception.BusinessRuleException;
import com.aeromaint.exception.NotFoundException;
import com.aeromaint.repository.EquipmentRepository;
import com.aeromaint.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final EquipmentRepository equipmentRepository;

    public ZoneService(ZoneRepository zoneRepository, EquipmentRepository equipmentRepository) {
        this.zoneRepository = zoneRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public List<ZoneResponse> list() {
        return zoneRepository.findAll().stream().map(ZoneResponse::from).toList();
    }

    public ZoneResponse get(Long id) {
        return ZoneResponse.from(findZone(id));
    }

    @Transactional
    public ZoneResponse create(ZoneRequest request) {
        if (zoneRepository.existsByName(request.name())) {
            throw new BusinessRuleException("A zone with this name already exists");
        }
        Zone zone = new Zone();
        zone.setName(request.name());
        zone.setDescription(request.description());
        return ZoneResponse.from(zoneRepository.save(zone));
    }

    @Transactional
    public ZoneResponse update(Long id, ZoneRequest request) {
        Zone zone = findZone(id);
        if (!zone.getName().equals(request.name()) && zoneRepository.existsByName(request.name())) {
            throw new BusinessRuleException("A zone with this name already exists");
        }
        zone.setName(request.name());
        zone.setDescription(request.description());
        return ZoneResponse.from(zone);
    }

    @Transactional
    public void delete(Long id) {
        Zone zone = findZone(id);
        if (equipmentRepository.existsByZoneId(id)) {
            throw new BusinessRuleException("Cannot delete a zone that still contains equipment");
        }
        zoneRepository.delete(zone);
    }

    Zone findZone(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Zone " + id + " not found"));
    }
}
