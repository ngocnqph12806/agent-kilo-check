# Phase 1 — MVP: Design

> **Tài liệu thiết kế kỹ thuật cho MVP.**
> **Triết lý:** Kiến trúc monolithic-modular (single Spring Boot app chia module rõ ràng), sẵn sàng tách microservice ở Phase 2+.

---

## 1. High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                  MVP ARCHITECTURE (Phase 1)                           │
└──────────────────────────────────────────────────────────────────────┘

   ┌────────────────┐   ┌────────────────┐   ┌────────────────┐
   │ Web (Next.js)  │   │ Mobile Web     │   │ (Future App)   │
   │ Vercel/Netlify │   │ (responsive)   │   │                │
   └────────┬───────┘   └────────┬───────┘   └────────────────┘
            │                    │
            └──────────┬─────────┘
                       │ HTTPS / WSS
                       ▼
        ┌──────────────────────────────────────────────┐
        │     Spring Boot Monolith (Port 8080)          │
        │ ┌──────────────────────────────────────────┐  │
        │ │ Modules:                                  │  │
        │ │  • auth          • user                  │  │
        │ │  • skill         • discover              │  │
        │ │  • booking       • session               │  │
        │ │  • rating        • wallet                │  │
        │ │  • notification  • admin                 │  │
        │ └──────────────────────────────────────────┘  │
        └─────┬─────────────────┬─────────────────┬──────┘
              │                 │                 │
              ▼                 ▼                 ▼
        ┌──────────┐      ┌──────────┐     ┌──────────┐
        │PostgreSQL│      │  Redis   │     │ External │
        │ (Primary)│      │ (Cache)  │     │ Services │
        └──────────┘      └──────────┘     └──────────┘
                                                │
                                  ┌─────────────┼─────────────┐
                                  ▼             ▼             ▼
                            ┌─────────┐  ┌─────────┐  ┌─────────┐
                            │ Daily.co│  │ SendGrid│  │ Google  │
                            │ (Video) │  │ (Email) │  │ OAuth   │
                            └─────────┘  └─────────┘  └─────────┘
```

**Lý do chọn Monolith (không Microservice):**
- Team 1–2 người — microservice sẽ là gánh nặng vận hành
- Single deploy, single database — dễ debug
- Khi MAU > 10K hoặc team > 5 người → tách service (Phase 2)

---

## 2. Tech Stack

### 2.1. Backend

| Layer | Tech | Version |
|-------|------|---------|
| Language | Java | 21 (LTS) |
| Framework | Spring Boot | 3.3.x |
| Persistence | Spring Data JPA + Hibernate | 6.x |
| Migration | Flyway | 10.x |
| Validation | Jakarta Bean Validation | 3.x |
| Security | Spring Security + OAuth2 Resource Server | 6.x |
| API Docs | springdoc-openapi (Swagger UI) | 2.x |
| WebSocket | Spring WebSocket + STOMP | — |
| Cache | Spring Data Redis | 3.x |
| Scheduling | Spring Scheduling (@Scheduled) | — |
| Testing | JUnit 5 + Mockito + Testcontainers | — |
| Build | Maven | 3.9.x |

### 2.2. Frontend

| Layer | Tech |
|-------|------|
| Framework | Next.js 15 (App Router) |
| Language | TypeScript 5.x |
| UI | shadcn/ui + TailwindCSS |
| State | Zustand |
| Forms | React Hook Form + Zod |
| Data fetching | TanStack Query (React Query) |
| Realtime | socket.io-client hoặc native WebSocket |
| Video | Daily.co React SDK |
| Charts | Recharts (cho dashboard admin) |

### 2.3. Data Layer

| Loại | Tech | Use case |
|------|------|----------|
| Primary DB | PostgreSQL 16 (Supabase free hoặc RDS) | User, booking, rating, transactions |
| Cache | Redis 7 (Upstash free) | Session, hot data, rate-limit |
| Search | PostgreSQL full-text search (Phase 1) | Skills search (Qdrant sẽ có ở Phase 2) |
| Object storage | Cloudflare R2 hoặc AWS S3 | Avatar, recording (recording chưa có ở MVP) |

### 2.4. Infrastructure

| Layer | Tech | Cost |
|-------|------|------|
| Backend hosting | Railway.app hoặc Render.com | $5–25/tháng |
| Frontend hosting | Vercel | Free tier |
| Database | Supabase (Postgres + Redis) | Free tier → $25/tháng |
| Email | Resend (3K free/tháng) | Free → $20/tháng |
| Video | Daily.co (2K minutes free) | Free → $50/tháng |
| Monitoring | Better Stack (free tier) hoặc Logtail | Free |
| **Tổng** | | **~$100–150/tháng** |

### 2.5. External Services

| Service | Mục đích | Plan |
|---------|---------|------|
| Daily.co | Video call SDK + TURN server | Free → Pro ($50/mo) |
| Resend | Email transactional | Free 3K → Pro $20/mo |
| Google OAuth | Social login | Free |
| Apple Sign In | iOS social login | Free (need dev account) |

---

## 3. Database Schema (Phase 1)

> **Ghi chú:** Schema rút gọn cho MVP. Một số bảng (skill_passports, learning_pods) đã có schema nhưng chưa dùng — để sẵn cho Phase 2+.

### 3.1. ERD

```
┌─────────────┐       ┌──────────────────┐       ┌──────────────────┐
│   users     │1─────*│user_skills_      │       │user_skills_      │
│             │       │  offered         │       │  wanted          │
│             │       └──────────────────┘       └──────────────────┘
│             │1
│             │*       ┌──────────────────┐
│             │───────*│user_availability │
│             │        └──────────────────┘
│             │1
│             │*
│             │        ┌──────────────────┐        ┌──────────────────┐
│             │*───────│    bookings      │1───────*│  ratings         │
│             │        │                  │        └──────────────────┘
└─────────────┘        └────────┬─────────┘
                                │1
                                │*
                       ┌────────▼─────────┐
                       │ session_recordings│
                       │  (Phase 2)        │
                       └──────────────────┘

