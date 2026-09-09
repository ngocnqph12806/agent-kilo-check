-- =============================================================================
-- V17__waitlist.sql
-- Captures marketing waitlist signups from the landing page (Phase 1, T-M423).
-- Email is the natural primary key (lower-cased). We never expose this list
-- beyond admins; the index supports the dedupe + admin lookups.
-- =============================================================================

CREATE TABLE waitlist (
    id              UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    email_lower     VARCHAR(255) NOT NULL UNIQUE,
    source          VARCHAR(40)  NOT NULL DEFAULT 'landing',
    referrer        VARCHAR(255),
    user_agent      VARCHAR(255),
    ip_address      VARCHAR(64),
    confirmed_at    TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_waitlist_source ON waitlist(source, created_at);
CREATE INDEX idx_waitlist_created_at ON waitlist(created_at);
