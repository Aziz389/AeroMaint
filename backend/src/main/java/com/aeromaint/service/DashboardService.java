package com.aeromaint.service;

import com.aeromaint.dto.DashboardResponse;
import com.aeromaint.entity.TicketStatus;
import com.aeromaint.repository.TicketRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Builds the dashboard statistics. Each number is one SQL query (see TicketRepository). */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    /** A ticket counts as "open" until it is resolved. */
    private static final List<TicketStatus> OPEN_STATUSES =
            List.of(TicketStatus.OPEN, TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS);

    /** Work a technician is actually holding. */
    private static final List<TicketStatus> ACTIVE_STATUSES =
            List.of(TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS);

    private final TicketRepository ticketRepository;

    public DashboardService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public DashboardResponse summary() {
        Double avgMinutes = ticketRepository.averageResolutionMinutes();
        Double avgHours = avgMinutes == null ? null : Math.round(avgMinutes / 60.0 * 10) / 10.0; // 1 decimal

        return new DashboardResponse(
                ticketRepository.countByStatusIn(OPEN_STATUSES),
                avgHours,
                ticketRepository.countByStatus(),
                ticketRepository.mostFailingEquipment(PageRequest.of(0, 5)),
                ticketRepository.technicianWorkload(ACTIVE_STATUSES));
    }
}
