-- V5__create_inventory.sql

CREATE TABLE stock_ledger (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID        NOT NULL REFERENCES tenants(id),
    product_id    UUID        NOT NULL UNIQUE REFERENCES products(id),
    current_stock INTEGER     NOT NULL DEFAULT 0,
    active        BOOLEAN     NOT NULL DEFAULT TRUE,
    version       BIGINT      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by    VARCHAR(255),
    updated_at    TIMESTAMPTZ,
    updated_by    VARCHAR(255),
    deleted_at    TIMESTAMPTZ,
    deleted_by    VARCHAR(255)
);

CREATE TABLE stock_movements (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id    UUID         NOT NULL REFERENCES tenants(id),
    product_id   UUID         NOT NULL REFERENCES products(id),
    type         VARCHAR(20)  NOT NULL,
    quantity     INTEGER      NOT NULL,
    stock_before INTEGER      NOT NULL,
    stock_after  INTEGER      NOT NULL,
    reference_id VARCHAR(255),
    note         TEXT,
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    version      BIGINT       NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by   VARCHAR(255),
    updated_at   TIMESTAMPTZ,
    updated_by   VARCHAR(255),
    deleted_at   TIMESTAMPTZ,
    deleted_by   VARCHAR(255)
);

CREATE TABLE stock_adjustments (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         UUID        NOT NULL REFERENCES tenants(id),
    product_id        UUID        NOT NULL REFERENCES products(id),
    adjusted_quantity INTEGER     NOT NULL,
    stock_before      INTEGER     NOT NULL,
    stock_after       INTEGER     NOT NULL,
    reason            TEXT        NOT NULL,
    approved_by       VARCHAR(255),
    active            BOOLEAN     NOT NULL DEFAULT TRUE,
    version           BIGINT      NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by        VARCHAR(255),
    updated_at        TIMESTAMPTZ,
    updated_by        VARCHAR(255),
    deleted_at        TIMESTAMPTZ,
    deleted_by        VARCHAR(255)
);

CREATE INDEX idx_stock_ledger_product      ON stock_ledger(product_id);
CREATE INDEX idx_stock_ledger_tenant       ON stock_ledger(tenant_id);
CREATE INDEX idx_stock_movements_product   ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_tenant    ON stock_movements(tenant_id);
CREATE INDEX idx_stock_movements_type      ON stock_movements(tenant_id, type);
CREATE INDEX idx_stock_adjustments_product ON stock_adjustments(product_id);
CREATE INDEX idx_stock_adjustments_tenant  ON stock_adjustments(tenant_id);
