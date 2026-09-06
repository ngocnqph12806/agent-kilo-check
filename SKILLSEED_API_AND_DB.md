# SkillSeed — API Design & Database Indexing Strategy

> **Mục đích:** Đặc tả REST API contract đầy đủ cho 5 service + chiến lược indexing/caching/optimization để scale.

> **Last updated:** 2026-09-06

---

## Mục lục

### Phần A — API Design
1. [Nguyên tắc thiết kế API](#1-nguyên-tắc-thiết-kế-api)
2. [Versioning & Naming Convention](#2-versioning--naming-convention)
3. [Standard Response & Error Codes](#3-standard-response--error-codes)
4. [Common Patterns (Pagination, Filter, Sort)](#4-common-patterns-pagination-filter-sort)
5. [Idempotency & Rate Limiting](#5-idempotency--rate-limiting)
6. [REST API Specification — user-service](#6-rest-api-specification--user-service)
7. [REST API Specification — matching-service](#7-rest-api-specification--matching-service)
8. [REST API Specification — booking-service](#8-rest-api-specification--booking-service)
9. [REST API Specification — session-service](#9-rest-api-specification--session-service)
10. [REST API Specification — wallet-service](#10-rest-api-specification--wallet-service)
11. [WebSocket & Real-time Contracts](#11-websocket--real-time-contracts)
12. [Event-driven (Kafka) Contracts](#12-event-driven-kafka-contracts)
13. [Internal Service-to-Service Auth](#13-internal-service-to-service-auth)

### Phần B — Database & Performance
14. [PostgreSQL Configuration](#14-postgresql-configuration)
15. [Indexing Strategy — user-service](#15-indexing-strategy--user-service)
16. [Indexing Strategy — booking-service](#16-indexing-strategy--booking-service)
17. [Indexing Strategy — wallet-service](#17-indexing-strategy--wallet-service)
18. [Query Optimization Patterns](#18-query-optimization-patterns)
19. [Redis Caching Strategy](#19-redis-caching-strategy)
20. [Database Partitioning](#20-database-partitioning)
21. [Read Replica & Connection Pool](#21-read-replica--connection-pool)
22. [Slow Query Monitoring](#22-slow-query-monitoring)

---

## 1. Nguyên tắc thiết kế API

### 1.1. RESTful Conventions (đã custom cho SkillSeed)

| Nguyên tắc | Áp dụng |
|-----------|---------|
| Resource-based URL | `/api/v1/users/{id}` thay vì `/getUser?id=` |
| HTTP verbs | GET (read), POST (create), PUT (full update), PATCH (partial), DELETE |
| Plural nouns | `/users`, `/bookings`, `/skills` |
| Stateless | Mỗi request có JWT token đầy đủ |
| HATEOAS (optional) | Link tới related resources trong response khi cần |
| Idempotent operations | PUT, DELETE idempotent; POST có idempotency key |
| Async for long ops | Dùng 202 Accepted + webhook/poll cho AI matching |
| Standard error format | Mọi error trả về cùng `ApiResponse.error` shape |

### 1.2. URL Structure

```
https://api.skillseed.app/{service}/{version}/{resource}

Ví dụ:
POST   https://api.skillseed.app/user/v1/users/register
GET    https://api.skillseed.app/user/v1/users/{id}
GET    https://api.skillseed.app/matching/v1/matches?skill=java&limit=10
POST   https://api.skillseed.app/booking/v1/bookings
```

Hoặc single domain với path:
```
POST   https://api.skillseed.app/api/v1/users/register
GET    https://api.skillseed.app/api/v1/matches?skill=java&limit=10
```

→ **Chọn single domain** vì đơn giản hơn cho client, dễ rate-limit tập trung.

---

## 2. Versioning & Naming Convention

### 2.1. Versioning Strategy

| Strategy | Khi nào dùng |
|----------|--------------|
| **URL path versioning** (`/v1/`) | ✅ Chọn — đơn giản, dễ debug, client thấy rõ |
| Header versioning | Không dùng — khó cho mobile |
| Media type versioning | Không dùng — overkill cho MVP |

**Quy tắc:**
- `/v1/` ổn định tối thiểu 12 tháng
- Breaking changes → `/v2/`
- Sunsetting policy: v1 cũ chạy song song 6 tháng sau khi ra v2
- Deprecation notice trong `Sunset` header (RFC 8594)

### 2.2. Naming Convention

| Element | Convention | Ví dụ |
|---------|------------|-------|
| URL paths | kebab-case | `/user-sessions`, `/skill-categories` |
| Query params | camelCase hoặc snake_case | `pageSize`, `user_id` → **chọn camelCase** |
| JSON fields | camelCase | `firstName`, `createdAt` |
| Boolean fields | is/has prefix | `isVerified`, `hasCompleted` |
| Date fields | suffix At | `createdAt`, `scheduledAt` |
| ID fields | suffix Id | `userId`, `bookingId` |
| List fields | suffix s hoặc List | `tags`, `skillIds`, `items` |
| Enum values | SCREAMING_SNAKE | `BOOKING_STATUS_CONFIRMED` |

### 2.3. Pagination Format

```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 1523,
  "totalPages": 77,
  "first": true,
  "last": false,
  "hasNext": true,
  "hasPrevious": false
}
```

### 2.4. Cursor vs Offset

| Use case | Strategy |
|----------|----------|
| Admin/listing có filter → cần total count | Offset (`page`, `size`) |
| Infinite scroll (Discover feed, chat history) | Cursor (`cursor`, `limit`) |
| Real-time updates | Cursor |

---

## 3. Standard Response & Error Codes

### 3.1. Success Response

```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    ...
  },
  "error": null,
  "timestamp": "2026-09-06T10:30:00Z"
}
```

### 3.2. Error Response

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "User with id abc-123 not found",
    "details": {
      "resource": "User",
      "id": "abc-123"
    }
  },
  "timestamp": "2026-09-06T10:30:00Z"
}
```

### 3.3. Error Code Catalog

| HTTP Status | Error Code | Mô tả | Retry? |
|-------------|-----------|--------|--------|
| 400 | `VALIDATION_ERROR` | Request body invalid | No |
| 400 | `BAD_REQUEST` | Malformed request | No |
| 401 | `UNAUTHENTICATED` | Missing/invalid JWT | No |
| 403 | `FORBIDDEN` | Không đủ quyền | No |
| 403 | `VERIFICATION_REQUIRED` | Cần verify level cao hơn | No |
| 404 | `RESOURCE_NOT_FOUND` | Resource không tồn tại | No |
| 409 | `CONFLICT` | Duplicate email, double booking | No |
| 409 | `INSUFFICIENT_BALANCE` | Wallet không đủ seed | No |
| 422 | `BUSINESS_RULE_VIOLATED` | Vi phạm rule nghiệp vụ | No |
| 429 | `RATE_LIMIT_EXCEEDED` | Quá nhiều request | Yes (sau backoff) |
| 500 | `INTERNAL_ERROR` | Server error | Yes |
| 502 | `UPSTREAM_ERROR` | Service khác lỗi | Yes |
| 503 | `SERVICE_UNAVAILABLE` | Maintenance/overload | Yes |
| 504 | `TIMEOUT` | Quá thời gian | Yes |

---

## 4. Common Patterns (Pagination, Filter, Sort)

### 4.1. Pagination Query

```
GET /api/v1/users?page=0&size=20&sort=createdAt,desc
```

| Param | Type | Default | Range |
|-------|------|---------|-------|
| `page` | int | 0 | 0+ |
| `size` | int | 20 | 1-100 |
| `sort` | string | varies | `field,direction` |

### 4.2. Filtering

```
GET /api/v1/users?country=VN&verified=true&skill=java
```

- Operators: `eq`, `ne`, `gt`, `gte`, `lt`, `lte`, `in`, `like`
- Multiple values: `?skill=java&skill=python`
- Date range: `?createdAfter=2026-01-01&createdBefore=2026-12-31`

### 4.3. Sorting

```
GET /api/v1/matches?sort=score,desc&sort=createdAt,desc
```

- Multi-field sort: comma-separated trong nhiều `sort` params
- Field phải được index hoặc có whitelist

### 4.4. Field Selection (Sparse Fieldsets)

```
GET /api/v1/users/123?fields=id,fullName,avatarUrl
```

→ Giảm bandwidth cho mobile.

### 4.5. Embedding Related Resources

```
GET /api/v1/bookings/abc?include=teacher,learner,rating
```

→ Tránh N+1 phía client.

---

## 5. Idempotency & Rate Limiting

### 5.1. Idempotency Keys

Áp dụng cho: POST endpoints tạo resource quan trọng (booking, payment, transfer).

```
POST /api/v1/bookings
Headers:
  Idempotency-Key: <uuid-v4>
  Content-Type: application/json
Body:
  {...}
```

**Flow:**
1. Client generate UUID v4, gửi kèm request
2. Server lưu key + response vào Redis (TTL 24h)
3. Nếu key trùng → trả lại response đã lưu, không xử lý lại
4. Tránh duplicate booking khi client retry

### 5.2. Rate Limiting

| Tier | Limit | Window |
|------|-------|--------|
| Anonymous | 60 req | 1 phút |
| Free user | 200 req | 1 phút |
| Premium | 1000 req | 1 phút |
| B2B API key | 10.000 req | 1 phút |

**Response headers:**
```
X-RateLimit-Limit: 200
X-RateLimit-Remaining: 195
X-RateLimit-Reset: 1694000060
```

**Khi vượt limit → 429 với `Retry-After` header.**

Implementation: Redis + sliding window algorithm (Bucket4j hoặc custom).

---

## 6. REST API Specification — user-service

**Base URL:** `/api/v1`  
**Port:** 8081

### 6.1. Authentication

```
POST /auth/register
POST /auth/login
POST /auth/refresh
POST /auth/logout
POST /auth/forgot-password
POST /auth/reset-password
POST /auth/verify-email
POST /auth/verify-phone
```

**POST /auth/register**
```yaml
Request:
  body:
    email: string (required, email)
    password: string (required, min 8)
    fullName: string (required, 2-100)
    phone: string (optional, E.164)
    countryCode: string (optional, ISO-3166-1 alpha-2)
    timezone: string (optional, IANA tz)
    inviteCode: string (optional, referral)

Response 201:
  data:
    id: uuid
    email: string
    fullName: string
    verificationLevel: 0
    createdAt: datetime
  headers:
    Location: /api/v1/users/{id}

Errors:
  409 EMAIL_ALREADY_EXISTS
  400 VALIDATION_ERROR
```

**POST /auth/login**
```yaml
Request:
  body:
    email: string (required)
    password: string (required)

Response 200:
  data:
    accessToken: string (JWT, 15 min)
    refreshToken: string (opaque, 30 days)
    expiresIn: 900
    user:
      id: uuid
      email: string
      fullName: string
      avatarUrl: string

Errors:
  401 INVALID_CREDENTIALS
  403 ACCOUNT_LOCKED (sau 5 lần fail)
  403 EMAIL_NOT_VERIFIED
```

### 6.2. User Profile

```
GET    /users/me                       # Current user
GET    /users/{id}                     # Public profile
PUT    /users/me                       # Update profile (full)
PATCH  /users/me                       # Update profile (partial)
DELETE /users/me                       # Soft delete account
GET    /users/me/stats                 # Teaching/learning stats
```

**GET /users/{id}**
```yaml
Query params:
  include: string[] (skillsOffered, skillsWanted, ratings, passports)
  fields: string[] (sparse fieldset)

Response 200:
  data:
    id: uuid
    fullName: string
    avatarUrl: string
    bio: string
    countryCode: string
    timezone: string
    languages: string[]
    verified: boolean
    verificationLevel: int (0-4)
    createdAt: datetime
    skillsOffered: [...]      # if include
    skillsWanted: [...]       # if include
    stats:                    # if include
      totalSessionsTaught: int
      totalSessionsLearned: int
      avgRating: decimal
      responseRate: decimal

Cache: 5 min public, 0 private
```

### 6.3. Skill Management

```
GET    /skills                         # List taxonomy
GET    /skills/{id}
GET    /skills/search?q=java&limit=20  # Autocomplete
POST   /skills                         # Submit custom skill (admin/verified only)

GET    /users/me/skills/offered
POST   /users/me/skills/offered
PATCH  /users/me/skills/offered/{id}
DELETE /users/me/skills/offered/{id}

GET    /users/me/skills/wanted
POST   /users/me/skills/wanted
DELETE /users/me/skills/wanted/{id}
```

**POST /users/me/skills/offered**
```yaml
Request:
  body:
    skillId: uuid (required, OR use skillSlug for new taxonomy item)
    level: int (required, 1-5)
    yearsExperience: int (optional)
    description: string (optional, max 500)
    hourlySeedRate: int (optional, default 60, range 30-1000)

Response 201:
  data:
    id: uuid
    skillId: uuid
    skillName: string
    level: int
    isVerified: false
    createdAt: datetime

Errors:
  409 SKILL_ALREADY_OFFERED
  422 VERIFICATION_REQUIRED (level 2+ needed)
```

### 6.4. Availability

```
GET    /users/me/availability
PUT    /users/me/availability          # Replace all
POST   /users/me/availability/slots   # Add specific slot
DELETE /users/me/availability/slots/{id}

GET    /users/{id}/availability       # Public read (for matching)
```

### 6.5. Verification Flow

```
POST   /users/me/verification/email/confirm        # Send OTP
POST   /users/me/verification/email/verify         # {otp}
POST   /users/me/verification/phone/send           # Send SMS
POST   /users/me/verification/phone/verify         # {otp, phone}
POST   /users/me/verification/government-id        # Upload ID
POST   /users/me/verification/liveness             # Selfie + liveness check
```

### 6.6. Internal Endpoints (service-to-service)

```
GET    /internal/users/{id}                        # Service auth required
GET    /internal/users/{id}/skill-dna              # For matching-service
POST   /internal/users/{id}/skills-offered/sync    # From matching feedback
```

---

## 7. REST API Specification — matching-service

**Base URL:** `/api/v1`  
**Port:** 8082

### 7.1. Discovery

```
GET    /matches/discover                           # Top matches cho user hiện tại
GET    /matches/teachers                           # Search teachers
GET    /matches/teachers/{userId}                  # Specific teacher profile
POST   /matches/recommend                          # Async request với custom query
```

**GET /matches/discover**
```yaml
Query params:
  skill: string (slug, required)              # Kỹ năng muốn học
  limit: int (default 20, max 50)
  cursor: string (optional, for pagination)
  filters:
    minRating: decimal (default 3.5)
    maxHourlyRate: int (seed)
    timezone: string (default user's)
    language: string
    verifiedOnly: boolean (default true)

Response 200:
  data:
    matches:
      - userId: uuid
        fullName: string
        avatarUrl: string
        countryCode: string
        score: decimal (0-1)
        matchReasons: [...]                  # Top 3 reasons
        aiExplanation: string                # Generated by LLM
        skills:
          - skillId: uuid
            name: string
            level: int
            yearsExperience: int
        rating:
          avg: decimal
          count: int
        hourlySeedRate: int
        nextAvailableSlot: datetime
    nextCursor: string (if more results)
    totalCandidates: int

Cache: 1 hour per (userId, skillSlug)
```

**POST /matches/recommend** (cho advanced filter, async)
```yaml
Request:
  body:
    targetSkills: string[] (slugs)
    targetLevel: int
    preferredLanguages: string[]
    timezone: string
    availability: object (weekly schedule)
    excludedUserIds: uuid[]
    weights:                              # Custom scoring weights
      skillMatch: decimal (default 0.5)
      scheduleOverlap: decimal (default 0.2)
      rating: decimal (default 0.2)
      language: decimal (default 0.1)

Response 202:
  data:
    jobId: uuid
    status: PROCESSING
    estimatedSeconds: 10

# Sau đó poll:
GET /matches/recommend/{jobId}
→ status: COMPLETED | PROCESSING | FAILED
→ result: same as /discover response
```

### 7.2. Compatibility Score

```
GET    /matches/compatibility/{userId1}/{userId2}    # Debug, internal use
```

---

## 8. REST API Specification — booking-service

**Base URL:** `/api/v1`  
**Port:** 8083

### 8.1. Booking Flow

```
GET    /bookings/availability/{userId}              # Teacher's open slots
POST   /bookings                                     # Create booking
GET    /bookings/{id}
PATCH  /bookings/{id}                                # Reschedule, notes
DELETE /bookings/{id}                                # Cancel

GET    /bookings/me?role=teacher|learner&status=...  # My bookings
```

**GET /bookings/availability/{userId}**
```yaml
Query params:
  from: datetime (required, ISO-8601)
  to: datetime (required, max range 30 days)
  durationMinutes: int (15|30|45|60, default 60)

Response 200:
  data:
    slots:
      - startAt: datetime
        endAt: datetime
        durationMinutes: int
    timezone: string (teacher's)
```

**POST /bookings**
```yaml
Headers:
  Idempotency-Key: uuid (required)

Request:
  body:
    teacherId: uuid (required)
    skillId: uuid (required)
    scheduledAt: datetime (required, ISO-8601)
    durationMinutes: int (required, 15|30|45|60)
    notes: string (optional, max 500)
    learnerGoals: string[] (optional)

Response 201:
  data:
    id: uuid
    status: PENDING  # chờ teacher confirm
    teacher:
      id: uuid
      fullName: string
      avatarUrl: string
    learner:
      id: uuid
      fullName: string
      avatarUrl: string
    skill:
      id: uuid
      name: string
    scheduledAt: datetime
    durationMinutes: int
    seedAmount: int                # Sẽ charge khi confirm
    meetingUrl: null               # Có khi confirm
    expiresAt: datetime            # Pending expiry (24h)
    createdAt: datetime

Side effects (Kafka):
  - Publish BookingCreatedEvent → wallet-service hold seed

Errors:
  409 SLOT_UNAVAILABLE
  409 INSUFFICIENT_BALANCE
  422 TEACHER_NOT_VERIFIED
  422 CANNOT_BOOK_SELF
```

### 8.2. Confirmation & Lifecycle

```
POST   /bookings/{id}/confirm       # Teacher accepts
POST   /bookings/{id}/decline       # Teacher rejects (with reason)
POST   /bookings/{id}/start         # Both click "Start" → meeting URL active
POST   /bookings/{id}/complete      # Either marks complete
POST   /bookings/{id}/no-show       # Report no-show (admin review)

POST   /bookings/{id}/cancel
  body:
    reason: enum (TEACHER_UNAVAILABLE | LEARNER_UNAVAILABLE | 
                  TECHNICAL_ISSUE | OTHER)
    message: string
```

### 8.3. Listing & Filtering

```
GET    /bookings/me
Query params:
  role: enum (teacher | learner)        # required
  status: enum[] (PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED)
  from: datetime
  to: datetime
  skillId: uuid
  page, size, sort

Response 200:
  data: PageResponse<BookingSummary>
```

---

## 9. REST API Specification — session-service

**Base URL:** `/api/v1`  
**Port:** 8084 (HTTP), 8085 (WebSocket)

### 9.1. Session Management

```
POST   /sessions/{bookingId}/start    # Generate meeting credentials
GET    /sessions/{id}                # Session metadata
POST   /sessions/{id}/end
POST   /sessions/{id}/recording      # Toggle recording (opt-in)
GET    /sessions/{id}/recording      # Get recording URL (if available)
GET    /sessions/{id}/transcript     # AI-generated transcript
GET    /sessions/{id}/notes          # AI-generated notes
```

**POST /sessions/{bookingId}/start**
```yaml
Request:
  body:
    enableRecording: boolean (default false)
    enableTranscription: boolean (default true)
    enableAiCoPilot: boolean (default true)

Response 200:
  data:
    sessionId: uuid
    bookingId: uuid
    signalingUrl: wss://signaling.skillseed.app/sessions/{id}
    iceServers:
      - urls: stun:stun.skillseed.app:3478
      - urls: turn:turn.skillseed.app:3478
        username: string
        credential: string
    aiCoPilot:
      enabled: true
      features: [NOTES, ACTION_ITEMS, SUGGESTIONS]
    expiresAt: datetime

Auth: Both teacher and learner must call this (returns same sessionId)
```

### 9.2. Real-time Events (WebSocket)

Xem mục 11.

### 9.3. Post-session

```
POST   /sessions/{id}/rate           # Submit rating
GET    /sessions/{id}/feedback       # Get feedback summary
```

**POST /sessions/{id}/rate**
```yaml
Request:
  body:
    knowledgeScore: int (1-5)
    clarityScore: int (1-5)
    helpfulnessScore: int (1-5)
    punctualityScore: int (1-5)
    friendlinessScore: int (1-5)
    reviewText: string (optional, max 1000)
    wouldRecommend: boolean

Response 200:
  data:
    ratingId: uuid
    overallScore: decimal (auto-calc)
    skillPassportIssued: boolean    # Nếu đủ điều kiện

Side effects:
  - Update user reputation
  - Trigger Skill Passport issuance (nếu eligible)
  - Wallet: complete credit/debit
```

---

## 10. REST API Specification — wallet-service

**Base URL:** `/api/v1`  
**Port:** 8086

### 10.1. Wallet Operations

```
GET    /wallets/me                   # Current balance + history summary
GET    /wallets/me/transactions      # Transaction history
GET    /wallets/me/transactions/{id}
```

**GET /wallets/me**
```yaml
Response 200:
  data:
    balance: int                    # Current available
    pendingHold: int               # Đang hold cho booking pending
    totalEarned: int
    totalSpent: int
    totalExpired: int
    expiringSoon:                   # Sắp expire trong 30 ngày
      amount: int
      oldestExpiresAt: datetime
    tier: enum (BRONZE | SILVER | GOLD | PLATINUM)
    monthlyStats:
      earned: int
      spent: int
      month: string (YYYY-MM)
```

**GET /wallets/me/transactions**
```yaml
Query params:
  type: enum[] (EARN, SPEND, EXPIRE, GRANT, REFUND)
  from: datetime
  to: datetime
  page, size, sort

Response 200:
  data:
    content:
      - id: uuid
        type: EARN
        amount: 60                   # positive=earn, negative=spend
        description: "Taught Java - Booking abc"
        bookingId: uuid
        expiresAt: datetime          # null if already expired/spent
        createdAt: datetime
```

### 10.2. Seed Packs (Purchases)

```
GET    /seed-packs                   # Available packs
POST   /seed-packs/purchase          # Buy via Stripe
GET    /seed-packs/purchase/{id}     # Check status
```

**POST /seed-packs/purchase**
```yaml
Request:
  body:
    packId: string (small|medium|large|xl)
    paymentMethodId: string          # Stripe payment method

Response 200:
  data:
    purchaseId: uuid
    status: SUCCEEDED | PROCESSING | FAILED
    seedsCredited: int
    newBalance: int
    payment:
      amount: decimal
      currency: string
      receiptUrl: string

Side effects:
  - Stripe charge
  - Wallet GRANT transaction (no expiry for purchased seeds)
```

### 10.3. Internal Endpoints

```
POST   /internal/wallets/credit       # Service auth required
  body:
    userId: uuid
    amount: int
    reason: string
    bookingId: uuid
    expiresInDays: int (default 180)

POST   /internal/wallets/debit
  body:
    userId: uuid
    amount: int
    reason: string
    bookingId: uuid

POST   /internal/wallets/hold         # Reserve seed for pending booking
POST   /internal/wallets/release      # Release hold
POST   /internal/wallets/capture      # Convert hold → actual debit
```

---

## 11. WebSocket & Real-time Contracts

### 11.1. Signaling Channel (WebRTC)

**URL:** `wss://signaling.skillseed.app/sessions/{sessionId}`

**Auth:** JWT trong query hoặc `Sec-WebSocket-Protocol` header.

**Message types (JSON):**

#### Client → Server
```json
// 1. Join session
{
  "type": "JOIN",
  "userId": "uuid",
  "role": "teacher|learner"
}

// 2. WebRTC offer
{
  "type": "OFFER",
  "sdp": "v=0\r\no=-..."
}

// 3. WebRTC answer
{
  "type": "ANSWER",
  "sdp": "v=0\r\no=-..."
}

// 4. ICE candidate
{
  "type": "ICE_CANDIDATE",
  "candidate": {
    "candidate": "candidate:...",
    "sdpMid": "0",
    "sdpMLineIndex": 0
  }
}

// 5. Chat message
{
  "type": "CHAT",
  "message": "Hello",
  "replyTo": null
}

// 6. Reaction
{
  "type": "REACTION",
  "emoji": "👍",
  "timestamp": 1234567890
}

// 7. Media state
{
  "type": "MEDIA_STATE",
  "audioEnabled": true,
  "videoEnabled": false,
  "screenSharing": false
}

// 8. AI co-pilot request
{
  "type": "AI_REQUEST",
  "requestType": "EXPLAIN_CONCEPT|GENERATE_QUIZ|SUMMARIZE",
  "context": "string"
}
```

#### Server → Client
```json
// 1. Peer joined
{ "type": "PEER_JOINED", "userId": "uuid", "role": "..." }

// 2. Peer left
{ "type": "PEER_LEFT", "userId": "uuid" }

// 3. WebRTC offer/answer (relayed)
{ "type": "OFFER", "from": "uuid", "sdp": "..." }
{ "type": "ANSWER", "from": "uuid", "sdp": "..." }

// 4. ICE candidate (relayed)
{ "type": "ICE_CANDIDATE", "from": "uuid", "candidate": {...} }

// 5. Chat broadcast
{ "type": "CHAT", "from": "uuid", "message": "...", "timestamp": ... }

// 6. Reaction broadcast
{ "type": "REACTION", "from": "uuid", "emoji": "🎉", "timestamp": ... }

// 7. Media state change (peer)
{ "type": "PEER_MEDIA_STATE", "from": "uuid", "videoEnabled": false }

// 8. AI response (streaming)
{
  "type": "AI_RESPONSE",
  "requestType": "EXPLAIN_CONCEPT",
  "content": "This concept...",
  "isPartial": true,
  "streamId": "abc"
}

// 9. Session events
{ "type": "RECORDING_STARTED" }
{ "type": "RECORDING_STOPPED" }
{ "type": "TIME_WARNING", "remainingMinutes": 5 }
{ "type": "SESSION_ENDING", "remainingSeconds": 60 }
```

### 11.2. Notification Channel

**URL:** `wss://notifications.skillseed.app/user/{userId}`

**Server → Client events:**
```json
{ "type": "BOOKING_CREATED", "bookingId": "uuid", "data": {...} }
{ "type": "BOOKING_CONFIRMED", "bookingId": "uuid" }
{ "type": "BOOKING_CANCELLED", "bookingId": "uuid", "reason": "..." }
{ "type": "MATCH_FOUND", "userId": "uuid", "skill": "..." }
{ "type": "RATING_RECEIVED", "ratingId": "uuid", "score": 4.8 }
{ "type": "PASSPORT_ISSUED", "passportId": "uuid", "skill": "..." }
{ "type": "SEED_EXPIRING_SOON", "amount": 30, "expiresAt": "..." }
{ "type": "NEW_MESSAGE", "fromUserId": "uuid", "preview": "..." }
```

### 11.3. Heartbeat & Reconnection

```
Client gửi mỗi 30s:
{ "type": "PING", "timestamp": 1694000000 }

Server trả:
{ "type": "PONG", "timestamp": 1694000000 }

Nếu client miss 2 heartbeats → reconnect với exponential backoff.
```

---

## 12. Event-driven (Kafka) Contracts

### 12.1. Topic Naming Convention

```
{domain}.{entity}.{action}

Ví dụ:
- user.profile.updated
- booking.created
- booking.confirmed
- booking.cancelled
- session.started
- session.completed
- wallet.transaction.created
- reputation.passport.issued
- matching.embedding.indexed
```

### 12.2. Event Schema (CloudEvents format)

```json
{
  "specversion": "1.0",
  "type": "com.skillseed.booking.created.v1",
  "source": "booking-service",
  "id": "uuid",
  "time": "2026-09-06T10:30:00Z",
  "datacontenttype": "application/json",
  "subject": "booking/abc-123",
  "data": {
    "bookingId": "uuid",
    "teacherId": "uuid",
    "learnerId": "uuid",
    "skillId": "uuid",
    "scheduledAt": "2026-09-10T15:00:00Z",
    "durationMinutes": 60,
    "seedAmount": 60,
    "status": "PENDING"
  }
}
```

### 12.3. Key Topics & Subscribers

| Topic | Producer | Consumers | Purpose |
|-------|----------|-----------|---------|
| `user.created` | user-service | matching-service | Index user vào vector DB |
| `user.skills.updated` | user-service | matching-service, reputation-service | Update embeddings, badges |
| `booking.created` | booking-service | wallet-service, notification-service | Hold seeds, notify teacher |
| `booking.confirmed` | booking-service | session-service, notification-service | Pre-create session room |
| `booking.completed` | booking-service | session-service, wallet-service, reputation-service | Finalize payment, issue passport |
| `booking.cancelled` | booking-service | wallet-service, notification-service | Release hold, refund |
| `session.completed` | session-service | ai-service | Generate transcript, notes |
| `wallet.transaction.created` | wallet-service | analytics-service | Update metrics |
| `reputation.passport.issued` | reputation-service | user-service, notification-service | Update user profile, notify |
| `matching.embedding.indexed` | matching-service | analytics-service | Track coverage |

### 12.4. Consumer Group Naming

```
{consumer-app}-{purpose}

Ví dụ:
- wallet-service-booking-handler
- notification-service-booking-handler
- reputation-service-session-handler
```

### 12.5. Idempotency cho Consumers

Mỗi event có `id` (UUID). Consumer lưu `processed_event_ids` vào Redis (TTL 7 ngày). Trước khi xử lý, check xem đã xử lý chưa.

---

## 13. Internal Service-to-Service Auth

### 13.1. Service Token

Mỗi service có `service.id` và `service.secret` (lưu trong Vault).

```java
// Service A gọi Service B
@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/internal/users/{id}")
    UserInternalResponse getUser(@PathVariable UUID id);
    
    // Feign interceptor tự động thêm:
    // Headers: X-Service-Id, X-Service-Signature, X-Request-Id
}
```

### 13.2. Signature Verification

```
X-Service-Id: booking-service
X-Service-Signature: HMAC-SHA256(secret, timestamp + method + path + body)
X-Timestamp: 1694000000
X-Request-Id: uuid

Service B verify:
- Timestamp trong vòng 5 phút (chống replay)
- Signature khớp với secret của service A
- Service A có permission cho endpoint này (RBAC)
```

### 13.3. mTLS (Production)

Production dùng mTLS qua service mesh (Istio/Linkerd) thay vì HMAC.

---

# PHẦN B — DATABASE & PERFORMANCE

## 14. PostgreSQL Configuration

### 14.1. Recommended `postgresql.conf` cho production

```ini
# Connections
max_connections = 200
superuser_reserved_connections = 3

# Memory (cho server 16GB RAM)
shared_buffers = 4GB
effective_cache_size = 12GB
work_mem = 64MB              # Per query
maintenance_work_mem = 1GB
huge_pages = try

# WAL
wal_buffers = 64MB
max_wal_size = 4GB
min_wal_size = 1GB
checkpoint_completion_target = 0.9

# Query Planner
random_page_cost = 1.1       # SSD
effective_io_concurrency = 200

# Logging
log_min_duration_statement = 500  # Log slow queries >500ms
log_checkpoints = on
log_connections = on
log_disconnections = on
log_lock_waits = on
log_temp_files = 0
log_autovacuum_min_duration = 0

# Statistics
default_statistics_target = 100  # Tăng độ chính xác query planner
```

### 14.2. Connection Pool (HikariCP)

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20        # Per service instance
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: skillseed-pool
      leak-detection-threshold: 30000
      data-source-properties:
        reWriteBatchedInserts: true
        prepareThreshold: 5
```

**Rule of thumb:** `pool_size = (core_count * 2) + effective_spindle_count`
→ 8 cores SSD → 18 connections → round to 20.

---

## 15. Indexing Strategy — user-service

### 15.1. Users Table

**Query patterns:**
1. Login: `WHERE email = ?` (1 query / login)
2. Phone lookup: `WHERE phone = ?` (rare)
3. Discover search: `WHERE country_code = ? AND verified = true AND id NOT IN (...)`
4. Leaderboard: `WHERE verified = true ORDER BY created_at DESC`
5. Admin search: `WHERE full_name ILIKE '%...%'`

**Current indexes:**
```sql
-- Email - unique, primary lookup
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- Phone - optional but unique
CREATE UNIQUE INDEX idx_users_phone ON users(phone) WHERE phone IS NOT NULL;

-- Discover query (partial index for verified users only)
CREATE INDEX idx_users_country_verified 
ON users(country_code, created_at DESC) 
WHERE verified = true;

-- Admin search
CREATE INDEX idx_users_fullname_trgm 
ON users USING gin (full_name gin_trgm_ops);

-- Soft delete filter (nếu có)
CREATE INDEX idx_users_active ON users(id) WHERE deleted_at IS NULL;
```

**Thêm sau:**
```sql
-- Khi cần text search bio (post-launch)
CREATE INDEX idx_users_bio_trgm ON users USING gin (bio gin_trgm_ops);

-- Timezone cho matching
CREATE INDEX idx_users_tz_verified ON users(timezone, verification_level) 
WHERE verified = true;
```

**EXPLAIN ANALYZE check:**
```sql
EXPLAIN ANALYZE
SELECT id, full_name, avatar_url
FROM users
WHERE country_code = 'VN' 
  AND verified = true
  AND id <> '00000000-0000-0000-0000-000000000000'
ORDER BY created_at DESC
LIMIT 20;

-- Expected: Index Scan using idx_users_country_verified
--         → Subquery Scan (exclude user)
--         → Limit
```

### 15.2. Skills & User Skills Tables

**Skills table (taxonomy):**
```sql
-- Slug lookup - primary
CREATE UNIQUE INDEX idx_skills_slug ON skills(slug);

-- Category browse
CREATE INDEX idx_skills_category ON skills(category) WHERE category IS NOT NULL;

-- Name search autocomplete
CREATE INDEX idx_skills_name_trgm ON skills USING gin (name gin_trgm_ops);
```

**user_skills_offered:**
```sql
-- User's own skills list
CREATE INDEX idx_uso_user ON user_skills_offered(user_id);

-- Find teachers by skill
CREATE INDEX idx_uso_skill_level 
ON user_skills_offered(skill_id, level DESC, years_experience DESC);

-- Find verified teachers (for premium search)
CREATE INDEX idx_uso_skill_verified 
ON user_skills_offered(skill_id) 
WHERE is_verified = true;
```

**user_skills_wanted:**
```sql
-- User's wanted list
CREATE INDEX idx_usw_user ON user_skills_wanted(user_id);

-- Find learners by skill (for matching reverse)
CREATE INDEX idx_usw_skill ON user_skills_wanted(skill_id);
```

### 15.3. Availability Table

```sql
-- Teacher's schedule lookup
CREATE INDEX idx_avail_user_dow 
ON user_availability(user_id, day_of_week);

-- Find teachers available at specific time
CREATE INDEX idx_avail_global 
ON user_availability(day_of_week, start_time, end_time);

-- Range query: who is free between X and Y
CREATE INDEX idx_avail_time_range 
ON user_availability USING gist (
    tsrange(
        ('2000-01-01'::date + start_time)::timestamp,
        ('2000-01-01'::date + end_time)::timestamp
    )
);
```

### 15.4. Composite Index Best Practices

| Quy tắc | Ví dụ |
|---------|-------|
| **Equality trước, range sau** | `WHERE country = ? AND created_at > ?` → `(country, created_at)` |
| **ORDER BY column ở cuối** | `WHERE user_id = ? ORDER BY created_at DESC` → `(user_id, created_at DESC)` |
| **Covering index cho query thường xuyên** | Include columns để tránh lookup bảng chính |
| **Partial index cho filter hay dùng** | `WHERE verified = true AND deleted_at IS NULL` |

**Ví dụ covering index:**
```sql
CREATE INDEX idx_users_listing 
ON users(country_code, created_at DESC) 
INCLUDE (id, full_name, avatar_url)
WHERE verified = true;
-- Có thể serve query "list verified users in VN, newest first, with avatar" 
-- chỉ bằng index scan, không cần table lookup.
```

---

## 16. Indexing Strategy — booking-service

### 16.1. Bookings Table

**Query patterns:**
1. Teacher's upcoming bookings: `WHERE teacher_id = ? AND scheduled_at > now() ORDER BY scheduled_at`
2. Learner's history: `WHERE learner_id = ? ORDER BY scheduled_at DESC`
3. Status filter: `WHERE status = 'PENDING' AND scheduled_at < now() - 24h` (timeout)
4. Date range: `WHERE scheduled_at BETWEEN ? AND ?`
5. Skill stats: `WHERE skill_id = ? AND status = 'COMPLETED' GROUP BY date_trunc('month', scheduled_at)`

**Indexes:**
```sql
-- Composite cho teacher dashboard
CREATE INDEX idx_bookings_teacher_upcoming 
ON bookings(teacher_id, scheduled_at) 
WHERE status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS');

-- Composite cho learner dashboard
CREATE INDEX idx_bookings_learner_history 
ON bookings(learner_id, scheduled_at DESC);

-- Status + time cho cleanup jobs
CREATE INDEX idx_bookings_pending_timeout 
ON bookings(scheduled_at) 
WHERE status = 'PENDING';

-- Date range scan
CREATE INDEX idx_bookings_scheduled_range 
ON bookings USING brin (scheduled_at);

-- Skill analytics (covering)
CREATE INDEX idx_bookings_skill_completed 
ON bookings(skill_id, scheduled_at DESC) 
INCLUDE (duration_minutes, seed_amount)
WHERE status = 'COMPLETED';

-- Avoid double-booking
CREATE UNIQUE INDEX idx_bookings_teacher_slot 
ON bookings(teacher_id, scheduled_at) 
WHERE status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS');

-- Lookup by booking_id (PK is enough, no extra index needed)
```

### 16.2. Ratings Table

**Query patterns:**
1. User's received ratings: `WHERE ratee_id = ? ORDER BY created_at DESC`
2. Aggregate stats: `WHERE ratee_id = ? AND created_at > ?`
3. Skill-specific rating: `WHERE ratee_id = ? AND booking_id IN (SELECT id FROM bookings WHERE skill_id = ?)`

**Indexes:**
```sql
-- User's ratings list
CREATE INDEX idx_ratings_ratee 
ON ratings(ratee_id, created_at DESC);

-- User's given ratings
CREATE INDEX idx_ratings_rater 
ON ratings(rater_id, created_at DESC);

-- Booking lookup (unique already due to FK)
CREATE UNIQUE INDEX idx_ratings_booking ON ratings(booking_id);

-- Aggregation by skill + month
CREATE INDEX idx_ratings_ratee_created 
ON ratings(ratee_id, created_at) 
INCLUDE (overall_score, knowledge_score, clarity_score, helpfulness_score, punctuality_score, friendliness_score);
-- Covering index cho avg rating queries
```

---

## 17. Indexing Strategy — wallet-service

### 17.1. Seed Wallets Table

```sql
-- PK lookup (sufficient)
CREATE UNIQUE INDEX idx_wallets_user ON seed_wallets(user_id);

-- Admin search by balance tier
CREATE INDEX idx_wallets_balance 
ON seed_wallets(balance DESC) 
WHERE balance > 1000;
```

### 17.2. Seed Transactions Table (Ledger)

**⚠️ Đây là table hot nhất — cần optimize kỹ.**

**Query patterns:**
1. User's transactions: `WHERE wallet_id = ? ORDER BY created_at DESC LIMIT 20`
2. Daily expiry job: `WHERE expires_at < now() AND type = 'EARN' AND amount > 0`
3. Admin audit: `WHERE booking_id = ?`
4. Aggregate user stats: `WHERE wallet_id = ? AND created_at > ? GROUP BY type`

**Indexes:**
```sql
-- User's transaction history (covering)
CREATE INDEX idx_seed_tx_user_recent 
ON seed_transactions(wallet_id, created_at DESC) 
INCLUDE (type, amount, description, expires_at);

-- Expiry job scan
CREATE INDEX idx_seed_tx_expiring 
ON seed_transactions(expires_at) 
WHERE type = 'EARN' AND expired = false;

-- Audit by booking
CREATE INDEX idx_seed_tx_booking 
ON seed_transactions(booking_id) 
WHERE booking_id IS NOT NULL;

-- Monthly stats aggregation
CREATE INDEX idx_seed_tx_user_type_date 
ON seed_transactions(wallet_id, type, created_at);

-- Partial index for active (non-expired) earning transactions
CREATE INDEX idx_seed_tx_active_earnings 
ON seed_transactions(wallet_id, expires_at) 
WHERE type = 'EARN' AND expired = false;
```

### 17.3. Partitioning Strategy (khi table > 100M rows)

```sql
-- Partition by created_at monthly
CREATE TABLE seed_transactions (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    wallet_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount INT NOT NULL,
    booking_id UUID,
    description TEXT,
    expires_at TIMESTAMPTZ,
    expired BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE seed_transactions_2026_09 PARTITION OF seed_transactions
    FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');
CREATE TABLE seed_transactions_2026_10 PARTITION OF seed_transactions
    FOR VALUES FROM ('2026-10-01') TO ('2026-11-01');
-- ... tạo sẵn 12 tháng

-- Auto create partition function (cron job)
CREATE OR REPLACE FUNCTION create_next_month_partition()
RETURNS void AS $$
DECLARE
    next_month DATE := date_trunc('month', now() + interval '1 month');
    partition_name TEXT := 'seed_transactions_' || to_char(next_month, 'YYYY_MM');
BEGIN
    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I PARTITION OF seed_transactions 
         FOR VALUES FROM (%L) TO (%L)',
        partition_name, next_month, next_month + interval '1 month'
    );
END;
$$ LANGUAGE plpgsql;
```

---

## 18. Query Optimization Patterns

### 18.1. N+1 Problem

**❌ Anti-pattern:**
```java
List<Booking> bookings = bookingRepo.findAll();
for (Booking b : bookings) {
    User teacher = userClient.getUser(b.getTeacherId()); // N+1!
    Skill skill = skillClient.getSkill(b.getSkillId());   // N+1!
}
```

**✅ Optimized:**
```java
// 1. Batch fetch
List<UUID> teacherIds = bookings.stream().map(Booking::getTeacherId).toList();
Map<UUID, User> teachers = userClient.getUsers(teacherIds); // 1 call

// 2. JPA EntityGraph
@EntityGraph(attributePaths = {"teacher", "skill"})
@Query("SELECT b FROM Booking b WHERE b.id = :id")
Optional<Booking> findByIdWithDetails(@Param("id") UUID id);

// 3. JOOQ hoặc native SQL với JOIN
@Query(value = """
    SELECT b.*, u.full_name as teacher_name, s.name as skill_name
    FROM bookings b
    JOIN users u ON u.id = b.teacher_id
    JOIN skills s ON s.id = b.skill_id
    WHERE b.learner_id = :userId
    ORDER BY b.scheduled_at DESC
    LIMIT 20
    """, nativeQuery = true)
List<BookingWithDetails> findUserBookings(@Param("userId") UUID userId);
```

### 18.2. Pagination Performance

**❌ OFFSET lớn rất chậm:**
```sql
SELECT * FROM bookings 
WHERE learner_id = '...' 
ORDER BY scheduled_at DESC 
LIMIT 20 OFFSET 10000;  -- Scan 10.020 rows!
```

**✅ Cursor-based pagination:**
```sql
-- First page
SELECT * FROM bookings 
WHERE learner_id = '...' 
ORDER BY scheduled_at DESC 
LIMIT 20;
-- cursor = last scheduled_at

-- Next page
SELECT * FROM bookings 
WHERE learner_id = '...' 
  AND scheduled_at < :cursor_timestamp
ORDER BY scheduled_at DESC 
LIMIT 20;
```

### 18.3. Avoid SELECT *

```sql
-- ❌ Trả về tất cả columns kể cả TEXT lớn
SELECT * FROM users WHERE id = ?;

-- ✅ Chỉ lấy columns cần
SELECT id, full_name, avatar_url FROM users WHERE id = ?;
```

Hoặc dùng projection:
```java
public interface UserSummary {
    UUID getId();
    String getFullName();
    String getAvatarUrl();
}

@Query("SELECT u.id as id, u.fullName as fullName, u.avatarUrl as avatarUrl FROM User u WHERE u.id = :id")
Optional<UserSummary> findSummary(@Param("id") UUID id);
```

### 18.4. Batch Operations

```java
@Transactional
public void creditMultiple(List<CreditRequest> requests) {
    // Single batch insert, không loop
    transactionRepository.saveAll(
        requests.stream().map(this::toTransaction).toList()
    );
    
    // Hoặc JDBC batch
    jdbcTemplate.batchUpdate(
        "INSERT INTO seed_transactions (...) VALUES (?, ?, ?, ?)",
        requests,
        100, // batch size
        (ps, req) -> {
            ps.setObject(1, req.getWalletId());
            ps.setString(2, req.getType().name());
            ps.setInt(3, req.getAmount());
            ps.setString(4, req.getDescription());
        }
    );
}
```

### 18.5. Aggregations

**Rating average:**
```sql
-- ❌ Slow với data lớn
SELECT AVG(overall_score) FROM ratings WHERE ratee_id = ?;

-- ✅ Materialized view refresh mỗi giờ
CREATE MATERIALIZED VIEW mv_user_rating_stats AS
SELECT 
    ratee_id,
    COUNT(*) as rating_count,
    AVG(overall_score) as avg_overall,
    AVG(knowledge_score) as avg_knowledge,
    -- ...
FROM ratings
WHERE created_at > now() - interval '1 year'
GROUP BY ratee_id;

CREATE UNIQUE INDEX ON mv_user_rating_stats(ratee_id);

-- Refresh job:
REFRESH MATERIALIZED VIEW CONCURRENTLY mv_user_rating_stats;
```

### 18.6. Read-Heavy Patterns

**Cache aside pattern (xem mục 19)** — đặc biệt cho:
- User profile public
- Skill taxonomy
- Rating stats

---

## 19. Redis Caching Strategy

### 19.1. Cache Levels

```
L1: In-process (Caffeine) - 30s, max 1000 entries, single instance
L2: Redis - 5-60 min, shared across instances
L3: PostgreSQL - source of truth
```

### 19.2. Caching Patterns by Use Case

| Data | TTL | Pattern | Invalidation |
|------|-----|---------|--------------|
| User profile (public) | 5 min | Cache-Aside | On profile update (pub/sub) |
| Skill taxonomy | 1 hour | Cache-Aside (write-through) | Manual invalidation |
| User rating stats | 30 min | Cache-Aside | On new rating (event) |
| Match recommendations | 1 hour | Cache-Aside | On profile update |
| Booking availability | 1 min | Cache-Aside | On booking create |
| Wallet balance | **NEVER** | N/A | Real-time only |
| Seed transactions | 5 min | Cache-Aside | On new transaction |
| Session JWT | 15 min | Write-Through | TTL matches JWT expiry |
| Rate limit counters | 1 min | Write-Through | TTL auto |

### 19.3. Implementation Example (Spring Cache + Redis)

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .disableCachingNullValues()
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        // Per-cache TTL
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("userProfile", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigs.put("skillTaxonomy", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigs.put("ratingStats", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigs.put("matches", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        return RedisCacheManager.builder(cf)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigs)
            .transactionAware()
            .build();
    }
}

@Service
public class UserService {
    
    @Cacheable(value = "userProfile", key = "#id", 
               unless = "#result == null")
    public UserResponse getUserPublicProfile(UUID id) {
        User user = userRepository.findById(id).orElseThrow();
        return mapToPublicResponse(user);
    }
    
    @CacheEvict(value = "userProfile", key = "#userId")
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest req) {
        // update logic
    }
}
```

### 19.4. Distributed Cache Coherence

**Vấn đề:** Cache ở instance A không biết instance B vừa update.

**Giải pháp: Pub/Sub Invalidation**

```java
@Component
public class CacheInvalidationPublisher {
    
    private final RedisTemplate<String, String> redis;
    
    public void publish(String cacheName, String key) {
        redis.convertAndSend("cache:invalidate", 
            cacheName + ":" + key);
    }
}

@Component
public class CacheInvalidationSubscriber implements MessageListener {
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        // Parse "userProfile:uuid-here"
        // Evict from local cache (Caffeine)
    }
}
```

### 19.5. Anti-patterns cần tránh

| ❌ Đừng | ✅ Làm thế này |
|--------|---------------|
| Cache wallet balance (cần real-time) | Query DB trực tiếp |
| Cache toàn bộ list data | Cache từng item, dùng Redis SET cho list IDs |
| Cache không có TTL | Luôn set TTL (memory leak nếu quên) |
| Cache data không stable | Cache read-only data (taxonomy, stats) |
| Cache update trong transaction | Cache evict AFTER commit (use `@TransactionalEventListener`) |

```java
// ✅ Đúng pattern: invalidate after commit
@Transactional
public UserResponse updateProfile(UUID id, UpdateProfileRequest req) {
    User user = userRepository.findById(id).orElseThrow();
    user.setFullName(req.fullName());
    userRepository.save(user);
    return mapToResponse(user);
}

// Cache evict ở method riêng
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onProfileUpdated(ProfileUpdatedEvent event) {
    cacheManager.getCache("userProfile").evict(event.getUserId());
    // Publish invalidation to other instances
    invalidationPublisher.publish("userProfile", event.getUserId().toString());
}
```

### 19.6. Redis Memory Budget

Cho 1GB Redis:
- 200K user profiles @ 5KB each = 1GB ❌ (quá nhiều)
- → Set max memory policy: `allkeys-lru`
- → Chỉ cache hot data (top 20% users tạo 80% traffic)

```ini
# redis.conf
maxmemory 1gb
maxmemory-policy allkeys-lru
maxmemory-samples 5
```

---

## 20. Database Partitioning

### 20.1. Khi nào cần partition?

| Table | Partition | Threshold |
|-------|-----------|-----------|
| `bookings` | Theo `scheduled_at` (monthly) | > 10M rows |
| `seed_transactions` | Theo `created_at` (monthly) | > 50M rows |
| `ratings` | Không cần (small) | < 5M rows |
| `users` | Không cần | < 10M rows |
| `audit_logs` | Theo `created_at` (daily) | > 100M rows |

### 20.2. Partition Strategy cho Bookings

```sql
CREATE TABLE bookings (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    teacher_id UUID NOT NULL,
    learner_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    scheduled_at TIMESTAMPTZ NOT NULL,
    duration_minutes SMALLINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    -- ...
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (id, scheduled_at)  -- Partition key phải trong PK
) PARTITION BY RANGE (scheduled_at);

-- Quarterly partitions
CREATE TABLE bookings_2026_q3 PARTITION OF bookings
    FOR VALUES FROM ('2026-07-01') TO ('2026-10-01');
CREATE TABLE bookings_2026_q4 PARTITION OF bookings
    FOR VALUES FROM ('2026-10-01') TO ('2027-01-01');

-- Old data archive
CREATE TABLE bookings_2025_archive PARTITION OF bookings
    FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

-- Indexes on parent (auto-propagate to partitions)
CREATE INDEX idx_bookings_teacher_scheduled 
ON bookings(teacher_id, scheduled_at);
```

**Query planner sẽ tự động partition pruning:**
```sql
-- Chỉ scan 1 partition
SELECT * FROM bookings 
WHERE teacher_id = ? 
  AND scheduled_at >= '2026-09-01' 
  AND scheduled_at < '2026-10-01';
```

### 20.3. Auto-create Partition Cron

```sql
-- Function
CREATE OR REPLACE FUNCTION ensure_booking_partition(target_date DATE)
RETURNS void AS $$
DECLARE
    partition_start DATE := date_trunc('quarter', target_date);
    partition_end DATE := partition_start + interval '3 months';
    partition_name TEXT := 'bookings_' || to_char(partition_start, 'YYYY_q') || 
        CASE extract(quarter from partition_start)
            WHEN 1 THEN 'q1' WHEN 2 THEN 'q2'
            WHEN 3 THEN 'q3' WHEN 4 THEN 'q4'
        END;
BEGIN
    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I PARTITION OF bookings 
         FOR VALUES FROM (%L) TO (%L)',
        partition_name, partition_start, partition_end
    );
END;
$$ LANGUAGE plpgsql;

-- Run daily via pg_cron
SELECT cron.schedule('create-booking-partition', '0 0 1 * *', 
    'SELECT ensure_booking_partition(now() + interval ''2 months'')');
```

---

## 21. Read Replica & Connection Pool

### 21.1. Khi nào cần read replica?

| DAU | DB load | Cần replica? |
|-----|---------|--------------|
| < 1.000 | Thấp | ❌ |
| 1.000 - 10.000 | Trung bình | ⚠️ Monitor first |
| 10.000 - 100.000 | Cao | ✅ Cần |
| > 100.000 | Rất cao | ✅ Cần 2-3 replicas |

### 21.2. Routing Logic (Java)

```java
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {
    
    @Bean
    @Primary
    public DataSource routingDataSource(
            @Qualifier("writeDataSource") DataSource write,
            @Qualifier("readDataSource") DataSource read) {
        
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("WRITE", write);
        targetDataSources.put("READ", read);
        
        AbstractRoutingDataSource routing = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return TransactionSynchronizationManager
                    .isCurrentTransactionReadOnly() ? "READ" : "WRITE";
            }
        };
        routing.setTargetDataSources(targetDataSources);
        routing.setDefaultTargetDataSource(write);
        return routing;
    }
}

// Sử dụng
@Service
@Transactional(readOnly = true)  // → routes to replica
public List<Booking> getUserBookings(UUID userId) {
    return bookingRepo.findByLearnerId(userId);
}

@Service
@Transactional  // → routes to master
public Booking createBooking(CreateBookingRequest req) {
    return bookingRepo.save(new Booking(...));
}
```

### 21.3. Replication Lag Handling

```java
// Khi vừa write, cần read lại ngay → force write
@Service
public class BookingService {
    
    @Transactional
    public Booking createBooking(CreateBookingRequest req) {
        Booking b = bookingRepo.save(...);
        // ...
        return b;
    }
    
    public Booking getBookingFresh(UUID id) {
        // Force read from master (vừa write, replica có thể chưa có)
        return transactionTemplate.execute(status -> {
            status.setReadOnly(false);
            return bookingRepo.findById(id).orElseThrow();
        });
    }
}
```

---

## 22. Slow Query Monitoring

### 22.1. PostgreSQL `auto_explain`

```sql
-- Enable auto_explain cho slow queries
LOAD 'auto_explain';

SET auto_explain.log_min_duration = '500ms';  -- Log queries >500ms
SET auto_explain.log_analyze = true;
SET auto_explain.log_buffers = true;
SET auto_explain.log_format = 'json';
SET auto_explain.log_nested_statements = true;
```

### 22.2. Slow Query Log Analysis

```sql
-- Top 20 slowest queries trong 24h qua
SELECT 
    substring(query, 1, 100) as query_preview,
    calls,
    mean_exec_time as avg_ms,
    total_exec_time as total_ms,
    rows
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 20;
```

### 22.3. Application-level (Spring AOP)

```java
@Aspect
@Component
public class QueryPerformanceAspect {
    
    private static final Logger log = LoggerFactory.getLogger("PERFORMANCE");
    private static final long SLOW_THRESHOLD_MS = 500;
    
    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
    public Object logQueryPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;
        
        if (elapsed > SLOW_THRESHOLD_MS) {
            log.warn("SLOW QUERY: {} took {}ms | Args: {}",
                joinPoint.getSignature().getName(),
                elapsed,
                Arrays.toString(joinPoint.getArgs()));
        }
        
        return result;
    }
}
```

### 22.4. Metrics Export (Micrometer + Prometheus)

```yaml
management:
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
        jdbc.connections.active: true
        hibernate.query.execution: true
      percentiles:
        http.server.requests: 0.5, 0.95, 0.99
        hibernate.query.execution: 0.95, 0.99

# Custom metrics
@Timed(value = "booking.create", percentiles = {0.5, 0.95, 0.99})
public Booking createBooking(...) { ... }
```

### 22.5. Alert Rules (Grafana)

```yaml
alerts:
  - name: SlowQueryP95
    condition: histogram_quantile(0.95, hibernate_query_execution_seconds) > 1
    for: 5m
    severity: warning
    
  - name: SlowQueryP99
    condition: histogram_quantile(0.99, hibernate_query_execution_seconds) > 3
    for: 2m
    severity: critical
    
  - name: ConnectionPoolExhaustion
    condition: hikaricp_connections_active / hikaricp_connections_max > 0.9
    for: 1m
    severity: critical
    
  - name: CacheHitRateLow
    condition: redis_keyspace_hits / (redis_keyspace_hits + redis_keyspace_misses) < 0.7
    for: 10m
    severity: warning
```

---

## 📚 Tổng kết & Best Practices

### API Design Checklist
- [x] Versioned URLs (`/v1/`)
- [x] Idempotency keys cho POST quan trọng
- [x] Standard error format với error codes
- [x] Pagination chuẩn (offset + cursor)
- [x] Rate limiting với headers rõ ràng
- [x] Sparse fieldsets cho mobile
- [x] Service-to-service auth (HMAC hoặc mTLS)
- [x] WebSocket contract đầy đủ với heartbeat
- [x] Kafka events theo CloudEvents spec
- [x] OpenAPI auto-generated docs

### Database Performance Checklist
- [x] Indexes cho mọi query pattern chính
- [x] Composite index: equality → range → sort
- [x] Partial index cho filter thường gặp (`WHERE verified = true`)
- [x] Covering index cho query quan trọng
- [x] Tránh SELECT *, dùng projection
- [x] Tránh OFFSET lớn, dùng cursor pagination
- [x] Materialized view cho aggregation
- [x] Partition cho table > 10M rows
- [x] Connection pool sized đúng
- [x] Read replica khi DAU > 10K
- [x] Cache invalidation qua pub/sub
- [x] Slow query monitoring + alerting

---

## Tham chiếu nhanh

| Topic | Section |
|-------|---------|
| Standard response shape | §3.1 |
| Error code list | §3.3 |
| Idempotency keys | §5.1 |
| Rate limit tiers | §5.2 |
| User API | §6 |
| Matching API | §7 |
| Booking API | §8 |
| Session WebSocket | §11.1 |
| Kafka events | §12.2 |
| Index patterns | §15-17 |
| Cache patterns | §19.3 |
| Partitioning | §20 |
| Read replica routing | §21.2 |
| Slow query monitoring | §22 |

---

> **Liên hệ với tài liệu khác:**  
> - **Schema**: `SKILLSEED.md` §8 (Database Design)  
> - **Code**: `SKILLSEED_CODE_SKELETON.md` (Spring Boot skeleton)  
> - **Roadmap**: `SKILLSEED.md` §16 (Implementation phases)

> **Tác giả:** SkillSeed Team — Phiên bản 1.0 — 2026-09-06
