# SkillSeed — Database ERD (Phase 1 MVP)

> **Diagram quan hệ giữa các bảng trong database Phase 1.**
> Render: GitHub / VS Code (Mermaid extension) / [mermaid.live](https://mermaid.live).
>
> **Nguyên tắc:** File này là **visual summary** — chi tiết DDL ở `.kiro/specs/phase-1-mvp/design.md` §3.2.

**Last updated:** 2026-09-07
**Source of truth:** `.kiro/specs/phase-1-mvp/design.md` §3 + `SKILLSEED_API_AND_DB.md` §8

---

## 1. ERD — Core entities

```mermaid
erDiagram
    USER ||--o{ USER_SKILL_OFFERED : "offers"
    USER ||--o{ USER_SKILL_WANTED  : "wants"
    USER ||--o{ USER_AVAILABILITY  : "has"
    USER ||--|| SEED_WALLET        : "owns"
    USER ||--o{ BOOKING            : "teaches (as teacher)"
    USER ||--o{ BOOKING            : "learns (as learner)"
    USER ||--o{ RATING             : "gives (as rater)"
    USER ||--o{ RATING             : "receives (as ratee)"
    USER ||--o{ SEED_TRANSACTION   : "owns (via wallet)"

    SKILL ||--o{ USER_SKILL_OFFERED : "is offered by"
    SKILL ||--o{ USER_SKILL_WANTED  : "is wanted by"
    SKILL ||--o{ BOOKING            : "is taught in"
    SKILL ||--o{ SKILL              : "has parent"

    BOOKING ||--o| RATING            : "is rated by (1 per booking)"
    BOOKING ||--o{ SEED_TRANSACTION  : "creates ledger entries"

    SEED_WALLET ||--o{ SEED_TRANSACTION : "has ledger"

    USER {
        uuid id PK
        string email UK
        string phone UK
        string password_hash
        string full_name
        string avatar_url
        text bio
        char2 country_code
        string timezone
        text_array languages
        string learning_style
        string auth_provider
        boolean verified
        smallint verification_level "0=email, 1=phone, 2=id, 3=liveness"
        boolean onboarding_completed
        decimal rating_avg
        int sessions_completed
        timestamptz created_at
        timestamptz updated_at
        timestamptz deleted_at "soft delete (GDPR)"
    }

    SKILL {
        uuid id PK
        string slug UK
        string name
        string category
        boolean is_custom
        uuid parent_id FK "self-reference for taxonomy"
        timestamptz created_at
    }

    USER_SKILL_OFFERED {
        uuid id PK
        uuid user_id FK
        uuid skill_id FK
        smallint level "1–5"
        int years_experience
        text description
        int hourly_seed_rate "default 60 = 1/min"
        boolean is_active
        timestamptz created_at
    }

    USER_SKILL_WANTED {
        uuid id PK
        uuid user_id FK
        uuid skill_id FK
        smallint priority "1–5"
        smallint target_level
        text notes
        timestamptz created_at
    }

    USER_AVAILABILITY {
        uuid id PK
        uuid user_id FK
        smallint day_of_week "0=Sun..6=Sat"
        time start_time
        time end_time
        string timezone
        timestamptz created_at
    }

    BOOKING {
        uuid id PK
        uuid teacher_id FK
        uuid learner_id FK
        uuid skill_id FK
        timestamptz scheduled_at
        smallint duration_minutes "15|30|45|60"
        string status "pending|confirmed|in_progress|completed|cancelled|expired|declined|no_show"
        int seed_amount "escrow amount"
        text meeting_url "Daily.co room"
        text recording_url
        text notes
        string cancellation_reason
        uuid cancelled_by FK
        timestamptz created_at
        timestamptz updated_at
    }

    RATING {
        uuid id PK
        uuid booking_id FK,UK "1 rating per booking"
        uuid rater_id FK
        uuid ratee_id FK
        smallint overall_score "1–5 (learner→teacher)"
        text review_text
        smallint helpfulness_score "1–5 (teacher→learner)"
        smallint respectfulness_score "1–5 (teacher→learner)"
        timestamptz created_at
    }

    SEED_WALLET {
        uuid user_id PK,FK "1 wallet per user"
        int balance_cached "denormalized, recalc from ledger"
        int total_earned
        int total_spent
        timestamptz last_expiring_at
        timestamptz updated_at
    }

    SEED_TRANSACTION {
        uuid id PK
        uuid wallet_id FK
        string type "earn|spend|grant|expire|refund"
        int amount "positive=in, negative=out"
        int balance_after
        string status "pending|completed|cancelled"
        uuid booking_id FK "nullable"
        text description
        timestamptz expires_at "nullable for non-expiring"
        timestamptz created_at
    }
```

---

## 2. Module → Tables mapping

| Module (BE) | Tables owned | Reads (cross-module) |
|---|---|---|
| `auth` | `users` (writes on register) | — |
| `user` | `users`, `user_availability`, soft-delete | `ratings`, `seed_wallets` |
| `skill` | `skills`, `user_skills_offered`, `user_skills_wanted` | — |
| `discover` | — (read-only) | `users`, `user_skills_offered`, `ratings` |
| `booking` | `bookings` | `users`, `skills`, `seed_wallets`, `seed_transactions` |
| `rating` | `ratings` | `bookings`, `users` |
| `wallet` | `seed_wallets`, `seed_transactions` | `bookings`, `users` |
| `session` | `bookings.meeting_url`, `bookings.recording_url` | `bookings`, `users` |
| `notification` | (no dedicated table — uses Redis + email) | `bookings`, `users` |
| `admin` | (no dedicated table — reads all above) | all |

**Lưu ý:** Module `wallet` chỉ ghi vào `seed_transactions` từ:
- `booking` (hold/release/capture cho booking flow)
- `session` (capture khi session complete)
- `admin` (grant/refund manual)

---

## 3. Critical indexes (đã có trong DDL §3.2)

| Index | Query phục vụ |
|---|---|
| `idx_users_email_lower` (functional, partial) | Login bằng email (case-insensitive, bỏ qua soft-deleted) |
| `idx_users_verified` (partial) | Filter verified user cho discover/matching |
| `idx_bookings_teacher_scheduled` | Calendar view cho teacher |
| `idx_bookings_learner_scheduled` | Calendar view cho learner |
| `idx_bookings_status_scheduled` | Filter pending/confirmed cho cron job timeout |
| `idx_ratings_ratee` | Tính rating_avg cho user profile |
| `idx_seed_tx_wallet_created` | Wallet transaction history (cursor) |
| `idx_seed_tx_expiring` (partial) | Cron job quét Seed sắp expire (chỉ `completed` chưa expire) |
| `idx_seed_tx_booking` (partial) | Lookup transaction theo booking |

**Pattern:** Index một chiều `(user_id, created_at DESC)` cho wallet history và `(teacher_id, scheduled_at)` cho booking calendar.

---

## 4. Ràng buộc quan trọng (CHECK constraints)

| Bảng | Constraint | Mục đích |
|---|---|---|
| `bookings` | `teacher_id != learner_id` (CHECK) | Không tự booking với mình |
| `bookings` | `duration_minutes IN (15,30,45,60)` (CHECK) | Slot chuẩn |
| `ratings` | `rater_id != ratee_id` (CHECK) | Không tự rate |
| `ratings` | `booking_id UNIQUE` | Mỗi booking tối đa 1 rating |
| `user_skills_offered` | `level BETWEEN 1 AND 5` (CHECK) | Skill level chuẩn |
| `user_skills_wanted` | `priority BETWEEN 1 AND 5` (CHECK) | Priority chuẩn |
| `user_availability` | `day_of_week BETWEEN 0 AND 6` (CHECK) | ISO day-of-week |
| `seed_transactions` | `balance_after >= 0` (CHECK — nên thêm nếu chưa) | Tránh âm balance do race condition |

---

## 5. Soft-delete policy (GDPR)

- Mọi bảng đều có `deleted_at TIMESTAMPTZ NULL`.
- Mọi query business phải filter `WHERE deleted_at IS NULL` (dùng partial index).
- **Reseed migration:** `deleted_at` của user KHÔNG cascade sang `user_skills_offered` (vì `ON DELETE CASCADE` chỉ chạy khi `DELETE FROM`, không chạy với soft delete).
- Khi xoá user → soft delete user + set `is_active=false` trên `user_skills_offered`.

---

## 6. Tables Phase 2+ (chưa dùng ở MVP)

Các bảng này **đã có schema** trong `phase-1-mvp/design.md` §3 nhưng Phase 1 **không dùng** — Phase 2+ mới trigger:

| Bảng | Phase | Mục đích |
|---|---|---|
| `session_recordings` | 2+ | Lưu video + transcript |
| `skill_passports` | 2+ | Skill Passport DB record (Phase 4 dùng SBT blockchain) |
| `learning_pods` | 3+ | Nhóm học tập nhiều người |
| `pod_members` | 3+ | Quan hệ user ↔ pod |
| `expert_profiles` | 3+ | Verified Expert marketplace |
| `premium_subscriptions` | 3+ | Premium tier |
| `creator_subscribers` | 3+ | Marketplace subscription |

---

## 7. Cross-reference

| File | Vai trò |
|---|---|
| `.kiro/specs/phase-1-mvp/design.md` §3.1 | ASCII ERD gốc |
| `.kiro/specs/phase-1-mvp/design.md` §3.2 | DDL đầy đủ |
| `.kiro/specs/phase-1-mvp/design.md` §3.3 | Indexing strategy |
| `SKILLSEED_API_AND_DB.md` §8 | Cross-phase DB design (Phase 4 blockchain, etc.) |
| `docs/STATE_MACHINES.md` | Booking / Wallet / Session state flow |
| `AGENTS.md` §3 | Source-of-truth map |
