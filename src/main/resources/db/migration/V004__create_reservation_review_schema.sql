-- ============================================================
-- V004: Reservation, Visit & Review tables
-- ============================================================

-- Reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id     UUID        NOT NULL REFERENCES properties(id),
    user_id         UUID        NOT NULL REFERENCES users(id),
    host_id         UUID        NOT NULL REFERENCES users(id),
    company_id      UUID        NOT NULL REFERENCES companies(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    start_date      DATE        NOT NULL,
    end_date        DATE        NOT NULL,
    total_price     DECIMAL(12, 2) NOT NULL,
    currency        VARCHAR(3)  NOT NULL DEFAULT 'USD',
    guest_count     INT         NOT NULL,
    notes           TEXT,
    cancel_reason   TEXT,
    confirmed_at    TIMESTAMP,
    cancelled_at    TIMESTAMP,
    completed_at    TIMESTAMP,
    payment_transaction_id VARCHAR(36),
    created_at      TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMP
);

CREATE INDEX idx_reservation_property ON reservations(property_id);
CREATE INDEX idx_reservation_user     ON reservations(user_id);
CREATE INDEX idx_reservation_status   ON reservations(status);

-- Visits table
CREATE TABLE IF NOT EXISTS visits (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id     UUID        NOT NULL REFERENCES properties(id),
    user_id         UUID        NOT NULL REFERENCES users(id),
    company_id      UUID        NOT NULL REFERENCES companies(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_at    TIMESTAMP   NOT NULL,
    notes           TEXT,
    cancel_reason   TEXT,
    confirmed_at    TIMESTAMP,
    completed_at    TIMESTAMP,
    payment_transaction_id VARCHAR(255),
    is_paid         BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMP
);

CREATE INDEX idx_visit_property ON visits(property_id);
CREATE INDEX idx_visit_user     ON visits(user_id);
CREATE INDEX idx_visit_status   ON visits(status);

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id     UUID        NOT NULL REFERENCES properties(id),
    user_id         UUID        NOT NULL REFERENCES users(id),
    company_id      UUID        NOT NULL REFERENCES companies(id),
    rating          INT         NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment         TEXT,
    is_approved     BOOLEAN     NOT NULL DEFAULT FALSE,
    is_flagged      BOOLEAN     NOT NULL DEFAULT FALSE,
    flag_reason     TEXT,
    created_at      TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT now(),
    deleted_at      TIMESTAMP,

    CONSTRAINT uk_review_user_property UNIQUE (user_id, property_id)
);

CREATE INDEX idx_review_property ON reviews(property_id);
CREATE INDEX idx_review_user     ON reviews(user_id);
