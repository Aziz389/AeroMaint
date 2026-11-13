package com.aeromaint.entity;

/** Ticket life cycle: OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED. */
public enum TicketStatus {
    OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED;

    /**
     * The only allowed move is to the next step. Skipping a step (OPEN -> RESOLVED),
     * going back (RESOLVED -> IN_PROGRESS) or leaving CLOSED is rejected.
     */
    public boolean canTransitionTo(TicketStatus next) {
        return switch (this) {
            case OPEN -> next == ASSIGNED;
            case ASSIGNED -> next == IN_PROGRESS;
            case IN_PROGRESS -> next == RESOLVED;
            case RESOLVED -> next == CLOSED;
            case CLOSED -> false;
        };
    }
}
