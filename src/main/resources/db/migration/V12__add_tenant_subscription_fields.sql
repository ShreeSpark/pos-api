-- V12__add_tenant_subscription_fields.sql

ALTER TABLE tenants
    ADD COLUMN IF NOT EXISTS subscription_plan VARCHAR(30),
    ADD COLUMN IF NOT EXISTS subscription_expiry DATE;
