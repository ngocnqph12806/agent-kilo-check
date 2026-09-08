-- Sprint 3 — record whether a rating was authored manually
-- or assigned by the auto-rate job. Defaults to false for
-- backwards compatibility with already-inserted rows.

ALTER TABLE ratings
    ADD COLUMN IF NOT EXISTS auto_rated BOOLEAN NOT NULL DEFAULT FALSE;
