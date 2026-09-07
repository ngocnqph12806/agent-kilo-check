-- =============================================================================
-- V1__init_schema.sql
-- SkillSeed Phase 1 MVP — Initial database schema.
-- Source of truth: .kiro/specs/phase-1-mvp/design.md §3.2 + §3.3
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- -----------------------------------------------------------------------------
-- USERS
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email                   VARCHAR(255) UNIQUE NOT NULL,
    phone                   VARCHAR(20)  UNIQUE,
    password_hash           VARCHAR(255),
    full_name               VARCHAR(255) NOT NULL,
    avatar_url              TEXT,
    bio                     TEXT,
    country_code            CHAR(2),
    timezone                VARCHAR(50)  NOT NULL DEFAULT 'UTC',
    languages               TEXT[]       NOT NULL DEFAULT '{en}',
    learning_style          VARCHAR(20),
    auth_provider           VARCHAR(20)  NOT NULL DEFAULT 'email',
    verified                BOOLEAN      NOT NULL DEFAULT false,
    verification_level      SMALLINT     NOT NULL DEFAULT 0,
    onboarding_completed    BOOLEAN      NOT NULL DEFAULT false,
    rating_avg              DECIMAL(2,1) NOT NULL DEFAULT 0.0,
    sessions_completed      INT          NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at              TIMESTAMPTZ,
    CONSTRAINT chk_auth_provider CHECK (auth_provider IN ('email','google','apple')),
    CONSTRAINT chk_verification_level CHECK (verification_level BETWEEN 0 AND 3)
);

CREATE INDEX idx_users_country        ON users(country_code)        WHERE deleted_at IS NULL;
CREATE INDEX idx_users_verified       ON users(verified)            WHERE verified = true AND deleted_at IS NULL;
CREATE INDEX idx_users_email_lower    ON users(LOWER(email))        WHERE deleted_at IS NULL;
CREATE INDEX idx_users_rating         ON users(rating_avg DESC)     WHERE deleted_at IS NULL AND sessions_completed > 0;

-- -----------------------------------------------------------------------------
-- SKILLS (taxonomy)
-- -----------------------------------------------------------------------------
CREATE TABLE skills (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug        VARCHAR(100) UNIQUE NOT NULL,
    name        VARCHAR(255) NOT NULL,
    category    VARCHAR(50)  NOT NULL,
    is_custom   BOOLEAN      NOT NULL DEFAULT false,
    parent_id   UUID REFERENCES skills(id),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_skills_category CHECK (category IN ('tech','business','art','language','life','health','music','sport'))
);

CREATE INDEX idx_skills_category ON skills(category);
CREATE INDEX idx_skills_slug     ON skills(slug);
CREATE INDEX idx_skills_parent   ON skills(parent_id) WHERE parent_id IS NOT NULL;
CREATE INDEX idx_skills_custom   ON skills(is_custom) WHERE is_custom = true;

-- -----------------------------------------------------------------------------
-- USER_SKILLS_OFFERED
-- -----------------------------------------------------------------------------
CREATE TABLE user_skills_offered (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id            UUID NOT NULL REFERENCES skills(id),
    level               SMALLINT NOT NULL,
    years_experience    INT,
    description         TEXT,
    hourly_seed_rate    INT NOT NULL DEFAULT 60,
    is_active           BOOLEAN NOT NULL DEFAULT true,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(user_id, skill_id),
    CONSTRAINT chk_offered_level CHECK (level BETWEEN 1 AND 5)
);

CREATE INDEX idx_offered_skill ON user_skills_offered(skill_id) WHERE is_active = true;
CREATE INDEX idx_offered_user  ON user_skills_offered(user_id);

-- -----------------------------------------------------------------------------
-- USER_SKILLS_WANTED
-- -----------------------------------------------------------------------------
CREATE TABLE user_skills_wanted (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id        UUID NOT NULL REFERENCES skills(id),
    priority        SMALLINT NOT NULL DEFAULT 3,
    target_level    SMALLINT,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_wanted_priority CHECK (priority BETWEEN 1 AND 5),
    CONSTRAINT chk_wanted_target_level CHECK (target_level IS NULL OR target_level BETWEEN 1 AND 5)
);

CREATE INDEX idx_wanted_user  ON user_skills_wanted(user_id);
CREATE INDEX idx_wanted_skill ON user_skills_wanted(skill_id);

-- -----------------------------------------------------------------------------
-- USER_AVAILABILITY
-- -----------------------------------------------------------------------------
CREATE TABLE user_availability (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    day_of_week     SMALLINT NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    timezone        VARCHAR(50) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_day_of_week CHECK (day_of_week BETWEEN 0 AND 6),
    CONSTRAINT chk_time_order CHECK (end_time > start_time)
);

