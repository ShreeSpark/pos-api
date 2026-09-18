-- V14__add_hsn_and_product_gst_overrides.sql

-- Add master HSN code to gst_rates
ALTER TABLE gst_rates ADD COLUMN IF NOT EXISTS hsn_code VARCHAR(20);

-- Add product level HSN code and GST rate overrides to products
ALTER TABLE products ADD COLUMN IF NOT EXISTS hsn_code VARCHAR(20);
ALTER TABLE products ADD COLUMN IF NOT EXISTS gst_rate_id UUID REFERENCES gst_rates(id);

-- Create index for product GST rate lookup
CREATE INDEX IF NOT EXISTS idx_products_gst_rate ON products(gst_rate_id);
