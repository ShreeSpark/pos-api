-- V4__create_product_modules.sql

CREATE TABLE gst_rates (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100)  NOT NULL,
    rate        NUMERIC(5,2)  NOT NULL,
    cgst_rate   NUMERIC(5,2)  NOT NULL,
    sgst_rate   NUMERIC(5,2)  NOT NULL,
    igst_rate   NUMERIC(5,2)  NOT NULL,
    description TEXT,
    active      BOOLEAN       NOT NULL DEFAULT TRUE,
    version     BIGINT        NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMPTZ,
    updated_by  VARCHAR(255),
    deleted_at  TIMESTAMPTZ,
    deleted_by  VARCHAR(255)
);

-- Seed standard Indian GST slabs
INSERT INTO gst_rates (name, rate, cgst_rate, sgst_rate, igst_rate, description)
VALUES
    ('GST 0%',  0.00,  0.00,  0.00,  0.00,  'Exempt / Nil rated'),
    ('GST 5%',  5.00,  2.50,  2.50,  5.00,  'Essential goods'),
    ('GST 12%', 12.00, 6.00,  6.00,  12.00, 'Standard goods'),
    ('GST 18%', 18.00, 9.00,  9.00,  18.00, 'Standard services and goods'),
    ('GST 28%', 28.00, 14.00, 14.00, 28.00, 'Luxury goods');

CREATE TABLE categories (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenants(id),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    image_url   VARCHAR(500),
    hsn_code    VARCHAR(20),
    gst_rate_id UUID         REFERENCES gst_rates(id),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    version     BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMPTZ,
    updated_by  VARCHAR(255),
    deleted_at  TIMESTAMPTZ,
    deleted_by  VARCHAR(255),
    CONSTRAINT uq_category_name_tenant UNIQUE (tenant_id, name)
);

CREATE TABLE brands (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL REFERENCES tenants(id),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    image_url   VARCHAR(500),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    version     BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMPTZ,
    updated_by  VARCHAR(255),
    deleted_at  TIMESTAMPTZ,
    deleted_by  VARCHAR(255),
    CONSTRAINT uq_brand_name_tenant UNIQUE (tenant_id, name)
);

CREATE TABLE products (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID          NOT NULL REFERENCES tenants(id),
    name                VARCHAR(255)  NOT NULL,
    description         TEXT,
    sku                 VARCHAR(100),
    image_url           VARCHAR(500),
    retail_price        NUMERIC(10,2) NOT NULL,
    wholesale_price     NUMERIC(10,2),
    dealer_price        NUMERIC(10,2),
    cost_price          NUMERIC(10,2),
    low_stock_threshold INTEGER       NOT NULL DEFAULT 5,
    moq                 INTEGER       NOT NULL DEFAULT 1,
    category_id         UUID          REFERENCES categories(id),
    brand_id            UUID          REFERENCES brands(id),
    active              BOOLEAN       NOT NULL DEFAULT TRUE,
    version             BIGINT        NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by          VARCHAR(255),
    updated_at          TIMESTAMPTZ,
    updated_by          VARCHAR(255),
    deleted_at          TIMESTAMPTZ,
    deleted_by          VARCHAR(255),
    CONSTRAINT uq_product_sku_tenant UNIQUE (tenant_id, sku)
);

CREATE TABLE barcodes (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id    UUID         NOT NULL REFERENCES tenants(id),
    product_id   UUID         NOT NULL REFERENCES products(id),
    value        VARCHAR(255) NOT NULL,
    format       VARCHAR(20)  NOT NULL,
    image_base64 TEXT,
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    version      BIGINT       NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by   VARCHAR(255),
    updated_at   TIMESTAMPTZ,
    updated_by   VARCHAR(255),
    deleted_at   TIMESTAMPTZ,
    deleted_by   VARCHAR(255),
    CONSTRAINT uq_barcode_value_tenant UNIQUE (tenant_id, value)
);

CREATE INDEX idx_gst_rates_rate          ON gst_rates(rate);
CREATE INDEX idx_categories_tenant       ON categories(tenant_id);
CREATE INDEX idx_categories_gst_rate     ON categories(gst_rate_id);
CREATE INDEX idx_brands_tenant           ON brands(tenant_id);
CREATE INDEX idx_products_tenant         ON products(tenant_id);
CREATE INDEX idx_products_category       ON products(category_id);
CREATE INDEX idx_products_brand          ON products(brand_id);
CREATE INDEX idx_barcodes_product        ON barcodes(product_id);
CREATE INDEX idx_barcodes_value_tenant   ON barcodes(tenant_id, value);
