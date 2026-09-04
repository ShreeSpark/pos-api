-- V8: devices, subscription_plan_configs, sync_queue

-- Devices
CREATE TABLE devices (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    device_code     VARCHAR(100) NOT NULL,
    device_name     VARCHAR(255) NOT NULL,
    platform        VARCHAR(50),
    app_version     VARCHAR(50),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_seen_at    TIMESTAMPTZ,
    registered_by   VARCHAR(255),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    version         BIGINT DEFAULT 0,
    created_at      TIMESTAMPTZ DEFAULT now(),
    created_by      VARCHAR(255),
    updated_at      TIMESTAMPTZ DEFAULT now(),
    updated_by      VARCHAR(255),
    deleted_at      TIMESTAMPTZ,
    deleted_by      VARCHAR(255),
    CONSTRAINT uq_device_code_tenant UNIQUE (device_code, tenant_id)
);

CREATE INDEX idx_devices_tenant ON devices(tenant_id);

-- Subscription Plan Configs (SUPER_ADMIN managed)
CREATE TABLE subscription_plan_configs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan            VARCHAR(30) NOT NULL UNIQUE,
    display_name    VARCHAR(100) NOT NULL,
    monthly_price   NUMERIC(10,2) NOT NULL,
    yearly_price    NUMERIC(10,2) NOT NULL,
    max_devices     INT NOT NULL DEFAULT 1,
    max_staff       INT NOT NULL DEFAULT 5,
    max_products    INT NOT NULL DEFAULT -1,
    features        TEXT,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ DEFAULT now(),
    updated_at      TIMESTAMPTZ DEFAULT now()
);

-- Seed default plan configs
INSERT INTO subscription_plan_configs (plan, display_name, monthly_price, yearly_price, max_devices, max_staff, max_products, features) VALUES
('TRIAL',    'Trial',    0.00,    0.00,    1,  2,   100,  'Basic billing, inventory'),
('BASIC',    'Basic',    499.00,  4999.00, 1,  5,   500,  'Billing, inventory, reports'),
('STANDARD', 'Standard', 999.00,  9999.00, 3,  15,  2000, 'All Basic + khata, membership, barcode'),
('PREMIUM',  'Premium',  1999.00, 19999.00, 10, -1, -1,   'All features, unlimited staff & products');

-- Offline Sync Queue
CREATE TABLE sync_queue (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         UUID NOT NULL REFERENCES tenants(id),
    device_id         UUID NOT NULL REFERENCES devices(id),
    client_request_id VARCHAR(255) NOT NULL,
    operation         VARCHAR(50) NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payload           JSONB NOT NULL,
    result_id         VARCHAR(255),
    error_message     TEXT,
    sequence          BIGINT NOT NULL,
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    version           BIGINT DEFAULT 0,
    created_at        TIMESTAMPTZ DEFAULT now(),
    created_by        VARCHAR(255),
    updated_at        TIMESTAMPTZ DEFAULT now(),
    updated_by        VARCHAR(255),
    deleted_at        TIMESTAMPTZ,
    deleted_by        VARCHAR(255),
    CONSTRAINT uq_sync_client_request_id UNIQUE (client_request_id)
);

CREATE INDEX idx_sync_queue_tenant_device ON sync_queue(tenant_id, device_id);
CREATE INDEX idx_sync_queue_status ON sync_queue(tenant_id, status);
