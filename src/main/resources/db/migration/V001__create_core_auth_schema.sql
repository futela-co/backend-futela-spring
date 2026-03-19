-- ============================================================
-- V001 - Core & Auth Schema
-- Tables: companies, platform_settings, users, device_sessions, refresh_tokens
-- ============================================================

-- PostgreSQL ENUMs
CREATE TYPE user_role AS ENUM ('SUPER_ADMIN', 'ADMIN', 'OWNER', 'TENANT', 'USER');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED');

-- ============================================================
-- Companies (Tenant Root)
-- ============================================================
CREATE TABLE companies (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(255) NOT NULL,
    slug          VARCHAR(255) NOT NULL UNIQUE,
    email         VARCHAR(255),
    phone         VARCHAR(50),
    logo          VARCHAR(255),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at    TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_companies_slug ON companies (slug);
CREATE INDEX idx_companies_active ON companies (is_active) WHERE deleted_at IS NULL;

-- ============================================================
-- Platform Settings
-- ============================================================
CREATE TABLE platform_settings (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    setting_key   VARCHAR(100) NOT NULL UNIQUE,
    value         VARCHAR(255) NOT NULL,
    category      VARCHAR(100),
    description   VARCHAR(255),
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at    TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_platform_setting_key ON platform_settings (setting_key);

-- ============================================================
-- Users
-- ============================================================
CREATE TABLE users (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email               VARCHAR(180) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    phone               VARCHAR(20),
    avatar              VARCHAR(255),
    role                VARCHAR(20) NOT NULL DEFAULT 'USER',
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_verified         BOOLEAN NOT NULL DEFAULT FALSE,
    is_available        BOOLEAN NOT NULL DEFAULT TRUE,
    profile_completed   BOOLEAN NOT NULL DEFAULT FALSE,
    email_verified_at   TIMESTAMP WITH TIME ZONE,
    last_login_at       TIMESTAMP WITH TIME ZONE,
    company_id          UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_users_email ON users (email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_phone ON users (phone) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_company ON users (company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_role ON users (role) WHERE deleted_at IS NULL;

-- ============================================================
-- Device Sessions
-- ============================================================
CREATE TABLE device_sessions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_name         VARCHAR(255),
    device_fingerprint  VARCHAR(64) NOT NULL,
    ip_address          VARCHAR(45),
    user_agent          VARCHAR(500),
    location            VARCHAR(100),
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    is_trusted          BOOLEAN NOT NULL DEFAULT FALSE,
    last_active_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    revoked_at          TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_device_session_user_active ON device_sessions (user_id, is_active) WHERE deleted_at IS NULL;
CREATE INDEX idx_device_fingerprint ON device_sessions (device_fingerprint);

-- ============================================================
-- Refresh Tokens
-- ============================================================
CREATE TABLE refresh_tokens (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_session_id   UUID NOT NULL REFERENCES device_sessions(id) ON DELETE CASCADE,
    token_hash          VARCHAR(64) NOT NULL UNIQUE,
    expires_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at             TIMESTAMP WITH TIME ZONE,
    is_revoked          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_refresh_token_hash ON refresh_tokens (token_hash);
CREATE INDEX idx_refresh_token_expires ON refresh_tokens (expires_at);
CREATE INDEX idx_refresh_token_session ON refresh_tokens (device_session_id) WHERE is_revoked = FALSE;

-- ============================================================
-- Default data: insert default company
-- ============================================================
INSERT INTO companies (id, name, slug, is_active)
VALUES ('00000000-0000-0000-0000-000000000001', 'Futela', 'futela', TRUE);
