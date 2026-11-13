package com.aeromaint.dto;

import com.aeromaint.entity.Zone;

public record ZoneResponse(Long id, String name, String description) {

    public static ZoneResponse from(Zone zone) {
        return new ZoneResponse(zone.getId(), zone.getName(), zone.getDescription());
    }
}
