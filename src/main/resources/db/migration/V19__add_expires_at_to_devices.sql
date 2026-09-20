-- V19: Add expires_at column to devices table for time-bound activation keys & trials

ALTER TABLE devices ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP WITH TIME ZONE;
