package com.aeromaint.dto;

import java.util.List;

/**
 * Full history of one piece of equipment:
 * every ticket raised on it (faults and preventive maintenance, newest first)
 * and its preventive maintenance plans.
 */
public record EquipmentHistoryResponse(
        EquipmentResponse equipment,
        List<TicketResponse> tickets,
        List<MaintenancePlanResponse> maintenancePlans
) {}
