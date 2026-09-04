-- V1__create_tenants_and_staff.sql

CREATE TABLE tenants (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID,
    business_name VARCHAR(255) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    phone       VARCHAR(20)  NOT NULL,
    address     TEXT,
    gstin       VARCHAR(20),
    status      VARCHAR(20)  NOT NULL DEFAULT 'TRIAL',
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    version     BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMPTZ,
    updated_by  VARCHAR(255),
    deleted_at  TIMESTAMPTZ,
    deleted_by  VARCHAR(255)
);

CREATE TABLE staff (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenants(id),
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(20),
    role        VARCHAR(30)  NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    version     BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMPTZ,
    updated_by  VARCHAR(255),
    deleted_at  TIMESTAMPTZ,
    deleted_by  VARCHAR(255)
);

CREATE TABLE staff_permissions (
    staff_id    UUID         NOT NULL REFERENCES staff(id),
    permission  VARCHAR(50)  NOT NULL,
    PRIMARY KEY (staff_id, permission)
);

CREATE INDEX idx_staff_tenant ON staff(tenant_id);
CREATE INDEX idx_staff_email  ON staff(email);
