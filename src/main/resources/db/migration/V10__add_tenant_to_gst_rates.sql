-- V10__add_tenant_to_gst_rates.sql

ALTER TABLE gst_rates
    ADD COLUMN tenant_id UUID REFERENCES tenants(id);

CREATE INDEX idx_gst_rates_tenant ON gst_rates(tenant_id);
