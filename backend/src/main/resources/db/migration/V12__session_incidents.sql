-- =============================================================================
-- V12__session_incidents.sql
-- Per FR-M57: a "Report issue" tap on the in-session toolbar must
-- create an open incident ticket that ops / the on-call engineer
-- can pick up from the notification feed.
-- =============================================================================

CREATE TABLE IF NOT EXISTS session_incidents (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id  UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    reporter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category    VARCHAR(40) NOT NULL,
    description TEXT NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'open',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    resolved_at TIMESTAMPTZ,
    CONSTRAINT chk_incident_status CHECK (status IN ('open','investigating','resolved','dismissed'))
);

CREATE INDEX IF NOT EXISTS idx_incidents_booking_status
    ON session_incidents(booking_id, status);
CREATE INDEX IF NOT EXISTS idx_incidents_reporter
    ON session_incidents(reporter_id, created_at DESC);
