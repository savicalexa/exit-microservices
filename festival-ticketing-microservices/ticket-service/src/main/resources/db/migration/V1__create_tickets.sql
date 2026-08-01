CREATE TABLE ticket_inventory (
    festival_id BIGINT PRIMARY KEY,
    active_tickets INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_inventory_non_negative CHECK (active_tickets >= 0)
);

CREATE TABLE tickets (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    festival_id BIGINT NOT NULL,
    user_email VARCHAR(254) NOT NULL,
    festival_name VARCHAR(200) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    paid_at TIMESTAMPTZ NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_ticket_amount_positive CHECK (amount > 0)
);

CREATE INDEX ix_tickets_user_created ON tickets(user_id, created_at DESC);
CREATE INDEX ix_tickets_expiration ON tickets(status, expires_at);
CREATE INDEX ix_tickets_festival ON tickets(festival_id);
