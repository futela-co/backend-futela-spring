-- V006: Payment module schema

CREATE TABLE currencies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(3) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    exchange_rate DECIMAL(10,4) NOT NULL DEFAULT 1.0000,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ
);

INSERT INTO currencies (code, name, symbol, exchange_rate) VALUES
    ('USD', 'Dollar américain', '$', 1.0000),
    ('CDF', 'Franc congolais', 'FC', 2800.0000);

CREATE TABLE payment_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    provider VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    logo VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ
);

INSERT INTO payment_methods (name, code, provider) VALUES
    ('M-Pesa', 'MPESA', 'Vodacom'),
    ('Airtel Money', 'AIRTEL', 'Airtel'),
    ('Orange Money', 'ORANGE', 'Orange'),
    ('Cash', 'CASH', 'Manual');

CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reference VARCHAR(255) NOT NULL UNIQUE,
    external_ref VARCHAR(255) UNIQUE,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    phone_number VARCHAR(20) NOT NULL,
    provider VARCHAR(50),
    user_id UUID NOT NULL,
    company_id UUID NOT NULL,
    description TEXT,
    metadata JSONB DEFAULT '{}',
    failure_reason TEXT,
    processed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    CONSTRAINT fk_tx_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_tx_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE INDEX idx_tx_reference ON transactions(reference);
CREATE INDEX idx_tx_external_ref ON transactions(external_ref);
CREATE INDEX idx_tx_user ON transactions(user_id);
CREATE INDEX idx_tx_status ON transactions(status);
