-- V6__create_customer_membership.sql

CREATE TABLE customers (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID          NOT NULL REFERENCES tenants(id),
    name                VARCHAR(255)  NOT NULL,
    phone               VARCHAR(20),
    email               VARCHAR(255),
    address             TEXT,
    gstin               VARCHAR(20),
    type                VARCHAR(20)   NOT NULL DEFAULT 'RETAIL',
    credit_limit        NUMERIC(10,2) NOT NULL DEFAULT 0,
    outstanding_balance NUMERIC(10,2) NOT NULL DEFAULT 0,
    active              BOOLEAN       NOT NULL DEFAULT TRUE,
    version             BIGINT        NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by          VARCHAR(255),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(255),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(255)
);

CREATE TABLE memberships (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID          NOT NULL REFERENCES tenants(id),
    tier                VARCHAR(20)   NOT NULL,
    name                VARCHAR(100)  NOT NULL,
    discount_percent    NUMERIC(5,2)  NOT NULL,
    min_purchase_amount NUMERIC(10,2),
    validity_days       INTEGER       NOT NULL,
    description         TEXT,
    active              BOOLEAN       NOT NULL DEFAULT TRUE,
    version             BIGINT        NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by          VARCHAR(255),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(255),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(255),
    CONSTRAINT uq_membership_tier_tenant UNIQUE (tenant_id, tier)
);

CREATE TABLE membership_subscriptions (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID        NOT NULL REFERENCES tenants(id),
    customer_id   UUID        NOT NULL REFERENCES customers(id),
    membership_id UUID        NOT NULL REFERENCES memberships(id),
    start_date    DATE        NOT NULL,
    end_date      DATE        NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    active        BOOLEAN     NOT NULL DEFAULT TRUE,
    version       BIGINT      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by    VARCHAR(255),
    updated_at    TIMESTAMPTZ,
    updated_by    VARCHAR(255),
    deleted_at    TIMESTAMPTZ,
    deleted_by    VARCHAR(255)
);

CREATE INDEX idx_customers_tenant          ON customers(tenant_id);
CREATE INDEX idx_customers_phone           ON customers(tenant_id, phone);
CREATE INDEX idx_customers_type            ON customers(tenant_id, type);
CREATE INDEX idx_memberships_tenant        ON memberships(tenant_id);
CREATE INDEX idx_mem_subs_customer         ON membership_subscriptions(customer_id);
CREATE INDEX idx_mem_subs_status           ON membership_subscriptions(customer_id, status);
CREATE INDEX idx_mem_subs_end_date         ON membership_subscriptions(tenant_id, end_date);
