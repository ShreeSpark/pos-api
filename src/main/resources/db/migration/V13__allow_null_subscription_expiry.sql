-- V13__allow_null_subscription_expiry.sql
-- NULL subscription_expiry means lifetime / no expiry

ALTER TABLE tenants
    ALTER COLUMN subscription_expiry DROP NOT NULL;
