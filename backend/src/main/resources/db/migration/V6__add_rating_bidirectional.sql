-- V6__add_rating_bidirectional.sql — Sprint 3 (T-M170..T-M174)
-- Allow two ratings per booking: one from learner→teacher, one from teacher→learner.
-- The original V1 schema had UNIQUE(booking_id) which only allowed a single row.
-- We replace it with UNIQUE(booking_id, rater_id) so each participant can rate once.

ALTER TABLE ratings DROP CONSTRAINT IF EXISTS ratings_booking_id_key;

CREATE UNIQUE INDEX IF NOT EXISTS idx_ratings_booking_rater
    ON ratings(booking_id, rater_id);

-- Sanity: existing rows unaffected (none at this point since the table is fresh).
-- Recompute the discover-sort index on (ratee) to keep the leading column first.
CREATE INDEX IF NOT EXISTS idx_ratings_ratee ON ratings(ratee_id);
