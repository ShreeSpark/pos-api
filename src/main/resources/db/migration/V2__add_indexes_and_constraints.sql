-- V2__add_indexes_and_constraints.sql

-- Tenant indexes
CREATE INDEX IF NOT EXISTS idx_tenants_email  ON tenants(email);
CREATE INDEX IF NOT EXISTS idx_tenants_status ON tenants(status);

-- Staff indexes (already created in V1, adding composite)
CREATE INDEX IF NOT EXISTS idx_staff_tenant_role ON staff(tenant_id, role);
CREATE INDEX IF NOT EXISTS idx_staff_active      ON staff(tenant_id, active);
