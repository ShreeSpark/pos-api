-- V16__add_sale_audit_logs_and_staff_tracking.sql
ALTER TABLE sales ADD COLUMN IF NOT EXISTS staff_name VARCHAR(255);
ALTER TABLE sales ADD COLUMN IF NOT EXISTS edited_by_staff_id UUID;
ALTER TABLE sales ADD COLUMN IF NOT EXISTS edited_by_staff_name VARCHAR(255);

CREATE TABLE IF NOT EXISTS sale_audit_logs (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    sale_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    staff_id UUID,
    staff_name VARCHAR(255),
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_sale_audit_logs_sale_id ON sale_audit_logs(sale_id);
CREATE INDEX IF NOT EXISTS idx_sale_audit_logs_tenant_id ON sale_audit_logs(tenant_id);
