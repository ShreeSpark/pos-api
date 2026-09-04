-- V3__create_role_permissions.sql

CREATE TABLE role_permissions (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID        NOT NULL REFERENCES tenants(id),
    role        VARCHAR(30) NOT NULL,
    permission  VARCHAR(50) NOT NULL,
    CONSTRAINT uq_tenant_role_permission UNIQUE (tenant_id, role, permission)
);

CREATE INDEX idx_role_permissions_tenant_role ON role_permissions(tenant_id, role);