CREATE INDEX idx_availability_user_dow ON user_availability(user_id, day_of_week);

-- -----------------------------------------------------------------------------
-- BOOKINGS
-- -----------------------------------------------------------------------------
CREATE TABLE bookings (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id           UUID NOT NULL REFERENCES users(id),
    learner_id           UUID NOT NULL REFERENCES users(id),
    skill_id             UUID NOT NULL REFERENCES skills(id),
    scheduled_at         TIMESTAMPTZ NOT NULL,
    duration_minutes     SMALLINT NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'pending',
    seed_amount          INT NOT NULL,
    meeting_url          TEXT,
    recording_url        TEXT,
    notes                TEXT,
    cancellation_reason  VARCHAR(50),
    cancelled_by         UUID REFERENCES users(id),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_no_self_booking   CHECK (teacher_id != learner_id),
    CONSTRAINT chk_booking_status    CHECK (status IN ('pending','confirmed','declined','in_progress','completed','cancelled','expired','no_show','rated')),
    CONSTRAINT chk_booking_duration  CHECK (duration_minutes IN (15,30,45,60)),
    CONSTRAINT chk_seed_amount_pos   CHECK (seed_amount >= 0)
);

CREATE INDEX idx_bookings_teacher_scheduled ON bookings(teacher_id, scheduled_at);
CREATE INDEX idx_bookings_learner_scheduled ON bookings(learner_id, scheduled_at);
CREATE INDEX idx_bookings_status_scheduled  ON bookings(status, scheduled_at);
CREATE INDEX idx_bookings_skill             ON bookings(skill_id);

-- -----------------------------------------------------------------------------
-- RATINGS
-- -----------------------------------------------------------------------------
CREATE TABLE ratings (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id               UUID NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
    rater_id                 UUID NOT NULL REFERENCES users(id),
    ratee_id                 UUID NOT NULL REFERENCES users(id),
    overall_score            SMALLINT,
    review_text              TEXT,
    helpfulness_score        SMALLINT,
    respectfulness_score     SMALLINT,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_no_self_rating           CHECK (rater_id != ratee_id),
    CONSTRAINT chk_overall_score_range      CHECK (overall_score IS NULL OR overall_score BETWEEN 1 AND 5),
    CONSTRAINT chk_helpfulness_score_range  CHECK (helpfulness_score IS NULL OR helpfulness_score BETWEEN 1 AND 5),
    CONSTRAINT chk_respectfulness_range     CHECK (respectfulness_score IS NULL OR respectfulness_score BETWEEN 1 AND 5)
);

CREATE INDEX idx_ratings_ratee ON ratings(ratee_id);
CREATE INDEX idx_ratings_rater ON ratings(rater_id);

-- -----------------------------------------------------------------------------
-- SEED WALLETS (1:1 with user)
-- -----------------------------------------------------------------------------
CREATE TABLE seed_wallets (
    user_id           UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    balance_cached    INT NOT NULL DEFAULT 0,
    total_earned      INT NOT NULL DEFAULT 0,
    total_spent       INT NOT NULL DEFAULT 0,
    last_expiring_at  TIMESTAMPTZ,
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_balance_nonneg CHECK (balance_cached >= 0)
);

-- -----------------------------------------------------------------------------
-- SEED TRANSACTIONS (ledger)
-- -----------------------------------------------------------------------------
CREATE TABLE seed_transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id       UUID NOT NULL REFERENCES seed_wallets(user_id) ON DELETE CASCADE,
    type            VARCHAR(20) NOT NULL,
    amount          INT NOT NULL,
    balance_after   INT NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'completed',
    booking_id      UUID REFERENCES bookings(id),
    description     TEXT,
    expires_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_tx_type   CHECK (type   IN ('earn','spend','grant','expire','refund')),
    CONSTRAINT chk_tx_status CHECK (status IN ('pending','completed','cancelled'))
);

CREATE INDEX idx_seed_tx_wallet_created ON seed_transactions(wallet_id, created_at DESC);
CREATE INDEX idx_seed_tx_expiring       ON seed_transactions(expires_at)
    WHERE expires_at IS NOT NULL AND status = 'completed';
CREATE INDEX idx_seed_tx_booking        ON seed_transactions(booking_id) WHERE booking_id IS NOT NULL;
CREATE INDEX idx_seed_tx_wallet_status  ON seed_transactions(wallet_id, status);

-- -----------------------------------------------------------------------------
-- updated_at triggers
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_user_skills_offered_updated_at
    BEFORE UPDATE ON user_skills_offered
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_seed_wallets_updated_at
    BEFORE UPDATE ON seed_wallets
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
