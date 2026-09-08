-- =============================================================================
-- V10__rating_subscores.sql
-- Expansion of rating sub-scores per SKILLSEED_API_AND_DB.md §9.3
-- (audit #25). Existing respectfulness_score column is kept for
-- backward-compat with rows written before the audit fix.
-- =============================================================================

ALTER TABLE ratings
    ADD COLUMN IF NOT EXISTS knowledge_score      SMALLINT
        CHECK (knowledge_score IS NULL OR knowledge_score BETWEEN 1 AND 5),
    ADD COLUMN IF NOT EXISTS clarity_score         SMALLINT
        CHECK (clarity_score IS NULL OR clarity_score BETWEEN 1 AND 5),
    ADD COLUMN IF NOT EXISTS punctuality_score    SMALLINT
        CHECK (punctuality_score IS NULL OR punctuality_score BETWEEN 1 AND 5),
    ADD COLUMN IF NOT EXISTS friendliness_score   SMALLINT
        CHECK (friendliness_score IS NULL OR friendliness_score BETWEEN 1 AND 5),
    ADD COLUMN IF NOT EXISTS would_recommend      BOOLEAN;
