-- =============================================================================
-- V14__user_role.sql
-- Adds the `role` column on users for the Phase 1 admin surface (FR-M22).
-- Existing users default to USER; admins are seeded by V15 + AdminSeedRunner.
-- =============================================================================

ALTER TABLE users
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user';

ALTER TABLE users
    ADD CONSTRAINT chk_user_role CHECK (role IN ('user', 'admin'));

CREATE INDEX idx_users_role ON users(role) WHERE deleted_at IS NULL;
