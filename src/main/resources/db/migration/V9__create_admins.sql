-- V9__create_admins.sql

CREATE TABLE admins (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    version     BIGINT,
    created_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ
);
