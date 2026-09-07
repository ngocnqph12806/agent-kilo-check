-- =============================================================================
-- V4__add_notifications.sql
-- In-app notification inbox (T-M91).
--
-- A single notifications table powers the bell-icon dropdown in the FE
-- (T-M92 polling every 60s). payload is free-form JSON so future events
-- can be added without further migrations; the FE picks the right shape
-- by `type`. read_at IS NULL means unread.
-- =============================================================================

CREATE TABLE notifications (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type        VARCHAR(50) NOT NULL,
    payload     JSONB       NOT NULL DEFAULT '{}'::jsonb,
    read_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_notifications_type CHECK (type IN (
        'welcome',
        'email_verified',
        'booking_request',
        'booking_accepted',
        'booking_declined',
        'booking_cancelled',
        'booking_reminder_24h',
        'booking_reminder_1h',
        'session_started',
        'session_completed',
        'rating_prompt',
        'system'
    ))
);

-- Hot-path index for the polling GET /notifications/me?unreadOnly=true
CREATE INDEX idx_notifications_user_created
    ON notifications(user_id, created_at DESC);

CREATE INDEX idx_notifications_user_unread
    ON notifications(user_id, created_at DESC)
    WHERE read_at IS NULL;
