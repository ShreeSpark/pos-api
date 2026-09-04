-- V7__create_khata_sales_payment.sql

CREATE TABLE khata_entries (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL REFERENCES tenants(id),
    customer_id     UUID          NOT NULL REFERENCES customers(id),
    type            VARCHAR(10)   NOT NULL,
    amount          NUMERIC(10,2) NOT NULL,
    balance_before  NUMERIC(10,2) NOT NULL,
    balance_after   NUMERIC(10,2) NOT NULL,
    reference_id    UUID,
    note            TEXT,
    active          BOOLEAN       NOT NULL DEFAULT TRUE,
    version         BIGINT        NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by      VARCHAR(255),
    updated_at      TIMESTAMPTZ,
    updated_by      VARCHAR(255),
    deleted_at      TIMESTAMPTZ,
    deleted_by      VARCHAR(255)
);

CREATE TABLE sales (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID          NOT NULL REFERENCES tenants(id),
    invoice_number  VARCHAR(20)   NOT NULL,
    customer_id     UUID          REFERENCES customers(id),
    customer_name   VARCHAR(255),
    staff_id        UUID,
    status          VARCHAR(20)   NOT NULL DEFAULT 'COMPLETED',
    payment_method  VARCHAR(20)   NOT NULL,
    subtotal        NUMERIC(10,2) NOT NULL,
    discount_amount NUMERIC(10,2) NOT NULL DEFAULT 0,
    taxable_amount  NUMERIC(10,2) NOT NULL,
    cgst_amount     NUMERIC(10,2) NOT NULL DEFAULT 0,
    sgst_amount     NUMERIC(10,2) NOT NULL DEFAULT 0,
    igst_amount     NUMERIC(10,2) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(10,2) NOT NULL,
    paid_amount     NUMERIC(10,2) NOT NULL DEFAULT 0,
    khata_amount    NUMERIC(10,2) NOT NULL DEFAULT 0,
    inter_state     BOOLEAN       NOT NULL DEFAULT FALSE,
    note            TEXT,
    active          BOOLEAN       NOT NULL DEFAULT TRUE,
    version         BIGINT        NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by      VARCHAR(255),
    updated_at      TIMESTAMPTZ,
    updated_by      VARCHAR(255),
    deleted_at      TIMESTAMPTZ,
    deleted_by      VARCHAR(255),
    CONSTRAINT uq_invoice_tenant UNIQUE (tenant_id, invoice_number)
);

CREATE TABLE sale_items (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID          NOT NULL REFERENCES tenants(id),
    sale_id          UUID          NOT NULL REFERENCES sales(id),
    product_id       UUID          NOT NULL,
    product_name     VARCHAR(255)  NOT NULL,
    hsn_code         VARCHAR(20),
    quantity         INTEGER       NOT NULL,
    unit_price       NUMERIC(10,2) NOT NULL,
    discount_percent NUMERIC(5,2)  NOT NULL DEFAULT 0,
    discount_amount  NUMERIC(10,2) NOT NULL DEFAULT 0,
    taxable_amount   NUMERIC(10,2) NOT NULL,
    cgst_percent     NUMERIC(5,2)  NOT NULL DEFAULT 0,
    cgst_amount      NUMERIC(10,2) NOT NULL DEFAULT 0,
    sgst_percent     NUMERIC(5,2)  NOT NULL DEFAULT 0,
    sgst_amount      NUMERIC(10,2) NOT NULL DEFAULT 0,
    igst_percent     NUMERIC(5,2)  NOT NULL DEFAULT 0,
    igst_amount      NUMERIC(10,2) NOT NULL DEFAULT 0,
    line_total       NUMERIC(10,2) NOT NULL,
    active           BOOLEAN       NOT NULL DEFAULT TRUE,
    version          BIGINT        NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by       VARCHAR(255),
    updated_at       TIMESTAMPTZ,
    updated_by       VARCHAR(255),
    deleted_at       TIMESTAMPTZ,
    deleted_by       VARCHAR(255)
);

CREATE TABLE payment_transactions (
    id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID          NOT NULL REFERENCES tenants(id),
    sale_id          UUID          NOT NULL REFERENCES sales(id),
    customer_id      UUID          REFERENCES customers(id),
    method           VARCHAR(20)   NOT NULL,
    status           VARCHAR(20)   NOT NULL DEFAULT 'SUCCESS',
    amount           NUMERIC(10,2) NOT NULL,
    reference_number VARCHAR(255),
    note             TEXT,
    active           BOOLEAN       NOT NULL DEFAULT TRUE,
    version          BIGINT        NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by       VARCHAR(255),
    updated_at       TIMESTAMPTZ,
    updated_by       VARCHAR(255),
    deleted_at       TIMESTAMPTZ,
    deleted_by       VARCHAR(255)
);

-- Indexes
CREATE INDEX idx_khata_customer        ON khata_entries(customer_id);
CREATE INDEX idx_khata_tenant          ON khata_entries(tenant_id);
CREATE INDEX idx_sales_tenant          ON sales(tenant_id);
CREATE INDEX idx_sales_customer        ON sales(tenant_id, customer_id);
CREATE INDEX idx_sales_invoice         ON sales(tenant_id, invoice_number);
CREATE INDEX idx_sales_status          ON sales(tenant_id, status);
CREATE INDEX idx_sales_created         ON sales(tenant_id, created_at);
CREATE INDEX idx_sale_items_sale       ON sale_items(sale_id);
CREATE INDEX idx_sale_items_product    ON sale_items(product_id);
CREATE INDEX idx_payment_sale          ON payment_transactions(sale_id);
CREATE INDEX idx_payment_customer      ON payment_transactions(customer_id);
CREATE INDEX idx_payment_tenant        ON payment_transactions(tenant_id);
