-- =============================================================================
-- V9__audit_constraints.sql
-- Phase 1 audit fixes (issue #12, #13, #14 from 2026-09-08 audit).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Enforce booking status transition matrix from STATE_MACHINES.md §1.3
--    at the DB layer (defense in depth alongside the service-layer guard).
--    Allowed transitions:
--       pending     -> confirmed | declined | cancelled | expired
--       confirmed   -> cancelled  | in_progress | no_show
--       in_progress -> completed  | cancelled   | no_show
--       completed   -> rated
--       (any -> self-update is allowed so updated_at triggers can fire)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION enforce_booking_transition()
RETURNS TRIGGER AS $$
DECLARE
    allowed BOOLEAN := FALSE;
BEGIN
    IF OLD.status = NEW.status THEN
        RETURN NEW;
    END IF;

    allowed := CASE OLD.status
        WHEN 'pending'     THEN NEW.status IN ('confirmed', 'declined', 'cancelled', 'expired')
        WHEN 'confirmed'   THEN NEW.status IN ('cancelled',  'in_progress', 'no_show')
        WHEN 'in_progress' THEN NEW.status IN ('completed',  'cancelled',  'no_show')
        WHEN 'completed'   THEN NEW.status = 'rated'
        ELSE FALSE
    END;

    IF NOT allowed THEN
        RAISE EXCEPTION 'INVALID_STATE_TRANSITION: % -> % not allowed', OLD.status, NEW.status
            USING ERRCODE = 'check_violation';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_bookings_state_transition ON bookings;
CREATE TRIGGER trg_bookings_state_transition
    BEFORE UPDATE OF status ON bookings
    FOR EACH ROW
    WHEN (OLD.status IS DISTINCT FROM NEW.status)
    EXECUTE FUNCTION enforce_booking_transition();

-- -----------------------------------------------------------------------------
-- 2. Enforce ledger invariant #5: at most one EARN per booking (idempotency).
--    Closes the double-complete race window between the manual REST
--    /bookings/{id}/complete and the Daily webhook.
-- -----------------------------------------------------------------------------
CREATE UNIQUE INDEX IF NOT EXISTS uq_seed_tx_earn_per_booking
    ON seed_transactions (booking_id)
    WHERE type = 'earn' AND booking_id IS NOT NULL;

-- -----------------------------------------------------------------------------
-- 3. Ensure a single SPEND row per booking (escrow hold must be unique).
-- -----------------------------------------------------------------------------
CREATE UNIQUE INDEX IF NOT EXISTS uq_seed_tx_spend_per_booking
    ON seed_transactions (booking_id)
    WHERE type = 'spend' AND booking_id IS NOT NULL;
