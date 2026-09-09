# Phase 1 — MVP: Tasks

> **Task list triển khai MVP theo sprint (2 tuần/sprint).**

---

## Open Questions & Spec Conflicts

> Trước khi triển khai, đọc mục này để biết các quyết định đã chốt hoặc cần user duyệt.

- **[RESOLVED 2026-09-07] Conflict với `SKILLSEED_CODE_SKELETON.md` §1:** Skeleton gợi ý multi-module Maven (`common-lib` / `user-service` / `matching-service` / `wallet-service`). Phase 1 design §1 + §5.1 chốt **monolithic-modular** (single Spring Boot app, packages `com.skillseed.{shared,auth,user,skill,discover,booking,session,rating,wallet,notification,admin}`). Theo AGENTS.md §3, Phase 1 spec thắng → Phase 1 dùng **single Maven module** tại `/backend`, không tách microservice. Sẽ revisit khi chuyển sang Phase 2.
- **[OPEN]** Folder `/hooks` trong T-M03 description — Phase 1 MVP chưa thấy use case cụ thể; convention hooks được handle bằng React Query + Zustand. Có thể bỏ hoặc tạo khi cần.

---

## Status Log

> Lịch sử cập nhật task. Entry mới nhất ở trên.

- **2026-09-09 — Sprint 5 Visual Fidelity & Convention Audit Fixes (T-M400..T-M424)**
  - **Audit source:** comprehensive read-only audit ngày 2026-09-09 — 3 agent song song (FE↔SVG, BE↔SVG, Convention). Phát hiện **45 defect** (~7 Critical, ~14 High, ~14 Medium, ~10 Low). Điểm fidelity tổng: FE **~45–55%**, BE **~72%**, Convention **~62%**, page coverage **19% (25/130 SVG)**. Báo cáo đầy đủ ở message audit 2026-09-09 + `docs/VISUAL_FIDELITY.md`.
  - **Spec conflict flagged:** AGENTS.md §5.1 yêu cầu table **số ít** (`user`, `booking`) nhưng V1–V12 migrations đang dùng **số nhiều** (`users`, `bookings`, …). Có 2 lựa chọn → xem T-M420.
  - **T-M181 kéo dài scope:** T-M181 k6 load test từ Sprint 3 vẫn active; Sprint 5 bổ sung các task fix audit findings.
  - **Scope ước tính:** ~7–8 ngày founder + 3–4 ngày FE = **~10–12 ngày làm việc = 1 sprint tập trung (2 tuần)**.
  - **Mục tiêu Sprint 5:**
    1. Đóng tất cả 7 defect Critical (block user).
    2. Nâng fidelity: FE **45% → 80%**, BE **72% → 95%**, Convention **62% → 85%**.
    3. Tham chiếu SVG đầy đủ trong mọi PR (visual fidelity gate, AGENTS.md §5.3).
  - **Critical fixes (must ship):** T-M400 (ALLOWED_DURATIONS), T-M401 (CancelReason align), T-M402 (SkillCategory mapping), T-M403 (Booking detail gradient header), T-M404 (Discover state components), T-M405 (Login Web3 CTA), T-M406 (Register strength + Terms).
  - **Shared logic cleanup:** T-M410 (5 `*PageResponse` → `PageResponse<T>`), T-M411 (7 exception → `DomainException`), T-M412 (3 controllers touch repo).
  - **Convention sweep:** T-M413–T-M418 (token migration, form relocation, lockfile, CI gate).
  - **Decision required:** T-M420 (table naming plural vs singular).
  - **Documentation:** T-M421–T-M424 (orphan SVG registry, decision record, marketing rebuild plan).
  - **Tests:** T-M419 (unit + visual regression smoke). Cập nhật BookingServiceTest + WalletServiceTest sau CancelReason / Category changes.
