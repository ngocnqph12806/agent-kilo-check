-- =============================================================================
-- V15__seed_admin_user.sql
-- Inserts a placeholder admin row. We don't set password_hash here because
-- BCrypt is salted and non-deterministic — the AdminSeedRunner reads
-- ADMIN_PASSWORD from the environment and stamps the hash at boot if the row
-- is still missing one. Set ADMIN_EMAIL/ADMIN_PASSWORD/ADMIN_FULL_NAME env
-- vars to control credentials (defaults: admin@skillseed.local /
-- SkillSeed!Admin2026 / "SkillSeed Admin").
-- =============================================================================

INSERT INTO users (id, email, password_hash, full_name, timezone, languages,
                    auth_provider, verified, verification_level,
                    onboarding_completed, rating_avg, sessions_completed, role)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    COALESCE(NULLIF(current_setting('skillseed.admin_email', true), ''),
             'admin@skillseed.local'),
    NULL,
    COALESCE(NULLIF(current_setting('skillseed.admin_full_name', true), ''),
             'SkillSeed Admin'),
    'UTC',
    ARRAY['en']::text[],
    'email',
    TRUE,
    2,
    TRUE,
    0.0,
    0,
    'admin'
)
ON CONFLICT (email) DO UPDATE SET role = 'admin';
