-- AeroMaint schema, version 1.
-- Enums (role, status, priority) are stored as VARCHAR so the same SQL runs on MySQL 8 and MariaDB.

CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL,   -- TECHNICIAN, SUPERVISOR, ADMIN
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE zones (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uk_zones_name UNIQUE (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE equipment (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(100) NOT NULL,
    type         VARCHAR(50)  NOT NULL,
    zone_id      BIGINT       NOT NULL,
    status       VARCHAR(20)  NOT NULL,    -- OPERATIONAL, UNDER_MAINTENANCE, OUT_OF_SERVICE
    installed_at DATE         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_equipment_zone FOREIGN KEY (zone_id) REFERENCES zones (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE tickets (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    equipment_id BIGINT       NOT NULL,
    reporter_id  BIGINT,                   -- NULL when created automatically by the maintenance scheduler
    assignee_id  BIGINT,                   -- NULL until a supervisor assigns a technician
    title        VARCHAR(150) NOT NULL,
    description  TEXT,
    priority     VARCHAR(20)  NOT NULL,    -- LOW, MEDIUM, HIGH, CRITICAL
    status       VARCHAR(20)  NOT NULL,    -- OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED
    created_at   DATETIME     NOT NULL,
    resolved_at  DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_tickets_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id),
    CONSTRAINT fk_tickets_reporter FOREIGN KEY (reporter_id) REFERENCES users (id),
    CONSTRAINT fk_tickets_assignee FOREIGN KEY (assignee_id) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Indexes for the ticket filters (status, priority, assignee); equipment_id is already indexed by its foreign key.
CREATE INDEX idx_tickets_status ON tickets (status);
CREATE INDEX idx_tickets_priority ON tickets (priority);

CREATE TABLE ticket_comments (
    id         BIGINT   NOT NULL AUTO_INCREMENT,
    ticket_id  BIGINT   NOT NULL,
    author_id  BIGINT   NOT NULL,
    content    TEXT     NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_comments_ticket FOREIGN KEY (ticket_id) REFERENCES tickets (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE maintenance_plans (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    equipment_id  BIGINT       NOT NULL,
    interval_days INT          NOT NULL,
    next_due      DATE         NOT NULL,
    description   VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_plans_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE audit_log (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT,                    -- NULL for actions done by the system (e.g. scheduler)
    action      VARCHAR(50)  NOT NULL,     -- e.g. TICKET_CREATED, TICKET_ASSIGNED, USER_CREATED
    entity_type VARCHAR(50)  NOT NULL,     -- e.g. TICKET, EQUIPMENT, USER
    entity_id   BIGINT,
    details     VARCHAR(500),
    created_at  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