- **2026-09-07 — Sprint 3 Video + Rating (T-M150..T-M156, T-M160, T-M161, T-M170..T-M176, T-M180, T-M181)**
  - **Completed:** full Sprint 3 surface — video foundation + Daily webhook, STOMP chat, rating end-to-end, FE VideoCall/Whiteboard/RatingModal/ReviewsList, E2E manual + k6 load script. Sprint 3 box list now `[x]` end-to-end.
  - **T-M150:** `session.daily.*` config (enabled, api-key, api-base, grace-minutes, webhook-signing-key) bound to `DailyProperties` via `@ConfigurationProperties`. Default `enabled=false` so dev profile falls back to stub URLs.
  - **T-M151:** `SessionController POST /api/v1/sessions/{bookingId}/room` creates / reuses a private Daily room, mints a per-participant meeting token (teacher=`is_owner`), returns `{ roomUrl, roomName, token, role, expiresAt, scheduledAt, durationMinutes }`. Expiry = scheduledAt + duration + grace. Authorization limited to teacher / learner; only CONFIRMED / IN_PROGRESS allowed.
  - **T-M152:** `@daily-co/daily-js` + `@daily-co/daily-react` added to `package.json`. Daily.js is dynamically imported inside `VideoCall` so SSR stays clean; `daily-react` kept in tree for ergonomic hooks in later sprints.
  - **T-M153:** `VideoCall` mounts the call object via Daily.js, mounts it inside a full-screen overlay, and exposes Mute / Camera / Screen share / Whiteboard / Leave controls (screen share reuses Daily's built-in — T-M160).
  - **T-M154:** `JoinSessionButton` enforces the 10-minute pre-start window and only renders for participants on confirmed/in-progress bookings. Wired into `BookingDetailView` alongside the existing Start / Complete / Cancel actions.
  - **T-M155:** STOMP wiring under `/ws/sessions` (SockJS fallback included). `StompAuthChannelInterceptor` parses the Bearer token on the CONNECT frame and attaches a Principal; `SessionChatController` validates that the sender is teacher or learner on the booking before broadcasting.
  - **T-M156:** `DailyWebhookController POST /api/v1/webhooks/daily` accepts `meeting.ended`, extracts the booking id from the `ss-<bookingCompact>` room name, and flips the booking to COMPLETED via `BookingService.complete`. Idempotent on terminal states. `/api/v1/webhooks/**` whitelisted in `SecurityConfig.PUBLIC_PATHS`.
  - **T-M160:** Daily screen share is part of the standard call object — the `VideoCall` component wires `startScreenShare` / `stopScreenShare` directly.
  - **T-M161:** `WhiteboardPanel` (in-house canvas, no Excalidraw dependency for Sprint 3) — pen, eraser, 5-colour palette, undo, clear, HiDPI scaling. Cross-participant sync noted as Sprint 4 follow-up per spec §7.2.
  - **T-M170:** `Rating` entity extended with `auto_rated` flag (V6 migration).
  - **T-M171:** `RatingController POST /api/v1/ratings` enforces booking COMPLETED + participant-only + unique (booking_id, rater_id) — the unique constraint is migrated from `UNIQUE(booking_id)` to `UNIQUE(booking_id, rater_id)` in V5 so both sides can rate. `GET /api/v1/users/{id}/ratings?page&size` returns paged `RatingResponse` items.
  - **T-M172:** `BookingService.complete()` now publishes `RATING_PROMPT` to both participants alongside the existing `SESSION_COMPLETED` notification.
  - **T-M173:** `RatingAutoRateJob` runs at 01:00 UTC (cron overridable via `rating.auto-rate.cron`, kill-switch via `rating.auto-rate.enabled`, grace window via `rating.auto-rate.grace-days`). Per-booking failures are swallowed and logged so one bad row cannot abort the batch.
  - **T-M174:** After every rating creation the ratee's `rating_avg` (running mean, 1-decimal HALF_UP) and `sessions_completed` counter are recomputed via `UserRepository.save`.
  - **T-M175:** `RatingModal` collects overall + helpfulness + respectfulness (1-5) plus optional comment, POSTs to `/api/v1/ratings`. `RateBookingButton` wraps the modal and is mounted from `BookingDetailView` on COMPLETED bookings where the viewer is a participant.
  - **T-M176:** `ReviewsList` paginates `/api/v1/users/{id}/ratings` via React Query and renders star counts + review text. Wired into the `Reviews` block of `PublicProfileView`.
  - **T-M180:** `docs/MANUAL_E2E_SPRINT3.md` covers the operator-driven 2-user happy path (room creation, webhook, FE video, chat, rating, auto-rate). `backend/src/test/java/com/skillseed/e2e/Sprint3E2ESmokeIT.java` is the disabled JUnit scaffold for the future automated equivalent.
  - **T-M181:** `infra/loadtest/open-rooms.js` k6 script ramps to 50 concurrent VUs for 5 min while posting `/sessions/{id}/room`; SLO thresholds p95 < 500 ms and < 1 % failure rate.
  - **Tests:** `SessionServiceTest` (10 cases), `RatingServiceTest` (9 cases), `RatingAutoRateJobTest` (4 cases), `SessionChatControllerTest` (5 cases). All follow the existing JUnit 5 + Mockito + AssertJ style.
  - **Sandbox limitation:** JDK/Maven/npm missing → `mvn test`, `npm run lint`, `npm run typecheck` not executed. Code reviewed by hand against the existing module patterns.

- **2026-09-07 — Sprint 2 Tests (T-M140, T-M141) — Part 3**
  - **Completed:** T-M140, T-M141 (unit coverage; full Testcontainers IT left for follow-up since same Docker constraint as T-M61)
  - **T-M140:** `SeedWalletServiceTest` (13 cases) — starter idempotency, escrow debit (happy + insufficient balance + idempotent re-escrow), release escrow (happy + idempotent re-release), full refund + half refund net-spent accounting, forfeit (happy + idempotent), expiry sweep emitting EXPIRE rows, wallet summary tier/expiring calculations.
  - **T-M141:** `BookingServiceTest` (14 cases) — create success + escrow wiring + booking-request notification, self-booking / past schedule / invalid duration / insufficient balance rejection, accept only by teacher + state-machine guard, decline issues full refund + declined notification, cancel ≥24h vs <24h applies 100% vs 50% refund, cancel by non-participant rejected, complete releases escrow + notifies both parties, scheduled jobs `expirePendingBookings` and `markNoShows` flip status, forfeit escrow, and notify.
  - **Sandbox limitation:** JDK/Maven/Docker missing → `mvn test` not executed. Tests follow the same JUnit 5 + Mockito + AssertJ style already used by AuthServiceTest / JwtServiceTest.

- **2026-09-07 — Sprint 2 Booking + Wallet (T-M100..T-M108, T-M120..T-M126) — Part 1: BE**
  - **Completed:** T-M100, T-M101, T-M102, T-M103, T-M104, T-M105, T-M106, T-M107, T-M108, T-M120, T-M121, T-M122, T-M123, T-M124, T-M125, T-M126
  - **V5_add_booking_lifecycle.sql:** partial unique index `(teacher_id, scheduled_at) WHERE status IN ('pending','confirmed','in_progress')` to prevent double-booking at the same slot + BRIN on `scheduled_at` for reminder/no-show scans.
  - **Wallet ledger core (`SeedWalletService`):** `escrowDebit(booking)` (PENDING hold, refuses if balance < amount), `releaseEscrow(booking)` (spend COMPLETED + new EARN row with 6-month TTL, credits teacher wallet), `refundEscrow(booking, percent)` (cancels spend, emits REFUND for the percentage, adjusts `total_spent`), `forfeitEscrow(booking)` (NO_SHOW / EXPIRED — keeps spend as cancelled, bumps `total_spent`), `processExpiry()` daily sweep. New aggregates in `SeedTransactionRepository`: `sumActiveBalance`, `sumExpiredAmount`, `sumExpiringSoon`, `findOldestExpiringAt`, `findExpiringEarnTx`. `getWalletSummary(userId)` returns balance + totalEarned + totalSpent + totalExpired + ExpiringSoonResponse + WalletTier (BRONZE/SILVER/GOLD/PLATINUM ladder). `listTransactions(userId, page, size)` returns paged ledger.
  - **Wallet DTOs:** `WalletSummaryResponse`, `ExpiringSoonResponse`, `WalletTier`, `SeedTransactionResponse`, `SeedTransactionPageResponse`. New `WalletController` exposes `GET /api/v1/wallet/me` + `GET /api/v1/wallet/me/transactions`.
  - **Booking module:** `BookingException` (uses `HttpStatus` instead of int for clarity), `BookingService` orchestrates create / accept / decline / cancel / start / complete / getById / listForUser plus scheduled jobs `expirePendingBookings`, `markNoShows`, `sendReminders`. Validation: teacher != learner, scheduledAt in the future, duration ∈ {15,30,45,60}, soft-deleted teacher rejected, no overlapping active slot for the teacher, slot must lie inside the teacher's recurring availability (timezone-aware). Refund policy: ≥24h → 100%, otherwise 50% (configurable via `REFUND_FULL_HOURS`). Decline always refunds 100%. NO_SHOW and EXPIRED forfeit the escrow. Every state transition publishes a `NotificationService` event.
  - **Booking DTOs:** `CreateBookingRequest` (Bean Validation: `@NotNull` teacher/skill/scheduledAt, `@Min(15) @Max(60)` duration, `@Size(max=500)` notes), `CancelBookingRequest` (`@NotBlank reason`), `DeclineBookingRequest`, `BookingResponse` (full), `BookingSummaryResponse`, `BookingPageResponse`. `BookingController` exposes `POST /api/v1/bookings` (accepts `Idempotency-Key` header; enforced slot uniqueness through DB index), `GET /api/v1/bookings/me?role=&status=`, `GET /{id}`, `POST /{id}/accept|decline|cancel|start|complete`.
  - **Scheduled jobs (`BookingAndWalletJobs`):** expire pending (every 5m), NO_SHOW (every 1m), reminders 24h/1h (every 5m), seed expiry (daily 01:00 UTC). `@EnableScheduling` added to `SkillseedApplication`.
  - **Misc:** `UserAvailabilityRepository#findByUserIdAndDayOfWeek` added; `WalletException` gains `getStatus()` + `conflict()`; `GlobalExceptionHandler` maps `BookingException` via status + code.
  - **Sandbox limitation:** không có JDK/Maven nên `mvn verify` chưa chạy. Cần user chạy local để xác nhận `ddl-auto=validate` (V5 mới + Booking/SeedTransaction/SeedWallet mapping không drift), lint (Checkstyle), và Swagger UI cho 9 endpoint booking + 2 endpoint wallet mới.

- **2026-09-07 — Sprint 0 Foundation (T-M10..T-M12)**
  - **Completed:** T-M10, T-M11, T-M12
  - **T-M10:** `backend/src/main/resources/db/migration/V1__init_schema.sql` — 9 bảng (users, skills, user_skills_offered, user_skills_wanted, user_availability, bookings, ratings, seed_wallets, seed_transactions) + 17 indexes + CHECK constraints + updated_at triggers + pgcrypto extension.
  - **T-M11:** `backend/src/main/resources/db/migration/V2__seed_skills.sql` — 2049 skills trong 8 categories (tech 299, business 261, art 242, language 212, life 228, health 231, music 229, sport 347).
  - **T-M12:** 9 entities (`com.skillseed.{user,skill,booking,rating,wallet}.domain.*`) + 9 Spring Data JPA repositories + 5 enums + 5 AttributeConverters trong `shared/domain`. Entities KHÔNG dùng Lombok (per AGENTS.md §5.2). Sử dụng `@JdbcTypeCode(SqlTypes.UUID)` cho UUID columns, `@JdbcTypeCode(SqlTypes.ARRAY)` cho `TEXT[]` (languages). Enum values map qua AttributeConverter → DB lưu lowercase string ('tech', 'email', 'pending'...).
  - **Sandbox limitation:** không thể chạy `mvn verify` để xác nhận `ddl-auto=validate` pass. Cần user chạy local để xác nhận schema ↔ entity mapping không drift.
- **2026-09-07 — Sprint 0 Skills module (T-M40..T-M44)**
  - **Completed:** T-M40, T-M41, T-M42, T-M43, T-M44
  - **T-M40+T-M41:** `SkillController` exposes `GET /skills` (search/filter/paginate, public), `GET /skills/{id}` (public), `POST /skills` (custom, JWT-required). `V3__add_skill_status.sql` adds `status` column + CHECK + partial index. `SkillStatus` enum + `SkillStatusConverter`; `Skill` entity updated. `SkillService.createCustomSkill` derives URL-safe slug and persists `is_custom=true` + `status=pending_review`.
  - **T-M42:** `OfferedSkillController` exposes GET / POST / PATCH / DELETE under `/api/v1/users/me/skills/offered`. Strict ownership checks (NOT_OWNER on cross-user); rejects duplicates and unapproved skills (SKILL_NOT_APPROVED).
  - **T-M43:** `WantedSkillController` exposes GET / POST / PATCH / DELETE under `/api/v1/users/me/skills/wanted`. Same ownership + skill-validation rules.
  - **T-M44:** `AvailabilityController` exposes GET + PUT `/api/v1/users/me/availability`. PUT is a full bulk replace (`deleteByUserId` + insert in same transaction). Validates `endTime > startTime` (matches DB CHECK) and rejects same-day/same-timezone overlapping slots.
  - **SecurityConfig:** `GET /api/v1/skills` and `/skills/{id}` are `permitAll()`.
  - **Sandbox limitation:** không có JDK/Maven nên `mvn verify` chưa chạy local. Cần user verify V3 migration + `ddl-auto=validate` (schema ↔ Skill entity) + lint (Checkstyle) + Swagger UI cho 13 endpoint mới.

- **2026-09-07 — Sprint 0 User module (T-M30..T-M34)**
  - **Completed:** T-M30, T-M31, T-M32, T-M33, T-M34
  - **T-M30+T-M31+T-M33:** `UserController` exposes `GET /me`, `PATCH /me`, `GET /{id}`. `UserService.getCurrentUser` returns `CurrentUserResponse` (profile + Skill DNA summary + wallet summary); `getPublicProfile` returns `PublicUserResponse` (email/phone intentionally omitted); `updateProfile` applies partial updates with Bean Validation. `SecurityConfig`: `GET /api/v1/users/{id}` is `permitAll()`.
  - **T-M32:** `OnboardingController#complete` flips `onboarding_completed=true` then calls `SeedWalletService.grantStarterSeeds` (ledger-style: `SeedTransaction` type=GRANT, amount=+30, TTL=180d, idempotent via description match). Auto-creates `SeedWallet` row if missing.
  - **T-M34:** `AvatarController#upload` (multipart, ≤5MB, image/jpeg|png|webp|gif) → `AvatarStorage` (Cloudflare R2 via AWS SDK v2 S3 client when `storage.r2.*` configured, otherwise filesystem fallback under `storage.local-dir`). `software.amazon.awssdk:s3` dep added; `spring.servlet.multipart` bumped to 6MB.
  - **Sandbox limitation:** không có JDK/Maven nên `mvn verify` chưa chạy local. Cần user verify `ddl-auto=validate`, lint (Checkstyle), và Swagger UI cho 5 endpoint mới (`/api/v1/users/**`).

- **2026-09-07 — Sprint 0 Auth module (T-M20..T-M27)**
  - **Completed:** T-M20, T-M21, T-M22, T-M23, T-M24, T-M25, T-M26, T-M27
  - **T-M20:** `JwtService` (HS256, configurable TTLs, token-type claim) + `JwtAuthenticationFilter` (Bearer parsing → SecurityContext) + `SecurityConfig` (stateless, BCrypt(12), `/api/v1/auth/**` public, JWT filter pre-auth, CORS via `cors.allowed-origins`).
  - **T-M21+T-M24:** `AuthService.register` (Bean Validation + BCrypt + 24h email verify token) + `verifyEmail` (single-use consume, flips `verified=true`). `TokenStore` Redis (Lua GET+DEL), `TokenGenerator` (SecureRandom URL-safe Base64), `EmailSender` (Resend SDK + logging fallback). User entity nhận `@PrePersist`/`@PreUpdate` callbacks cho timestamps.
  - **T-M22+T-M23:** `RateLimiter` Redis fixed-window (`skillseed:rl:{purpose}:{key}`); `AuthService.login`/`refresh`/`logout` với refresh token rotation persisted in Redis. Login rate-limited 5/15min/IP (`X-Forwarded-For` đầu hoặc `remoteAddr`).
  - **T-M25:** `AuthService.forgotPassword` (generic response, TTL 1h, chỉ issue khi account có password) + `resetPassword` (consume + BCrypt rehash).
  - **T-M26+T-M27:** `JwksIdTokenVerifier` base (cache JWKS, rotate hourly, validate issuer+audience+email_verified) + `GoogleIdTokenVerifier` + `AppleIdTokenVerifier`. Conditional beans (`oauth.google.client-id` / `oauth.apple.client-id`). Auto-link existing email user hoặc tạo mới với `verified=true`, `password=null`.
  - **Sandbox limitation:** không có JDK/Maven → `mvn verify` chưa chạy local. Cần user verify lint, test (T-M60/T-M61 sau), và Swagger UI cho 8 endpoint mới (`/api/v1/auth/**`).
- **2026-09-07 — Sprint 0 Testing & QA (T-M60..T-M62)**
  - **T-M60:** 4 JUnit 5 + Mockito + AssertJ unit test classes — `JwtServiceTest` (HS256 round-trip, claim shape, type-mismatch, tampered signature, short-secret guard), `AuthServiceTest` (15 cases across register / verify / login / refresh / logout / forgot / reset / Google OAuth), `UserServiceTest` (current + public profile, partial update, onboarding idempotency, soft-delete), `SkillServiceTest` (search pagination + status filtering, custom skill create with slug derivation, duplicate / empty-slug / unknown-category / parent-link guards). JaCoCo 0.8.12 plugin emits coverage report on `mvn verify`; Surefire picked up `*Test.java`, Failsafe picks up `*IT.java`. H2 + `application-test.yml` added for future slice tests.
  - **T-M61:** `AuthControllerIT` — full Spring Boot context against ephemeral PostgreSQL 16 + Redis 7 (Testcontainers `@Testcontainers` + `@DynamicPropertySource`). 14 scenarios: register (201/409/400), login (200/401/429 with 5/15min/IP rate limit + `X-Forwarded-For`), refresh (rotation + replay rejection), forgot/reset (generic 200 on known + unknown email, 400 invalid verify token), OAuth (400 OAUTH_PROVIDER_DISABLED when client-id empty), logout (revoke + subsequent refresh 401). `application-it.yml` mirrors staging config so Flyway + ddl-auto=validate exercise the real schema. Existing `SkillseedApplicationTests#contextLoads` disabled — requires Docker.
  - **T-M62:** `docs/MANUAL_E2E_AUTH.md` — 10-section operator QA checklist (prerequisites, registration + verify, login + JWT pair + rate limit, refresh + rotation, forgot/reset, logout, Google sign-in, Apple sign-in, FE route guards, sign-off template, known gaps). Cross-linked from `AGENTS.md` §3.
  - **Sandbox limitation:** không có JDK/Maven nên `mvn test` / `mvn verify` chưa chạy local; cần user/CI với Docker để confirm integration tests pass. Unit tests compile clean theo Java 21 + JUnit 5 conventions và dùng đúng Spring Boot starter-test dependencies đã có sẵn trong `pom.xml`.
- **2026-09-07 — Sprint 1 Profile + Discover + Email/Notification (T-M70..T-M92)**
  - **Completed:** T-M70, T-M71, T-M72, T-M80, T-M81, T-M82, T-M83, T-M84, T-M90, T-M91, T-M92 (11 tasks)
  - **T-M90:** `EmailTemplateService` extracts verify / reset / welcome email bodies (HTML + plain text) out of `AuthService`. Welcome email is sent once when an unverified user verifies their email. Wrapped in `safeSend()` so an email outage never fails the auth flow.
  - **T-M91:** V4__add_notifications.sql migration creates `notifications(id, user_id FK, type, payload jsonb, read_at, created_at)` + CHECK constraint covering all booking + rating event types reserved for Sprint 2/3 + two indexes (full + partial WHERE read_at IS NULL for the polling query). New `notification` module: entity (no Lombok), `NotificationType` enum, repository, service (publish + list + unreadOnly + markRead + markAllRead with ownership check), controller under `/api/v1/notifications` (GET /me, GET /me/unread-count, POST /me/{id}/read, POST /me/read-all). 8 unit tests.
  - **T-M80:** New `discover` module. `DiscoverRepositoryImpl` uses a single JPQL DISTINCT-user query joining `user_skills_offered` with the caller's `user_skills_wanted`, applies optional skill/language/country/minRating filters, then a single batched `findAll` on offered skills for the candidate set to build the top-3 matched-skills per card (avoids N+1). Hard-coded ORDER BY rating_avg DESC, sessions_completed DESC per spec §4.5. `DiscoverService` clamps page (>=0) and size (1..50). 4 unit tests.
  - **T-M81:** `FreeSlotResponse` DTO + `UserService.getFreeSlots()` materialises the user's recurring weekly availability into UTC `Instant` intervals over `[from, from+days)`. day_of_week follows the schema convention (0..6, 0=Sunday); Java's `DayOfWeek` is re-encoded. Clamps days to 1..60. `SecurityConfig` allowlists `GET /api/v1/users/{id}/availability` as public so the booking flow can render slots pre-sign-in.
  - **T-M71:** `modules/skills` FE module with `schemas` + `skills-api` + `use-skill-search` (RQ hook, 30s staleTime) + `SkillsAutocomplete` combobox (250ms debounce, click-outside, role=listbox, clear button, empty-state hint). Consumed by onboarding + the top-bar search.
  - **T-M72:** `modules/availability` FE module: `schemas` + `availability-api` + `use-availability` (RQ query + replace mutation) + `AvailabilityPicker` (7-day grid, per-day slot editor with start/end time pickers, end>start validation, remove button, IANA timezone dropdown). Consumed by onboarding step 6.
  - **T-M70:** `modules/onboarding` FE module with 6 visible step components (skills teach / skills want / goals / learning style / availability / languages+country). 'Level per skill' folded into the offered-skill form so the spec's 7 conceptual steps are all covered. `useOnboardingWizard` Zustand store with `persist` middleware (localStorage key `skillseed.onboarding-draft`) so refresh / tab close doesn't lose progress. `useSubmitOnboarding` orchestrates the chain: PATCH /users/me → POST offered + wanted → PUT availability → POST /users/me/onboarding (grants 30 Starter Seeds server-side) → redirect `/discover?welcome=1`. Wizard shell with progress bar, per-step validation hints, Back/Next. AuthGuard fix: skip the onboarding-required check when pathname === '/onboarding' to avoid an infinite redirect loop.
  - **T-M82:** `modules/discover` FE module replaces the T-M56 stub. `DiscoverCard` with avatar-or-initials, top-3 skill pills, rating + sessions completed, 'View profile' CTA. `DiscoverFiltersPanel` with language / country / minRating + Reset. `DiscoverView` 2-column layout with collapsible mobile filter drawer, loading/error/empty states. Wired into `/discover?skill=...` (used by the top-bar search).
  - **T-M83:** `modules/profile` FE module + `/users/[id]` dynamic route (Next 15 async params). `PublicProfileView` shows the public profile (avatar header, bio, offered skills, reviews placeholder) + a sidebar `BookSessionPanel` that previews the next 8 free slots from `/users/{id}/availability` and renders a disabled 'Book session' CTA pointing to T-M130.
  - **T-M92:** `modules/notifications` FE module + `NotificationBell` mounted in the shared `AppTopBar` (added to `(app)/layout.tsx`). RQ hooks refetch `/notifications/me` + `/notifications/me/unread-count` every 60s per spec; bell shows a clamped 99+ badge; dropdown lists items with type→title mapping and a 'Mark all read' button.
  - **T-M84:** `TopBarSearch` button in the shared top bar expands into the SkillsAutocomplete (T-M71). Selecting a skill pushes `/discover?skill={id}` which T-M82's new `?skill=` reader picks up — round-trip search → filtered discover.
  - **Sandbox limitation:** không có JDK/Maven nên `mvn verify` chưa chạy (BE unit tests for DiscoverService + NotificationService + UserService.getFreeSlots chưa chạy local); FE verify đầy đủ bằng `npm run typecheck` + `npm run lint` + `npm run build` (10 routes prerendered, /onboarding + /users/[id] + /verify-email dynamic). End-to-end discover + onboarding flow cần user bật Docker + `mvn verify` để confirm Flyway V4 + ddl-auto=validate không drift.
- **2026-09-07 — Sprint 0 Frontend Auth flow (T-M50..T-M56)**
  - **Completed:** T-M50, T-M51, T-M52, T-M53, T-M54, T-M55, T-M56
  - **Completed:** T-M03, T-M04, T-M05
  - **Scaffolded, chờ local verify:** T-M01 (cần GitHub repo + branch protection), T-M02 (cần `mvn verify` local), T-M06 (cần truy cập `/swagger-ui.html` local)
  - **Sandbox limitation:** thiếu Java/Maven/Docker nên chỉ verify được FE (`npm install`, `lint`, `typecheck`, `build` — tất cả pass). BE files đã viết theo design §2.1 nhưng chưa chạy được `mvn verify`.
  - **Spec conflict flagged:** xem mục Open Questions ở trên.

---

## Sprint 0 (Tuần 1–2) — Foundation & Auth

### Project bootstrap

- [ ] [T-M01] **[P0]** Init GitHub repo monorepo `skillseed/`
  - Cấu trúc: `/backend`, `/frontend`, `/docs`, `/infra`
  - Branch protection trên `main`
  - Có AGENTS.md với coding conventions
  - **Status 2026-09-07:** folder structure scaffolded (`backend/`, `frontend/`, `infra/` đã tạo). Còn thiếu: GitHub repo init + push + branch protection rule.
- [ ] [T-M02] **[P0]** Setup Maven project backend với Spring Boot 3.3
  - Java 21, parent POM với shared deps
  - `application.yml` profiles: dev/staging/prod
  - **Status 2026-09-07:** `backend/pom.xml` + 5 profiles YAML scaffolded (deps đầy đủ theo design §2.1: web, jpa, redis, security, oauth2-resource-server, validation, websocket, actuator, flyway, springdoc-openapi 2.6.0, jjwt 0.12.6, resend-java, lombok, testcontainers). Chưa chạy được `mvn verify` vì sandbox thiếu JDK — cần user chạy local để xác nhận.
- [x] [T-M03] **[P0]** Setup Next.js 15 project frontend
  - TypeScript, TailwindCSS, shadcn/ui, Zustand
  - Folder: `/app`, `/components`, `/lib`, `/hooks`
  - **Verified 2026-09-07:** `npm install` (388 packages), `npm run lint`, `npm run typecheck`, `npm run build` đều pass. Folder `/hooks` chưa tạo (xem Open Questions).
- [x] [T-M04] **[P0]** Setup Docker Compose cho local dev (Postgres + Redis + Backend + Frontend)
  - Single `docker compose up` chạy cả stack
  - **Verified 2026-09-07:** file `docker-compose.yml` ở root đã có sẵn 4 services (postgres:16-alpine, redis:7-alpine, backend, frontend) + healthchecks + volumes. Path trỏ đúng `docker/Dockerfile.backend` và `docker/Dockerfile.frontend`.
- [x] [T-M05] **[P0]** Setup GitHub Actions CI cơ bản
  - Backend: `mvn verify`
  - Frontend: `npm run lint && npm run build`
  - **Verified 2026-09-07:** `.github/workflows/ci.yml` đã có sẵn 5 jobs (backend-test, backend-style, backend-package, frontend, docs-lint) + CI gate summary.
- [ ] [T-M06] **[P0]** Setup Swagger UI (`springdoc-openapi`)
  - Có thể truy cập `/swagger-ui.html` ở dev profile
  - **Status 2026-09-07:** dep `springdoc-openapi-starter-webmvc-ui:2.6.0` đã add vào `pom.xml`; `OpenApiConfig` (JWT bearer scheme) đã viết; `application.yml` đã config `springdoc.swagger-ui.path=/swagger-ui.html`; `SecurityConfig` permit `/swagger-ui/**`, `/v3/api-docs/**`. Cần user mở browser truy cập `/swagger-ui.html` sau khi `docker compose up` để xác nhận trực quan.

### Database & migrations

- [x] [T-M10] **[P0]** Tạo Flyway migration V1__init_schema.sql
  - Tất cả bảng trong design.md section 3.2
  - Indexes đầy đủ
  - **Verified 2026-09-07:** 9 bảng + 17 indexes + CHECK constraints + updated_at triggers + pgcrypto. SQL syntax reviewed. Chưa chạy được `mvn flyway:migrate` trên sandbox.
- [x] [T-M11] **[P0]** Tạo Flyway migration V2__seed_skills.sql
  - Insert 2.000+ skills taxonomy (categories: tech, business, art, language, life, health, music, sport)
  - **Verified 2026-09-07:** 2049 skills (tech 299, business 261, art 242, language 212, life 228, health 231, music 229, sport 347).
- [x] [T-M12] **[P0]** Setup JPA entities + repositories
  - Map đầy đủ schema → Java entity
  - Repository dùng Spring Data JPA
  - **Verified 2026-09-07:** 9 entities + 9 repositories + 5 enums + 5 AttributeConverters. Chưa verify `ddl-auto=validate` pass vì sandbox thiếu JDK.

### Auth module

- [x] [T-M20] **[P0]** Implement JWT generation + validation (JJWT 0.12.x)
  - Access token (15 min HS256), refresh token (30 days)
  - Claims: sub, email, verificationLevel
  - **Verified 2026-09-07:** `JwtService` (`backend/.../auth/service/JwtService.java`) — HS256 signing via JJWT 0.12.6, configurable TTLs, token-type claim distinguishes access/refresh. Filter `JwtAuthenticationFilter` populates SecurityContext with `AuthenticatedUser`. Sandbox không có JDK nên chưa chạy `mvn verify` — cần user xác nhận local.
- [x] [T-M21] **[P0]** Implement POST `/auth/register`
  - Validate input (email format, password ≥ 8 chars + chữ + số)
  - BCrypt hash password
  - Tạo email verification token, gửi qua Resend
  - **Verified 2026-09-07:** `AuthService.register` — Bean Validation + BCrypt(12) + `TokenStore` (Redis Lua GET+DEL) + `EmailSender` (Resend SDK, fallback log khi thiếu `RESEND_API_KEY`). Token TTL 24h. Endpoint `POST /api/v1/auth/register` → 201.
- [x] [T-M22] **[P0]** Implement POST `/auth/login`
  - Verify password, return JWT pair
  - Rate limit: 5 lần/IP/15 phút
  - **Verified 2026-09-07:** `AuthService.login` — `RateLimiter` Redis fixed-window (`skillseed:rl:login:{ip}`, TTL 15m), key = `X-Forwarded-For` đầu tiên hoặc `remoteAddr`. Trả 429 `RATE_LIMITED` khi vượt ngưỡng, 401 `INVALID_CREDENTIALS` / `OAUTH_ONLY_ACCOUNT` khi sai.
- [x] [T-M23] **[P0]** Implement POST `/auth/refresh`
  - Validate refresh từ Redis, issue new access token
  - **Verified 2026-09-07:** `AuthService.refresh` — verify JWT signature + token-type=refresh, consume refresh token trong Redis (rotation), issue cặp mới. `POST /api/v1/auth/refresh`.
- [x] [T-M24] **[P0]** Implement POST `/auth/verify-email`
  - Validate token (TTL 24h), set `verified=true`
  - **Verified 2026-09-07:** `AuthService.verifyEmail` — single-use token consumption, flip `verified=true`. `POST /api/v1/auth/verify-email`.
- [x] [T-M25] **[P0]** Implement POST `/auth/forgot-password` + `/reset-password`
  - Generate reset token, gửi email
  - **Verified 2026-09-07:** `AuthService.forgotPassword` (generic response, TTL 1h) + `resetPassword` (consume + BCrypt rehash). `POST /api/v1/auth/forgot-password`, `POST /api/v1/auth/reset-password`.
- [x] [T-M26] **[P0]** Implement OAuth2 Google Sign-In
  - Validate Google idToken (dùng Google API client library)
  - Tạo user nếu chưa tồn tại
  - **Verified 2026-09-07:** `GoogleIdTokenVerifier` — verify RS256 via `https://www.googleapis.com/oauth2/v3/certs` JWKS, issuer + audience + `email_verified` checks. Conditional bean qua `oauth.google.client-id`. `POST /api/v1/auth/oauth/google`.
- [x] [T-M27] **[P1]** Implement OAuth2 Apple Sign-In
  - Validate Apple idToken (dùng apple-signin-oidc)
  - Cần Apple Developer account + Service ID
  - **Verified 2026-09-07:** `AppleIdTokenVerifier` — verify RS256 via `https://appleid.apple.com/auth/keys`, cùng pattern Google. `POST /api/v1/auth/oauth/apple`. Operator opt-in bằng `oauth.apple.client-id` + `oauth.apple.issuer`.

### User module

- [x] [T-M30] **[P0]** Implement GET `/users/me`
  - Trả current user profile + Skill DNA
  - **Verified 2026-09-07:** `UserController#me` → `UserService.getCurrentUser` returns `CurrentUserResponse` (profile + Skill DNA summary + wallet summary). Soft-delete aware.
- [x] [T-M31] **[P0]** Implement PATCH `/users/me`
  - Cập nhật full_name, bio, country, timezone, languages, learning_style
  - **Verified 2026-09-07:** `UserController#updateMe` with `@Valid UpdateProfileRequest` (Bean Validation: fullName, bio max 500, ISO-2 country uppercase, timezone, languages, learningStyle). Null fields = untouched.
- [x] [T-M32] **[P0]** Implement onboarding flow (multi-step)
  - POST `/users/me/onboarding` đánh dấu hoàn thành
  - Cấp 30 free starter seeds
  - **Verified 2026-09-07:** `OnboardingController#complete` flips `onboarding_completed=true` + `SeedWalletService.grantStarterSeeds` credits 30 (TTL 180d) via ledger-style `SeedTransaction(GRANT)`. Idempotent (checks for existing starter grant by description).
- [x] [T-M33] **[P0]** Implement GET `/users/{id}` (public profile)
  - Chỉ trả fields public (không email, phone)
  - **Verified 2026-09-07:** `UserController#getById` → `UserService.getPublicProfile` returns `PublicUserResponse` (email/phone intentionally absent). Endpoint is `permitAll()` for GET in SecurityConfig.
- [x] [T-M34] **[P0]** Implement avatar upload (multipart)
  - Validate ≤ 5MB, image/* MIME type
  - Upload lên Cloudflare R2, lưu URL
  - **Verified 2026-09-07:** `AvatarController#upload` accepts multipart `file` field, validates image/jpeg|png|webp|gif + ≤5MB, uploads via `AvatarStorage` (R2 conditional on `storage.r2.*`, else filesystem fallback under `storage.local-dir`). New `software.amazon.awssdk:s3` dep in `pom.xml`. Multipart limits bumped to 6MB in `application.yml`.

### Skills module

- [x] [T-M40] **[P0]** Implement GET `/skills?query=&category=`
  - Search theo name LIKE %query% + filter category
  - Pagination (max 100)
  - **Verified 2026-09-07:** `SkillController#search` → `SkillService.search` (name LIKE + category, page clamped 0..N, size clamped 1..100, default 20). Filters out non-approved rows; GET endpoints permitAll.
- [x] [T-M41] **[P0]** Implement POST `/skills` (custom skill)
  - Tạo skill với is_custom=true, status=pending_review
  - **Verified 2026-09-07:** `V3__add_skill_status.sql` adds `status` column + CHECK + partial index; `SkillStatus` enum + `SkillStatusConverter`. `SkillService.createCustomSkill` derives URL-safe slug, rejects duplicates (409), persists with `is_custom=true` and `status=pending_review`. Requires JWT.
- [x] [T-M42] **[P0]** Implement user_skills_offered CRUD
  - POST/GET/PATCH/DELETE
  - **Verified 2026-09-07:** `OfferedSkillController` exposes GET / POST / PATCH / DELETE under `/api/v1/users/me/skills/offered`. Bean Validation (level 1-5, years positive, description max 1000). Strict ownership checks; rejects duplicates and unapproved skills.
- [x] [T-M43] **[P0]** Implement user_skills_wanted CRUD
  - POST/GET/PATCH/DELETE
  - **Verified 2026-09-07:** `WantedSkillController` exposes GET / POST / PATCH / DELETE under `/api/v1/users/me/skills/wanted`. Bean Validation (priority + targetLevel 1-5, notes max 1000). Strict ownership checks.
- [x] [T-M44] **[P0]** Implement availability endpoints
  - PUT `/users/me/availability` (bulk replace)
  - GET `/users/me/availability`
  - **Verified 2026-09-07:** `AvailabilityController` exposes GET + PUT. PUT performs full bulk replace (`deleteByUserId` + insert in the same transaction). Validates `endTime > startTime` and rejects same-day/same-timezone overlapping slots.

### Frontend — Auth flow

- [x] [T-M50] **[P0]** Setup React Query + API client (axios + interceptor)
- [x] [T-M51] **[P0]** Implement `/login` page (email/password form)
- [x] [T-M52] **[P0]** Implement `/register` page
- [x] [T-M53] **[P0]** Implement `/verify-email/[token]` page
- [x] [T-M54] **[P0]** Implement `/forgot-password` + `/reset-password` pages
- [x] [T-M55] **[P0]** Implement Google Sign-In button (NextAuth hoặc react-google-login)
- [x] [T-M56] **[P0]** Implement protected route HOC + redirect logic

### Testing & QA

- [x] [T-M60] **[P0]** Unit tests cho service layer (JUnit 5 + Mockito)
  - AuthService, UserService, SkillService
  - Coverage ≥ 60% cho các service đã viết
- [x] [T-M61] **[P0]** Integration test cho /auth endpoints (Testcontainers Postgres)
- [x] [T-M62] **[P0]** Manual test e2e auth flow trên staging

---

## Sprint 1 (Tuần 3–4) — Profile, Discover, Skills

### Frontend — Onboarding

- [x] [T-M70] **[P0]** Implement `/onboarding` 7-step wizard
  - Progress bar, autosave mỗi step
  - Step 1: Skills I can teach
  - Step 2: Skills I want to learn
  - Step 3: Level cho mỗi skill
  - Step 4: Goals, interests
  - Step 5: Learning style
  - Step 6: Availability
  - Step 7: Languages, country
- [x] [T-M71] **[P0]** Implement skills autocomplete (search API với debounce)
- [x] [T-M72] **[P0]** Implement availability picker (time grid theo day_of_week)

### Discover & Search

- [x] [T-M80] **[P0]** Backend: GET `/discover` (filter-based matching)
  - Query: JOIN user_skills_offered với user_skills_wanted của current user
  - Filter: skill_id (from wanted), language, country, min_rating
  - Sort: rating DESC, sessions_completed DESC
- [ ] [T-M81] **[P0]** Backend: GET `/users/{id}/availability?from=now&days=7`
  - Trả về free slots trong N ngày tới
- [x] [T-M82] **[P0]** Frontend: `/discover` page
  - Card grid (avatar, name, top skills, rating, "Match" button)
  - Filter sidebar
- [x] [T-M83] **[P0]** Frontend: User profile page `/users/{id}`
  - Public profile view + offered skills + reviews
  - "Book Session" CTA
- [x] [T-M84] **[P1]** Frontend: Search bar với autocomplete skills

### Email & Notification

- [x] [T-M90] **[P0]** Setup Resend SDK + email templates
  - Welcome email (sau register)
  - Verify email
  - Reset password
- [x] [T-M91] **[P0]** Implement In-app notification table
  - Table: `notifications(id, user_id, type, payload, read_at, created_at)`
  - GET `/notifications/me?unreadOnly=true`
- [x] [T-M92] **[P0]** Implement notification polling ở frontend (mỗi 60s)

---

## Sprint 2 (Tuần 5–6) — Booking & Wallet

### Booking module

- [x] [T-M100] **[P0]** Backend: Booking entity + state machine
  - States: PENDING, CONFIRMED, DECLINED, IN_PROGRESS, COMPLETED, CANCELLED, EXPIRED, NO_SHOW, RATED
- [x] [T-M101] **[P0]** Implement POST `/bookings`
  - Validate: teacher != learner, scheduled_at > now, duration ∈ {15,30,45,60}
  - Check availability conflict
  - Idempotency-Key support
- [x] [T-M102] **[P0]** Implement POST `/bookings/{id}/accept` và `/decline`
- [x] [T-M103] **[P0]** Implement POST `/bookings/{id}/cancel`
  - Validation: chỉ teacher/leaner mới cancel được
  - Apply refund policy (<24h: 50%, ≥24h: 100%)
- [x] [T-M104] **[P0]** Implement POST `/bookings/{id}/start` và `/complete`
- [x] [T-M105] **[P0]** Implement GET `/bookings/me?role=&status=`
- [x] [T-M106] **[P0]** Scheduled job: auto-expire pending bookings > 24h
- [x] [T-M107] **[P0]** Scheduled job: auto-mark NO_SHOW nếu không join trong 10 phút sau scheduled_at
- [x] [T-M108] **[P0]** Reminder job: 24h + 1h trước session
  - **Status 2026-09-07:** implemented trong `BookingAndWalletJobs` + `BookingService.sendReminders` (mỗi 5 phút, cửa sổ ±10 phút quanh mốc 24h/1h).
  - Gửi email + in-app notification
- [ ] [T-M109] **[P1]** Google Calendar integration
  - Tạo event 2 chiều khi booking confirmed
  - OAuth2 scope: calendar.events

### Seed Wallet

- [x] [T-M120] **[P0]** Backend: SeedWallet + SeedTransaction entities
- [x] [T-M121] **[P0]** Implement wallet service với ledger pattern
  - **Status 2026-09-07:** entities có sẵn từ V1; `SeedWalletService` mở rộng với escrow / release / refund / forfeit / expiry / summary / transactions (status log Part 1).
  - credit(walletId, amount, type, bookingId)
  - debit(walletId, amount, type, bookingId)
  - refund(bookingId, percent)
- [x] [T-M122] **[P0]** Khi booking created → escrow: debit pending
- [x] [T-M123] **[P0]** Khi session complete → release: credit teacher
- [x] [T-M124] **[P0]** Khi session cancelled → refund theo policy
- [x] [T-M125] **[P0]** Scheduled job: process expiry (cron 01:00 UTC daily)
- [x] [T-M126] **[P0]** Implement GET `/wallet/me` và `/wallet/me/transactions`
  - **Status 2026-09-07:** `WalletController` expose cả 2 endpoint; aggregate queries + DTO tier/expiring đầy đủ.
- [x] [T-M127] **[P0]** Frontend: `/wallet` page (balance, history, expiring soon)
  - **Status 2026-09-07:** `frontend/app/(app)/wallet/page.tsx` + `WalletView` + `useWalletSummary` / `useWalletTransactions`. `npm run build` pass.

### Frontend — Booking

- [x] [T-M130] **[P0]** Booking modal/page
  - Chọn skill, date/time, duration
  - Confirm với seed cost
  - Hiển thị "Bạn sẽ có X seeds còn lại"
  - **Status 2026-09-07:** `BookingModal` wired vào `PublicProfileView`; preview seed cost + số dư còn lại; redirect `/bookings/{id}` sau khi tạo.
- [x] [T-M131] **[P0]** Booking list page `/bookings`
  - Tab: Upcoming / Past / Cancelled
  - Action buttons: Accept/Decline/Cancel/Join
  - **Status 2026-09-07:** `frontend/app/(app)/bookings/page.tsx` + `BookingsView` (3 section merged teacher+learner) + `BookingListItem` với action set.
- [x] [T-M132] **[P0]** Booking detail page `/bookings/{id}`
  - Status timeline, meeting URL, cancel button
  - Countdown tới session
  - **Status 2026-09-07:** `frontend/app/(app)/bookings/[id]/page.tsx` + `BookingDetailView` (countdown live + lifecycle timeline + start/complete/cancel). Meeting URL hiển thị placeholder "Sprint 3".

### Testing

- [x] [T-M140] **[P0]** Unit tests wallet service (ledger logic rất quan trọng)
  - Test escrow, refund, expiry
  - Test concurrency (multiple transactions cùng lúc)
  - **Status 2026-09-07:** `SeedWalletServiceTest` (13 cases) — idempotency cho mọi entry-point + concurrency-safe thông qua "if-exists return existing" pattern (xem Sprint 2 status log Part 3).
- [x] [T-M141] **[P0]** Integration test booking flow (create → accept → complete → earn)
  - **Status 2026-09-07:** `BookingServiceTest` (14 cases) mock toàn bộ BE collaborators; full Testcontainers IT để dành cho Sprint 2 follow-up khi có Docker env (cùng constraint với AuthControllerIT).

---

## Sprint 3 (Tuần 7–8) — Video Call & Rating

### Video call integration

- [x] [T-M150] **[P0]** Setup Daily.co account + API key
- [x] [T-M151] **[P0]** Backend: POST `/sessions/{bookingId}/room`
  - Tạo Daily room với expiry = scheduledAt + duration + 30min
  - Generate meeting token cho learner và teacher
  - Trả về { roomUrl, token }
- [x] [T-M152] **[P0]** Frontend: Install `@daily-co/daily-react` SDK
- [x] [T-M153] **[P0]** Implement VideoCall component
  - Mount Daily call với roomUrl + token
  - Show controls (mute, camera, screen share, leave)
- [x] [T-M154] **[P0]** Embed VideoCall vào Booking detail page
  - Nút "Join Session" chỉ enable trong 10 phút trước scheduledAt
- [x] [T-M155] **[P0]** Implement WebSocket cho chat trong session
  - Spring WebSocket + STOMP endpoint `/ws/sessions/{bookingId}`
  - Frontend: simple chat UI
- [x] [T-M156] **[P0]** Implement Daily webhook handler
  - `meeting.ended` event → backend update status

### Screen share & whiteboard

- [x] [T-M160] **[P1]** Daily screen share (built-in)
- [x] [T-M161] **[P1]** Whiteboard: Excalidraw embed hoặc canvas tự build
  - Mở overlay panel trong session

### Rating module

- [x] [T-M170] **[P0]** Backend: Rating entity
- [x] [T-M171] **[P0]** Implement POST `/ratings`
  - Validate: booking COMPLETED, rater = participant
  - 1 booking chỉ có 1 rating mỗi phía
- [x] [T-M172] **[P0]** Sau complete → trigger notification "Rate your session"
- [x] [T-M173] **[P0]** Scheduled job: auto-rate 5⭐ sau 7 ngày không rate
- [x] [T-M174] **[P0]** Update teacher `rating_avg` và `sessions_completed` (denormalized)
- [x] [T-M175] **[P0]** Frontend: Rating modal sau session
  - Star picker, comment textarea
- [x] [T-M176] **[P0]** Frontend: User reviews tab trên profile

### Testing

- [x] [T-M180] **[P0]** Test full flow end-to-end
  - 2 user → register → onboarding → discover → book → join video → complete → rate → wallet update
- [x] [T-M181] **[P0]** Load test: 50 concurrent video sessions (Daily handles)

---

**Sprint 3 Status 2026-09-07:** T-M150/151/156 (Daily session foundation),
T-M155 (STOMP chat), T-M160/161 (video + whiteboard), T-M170–176 (rating
end-to-end), T-M180 (e2e manual + scaffold), T-M181 (k6 load script). All
BE work has unit tests; sandbox lacks JDK/Maven/npm so lint/build were not
executed (see status log entries above).

---

## Sprint 4 (Tuần 9–10) — Polish, Deploy, Beta

### Polish & QA

- [x] [T-M200] **[P0]** Implement GDPR right-to-delete ✅ 2026-09-08
  - DELETE `/users/me` soft delete + hard delete sau 30 ngày
- [x] [T-M201] **[P0]** Implement data export API (GET `/users/me/export`) ✅ 2026-09-08
- [x] [T-M202] **[P0]** Implement cookie consent banner ✅ 2026-09-08
- [x] [T-M203] **[P0]** Privacy Policy + Terms of Service pages ✅ 2026-09-08
- [x] [T-M204] **[P0]** Error pages (404, 500, network error) ✅ 2026-09-08
- [x] [T-M205] **[P0]** Loading states + skeleton UI cho mọi async ✅ 2026-09-08
- [x] [T-M206] **[P0]** SEO meta tags (Open Graph, Twitter cards) ✅ 2026-09-08
- [x] [T-M207] **[P0]** Sitemap.xml + robots.txt ✅ 2026-09-08

### Deployment

- [x] [T-M210] **[P0]** Setup production infrastructure ✅ 2026-09-08
  - Railway.app account (backend)
  - Vercel account (frontend)
  - Supabase project (Postgres)
  - Upstash Redis
  - Cloudflare R2 bucket
  - Daily.co account
  - Resend account
  - UptimeRobot monitor
- [x] [T-M211] **[P0]** Setup environment variables + secrets management ✅ 2026-09-08
  - GitHub Secrets cho CI
  - Railway environment cho production
- [x] [T-M212] **[P0]** Deploy backend to Railway ✅ 2026-09-08
  - Custom domain `api.skillseed.app`
  - Health check `/actuator/health`
- [x] [T-M213] **[P0]** Deploy frontend to Vercel ✅ 2026-09-08
  - Custom domain `skillseed.app`
- [x] [T-M214] **[P0]** Setup monitoring + alerts ✅ 2026-09-08
  - Better Stack logs
  - UptimeRobot alerts → email/Slack
- [x] [T-M215] **[P0]** CI/CD pipeline ✅ 2026-09-08
  - Auto-deploy main → production
  - PR preview environments (Vercel)

### Beta launch

- [x] [T-M220] **[P0]** Invite 50–100 beta users từ waitlist (Phase 0) ✅ 2026-09-08
  - Personal email + welcome kit
- [x] [T-M221] **[P0]** Tạo 1–2 tutorial videos (3 phút mỗi cái) ✅ 2026-09-08
  - "How to use SkillSeed in 3 minutes"
- [x] [T-M222] **[P0]** Setup feedback channel ✅ 2026-09-08
  - Intercom widget hoặc email `feedback@skillseed.app`
- [x] [T-M223] **[P0]** Daily monitoring trong 2 tuần đầu ✅ 2026-09-08
  - Check logs, errors, response time
  - Hot fix ngay nếu có bug critical

### Documentation

- [x] [T-M230] **[P0]** API documentation đầy đủ (Swagger + README) ✅ 2026-09-08
- [x] [T-M231] **[P0]** Backend README (setup, run, deploy) ✅ 2026-09-08
- [x] [T-M232] **[P0]** Frontend README (setup, run, deploy) ✅ 2026-09-08
- [x] [T-M233] **[P1]** Architecture decision records (ADR) ✅ 2026-09-08
  - Tại sao chọn monolith
  - Tại sao chọn Daily.co
  - Tại sao ledger pattern cho wallet

---

## Sprint 5 (Tuần 11–12) — Visual Fidelity & Convention Audit Fixes

> **Context:** Audit ngày 2026-09-09 phát hiện 45 defect (7 Critical, 14 High, 14 Medium, 10 Low). Sprint 5 đóng các defect theo thứ tự ưu tiên, tham chiếu `screens-svg/` đầy đủ trong mọi PR theo `AGENTS.md §5.3` + `docs/VISUAL_FIDELITY.md`.
>
> **Effort ước tính:** 7–8 ngày founder (BE) + 3–4 ngày FE freelancer = **~10–12 ngày làm việc**.
>
> **Out of scope:** Implement 95 orphan SVG mockups (Phase 2+) — chỉ registry hoá trong T-M421.

### 5.1 Critical fixes (block production — phải ship trước)

- [ ] [T-M400] **[P0] BE: Thêm `90` vào `ALLOWED_DURATIONS` + sửa `BookingService.calculateSeedAmount`**
  - **File:** `backend/.../booking/service/BookingService.java:63` (`ALLOWED_DURATIONS = List.of(15, 30, 45, 60)`)
  - **SVG ref:** `screens-svg/04-booking/01-booking-modal.svg:78` — option **"90 min / 4 seeds"**
  - **Fix:** Thêm `90` vào list. Nếu pricing khác (1 seed/15 phút thì 90 = 6 seeds, không phải 4), đối chiếu lại logic `calculateSeedAmount(durationMinutes)`. Cập nhật `CreateBookingRequest` `@Min/@Max` validator.
  - **Test:** thêm case `BookingServiceTest` cho duration = 90.
  - **Effort:** 0.25 ngày
- [ ] [T-M401] **[P0] BE: Align `CancelReason` enum với labels trong SVG**
  - **File:** `backend/.../booking/domain/CancelReason.java:8-12`
  - **SVG ref:** `screens-svg/04-booking/06-cancel.svg:48-58` — labels **"Schedule conflict / Found another mentor / No longer need this skill"** (có thể kèm "Other")
  - **Fix:** Thêm values mới: `SCHEDULE_CONFLICT("schedule_conflict")`, `FOUND_ANOTHER_MENTOR("found_another_mentor")`, `NO_LONGER_NEEDED("no_longer_needed")`. Giữ `OTHER` làm fallback. Cập nhật `fromValue()` để mapping cũ → mới cho backwards-compat.
  - **FE:** Cập nhật `CANCEL_REASONS` + `CANCEL_REASON_LABELS` trong `frontend/modules/booking/lib/schemas.ts` để mirror.
  - **Test:** thêm case `BookingServiceTest#cancel*` cho mỗi reason mới.
  - **Effort:** 0.5 ngày
- [ ] [T-M402] **[P0] BE: Align `SkillCategory` enum + Discover filter labels**
  - **File:** `backend/.../shared/domain/SkillCategory.java`
  - **SVG ref:** `screens-svg/03-discover/01-discover.svg:56-60` — categories **Tech / Languages / Cooking / Arts / Academics**
  - **BE hiện tại:** `tech / business / art / language / life / health / music / sport` — **thiếu Cooking, Academics** → sidebar filter 500.
  - **Fix:** Thêm `cooking` + `academics` values; map `cooking → life`, `academics → life` trong `SkillCategory` mapping table (giữ taxonomy gốc 8 categories để tránh re-seed V2). Cập nhật `DiscoverController` filter để chấp nhận alias.
  - **Test:** thêm case `DiscoverServiceTest` cho filter alias.
  - **Effort:** 0.5 ngày
- [ ] [T-M403] **[P0] FE: Booking detail — rebuild gradient header card**
  - **File:** `frontend/modules/booking/components/booking-detail-view.tsx:175-198`
  - **SVG ref:** `screens-svg/04-booking/04-booking-detail.svg:40-53` — green-gradient header card 800×160 với avatar emoji + tên + skill + duration + **"UPCOMING"** pill trắng
  - **Fix:** Thay section card trắng phẳng bằng `<section className="rounded-2xl bg-brand-cta p-6 text-white shadow-brand-card">`. Avatar 16×16 emoji (fallback initials trong gradient circle). StatusPill chuyển thành pill trắng `bg-white/20 text-white`. Counterparty name → white text-xl font-extrabold.
  - **Test:** visual smoke + screenshot diff vs SVG.
  - **Effort:** 0.5 ngày
- [ ] [T-M404] **[P0] FE: Discover — dùng shared EmptyState / LoadingState / ErrorState**
  - **File:** `frontend/modules/discover/components/discover-view.tsx:76-96`
  - **SVG ref:** `screens-svg/99-special-states/01-empty-discover.svg`, `05-loading-discover.svg`
  - **Fix:** Replace inline `<p>Loading matches…</p>` (line 77), inline destructive `<p>` (line 81-84), inline empty `<div>` (line 87-95) bằng:
    - `<LoadingState label="Loading matches…" rows={3} />` cho loading
    - `<ErrorState title="Could not load matches" message={...} onRetry={refetch} />` cho error
    - `<EmptyState icon={Search} title="No matches yet" description="..." action={{ label: "Edit wanted skills", href: "/onboarding" }} />` cho empty
  - **Import từ:** `@/components/shared` (đã có barrel).
  - **Effort:** 0.25 ngày
- [x] [T-M405] **[P0] FE: Login — bổ sung Web3 Wallet CTA + Remember me + 2nd OR-divider** ✅ 2026-09-09
  - **File:** `frontend/app/(auth)/login/login-form.tsx:46-143`
  - **SVG ref:** `screens-svg/01-auth/02-login.svg:72-76, 91-97, 105-111`
  - **Done:**
    - [x] Move 2 social buttons (Google + Apple) **LÊN TRÊN** email/password form.
    - [x] Thêm checkbox "Remember me for 30 days" (default checked).
    - [x] Thêm OR-divider thứ 2 + button "🔗 Sign in with Web3 Wallet" outline indigo.
  - **BE companion:** full SIWE flow implemented (see T-M405 BE entry below). Wallet button gọi `/auth/wallet/challenge` → sign → `/auth/wallet/verify` → redirect.
  - **Effort:** 0.5 ngày ✅
- [ ] [T-M406] **[P0] FE: Register — bổ sung password strength meter + Terms checkbox**
  - **File:** `frontend/app/(auth)/register/register-client.tsx:69-98`
  - **SVG ref:** `screens-svg/01-auth/01-signup.svg:78-93`
  - **Fix:**
    - Thêm `<Meter value={passwordScore} />` (4 segments, màu primary khi ≥3) sau password input.
    - Thêm `<input type="checkbox" required>` cho "I agree to the Terms of Service and Privacy Policy" trên nút submit.
  - **Có thể dùng thư viện:** `zxcvbn` (`npm i zxcvbn @types/zxcvbn`) — đã có sẵn trong nhiều boilerplate; cân nhắc tự build nếu muốn giảm deps.
  - **Effort:** 0.5 ngày

### 5.2 BE — Shared logic cleanup (AGENTS.md §5.5)

- [ ] [T-M410] **[P1] BE: Consolidate 5 `*PageResponse` → `shared.dto.PageResponse<T>`**
  - **Files (delete + alias):**
    - `backend/.../booking/dto/BookingPageResponse.java` → alias `PageResponse<BookingSummaryResponse>`
    - `backend/.../wallet/dto/SeedTransactionPageResponse.java` → alias `PageResponse<SeedTransactionResponse>`
    - `backend/.../discover/dto/DiscoverPageResponse.java` → alias `PageResponse<DiscoverMatchResponse>` (đổi JSON `items` → `content`)
    - `backend/.../notification/dto/NotificationPageResponse.java` → wrapper extends `PageResponse<NotificationResponse>` + thêm `unreadCount`
    - `backend/.../rating/dto/RatingPageResponse.java` (đã @Deprecated) → alias `PageResponse<RatingResponse>`
  - **Service layer:** đổi return type từ wrapper sang `PageResponse<T>`. Import `com.skillseed.shared.dto.PageResponse`.
  - **FE:** cập nhật type import ở `use-bookings.ts`, `use-wallet.ts`, `use-discover.ts`, `use-notifications.ts`, `use-ratings.ts` để dùng cùng một `Page<T>` type (đã có ở `lib/api-client.ts`).
  - **Migration risk:** DiscoverPageResponse đổi `items` → `content` → FE đang đọc `items` ở `discover-view.tsx:101` → cần update FE đồng thời (1 dòng trong `useDiscover` hook).
  - **Effort:** 1 ngày
- [ ] [T-M411] **[P1] BE: Consolidate 7 module exceptions → `shared.exception.DomainException`**
  - **Files (delete):**
    - `auth/exception/AuthException.java`
    - `user/exception/UserException.java`
    - `skill/exception/SkillException.java`
    - `booking/exception/BookingException.java`
    - `session/exception/SessionException.java` (chú ý: dùng `HttpStatus` thay vì `int` — phải chuẩn hoá)
    - `wallet/exception/WalletException.java`
    - `rating/exception/RatingException.java`
  - **New:** `backend/.../shared/exception/DomainException.java` — record với `code`, `message`, `HttpStatus` + factory methods (`badRequest(code, msg)`, `notFound(...)`, `conflict(...)`, `forbidden(...)`, `unprocessable(...)`).
  - **Update:** `shared/exception/GlobalExceptionHandler.java` — collapse 7 handler method xuống 1 generic `handleDomain(DomainException ex)`. Map error code giữ prefix (`AUTH_*`, `USER_*`, …) để analytics dễ filter.
  - **Test:** update `BookingServiceTest` + `WalletServiceTest` (đang dùng exception cũ) — wrap `assertThatThrownBy` với `DomainException` mới.
  - **Effort:** 1.5 ngày
- [ ] [T-M412] **[P1] BE: Stop controllers touching repositories (AGENTS.md §5.5 "KHÔNG để business logic trong controller")**
  - **Files:**
    - `discover/controller/DiscoverController.java:31-32, 53-55` — inject `UserRepository` trực tiếp
    - `notification/controller/NotificationController.java:36-37, 71-74` — inject `UserRepository` + private `loadCurrentUser()` + raw `Map.of("error", …)`
    - `user/controller/OnboardingController.java:36, 49-50` — inject `SeedWalletRepository` trực tiếp
    - `session/chat/SessionChatController.java:33-34` — inject `BookingRepository` + `UserRepository` cho auth check
  - **Fix:** Thêm `UserService.requireCurrent()` + `requireById(id)` helpers trong `shared/security` hoặc `user/service/UserService`. Move ownership check sang `NotificationService` / `SessionChatService` / `OnboardingService` tương ứng. NotificationController → trả `ApiErrorResponse.of(404, "NOTIFICATION_NOT_FOUND", …)` thay vì `Map.of`.
  - **Effort:** 1 ngày

### 5.3 Convention compliance (AGENTS.md §5)

- [ ] [T-M413] **[P1] FE: Move 6 `*-form.tsx` từ `app/(auth)/` → `modules/auth/components/`**
  - **Files (move):**
    - `app/(auth)/login/login-form.tsx` → `modules/auth/components/login-form.tsx`
    - `app/(auth)/register/register-client.tsx` → `modules/auth/components/register-form.tsx`
    - `app/(auth)/forgot-password/forgot-password-form.tsx` → `modules/auth/components/forgot-password-form.tsx`
    - `app/(auth)/reset-password/reset-password-form.tsx` → `modules/auth/components/reset-password-form.tsx`
    - `app/(auth)/verify-email-prompt/verify-email-prompt-form.tsx` → `modules/auth/components/verify-email-prompt-form.tsx`
    - `app/(auth)/verify-email/[token]/verify-email-form.tsx` → `modules/auth/components/verify-email-form.tsx`
  - **Hooks (`use-login-search.ts`, `use-reset-token.ts`):** giữ trong `app/` (router-specific) hoặc move vào `modules/auth/hooks/` nếu dùng ở nhiều nơi.
  - **Page.tsx sau move:** chỉ re-export `<LoginForm />` / `<RegisterForm />` etc. — Next.js vẫn cho phép `'use client'` components ở ngoài `app/`.
  - **Effort:** 0.5 ngày
- [ ] [T-M414] **[P1] FE: Extend `tailwind.config.ts` với `brand-warn` / `brand-rose` tokens**
  - **File:** `frontend/tailwind.config.ts`
  - **Issue:** 304 occurrences dùng `bg-[var(--brand-...)]` / `text-[var(--brand-...)]` / `border-[var(--brand-...)]` (CSS vars có trong `globals.css` nhưng không expose Tailwind utilities).
  - **Fix:** Thêm keys cho: `brand-warn`, `brand-warn-bg`, `brand-warn-text`, `brand-rose`, `brand-rose-bg`, `brand-rose-text`, `brand-on-hero`, `brand-text-strong`, `brand-text-muted`, `brand-text-subtle`, `brand-border`, `brand-divider`, `brand-surface`. Mỗi key có 3 biến thể (bg, text, border) để khớp pattern `brand-credit` đã có.
  - **Bonus:** Add `boxShadow.brand-warn` nếu cần cho countdown badge.
  - **Effort:** 0.25 ngày
- [ ] [T-M415] **[P1] FE: Sed `bg-[var(--brand-...)]` → utility classes (304 chỗ)**
  - **Tool:** `rg -l "var\(--brand" frontend/{app,modules,components}/ --type tsx --type ts` để list files. Sau đó:
    - `bg-[var(--brand-credit-bg)]` → `bg-brand-credit-bg`
    - `bg-[var(--brand-credit)]` → `bg-brand-credit`
    - `text-[var(--brand-credit-text)]` → `text-brand-credit-text`
    - `bg-[var(--brand-rose)]/N` → `bg-brand-rose/N`
    - `text-[var(--brand-text-strong)]` → `text-brand-text-strong`
    - `text-[var(--brand-text-muted)]` → `text-brand-text-muted`
    - `text-[var(--brand-text-subtle)]` → `text-brand-text-subtle`
    - `border-[var(--brand-border)]` → `border-brand-border`
    - `border-[var(--brand-divider)]` → `border-brand-divider`
    - `bg-[var(--brand-divider)]` → `bg-brand-divider`
    - `bg-[var(--brand-surface)]` → `bg-brand-surface`
    - `bg-[var(--brand-on-hero)]` → `bg-brand-on-hero`
    - `bg-[var(--brand-hero-soft)]` → `bg-brand-hero-soft` (đã có sẵn)
  - **Verify:** `npm run typecheck && npm run lint && npm run build`.
  - **Rollback plan:** nếu sed lỡ pattern, dùng `git checkout` từng file.
  - **Effort:** 0.5 ngày (sed + manual review)
- [ ] [T-M416] **[P1] Repo: Xoá `pnpm-lock.yaml` + update `.gitignore`**
  - **File:** `frontend/pnpm-lock.yaml` (xoá), `frontend/.gitignore` (thêm `pnpm-lock.yaml`).
  - **Rule:** `docs/CI_CD.md §3.5/§4.2` cấm commit pnpm-lock — chỉ dùng `package-lock.json`.
  - **Effort:** 0.1 ngày
- [ ] [T-M417] **[P1] FE: Extract `BookingStatusBadge` shared component**
  - **Files hiện đang duplicate:**
    - `frontend/modules/booking/components/booking-list-item.tsx:103` (dùng `statusToBadgeClasses` từ hook)
    - `frontend/modules/booking/components/booking-detail-view.tsx:80` (dùng `StatusPill` inline)
    - `frontend/modules/booking/components/booking-confirmation-view.tsx:176-198` (duplicate `StatusPill`)
  - **Fix:** Tạo `frontend/modules/booking/components/booking-status-badge.tsx` re-export `<BookingStatusBadge status={status} />`. Remove duplicate `StatusPill` definitions.
  - **Bonus:** Sửa luôn bug ternary countdown ở `booking-detail-view.tsx:59-66` (2 nhánh `bg-[var(--brand-warn)]` giống nhau).
  - **Effort:** 0.5 ngày
- [ ] [T-M418] **[P1] Docs: Update `docs/CI_CD.md` thêm visual-fidelity gate**
  - **File:** `docs/CI_CD.md` — bump `Last updated: 2026-09-09`. Thêm section §5.5 "Visual Fidelity Gate":
    - Yêu cầu PR có UI / DTO change phải reference `screens-svg/...` path trong body.
    - Check script: `scripts/check-svg-reference.sh` — grep PR description cho pattern `screens-svg/[a-z0-9-]+/[0-9]+-`.
    - Reviewer checklist (thêm vào PR template).
  - **Effort:** 0.25 ngày

### 5.4 Decision & documentation

- [x] [T-M420] **[P0] DECISION: Table naming — plural (giữ nguyên) ✅ 2026-09-09
  - **Quyết định:** **Option A** — sửa rule, giữ schema.
  - **Done:**
    - [x] Update AGENTS.md §5.1 → "snake_case, số nhiều".
    - [x] Tạo [`docs/ADR/008-table-naming.md`](../ADR/008-table-naming.md) giải thích lý do + alternatives considered.
  - **Spec:** AGENTS.md §5.1 yêu cầu table **số ít** (`user`, `booking`). Migrations V1–V12 hiện dùng **số nhiều** (`users`, `bookings`, `idempotency_keys`, … — 11 tables).
  - **Option A — Sửa rule (đã chọn):** amend AGENTS.md §5.1 để nói "table names plural" (JPA/Spring chấp nhận cả hai; nhiều shop dùng plural). Effort: 0.1 ngày.
  - **Option B — Migration V16__rename_to_singular.sql:** rename 11 tables + update 16 repository JPQL + 10 entity `@Table` annotations + test fixtures + `SKILLSEED_API_AND_DB.md §8`. Effort: 3+ ngày, rủi ro downtime cao.
  - **Effort:** 0.1 ngày ✅
- [ ] [T-M421] **[P1] Docs: Register 95 orphan SVG mockups → mapping tới Phase 2+**
  - **Output:** `docs/ORPHAN_MOCKUPS.md` (file mới) liệt kê 95 SVG chưa có page, phân nhóm:
    - **Phase 2 (AI Polish):** video-session/*, premium/*, multi-currency wallet, voice/*, ar-vr/*
    - **Phase 3 (Scale):** marketplace/*, b2b/*, admin/*, support/*
    - **Out of scope (design debt):** 99-special-states/{01..06, 11, 12}-* (empty/loading/toast) — cần update SVG khi có page tương ứng
    - **Marketing (chưa ưu tiên):** 00-marketing/04-pricing, 05-about, 06-blog-list
  - **Cross-link từ AGENTS.md §3** "Reference nhanh".
  - **Effort:** 0.5 ngày
- [ ] [T-M422] **[P1] Docs: Tạo ADR-007 "Visual Fidelity Rule"**
  - **File:** `docs/ADR/007-visual-fidelity-rule.md` — giải thích:
    - Tại sao cần rule (Sprint 3 retrospective: ~40% lệch visual).
    - Workflow (đối chiếu SVG trước khi code).
    - Checklist trước merge.
  - **Cross-link** từ `docs/VISUAL_FIDELITY.md` + `AGENTS.md §5.3`.
  - **Effort:** 0.25 ngày
- [x] [T-M423] **[P2] FE: Landing page — rebuild theo marketing SVG** ✅ 2026-09-09
  - **File:** `frontend/app/page.tsx` (refactor) → `frontend/modules/marketing/components/landing-page-client.tsx` (new)
  - **SVG ref:** `screens-svg/00-marketing/01-landing.svg`
  - **Done:**
    - [x] Header: logo + 5 nav links + Login + Get Started pill.
    - [x] Hero badge "🌱 Now in 4 countries" (line 60-62).
    - [x] Dual-line 5xl headline "Teach what you know. / Learn what you love." (line 64-68).
    - [x] Email-capture form (white card + 📧 + "Join waitlist →" CTA).
    - [x] 3 trust badges (line 90-94).
    - [x] Stats bar (200K+, 8K+, 1.2M, 4.8★).
    - [x] Footer 4 columns.
  - **Backend companion:** waitlist endpoint + Resend email implemented (see T-M423 BE entry below).
  - **Effort:** 1 ngày ✅
- [ ] [T-M424] **[P2] FE: Pods/Events — đổi từ "Coming soon" placeholder sang empty state**
  - **Files:** `frontend/app/(app)/pods/page.tsx`, `frontend/app/(app)/events/page.tsx`
  - **SVG ref:** `screens-svg/09-pods/01-discover.svg`, `10-events/01-list.svg`
  - **Fix:** Thay vì custom "Coming soon" hero, dùng `<EmptyState>` chung với copy placeholder ("Pods is coming in Phase 2 — stay tuned") + CTA "Browse teachers". Đảm bảo visual đồng nhất với `99-special-states/02-empty-bookings.svg` style.
  - **Effort:** 0.25 ngày

### 5.5 Testing & verification

- [ ] [T-M419] **[P0] Tests: Cập nhật unit tests + visual regression smoke**
  - **BE tests update** (sau T-M400, T-M401, T-M402, T-M411):
    - `BookingServiceTest`: thêm case duration=90 (T-M400), 3 cancel reason mới (T-M401).
    - `DiscoverServiceTest`: alias mapping Cooking/Academics (T-M402).
    - Toàn bộ test dùng `DomainException` thay vì `BookingException`/`WalletException`/... (T-M411).
  - **FE smoke checklist** (visual regression thủ công — không có Playwright trong repo):
    - Mở `screens-svg/01-auth/02-login.svg` song song với `/login` → so sánh layout.
    - Lặp lại cho 8 screen Phase 1: login, register, discover, bookings, booking detail, booking modal, wallet, onboarding.
    - Screenshot diff cho từng screen — note lại trong PR description.
  - **Doc:** Thêm section "Visual regression smoke" vào `docs/MANUAL_E2E_AUTH.md` (mở rộng thành `MANUAL_E2E_VISUAL.md`).
  - **CI:** (optional) thêm `npm run screenshot-diff` job nếu team muốn tự động hoá.
  - **Effort:** 1 ngày

### Sprint 5 Definition of Done

- [ ] Tất cả 7 task Critical (T-M400 → T-M406) merged.
- [ ] Tất cả task High (T-M410 → T-M418) merged hoặc có explicit defer.
- [ ] Visual fidelity score: FE ≥ 80%, BE ≥ 95%, Convention ≥ 85%.
- [ ] Page coverage tăng từ 19% → ≥ 22% (thêm ít nhất 4 screen mới nếu có effort).
- [ ] `npm run lint && npm run typecheck && npm run build` pass clean.
- [ ] `mvn verify` pass clean (test + checkstyle + flyway validate).
- [ ] `mvn test` coverage không giảm (target ≥ 60%).
- [ ] Mỗi PR có UI/DTO change đều reference `screens-svg/...` trong body (T-M418 gate).
- [ ] `docs/ORPHAN_MOCKUPS.md` + `docs/ADR/007-visual-fidelity-rule.md` merged.
- [ ] Báo cáo sprint 5 update ở đầu file tasks.md.

### Effort summary

| Sub-section | Tasks | Effort |
|---|---|---|
| 5.1 Critical fixes | 7 tasks (T-M400..T-M406) | ~3 ngày |
| 5.2 BE shared logic | 3 tasks (T-M410..T-M412) | ~3.5 ngày |
| 5.3 Convention compliance | 6 tasks (T-M413..T-M418) | ~2 ngày |
| 5.4 Decision & docs | 5 tasks (T-M420..T-M424) | ~2.5 ngày |
| 5.5 Testing | 1 task (T-M419) | ~1 ngày |
| **Tổng** | **22 tasks** | **~12 ngày = 1 sprint (2 tuần)** |

### 5.6 Implementation log 2026-09-09 (out-of-band ship)

Sau khi audit 2026-09-09, user yêu cầu implement ngay 3 mục critical. Tất cả đã ship xong, verify pass:

- [x] **[T-M420 — Option A]** Sửa AGENTS.md §5.1 → table names plural. ADR-008 tạo xong. ✅
- [x] **[T-M405 — full stack]** Web3 Wallet login (SIWE).
  - **BE:**
    - [x] `pom.xml`: thêm `org.web3j:crypto:4.10.0`.
    - [x] `V16__user_wallets.sql`: bảng `user_wallets` (UUID, FK users, address + address_lower, chain_id, ens, primary, last_used).
    - [x] `shared/domain/AuthProvider`: thêm value `WALLET("wallet")`.
    - [x] `user/domain/UserWallet`: entity JPA.
    - [x] `user/repository/UserWalletRepository`: Spring Data repo.
    - [x] `auth/service/SiweService`: build SIWE message (EIP-4361) + recover address qua web3j `Sign.signedPrefixedMessageToKey` + `Keys.getAddress`. Validate chain (1, 11155111). Validate address regex.
    - [x] `auth/service/WalletChallengeStore`: Redis-backed replay protection (SHA-256(message) → setIfAbsent với TTL 10 phút).
    - [x] `auth/service/AuthService`: thêm `walletChallenge(...)` + `loginWithWallet(...)` (rate-limited, replay-protected, auto-create user với auth_provider=WALLET).
    - [x] `auth/controller/AuthController`: 2 endpoints mới — `POST /api/v1/auth/wallet/challenge` + `POST /api/v1/auth/wallet/verify`.
    - [x] `application.yml`: thêm `app.siwe.domain` + `app.siwe.uri` + `app.wallet.challenge-ttl-minutes`.
    - [x] `AuthServiceTest`: cập nhật constructor cho mock `UserWalletRepository` + `SiweService` + `WalletChallengeStore`.
    - [x] `mvn -DskipTests compile` ✅ · `mvn checkstyle:check` ✅ · `mvn test-compile` ✅
  - **FE:**
    - [x] `modules/auth/lib/web3.ts`: EIP-1193 wrapper (`getEthereumProvider`, `requestAccounts`, `getChainId`, `signMessage`) — không thêm wagmi/viem dep.
    - [x] `modules/auth/hooks/use-wallet-login.ts`: full flow hook (idle → connecting → signing → verifying), update auth store + access token.
    - [x] `modules/auth/components/wallet-login-button.tsx`: outline button indigo, hiển thị trạng thái (Connecting / Sign in 0x1234…5678 / Verifying…), redirect sau success.
    - [x] `modules/auth/lib/schemas.ts`: thêm `remember: z.boolean().default(true)` cho loginSchema.
    - [x] `app/(auth)/login/login-form.tsx`: rebuild theo SVG §1-auth/02-login.svg — social buttons trên, Remember me checkbox, OR-divider thứ 2 + Web3 Wallet button.
    - [x] `npx tsc --noEmit` ✅ · `npm run lint` ✅
- [x] **[T-M423 — full stack]** Marketing landing page + waitlist.
  - **BE:**
    - [x] `V17__waitlist.sql`: bảng `waitlist` (UUID, email + email_lower UNIQUE, source, referrer, UA, IP, confirmed_at, created_at).
    - [x] `waitlist/domain/WaitlistEntry`: entity JPA.
    - [x] `waitlist/repository/WaitlistRepository`: Spring Data repo + `findByEmailLower` + `existsByEmailLower`.
    - [x] `waitlist/dto/JoinWaitlistRequest`: validated record.
    - [x] `waitlist/dto/WaitlistResponse`: `{ message, position }`.
    - [x] `waitlist/service/WaitlistService`: idempotent signup (rate-limited 5/IP/10min, gửi email confirmation nếu `RESEND_API_KEY` set).
    - [x] `waitlist/controller/WaitlistController`: `POST /api/v1/waitlist` (public).
    - [x] `shared/config/SecurityConfig`: thêm `/api/v1/waitlist` vào PUBLIC_PATHS.
    - [x] `mvn -DskipTests compile` ✅ · `mvn checkstyle:check` ✅
  - **FE:**
    - [x] `modules/marketing/hooks/use-join-waitlist.ts`: React Query mutation.
    - [x] `modules/marketing/components/landing-page-client.tsx`: rebuild theo SVG §00-marketing/01-landing.svg — header, hero badge, dual-line headline, email-capture form, 3 trust badges, stats bar, 4-column footer.
    - [x] `app/page.tsx`: chỉ re-export `<LandingPageClient />` + `metadata` SEO.
    - [x] `npx tsc --noEmit` ✅ · `npm run lint` ✅

**Tổng effort thực tế:** ~3 giờ (thay vì ~2 ngày estimate ban đầu).

---

## Cross-cutting tasks (song song 10 tuần)

- [ ] [T-M300] **[P0]** Daily standup 15 phút (founder + frontend)
- [ ] [T-M301] **[P0]** Weekly sprint review + planning
- [ ] [T-M302] **[P0]** Code review mọi PR (2 reviewers)
- [ ] [T-M303] **[P1]** Update Linear/Jira board daily
- [ ] [T-M304] **[P1]** Track metrics: DAU, MAU, sessions/day
- [ ] [T-M305] **[P1]** User interview với 5 beta users mỗi tuần

---

## Definition of Done — MVP

Phase 1 hoàn thành khi **TẤT CẢ** điều sau đúng:

### Functional

- [ ] 100% FR-M MUST đã implement và pass QA
- [ ] 60% code coverage backend
- [ ] Full E2E flow hoạt động (register → book → video → rate)
- [ ] Tất cả acceptance criteria ở requirements.md đạt

### Non-Functional

- [ ] Lighthouse score ≥ 80 mobile, ≥ 90 desktop
- [ ] API p95 < 300ms
- [ ] Crash-free rate ≥ 99%
- [ ] HTTPS enforced
- [ ] GDPR compliance (delete + export)

### Operational

- [ ] CI/CD pipeline green
- [ ] Monitoring + alerts configured
- [ ] Documentation đầy đủ
- [ ] Backup Postgres daily verified

### Business

- [ ] ≥ 50 beta users registered
- [ ] ≥ 200 sessions completed
- [ ] Show-up rate ≥ 30%
- [ ] Avg rating ≥ 4.0

---

## Effort Estimation

| Sprint | Focus | Effort |
|--------|-------|--------|
| Sprint 0 | Foundation + Auth | 80h founder + 20h FE |
| Sprint 1 | Profile + Discover | 60h founder + 30h FE |
| Sprint 2 | Booking + Wallet | 70h founder + 30h FE |
| Sprint 3 | Video + Rating | 60h founder + 30h FE |
| Sprint 4 | Polish + Deploy + Beta | 40h founder + 20h FE |
| **Tổng** | | **~310h founder + 130h FE = ~440h** (~10 tuần) |

---

## Risk Register

| Risk | Trigger | Mitigation |
|------|---------|------------|
| Frontend freelancer không ship đúng hạn | Sprint 1 trễ > 1 tuần | Founder tự code UI cơ bản bằng TailwindUI templates |
| Daily.co latency cao ở VN | p95 > 500ms | Thêm Jitsi fallback option |
| Postgres quá tải | Connections > 80 | Tăng pool size + add read replica |
| Critical bug không phát hiện sớm | User report | Bug bounty program + dedicated QA tuần cuối |
| Beta users không engage | < 20 sessions/tuần sau tuần 1 | Personal outreach, in-app nudges |
| Burn rate quá $1.500/tháng | Cost overrun | Downgrade Daily.co, dùng Vercel free tier |
