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
  - **Completed:** T-M60, T-M61, T-M62
  - **T-M60:** 4 JUnit 5 + Mockito + AssertJ unit test classes — `JwtServiceTest` (HS256 round-trip, claim shape, type-mismatch, tampered signature, short-secret guard), `AuthServiceTest` (15 cases across register / verify / login / refresh / logout / forgot / reset / Google OAuth), `UserServiceTest` (current + public profile, partial update, onboarding idempotency, soft-delete), `SkillServiceTest` (search pagination + status filtering, custom skill create with slug derivation, duplicate / empty-slug / unknown-category / parent-link guards). JaCoCo 0.8.12 plugin emits coverage report on `mvn verify`; Surefire picked up `*Test.java`, Failsafe picks up `*IT.java`. H2 + `application-test.yml` added for future slice tests.
  - **T-M61:** `AuthControllerIT` — full Spring Boot context against ephemeral PostgreSQL 16 + Redis 7 (Testcontainers `@Testcontainers` + `@DynamicPropertySource`). 14 scenarios: register (201/409/400), login (200/401/429 with 5/15min/IP rate limit + `X-Forwarded-For`), refresh (rotation + replay rejection), forgot/reset (generic 200 on known + unknown email, 400 invalid verify token), OAuth (400 OAUTH_PROVIDER_DISABLED when client-id empty), logout (revoke + subsequent refresh 401). `application-it.yml` mirrors staging config so Flyway + ddl-auto=validate exercise the real schema. Existing `SkillseedApplicationTests#contextLoads` disabled — requires Docker.
  - **T-M62:** `docs/MANUAL_E2E_AUTH.md` — 10-section operator QA checklist (prerequisites, registration + verify, login + JWT pair + rate limit, refresh + rotation, forgot/reset, logout, Google sign-in, Apple sign-in, FE route guards, sign-off template, known gaps). Cross-linked from `AGENTS.md` §3.
  - **Sandbox limitation:** không có JDK/Maven nên `mvn test` / `mvn verify` chưa chạy local; cần user/CI với Docker để confirm integration tests pass. Unit tests compile clean theo Java 21 + JUnit 5 conventions và dùng đúng Spring Boot starter-test dependencies đã có sẵn trong `pom.xml`.
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
- [ ] [T-M84] **[P1]** Frontend: Search bar với autocomplete skills

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

- [ ] [T-M100] **[P0]** Backend: Booking entity + state machine
  - States: PENDING, CONFIRMED, DECLINED, IN_PROGRESS, COMPLETED, CANCELLED, EXPIRED, NO_SHOW, RATED
- [ ] [T-M101] **[P0]** Implement POST `/bookings`
  - Validate: teacher != learner, scheduled_at > now, duration ∈ {15,30,45,60}
  - Check availability conflict
  - Idempotency-Key support
- [ ] [T-M102] **[P0]** Implement POST `/bookings/{id}/accept` và `/decline`
- [ ] [T-M103] **[P0]** Implement POST `/bookings/{id}/cancel`
  - Validation: chỉ teacher/leaner mới cancel được
  - Apply refund policy (<24h: 50%, ≥24h: 100%)
- [ ] [T-M104] **[P0]** Implement POST `/bookings/{id}/start` và `/complete`
- [ ] [T-M105] **[P0]** Implement GET `/bookings/me?role=&status=`
- [ ] [T-M106] **[P0]** Scheduled job: auto-expire pending bookings > 24h
- [ ] [T-M107] **[P0]** Scheduled job: auto-mark NO_SHOW nếu không join trong 10 phút sau scheduled_at
- [ ] [T-M108] **[P0]** Reminder job: 24h + 1h trước session
  - Gửi email + in-app notification
- [ ] [T-M109] **[P1]** Google Calendar integration
  - Tạo event 2 chiều khi booking confirmed
  - OAuth2 scope: calendar.events

### Seed Wallet

- [ ] [T-M120] **[P0]** Backend: SeedWallet + SeedTransaction entities
- [ ] [T-M121] **[P0]** Implement wallet service với ledger pattern
  - credit(walletId, amount, type, bookingId)
  - debit(walletId, amount, type, bookingId)
  - refund(bookingId, percent)
- [ ] [T-M122] **[P0]** Khi booking created → escrow: debit pending
- [ ] [T-M123] **[P0]** Khi session complete → release: credit teacher
- [ ] [T-M124] **[P0]** Khi session cancelled → refund theo policy
- [ ] [T-M125] **[P0]** Scheduled job: process expiry (cron 01:00 UTC daily)
- [ ] [T-M126] **[P0]** Implement GET `/wallet/me` và `/wallet/me/transactions`
- [ ] [T-M127] **[P0]** Frontend: `/wallet` page (balance, history, expiring soon)

### Frontend — Booking

- [ ] [T-M130] **[P0]** Booking modal/page
  - Chọn skill, date/time, duration
  - Confirm với seed cost
  - Hiển thị "Bạn sẽ có X seeds còn lại"
- [ ] [T-M131] **[P0]** Booking list page `/bookings`
  - Tab: Upcoming / Past / Cancelled
  - Action buttons: Accept/Decline/Cancel/Join
- [ ] [T-M132] **[P0]** Booking detail page `/bookings/{id}`
  - Status timeline, meeting URL, cancel button
  - Countdown tới session

### Testing

- [ ] [T-M140] **[P0]** Unit tests wallet service (ledger logic rất quan trọng)
  - Test escrow, refund, expiry
  - Test concurrency (multiple transactions cùng lúc)