┌─────────────┐       ┌──────────────────┐        ┌──────────────────┐
│seed_wallets │1─────*│seed_transactions │        │     skills       │
│ (per user)  │       │ (ledger)         │*───────│  (taxonomy)      │
└─────────────┘       └──────────────────┘        └──────────────────┘
```

### 3.2. DDL chính

```sql
-- USERS
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255),
    full_name VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    bio TEXT,
    country_code CHAR(2),
    timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    languages TEXT[] DEFAULT '{en}',
    learning_style VARCHAR(20),
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'email', -- email/google/apple
    verified BOOLEAN DEFAULT false,
    verification_level SMALLINT DEFAULT 0, -- 0=email, 1=phone, 2=id, 3=liveness
    onboarding_completed BOOLEAN DEFAULT false,
    rating_avg DECIMAL(2,1) DEFAULT 0.0,
    sessions_completed INT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    deleted_at TIMESTAMPTZ  -- soft delete for GDPR
);

CREATE INDEX idx_users_country ON users(country_code) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_verified ON users(verified) WHERE verified = true AND deleted_at IS NULL;
CREATE INDEX idx_users_email_lower ON users(LOWER(email)) WHERE deleted_at IS NULL;

-- SKILLS (taxonomy)
CREATE TABLE skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50),
    is_custom BOOLEAN DEFAULT false,
    parent_id UUID REFERENCES skills(id),
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_skills_category ON skills(category);
CREATE INDEX idx_skills_slug ON skills(slug);

-- USER_SKILLS_OFFERED
CREATE TABLE user_skills_offered (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id),
    level SMALLINT NOT NULL CHECK (level BETWEEN 1 AND 5),
    years_experience INT,
    description TEXT,
    hourly_seed_rate INT DEFAULT 60, -- seeds per hour (mặc định 60 = 1/min)
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(user_id, skill_id)
);

CREATE INDEX idx_offered_skill ON user_skills_offered(skill_id) WHERE is_active = true;
CREATE INDEX idx_offered_user ON user_skills_offered(user_id);

-- USER_SKILLS_WANTED
CREATE TABLE user_skills_wanted (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id),
    priority SMALLINT DEFAULT 3 CHECK (priority BETWEEN 1 AND 5),
    target_level SMALLINT,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_wanted_user ON user_skills_wanted(user_id);
CREATE INDEX idx_wanted_skill ON user_skills_wanted(skill_id);

-- USER_AVAILABILITY
CREATE TABLE user_availability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6), -- 0=Sunday
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    timezone VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_availability_user_dow ON user_availability(user_id, day_of_week);

