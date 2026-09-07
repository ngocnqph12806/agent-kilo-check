-- Sprint 3 — relax ratings unique constraint so that
-- each booking can have ONE rating from the learner AND one
-- from the teacher (the "1 booking chỉ có 1 rating mỗi phía"
-- rule from the spec).
--
-- Before: ratings.booking_id UNIQUE  (only one row per booking)
-- After : UNIQUE(booking_id, rater_id) (one row per (booking, rater))

ALTER TABLE ratings DROP CONSTRAINT IF EXISTS ratings_booking_id_key;

ALTER TABLE ratings
    ADD CONSTRAINT ratings_booking_rater_unique UNIQUE (booking_id, rater_id);

CREATE INDEX IF NOT EXISTS idx_ratings_ratee_created
    ON ratings(ratee_id, created_at DESC);
