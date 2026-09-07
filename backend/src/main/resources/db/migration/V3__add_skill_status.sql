-- =============================================================================
-- V3__add_skill_status.sql
-- Adds a review status column to the skills taxonomy table so user-created
-- (custom) skills can be queued for admin review (FR-M22, T-M41).
-- Existing seeded skills are marked 'approved' so they continue to surface
-- in /skills search results without changes.
-- =============================================================================

ALTER TABLE skills
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'approved';

ALTER TABLE skills
    ADD CONSTRAINT chk_skills_status
        CHECK (status IN ('pending_review', 'approved', 'rejected'));

CREATE INDEX idx_skills_status
    ON skills(status)
    WHERE status = 'pending_review';