-- BOOKINGS
CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id UUID NOT NULL REFERENCES users(id),
    learner_id UUID NOT NULL REFERENCES users(id),
    skill_id UUID NOT NULL REFERENCES skills(id),
    scheduled_at TIMESTAMPTZ NOT NULL,
    duration_minutes SMALLINT NOT NULL CHECK (duration_minutes IN (15,30,45,60)),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    seed_amount INT NOT NULL, -- số seed escrow
    meeting_url TEXT,         -- Daily.co room URL
    recording_url TEXT,
    notes TEXT,
    cancellation_reason VARCHAR(50),
    cancelled_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT chk_no_self_booking CHECK (teacher_id != learner_id)
);

CREATE INDEX idx_bookings_teacher_scheduled ON bookings(teacher_id, scheduled_at);
CREATE INDEX idx_bookings_learner_scheduled ON bookings(learner_id, scheduled_at);
CREATE INDEX idx_bookings_status_scheduled ON bookings(status, scheduled_at);

-- RATINGS
CREATE TABLE ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
    rater_id UUID NOT NULL REFERENCES users(id),
    ratee_id UUID NOT NULL REFERENCES users(id),
    -- Learner rates teacher:
    overall_score SMALLINT CHECK (overall_score BETWEEN 1 AND 5),
    review_text TEXT,
    -- Teacher rates learner:
    helpfulness_score SMALLINT CHECK (helpfulness_score BETWEEN 1 AND 5),
    respectfulness_score SMALLINT CHECK (respectfulness_score BETWEEN 1 AND 5),
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT chk_no_self_rating CHECK (rater_id != ratee_id)
);

CREATE INDEX idx_ratings_ratee ON ratings(ratee_id);

