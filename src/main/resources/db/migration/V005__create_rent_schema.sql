-- V005: Rent module schema

CREATE TABLE leases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    landlord_id UUID NOT NULL,
    company_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    monthly_rent DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    deposit_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    payment_day_of_month INT NOT NULL DEFAULT 1,
    notes TEXT,
    terminated_at TIMESTAMPTZ,
    termination_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    CONSTRAINT fk_lease_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_lease_tenant FOREIGN KEY (tenant_id) REFERENCES users(id),
    CONSTRAINT fk_lease_landlord FOREIGN KEY (landlord_id) REFERENCES users(id),
    CONSTRAINT fk_lease_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE INDEX idx_lease_landlord ON leases(landlord_id);
CREATE INDEX idx_lease_tenant ON leases(tenant_id);
CREATE INDEX idx_lease_property ON leases(property_id);
CREATE INDEX idx_lease_status ON leases(status);

CREATE TABLE rent_invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lease_id UUID NOT NULL,
    company_id UUID NOT NULL,
    invoice_number VARCHAR(255) NOT NULL UNIQUE,
    amount DECIMAL(12,2) NOT NULL,
    paid_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    due_date DATE NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    late_fee DECIMAL(12,2) DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    CONSTRAINT fk_rinvoice_lease FOREIGN KEY (lease_id) REFERENCES leases(id),
    CONSTRAINT fk_rinvoice_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE INDEX idx_rinvoice_lease ON rent_invoices(lease_id);
CREATE INDEX idx_rinvoice_status ON rent_invoices(status);
CREATE INDEX idx_rinvoice_due_date ON rent_invoices(due_date);

CREATE TABLE rent_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID,
    lease_id UUID NOT NULL,
    company_id UUID NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50),
    reference VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    CONSTRAINT fk_rpayment_invoice FOREIGN KEY (invoice_id) REFERENCES rent_invoices(id),
    CONSTRAINT fk_rpayment_lease FOREIGN KEY (lease_id) REFERENCES leases(id),
    CONSTRAINT fk_rpayment_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE INDEX idx_rpayment_lease ON rent_payments(lease_id);
CREATE INDEX idx_rpayment_invoice ON rent_payments(invoice_id);

CREATE TABLE payment_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lease_id UUID NOT NULL,
    company_id UUID NOT NULL,
    due_date DATE NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    invoice_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    CONSTRAINT fk_pschedule_lease FOREIGN KEY (lease_id) REFERENCES leases(id),
    CONSTRAINT fk_pschedule_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE INDEX idx_pschedule_lease ON payment_schedules(lease_id);

CREATE TABLE rent_reminders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL,
    lease_id UUID NOT NULL,
    company_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    sent_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    channel VARCHAR(50) NOT NULL DEFAULT 'push',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    CONSTRAINT fk_rreminder_invoice FOREIGN KEY (invoice_id) REFERENCES rent_invoices(id),
    CONSTRAINT fk_rreminder_lease FOREIGN KEY (lease_id) REFERENCES leases(id),
    CONSTRAINT fk_rreminder_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE INDEX idx_rreminder_lease ON rent_reminders(lease_id);
CREATE INDEX idx_rreminder_invoice ON rent_reminders(invoice_id);
