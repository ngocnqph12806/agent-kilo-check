-- =============================================================================
-- V11__idempotency_keys.sql
-- Per FR-M47 / T-M101: enforce POST /bookings idempotency so retries with
-- the same Idempotency-Key header return the original response instead of
-- double-submitting (which would create 2 escrows).
-- =============================================================================

CREATE TABLE IF NOT EXISTS idempotency_keys (
    key             VARCHAR(255) PRIMARY KEY,
    user_id         UUID NOT NULL,
    endpoint        VARCHAR(64) NOT NULL,
    request_hash    VARCHAR(128) NOT NULL,
    response_status INT NOT NULL,
    response_body   JSONB NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_idempotency_user_endpoint
    ON idempotency_keys(user_id, endpoint, created_at DESC);