-- SEED WALLETS
CREATE TABLE seed_wallets (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    balance_cached INT NOT NULL DEFAULT 0,
    total_earned INT NOT NULL DEFAULT 0,
    total_spent INT NOT NULL DEFAULT 0,
    last_expiring_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- SEED TRANSACTIONS (ledger)
CREATE TABLE seed_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID NOT NULL REFERENCES seed_wallets(user_id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL, -- earn/spend/grant/expire/refund
    amount INT NOT NULL, -- positive=in, negative=out
    balance_after INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'completed', -- pending/completed/cancelled
    booking_id UUID REFERENCES bookings(id),
    description TEXT,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_seed_tx_wallet_created ON seed_transactions(wallet_id, created_at DESC);
CREATE INDEX idx_seed_tx_expiring ON seed_transactions(expires_at)
    WHERE expires_at IS NOT NULL AND status = 'completed';
CREATE INDEX idx_seed_tx_booking ON seed_transactions(booking_id) WHERE booking_id IS NOT NULL;
```

### 3.3. Indexing & Performance Strategy

| Query pattern | Index |
|---------------|-------|
| Find user by email | `idx_users_email_lower` (functional) |
| List discoverable users by skill | `idx_offered_skill` + filter `is_active=true` |
| List upcoming bookings for user | `idx_bookings_teacher_scheduled` + filter `status IN ('confirmed','in_progress')` |
| Calculate wallet balance | `idx_seed_tx_wallet_created` |
| Find expiring seeds | `idx_seed_tx_expiring` |
| Search skills by name | pg_trgm GIN index (Phase 1.5) |

**Caching strategy (Redis):**
- `user:profile:{id}` — TTL 5 phút
- `user:availability:{id}` — TTL 10 phút
- `wallet:balance:{user_id}` — TTL 1 phút (invalidate on new transaction)
- `skills:taxonomy` — TTL 1 ngày

---

## 4. API Design (rút gọn MVP)

> Chi tiết đầy đủ xem `SKILLSEED_API_AND_DB.md`. Dưới đây là các endpoint MVP chính.

### 4.1. Auth Service

```
POST   /api/v1/auth/register           { email, password, fullName }
POST   /api/v1/auth/login              { email, password } -> { accessToken, refreshToken }
POST   /api/v1/auth/refresh            { refreshToken } -> { accessToken }
POST   /api/v1/auth/logout
POST   /api/v1/auth/forgot-password    { email }
POST   /api/v1/auth/reset-password     { token, newPassword }
POST   /api/v1/auth/verify-email       { token }
POST   /api/v1/auth/oauth/google       { idToken } -> { accessToken, refreshToken }
POST   /api/v1/auth/oauth/apple        { idToken } -> { accessToken, refreshToken }
```

### 4.2. User Service

```
GET    /api/v1/users/me                 -> Current user profile + Skill DNA
PATCH  /api/v1/users/me                 -> Update profile
POST   /api/v1/users/me/onboarding      -> Complete onboarding
GET    /api/v1/users/{id}               -> Public profile
POST   /api/v1/users/me/avatar          -> Upload avatar (multipart)
DELETE /api/v1/users/me                 -> GDPR right-to-delete

# Skills
GET    /api/v1/users/me/skills/offered
POST   /api/v1/users/me/skills/offered   { skillId, level, ... }
PATCH  /api/v1/users/me/skills/offered/{id}
DELETE /api/v1/users/me/skills/offered/{id}

GET    /api/v1/users/me/skills/wanted
POST   /api/v1/users/me/skills/wanted
PATCH  /api/v1/users/me/skills/wanted/{id}
DELETE /api/v1/users/me/skills/wanted/{id}

# Availability
GET    /api/v1/users/me/availability
PUT    /api/v1/users/me/availability     [bulk replace]
```

### 4.3. Skills Service

```
GET    /api/v1/skills?query=java&category=tech&limit=20
GET    /api/v1/skills/{id}
POST   /api/v1/skills                   [user creates custom]
```

### 4.4. Discover Service

```
GET    /api/v1/discover?skill=java&language=en&minRating=4&page=0&size=20
GET    /api/v1/discover/search?q=python
```

### 4.5. Booking Service

```
POST   /api/v1/bookings                  { teacherId, skillId, scheduledAt, durationMinutes }
GET    /api/v1/bookings/{id}
GET    /api/v1/bookings/me?role=teacher|learner&status=upcoming|past
POST   /api/v1/bookings/{id}/accept
POST   /api/v1/bookings/{id}/decline     { reason }
POST   /api/v1/bookings/{id}/cancel      { reason }
POST   /api/v1/bookings/{id}/start       -> status = in_progress
POST   /api/v1/bookings/{id}/complete    -> status = completed
```

### 4.6. Session Service (Video)

```
POST   /api/v1/sessions/{bookingId}/room -> Create Daily.co room, return URL
WS     /ws/sessions/{bookingId}          -> Signaling + chat
POST   /api/v1/sessions/{bookingId}/report-issue
```

### 4.7. Rating Service

```
POST   /api/v1/ratings                   { bookingId, overallScore, reviewText }
GET    /api/v1/users/{id}/ratings?page=0&size=10
```

### 4.8. Wallet Service

```
GET    /api/v1/wallet/me                 -> { balance, totalEarned, totalSpent, expiringSoon }
GET    /api/v1/wallet/me/transactions?page=0&size=20
```

---

## 5. Module Boundaries (trong monolith)

### 5.1. Package structure

```
com.skillseed
├── SkillseedApplication.java
├── shared/                  # Common utilities
│   ├── config/              # Spring config
│   ├── exception/           # Global exception handler
│   ├── security/            # JWT filter, OAuth2 config
│   ├── audit/               # Audit logging
│   └── util/
├── auth/                    # Auth module
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── dto/
├── user/                    # User & Profile module
├── skill/                   # Skills taxonomy
├── discover/                # Discover & Search
├── booking/                 # Booking & lifecycle
├── session/                 # Video call orchestration
├── rating/                  # Rating & reviews
├── wallet/                  # Seed wallet
├── notification/            # Email + in-app notif
└── admin/                   # Admin endpoints (Phase 1.5)
```

### 5.2. Module communication

- **Trong monolith:** modules gọi trực tiếp qua Spring service injection (giữ transactional boundary rõ ràng).
- **Tới external service:** qua HTTP client (Daily.co, Google Calendar) hoặc SDK.
- **Cross-module events:** dùng `ApplicationEventPublisher` (Phase 1) → Kafka (Phase 2).

### 5.3. Transactional boundaries

Ví dụ flow "Complete session + release escrow + trigger rating":

```
@Transactional
public void completeSession(UUID bookingId) {
    Booking b = bookingRepo.findById(bookingId);
    b.setStatus(COMPLETED);
    bookingRepo.save(b);
    
    // 1. Release escrow: tạo earn transaction cho teacher
    walletService.credit(b.getTeacherId(), b.getSeedAmount(), 
                         TransactionType.EARN, b.getId());
    
    // 2. Publish event để gửi rating form
    eventPublisher.publishEvent(new SessionCompletedEvent(b));
}
```

---

## 6. Seed Wallet — Ledger Pattern (chi tiết)

### 6.1. Tại sao dùng ledger?

- **Audit trail:** Mọi giao dịch đều có lịch sử — phục vụ dispute.
- **Expiry tracking:** Mỗi transaction có `expires_at` riêng → tính được balance chính xác (trừ phần sắp hết hạn).
- **Idempotency:** Transaction unique theo (booking_id, type) → không trừ 2 lần.

### 6.2. Balance calculation

```sql
-- Balance hiện tại (không bao gồm expired):
SELECT COALESCE(SUM(amount), 0)
FROM seed_transactions
WHERE wallet_id = :userId
  AND status = 'completed'
  AND (expires_at IS NULL OR expires_at > now());
```

**Cache:** Lưu `balance_cached` trong `seed_wallets`. Khi có transaction mới → update cache + invalidate Redis.

### 6.3. Escrow flow (booking → complete)

```
1. POST /bookings:
   - Check wallet balance >= seed_amount
   - Tạo transaction type=spend, status=pending, amount=-seed_amount
   - balance_cached -= seed_amount
   - balance_after = balance_cached

2. Session COMPLETE:
   - Tìm transaction pending với booking_id
   - Set status = completed
   - Tạo transaction type=earn, status=completed, amount=+seed_amount
     expires_at = now() + 6 months
   - Update teacher balance_cached += seed_amount

3. Session CANCELLED (trước 24h):
   - Set transaction pending → cancelled
   - balance_cached += seed_amount (refund)

4. Session CANCELLED (trong 24h):
   - Set transaction pending → cancelled
   - Tạo transaction refund amount=+(seed_amount * 0.5)
   - balance_cached += seed_amount * 0.5
```

### 6.4. Expiry job

```java
@Scheduled(cron = "0 0 1 * * *") // 1AM UTC daily
public void processSeedExpiry() {
    // Tìm completed transactions có expires_at < now()
    List<SeedTransaction> expiring = txRepo.findByExpiresAtBeforeAndStatus(
        Instant.now(), TxStatus.COMPLETED);
    
    for (SeedTransaction tx : expiring) {
        // Tạo expire transaction (không expire tx gốc, audit trail)
        SeedTransaction expire = new SeedTransaction();
        expire.setType(EXPIRE);
        expire.setAmount(-tx.getAmount());
        expire.setWalletId(tx.getWalletId());
        expire.setBookingId(tx.getBookingId());
        expire.setDescription("Expired seed from tx " + tx.getId());
        txRepo.save(expire);
        
        // Update wallet balance
        walletService.updateBalanceCache(tx.getWalletId());
    }
}
```

---

## 7. Video Call (Daily.co Integration)

### 7.1. Lý do chọn Daily.co

- **Nhanh:** SDK React mạch lạc, embed được vào Next.js trong vài giờ.
- **Có TURN:** Daily cung cấp TURN server (giải quyết NAT traversal).
- **Free tier:** 2.000 phút/tháng free cho MVP.
- **Recording API:** Sẵn sàng cho Phase 2 (transcription).

### 7.2. Flow

```
1. Teacher/Leaner click "Join Session":
   Frontend → POST /api/v1/sessions/{bookingId}/room
   Backend → Daily.co REST API: tạo room với exp = scheduledAt + duration + 30min
   Backend → return { roomUrl, token }

2. Frontend mount Daily call component với roomUrl + token
   (Token = meeting token cho learner/teacher role)

3. Trong session:
   - screen share qua Daily API
   - Chat qua WebSocket tới backend (Daily có chat built-in)
   - Whiteboard: dùng canvas overlay (Phase 1.5)

4. Session kết thúc:
   - Daily.co trigger webhook "meeting.ended" → backend
   - Backend update booking status = in_progress → completed
   - Frontend show rating form
```

### 7.3. Security

- Daily.co room là private (chỉ user có token mới vào).
- Token generate qua REST API với `user_id` và `is_owner` flags.
- Token expire sau `scheduledAt + duration + 30 min`.

---

## 8. Booking Workflow — State Machine

```
                  ┌────────────┐
                  │  PENDING   │ ←── POST /bookings
                  └─────┬──────┘
                        │ (24h timeout → EXPIRED)
        ┌───────────────┼───────────────┐
        │ accept        │ decline       │ cancel
        ▼               ▼               ▼
  ┌───────────┐   ┌───────────┐   ┌───────────┐
  │ CONFIRMED │   │ DECLINED  │   │ CANCELLED │
  └─────┬─────┘   └───────────┘   └─────┬─────┘
        │ (cancel < 24h → 50% refund)         │
        │ (cancel ≥ 24h → 100% refund)        │
        ▼                                    ▼
  ┌─────────────┐                    ┌───────────┐
  │ IN_PROGRESS │ ←── start          │ CANCELLED │
  └─────┬───────┘                    └───────────┘
        │ complete
        ▼
  ┌───────────┐
  │ COMPLETED │ → ratings → RATED
  └───────────┘

Side states:
  EXPIRED (timeout 24h)
  NO_SHOW (learner/teacher không join trong 10 phút)
```

**Implementation:** Spring State Machine (Phase 1.5) hoặc enum + service method (MVP).

---

## 9. Authentication & Authorization

### 9.1. JWT structure

```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "verification_level": 1,
  "iat": 1694000000,
  "exp": 1694000900
}
```

- Access token: 15 phút, HS256.
- Refresh token: 30 ngày, lưu httpOnly cookie (Secure, SameSite=Strict).

### 9.2. Spring Security config

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    SecurityFilterChain chain(HttpSecurity http) {
        http
            .csrf(c -> c.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(...)))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

### 9.3. Verification levels (mapping actions)

| Level | Có thể làm |
|-------|-----------|
| 0 (email) | Tạo profile, xem discover |
| 1 (+phone) | Book session |
| 2 (+gov ID) | Dạy & nhận seeds (Phase 2) |
| 3 (+liveness) | Skill Passport (Phase 2) |

---

## 10. Deployment Architecture (MVP)

```
┌──────────────────────────────────────────────────────────────┐
│                    PRODUCTION STACK (Phase 1)                  │
└──────────────────────────────────────────────────────────────┘

Vercel (Frontend)
   └─ Next.js app, auto-deploy from main branch
   └─ Domain: skillseed.app

Railway.app (Backend)
   └─ 1 Spring Boot instance (auto-scale 1-2)
   └─ Connected to Postgres + Redis

Supabase (Postgres + Auth storage)
   └─ Postgres 16 (managed)
   └─ Daily backup

Upstash (Redis)
   └─ Serverless Redis, free tier

Daily.co (Video)
   └─ Cloud-hosted rooms

Resend (Email)
   └─ Transactional API

Better Stack / Logtail
   └─ Log aggregation + alerts

UptimeRobot
   └─ Health check ping mỗi 5 phút
```

**CI/CD:**
- GitHub Actions: run tests + build → push image → deploy to Railway
- Branch protection: main phải pass CI mới merge được

---

## 11. Out of Scope (Design — sẽ có ở Phase 2+)

- ❌ AI matching engine (chỉ filter-based matching)
- ❌ Multi-criteria rating (chỉ 1 overall + comment)
- ❌ Liveness check
- ❌ Government ID verification
- ❌ Skill Passport
- ❌ AI Co-Pilot trong session
- ❌ Mobile native app
- ❌ Push notification
- ❌ Stripe payment
- ❌ Kafka (Phase 1 dùng in-memory event)
- ❌ Qdrant vector DB (chỉ dùng SQL search)
- ❌ Microservices (monolithic modular)

---

## 12. Open Questions

| # | Câu hỏi | Owner | Deadline |
|---|----------|-------|----------|
| 1 | Daily.co hay Jitsi cho video? (Jitsi free hơn nhưng UX kém hơn) | Founder | Tuần 2 |
| 2 | Có cần Apple Sign In từ MVP không? (cần dev account $99) | Founder | Tuần 2 |
| 3 | Whiteboard nên tự build hay dùng third-party (Excalidraw embed)? | Founder | Tuần 4 |
| 4 | Có nên ghi âm session từ MVP không? (cần consent flow phức tạp) | Founder | Phase 1.5 |