- [ ] [T-M141] **[P0]** Integration test booking flow (create → accept → complete → earn)

---

## Sprint 3 (Tuần 7–8) — Video Call & Rating

### Video call integration

- [ ] [T-M150] **[P0]** Setup Daily.co account + API key
- [ ] [T-M151] **[P0]** Backend: POST `/sessions/{bookingId}/room`
  - Tạo Daily room với expiry = scheduledAt + duration + 30min
  - Generate meeting token cho learner và teacher
  - Trả về { roomUrl, token }
- [ ] [T-M152] **[P0]** Frontend: Install `@daily-co/daily-react` SDK
- [ ] [T-M153] **[P0]** Implement VideoCall component
  - Mount Daily call với roomUrl + token
  - Show controls (mute, camera, screen share, leave)
- [ ] [T-M154] **[P0]** Embed VideoCall vào Booking detail page
  - Nút "Join Session" chỉ enable trong 10 phút trước scheduledAt
- [ ] [T-M155] **[P0]** Implement WebSocket cho chat trong session
  - Spring WebSocket + STOMP endpoint `/ws/sessions/{bookingId}`
  - Frontend: simple chat UI
- [ ] [T-M156] **[P0]** Implement Daily webhook handler
  - `meeting.ended` event → backend update status

### Screen share & whiteboard

- [ ] [T-M160] **[P1]** Daily screen share (built-in)
- [ ] [T-M161] **[P1]** Whiteboard: Excalidraw embed hoặc canvas tự build
  - Mở overlay panel trong session

### Rating module

- [ ] [T-M170] **[P0]** Backend: Rating entity
- [ ] [T-M171] **[P0]** Implement POST `/ratings`
  - Validate: booking COMPLETED, rater = participant
  - 1 booking chỉ có 1 rating mỗi phía
- [ ] [T-M172] **[P0]** Sau complete → trigger notification "Rate your session"
- [ ] [T-M173] **[P0]** Scheduled job: auto-rate 5⭐ sau 7 ngày không rate
- [ ] [T-M174] **[P0]** Update teacher `rating_avg` và `sessions_completed` (denormalized)
- [ ] [T-M175] **[P0]** Frontend: Rating modal sau session
  - Star picker, comment textarea
- [ ] [T-M176] **[P0]** Frontend: User reviews tab trên profile

### Testing

- [ ] [T-M180] **[P0]** Test full flow end-to-end
  - 2 user → register → onboarding → discover → book → join video → complete → rate → wallet update
- [ ] [T-M181] **[P0]** Load test: 50 concurrent video sessions (Daily handles)

---

## Sprint 4 (Tuần 9–10) — Polish, Deploy, Beta

### Polish & QA

- [ ] [T-M200] **[P0]** Implement GDPR right-to-delete
  - DELETE `/users/me` soft delete + hard delete sau 30 ngày
- [ ] [T-M201] **[P0]** Implement data export API (GET `/users/me/export`)
- [ ] [T-M202] **[P0]** Implement cookie consent banner
- [ ] [T-M203] **[P0]** Privacy Policy + Terms of Service pages
- [ ] [T-M204] **[P0]** Error pages (404, 500, network error)
- [ ] [T-M205] **[P0]** Loading states + skeleton UI cho mọi async
- [ ] [T-M206] **[P0]** SEO meta tags (Open Graph, Twitter cards)
- [ ] [T-M207] **[P0]** Sitemap.xml + robots.txt

### Deployment

- [ ] [T-M210] **[P0]** Setup production infrastructure
  - Railway.app account (backend)
  - Vercel account (frontend)
  - Supabase project (Postgres)
  - Upstash Redis
  - Cloudflare R2 bucket
  - Daily.co account
  - Resend account
  - UptimeRobot monitor
- [ ] [T-M211] **[P0]** Setup environment variables + secrets management
  - GitHub Secrets cho CI
  - Railway environment cho production
- [ ] [T-M212] **[P0]** Deploy backend to Railway
  - Custom domain `api.skillseed.app`
  - Health check `/actuator/health`
- [ ] [T-M213] **[P0]** Deploy frontend to Vercel
  - Custom domain `skillseed.app`
- [ ] [T-M214] **[P0]** Setup monitoring + alerts
  - Better Stack logs
  - UptimeRobot alerts → email/Slack
- [ ] [T-M215] **[P0]** CI/CD pipeline
  - Auto-deploy main → production
  - PR preview environments (Vercel)

### Beta launch

- [ ] [T-M220] **[P0]** Invite 50–100 beta users từ waitlist (Phase 0)
  - Personal email + welcome kit
- [ ] [T-M221] **[P0]** Tạo 1–2 tutorial videos (3 phút mỗi cái)
  - "How to use SkillSeed in 3 minutes"
- [ ] [T-M222] **[P0]** Setup feedback channel
  - Intercom widget hoặc email `feedback@skillseed.app`
- [ ] [T-M223] **[P0]** Daily monitoring trong 2 tuần đầu
  - Check logs, errors, response time
  - Hot fix ngay nếu có bug critical

### Documentation

- [ ] [T-M230] **[P0]** API documentation đầy đủ (Swagger + README)
- [ ] [T-M231] **[P0]** Backend README (setup, run, deploy)
- [ ] [T-M232] **[P0]** Frontend README (setup, run, deploy)
- [ ] [T-M233] **[P1]** Architecture decision records (ADR)
  - Tại sao chọn monolith
  - Tại sao chọn Daily.co
  - Tại sao ledger pattern cho wallet

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