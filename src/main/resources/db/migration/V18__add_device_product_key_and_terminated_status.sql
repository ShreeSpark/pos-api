-- V18: Add product_key column and make tenant_id nullable for public device auto-registration

ALTER TABLE devices ADD COLUMN IF NOT EXISTS product_key VARCHAR(100);
ALTER TABLE devices ALTER COLUMN tenant_id DROP NOT NULL;
