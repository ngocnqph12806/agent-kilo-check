# Phase 2 — AI & Polish: Design

> **Tài liệu thiết kế kỹ thuật cho giai đoạn AI & Polish.**
> **Triết lý:** Tách AI Matching & Co-Pilot thành microservice riêng (vì cần Python sidecar + GPU cho embeddings LLM). Phần còn lại vẫn monolith.

---

## 1. High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│               PHASE 2 ARCHITECTURE (Hybrid Monolith + Microservice)  │
└──────────────────────────────────────────────────────────────────────┘

   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
   │ Web (PWA)    │   │ Mobile Web   │   │ (Native App │
   │ Vercel       │   │              │   │  Phase 3)   │
   └──────┬───────┘   └──────┬───────┘   └──────┬───────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             │ HTTPS / WSS
                             ▼
        ┌────────────────────────────────────────────┐
        │   API Gateway (Kong hoặc Spring Cloud GW)  │
        └─────┬───────────────┬───────────────┬──────┘
              │               │               │
              ▼               ▼               ▼
   ┌─────────────────┐ ┌─────────────┐ ┌─────────────────┐
   │ Spring Boot     │ │ matching-   │ │ ai-copilot-     │
   │ Monolith        │ │ service     │ │ service         │
   │ (core API)      │ │ (Python)    │ │ (Java + Python) │
   └────────┬────────┘ └──────┬──────┘ └──────┬──────────┘
            │                 │                │
            ├─────────────────┴────────────────┤
            ▼                                  ▼
   ┌──────────────────┐                ┌──────────────────┐
   │ PostgreSQL       │                │ Qdrant           │
   │ Redis            │                │ (Vector DB)      │
   │ Kafka (new)      │                └──────────────────┘
   └─────────┬────────┘                        │
             │                                 │
             ▼                                 ▼
   ┌──────────────────────────────────────────────────────┐
   │ External Services:                                    │
   │  OpenAI API (LLM) | AssemblyAI (Transcription)       │
   │  Twilio (SMS) | Onfido (KYC) | PostHog (Analytics)   │
   │  Daily.co (Video) | Resend (Email) | VAPID (Push)    │
   └──────────────────────────────────────────────────────┘
```

**Thay đổi so với Phase 1:**
- Tách `matching-service` (Python) và `ai-copilot-service` (Java + Python LLM wrapper) thành microservice.
- Thêm Kafka cho event-driven (giảm coupling giữa monolith ↔ AI services).
- Thêm Qdrant cho vector search.
- Thêm API Gateway để routing linh hoạt.

---

## 2. Tech Stack — phần mới

### 2.1. Matching Service (Python)

| Layer | Tech | Lý do |
|-------|------|-------|
| Framework | FastAPI | Async, nhanh, dễ integrate với ML libs |
| ML | sentence-transformers (custom) hoặc OpenAI embeddings | Embedding tiếng Việt + Anh |
| Vector DB client | qdrant-client | Official Python SDK |
| ML | scikit-learn (re-ranking) | Gradient Boost cho re-rank |
| MLOps | MLflow (tracking experiments) | A/B test scoring weights |
| Deploy | Railway hoặc Fly.io | Đơn giản cho Python service |

### 2.2. AI Co-Pilot Service

| Layer | Tech |
|-------|------|
| Framework | Spring Boot (Java) — wrap LLM + transcription |
| LLM | OpenAI GPT-4o-mini (cost-effective) hoặc Anthropic Claude Haiku |
| Streaming transcription | AssemblyAI Universal-Streaming |
| Prompt management | LangChain4j |
| Cache | Redis (cache LLM responses cho session recap) |

### 2.3. PWA

| Layer | Tech |
|-------|------|
| Service Worker | Workbox (Next.js PWA plugin) |
| Manifest | next-pwa |
| Push | web-push (npm) + VAPID |
| State offline | IndexedDB (Dexie.js) |

### 2.4. Analytics

| Layer | Tech |
|-------|------|
| Product analytics | PostHog Cloud (free tier 1M events/mo) hoặc self-host |
| Backend events | Custom Kafka topic → ClickHouse (Phase 3) |
| Visualization | PostHog dashboard + custom admin pages |

### 2.5. Verification

| Service | Purpose |
|---------|---------|
| Twilio Verify API | SMS OTP |
| Onfido / Persona | ID + liveness check (Phase 2.5) |

---

## 3. AI Matching Pipeline (chi tiết)

### 3.1. Pipeline flow

```
┌────────────────────────────────────────────────────────────────────┐
│                     AI MATCHING PIPELINE                            │
└────────────────────────────────────────────────────────────────────┘

[Trigger: User mở Discover page]
              │
              ▼
┌──────────────────────────┐
│ 1. Build query vector    │
│    Embed Skill DNA của   │
│    current user          │
│    (skills wanted + level│
│     + bio + goals)       │
└──────────┬───────────────┘
           │ (1536-dim vector)
           ▼
┌──────────────────────────┐
│ 2. Qdrant ANN search     │
│    top_k=200 candidates  │
│    filter:               │
│    - active user         │
│    - is_teacher=true     │
│    - languages overlap   │
│    - timezone overlap    │
└──────────┬───────────────┘
           │ (200 candidates)
           ▼
┌──────────────────────────┐
│ 3. Multi-factor scoring │
│    Python service        │
│    Features (10):        │
│    - skill_sim (cosine)  │
│    - rating_avg          │
│    - response_rate       │
│    - schedule_overlap    │
│    - personality (MBTI)  │
│    - lang_match          │
│    - recent_activity     │
│    - verif_level         │
│    - skill_score          │
│    - diversity           │
│    Weighted sum → score  │
└──────────┬───────────────┘
           │ (scored 200)
           ▼
┌──────────────────────────┐
│ 4. Top-10 selection     │
│    Sort by score DESC    │
│    Apply diversity rule  │
│    (không trùng top 5    │
│     tuần trước)          │
└──────────┬───────────────┘
           │ (top 10)
           ▼
┌──────────────────────────┐
│ 5. Generate explanation │
│    LLM (GPT-4o-mini)    │
│    "Why we matched"     │
│    Batch 10 trong 1 call │
└──────────┬───────────────┘
           │
           ▼
[Output: Top 10 matches + explanations]
```

### 3.2. Embedding strategy

**Input embedding string** (cho user muốn học):
```
"Skills wanted: Public Speaking (priority 5), Storytelling (priority 4).
Learning style: Visual. Goals: Improve presentation skills for product demos.
Languages: vi, en. Country: VN."
```

Model: `text-embedding-3-small` (OpenAI) — 1536-dim, $0.02/1M tokens.

**Output**: Vector stored trong Qdrant collection `user_skill_embeddings` với payload:
```json
{
  "user_id": "uuid",
  "country": "VN",
  "languages": ["vi", "en"],
  "rating_avg": 4.7,
  "is_teacher": true,
  "verification_level": 2,
  "embedding_text": "Skills offered: Public Speaking (level 5, 8y exp), ...",
  "created_at": "2026-..."
}
```

### 3.3. Re-ranking model

**Phase 2.1:** Rule-based weighted scoring (sum of features × weights).
**Phase 2.5:** Gradient Boosting Regressor trained trên historical booking outcomes.

Features cho re-rank model:
| Feature | Source |
|---------|--------|
| `skill_similarity` | cosine(query_vec, candidate_vec) |
| `rating_avg` | DB |
| `sessions_completed` | DB |
| `response_rate` | DB (booking accept / booking received) |
| `schedule_overlap_score` | computed from user_availability |
| `personality_match` | MBTI / RIASEC compat (nếu có) |
| `lang_match` | bool |
| `country_match` | bool |
| `recent_activity` | days since last session |
| `diversity_boost` | heuristic — boost nếu chưa từng xuất hiện trong top-5 tuần qua |

### 3.4. Cold start strategy

User mới (sessions_completed = 0):
- Không dùng re-rank model (chưa có data).
- Chỉ dùng skill_similarity + schedule_overlap + boost verification_level + boost "Founding Mentor" badge.
- Hiển thị banner "Newcomer-friendly mentors" filter.

Founding Mentor:
- Top 100 users đăng ký đầu tiên (manual tag).
- Được boost 1.2× trong scoring.

### 3.5. "Why we matched" — LLM prompt

```python
prompt = f"""
Bạn là trợ lý AI matching. Hãy giải thích ngắn gọn (2-3 câu, tiếng Việt) 
tại sao user A và user B là match tốt.

User A (người học):
- Muốn học: {a.skills_wanted}
- Trình độ: {a.level}
- Phong cách: {a.learning_style}

User B (người dạy):
- Có thể dạy: {b.skills_offered}
- Trình độ: {b.level}
- Kinh nghiệm: {b.years_exp}
- Rating: {b.rating_avg} ({b.sessions_count} buổi)
- Giọng nói / phong cách: {b.personality}

Match score: {score:.2f}/1.0
Top reasons: {top_reasons}

Output format:
"Bạn {match_pct}% hợp với {B.name} vì: <2-3 câu giải thích>"
"""
```

Cache explanation trong Redis 7 ngày.

---

## 4. AI Co-Pilot — Streaming Architecture

### 4.1. Real-time flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    AI CO-PILOT IN SESSION                         │
└─────────────────────────────────────────────────────────────────┘

   ┌────────────┐        ┌──────────────┐        ┌────────────┐
   │ Client     │ audio  │ Co-Pilot Svc │ stream │ AssemblyAI │
   │ (browser)  │───────►│ (Java WS)    │───────►│ (cloud)    │
   │ MediaRecorder       │              │◄───────│ transcript │
   └────────────┘        │              │ chunks │
                         │              │        └────────────┘
                         │   ┌──────────┐
                         │   │ LangChain4j│
                         │   │ (LLM)     │  ← GPT-4o-mini
                         │   └────┬─────┘
                         │        │ every 5 min
                         ▼        ▼
                  ┌──────────────────────┐
                  │  Notes chunks        │
                  │  "09:15 - Discussed  │
                  │   storytelling..."  │
                  └──────┬───────────────┘
                         │
                         ▼
                  ┌──────────────────────┐
                  │ Session DB (notes)   │
                  └──────────────────────┘

Sau session end:
- Generate final summary + action items (LLM)
- Save to DB
- Show in booking detail page
```

### 4.2. Implementation

**Audio capture (frontend):**
```typescript
const recorder = new MediaRecorder(stream, { mimeType: 'audio/webm;codecs=opus' });
recorder.ondataavailable = (e) => {
  socket.send(e.data); // Send chunks to backend
};
```

**Backend WebSocket handler:**
```java
@MessageMapping("/sessions/{id}/audio")
public void handleAudio(@DestinationVariable String id, byte[] chunk) {
    // Forward to AssemblyAI via gRPC streaming
    // Get transcript chunks back
    // Every 5 minutes → call LLM to summarize
}
```

**LLM note generation (every 5 min):**
```java
String prompt = """
Tóm tắt đoạn hội thoại sau thành 3-5 gạch đầu dòng ngắn gọn (tiếng Việt):
{transcript_chunk}
""";
String notes = chatClient.call(prompt);
sessionNotesRepo.save(bookingId, timestamp, notes);
```

### 4.3. Cost control

- Audio chỉ forward khi user opt-in vào AI Co-Pilot.
- Transcription: AssemblyAI streaming $0.0003/sec → 60min = $1.08.
- LLM: GPT-4o-mini $0.15/1M input tokens → 1 session ≈ $0.05.
- **Total per session: ~$1.13.** → Charge Premium user ≥$5/tháng hoặc freemium 3 session/tháng.

---

## 5. PWA Architecture

### 5.1. Service Worker strategy

```typescript
// next-pwa config
const config = {
  runtimeCaching: [
    {
      urlPattern: /^https:\/\/api\.skillseed\.app\/api\/v1\/users\/me/,
      handler: 'NetworkFirst',
      options: {
        cacheName: 'user-profile',
        expiration: { maxEntries: 1, maxAgeSeconds: 60 * 5 },
      },
    },
    {
      urlPattern: /^https:\/\/api\.skillseed\.app\/api\/v1\/bookings/,
      handler: 'NetworkFirst',
      options: {
        cacheName: 'bookings',
        expiration: { maxEntries: 50, maxAgeSeconds: 60 * 60 },
      },
    },
    {
      urlPattern: /\.(?:png|jpg|svg|webp)$/,
      handler: 'CacheFirst',
      options: { cacheName: 'images', expiration: { maxEntries: 200 } },
    },
  ],
};
```

### 5.2. Push notification

**Trigger từ backend (Kafka topic `notifications`):**
```java
@KafkaListener(topics = "booking-events")
public void onBookingConfirmed(BookingEvent event) {
    webPushService.send(event.getUserId(), {
        title: "Booking Confirmed",
        body: "Your session with Mai starts in 30 minutes",
        icon: "/icons/icon-192.png",
        url: `/bookings/${event.getBookingId()}`,
    });
}
```

**VAPID setup:** Generate key pair, lưu private key trong secrets, public key trong env.

---

## 6. Verification Flow

### 6.1. Phone verification

```
POST /api/v1/auth/verify-phone/send
  → Generate 6-digit OTP, store Redis (TTL 5min)
  → Twilio SMS API: send OTP to user's phone

POST /api/v1/auth/verify-phone/confirm { code }
  → Validate OTP
  → Update user.verification_level = 1
  → Mark phone as verified
```

### 6.2. Liveness (ID + selfie)

```
POST /api/v1/auth/verify-identity/start
  → Generate Onfido applicant ID
  → Return Onfido SDK token

Frontend → Onfido SDK flow:
  - User chụp ID (cmnd/passport)
  - User chụp selfie + liveness check (head turn)
  - Submit

Webhook → POST /api/v1/webhooks/onfido { applicantId, status }
  → status=approved → update user.verification_level = 3
  → status=declined → notify admin review
```

---

## 7. Multi-criteria Rating — Schema migration

### 7.1. New columns

```sql
ALTER TABLE ratings ADD COLUMN knowledge_score SMALLINT;
ALTER TABLE ratings ADD COLUMN clarity_score SMALLINT;
ALTER TABLE ratings ADD COLUMN helpfulness_score SMALLINT;
ALTER TABLE ratings ADD COLUMN punctuality_score SMALLINT;
ALTER TABLE ratings ADD COLUMN friendliness_score SMALLINT;

ALTER TABLE ratings ADD COLUMN preparedness_score SMALLINT; -- teacher rates learner
ALTER TABLE ratings ADD COLUMN engagement_score SMALLINT;
ALTER TABLE ratings ADD COLUMN respectfulness_score SMALLINT;

-- Drop overall_score (sẽ dùng generated column)
ALTER TABLE ratings DROP COLUMN overall_score;

ALTER TABLE ratings ADD COLUMN overall_score SMALLINT GENERATED ALWAYS AS (
    (COALESCE(knowledge_score,0) + COALESCE(clarity_score,0) + 
     COALESCE(helpfulness_score,0) + COALESCE(punctuality_score,0) + 
     COALESCE(friendliness_score,0)) / 5.0
) STORED;
```

**Migration path:** Backfill null values cho ratings cũ (treat as 3 — neutral).

### 7.2. Multi-dim breakdown endpoint

```
GET /api/v1/users/{id}/rating-breakdown
→ {
    knowledge: 4.8,
    clarity: 4.6,
    helpfulness: 4.9,
    punctuality: 4.7,
    friendliness: 5.0,
    overall: 4.8,
    total_ratings: 23
}
```

---

## 8. Reputation Tier System

### 8.1. Tier logic (computed view)

```sql
CREATE VIEW user_reputation_tier AS
SELECT
    u.id AS user_id,
    u.sessions_completed,
    u.rating_avg,
    CASE
        WHEN u.sessions_completed >= 500 AND u.rating_avg >= 4.9 THEN 'LEGEND'
        WHEN u.sessions_completed >= 100 AND u.rating_avg >= 4.7 THEN 'FOREST'
        WHEN u.sessions_completed >= 20  AND u.rating_avg >= 4.5 THEN 'TREE'
        WHEN u.sessions_completed >= 5   AND u.rating_avg >= 4.0 THEN 'SAPLING'
        ELSE 'SPROUT'
    END AS tier,
    CASE
        WHEN u.sessions_completed >= 500 AND u.rating_avg >= 4.9 THEN '🏆'
        WHEN u.sessions_completed >= 100 AND u.rating_avg >= 4.7 THEN '🌲'
        WHEN u.sessions_completed >= 20  AND u.rating_avg >= 4.5 THEN '🌳'
        WHEN u.sessions_completed >= 5   AND u.rating_avg >= 4.0 THEN '🌿'
        ELSE '🌱'
    END AS tier_icon
FROM users u WHERE u.deleted_at IS NULL;
```

Refresh materialized view mỗi giờ (or denormalize vào user table).

---

## 9. Admin Dashboard

### 9.1. Pages

| Page | Mục đích |
|------|---------|
| `/admin` | Tổng quan: MAU, DAU, sessions, conversion |
| `/admin/users` | List, search, ban, verify |
| `/admin/bookings` | List, filter by status, force-cancel |
| `/admin/ratings` | List, hide inappropriate |
| `/admin/skills` | Review custom skills, approve/reject |
| `/admin/reports` | Anomaly detection, fraud alerts |
| `/admin/moderation` | Reported content queue |

### 9.2. Tech

- Same Next.js app với route group `(admin)`.
- Backend admin endpoints có prefix `/api/v1/admin/**` yêu cầu role `ADMIN`.
- Charts: Recharts hoặc Tremor.

---

## 10. Kafka Topics (mới trong Phase 2)

| Topic | Producer | Consumer | Use |
|-------|----------|----------|-----|
| `user-events` | Monolith | Analytics service | Track user actions |
| `booking-events` | Monolith | Notification service, Analytics | Booking lifecycle |
| `session-events` | Monolith | AI Co-Pilot service | Start/stop session |
| `rating-events` | Monolith | Reputation service, Analytics | Update tier |
| `match-events` | Matching service | Analytics | A/B test tracking |

**Lý do dùng Kafka từ Phase 2:**
- Decouple monolith ↔ AI services.
- Async processing (rating → notification → analytics).
- Replay events cho audit.

**Lightweight Kafka:** Dùng Redpanda hoặc Confluent Cloud free tier (avoid Kafka operational overhead).

---

## 11. Deployment (Phase 2)

```
┌──────────────────────────────────────────────────────────────┐
│                 PHASE 2 PRODUCTION STACK                       │
└──────────────────────────────────────────────────────────────┘

Frontend: Vercel
   └─ Next.js 15 (PWA enabled)

Backend Monolith: Railway.app
   └─ Spring Boot, 2-3 instances

Matching Service: Fly.io
   └─ FastAPI Python, 1 instance (scale 0-3)

AI Co-Pilot Service: Railway.app
   └─ Spring Boot + WebSocket, 1 instance

API Gateway: Cloudflare Workers hoặc Kong Cloud
   └─ Route /api/v1/matches → matching-service
   └─ Route /api/v1/ai-copilot → ai-copilot-service
   └─ Route /api/v1/* → monolith

Data:
   └─ Supabase Postgres (main)
   └─ Upstash Redis (cache + session)
   └─ Qdrant Cloud (vector)
   └─ Redpanda Cloud (Kafka)

External:
   └─ Daily.co (video)
   └─ OpenAI (LLM)
   └─ AssemblyAI (transcription)
   └─ Twilio (SMS)
   └─ Onfido (KYC)
   └─ PostHog Cloud (analytics)
   └─ VAPID (push)
   └─ Resend (email)

Monitoring:
   └─ Better Stack (logs)
   └─ UptimeRobot (uptime)
   └─ Sentry (errors)
```

**Cost estimate (1k MAU):**
| Service | Cost |
|---------|------|
| Vercel | $20/mo |
| Railway (monolith + co-pilot) | $50/mo |
| Fly.io (matching) | $30/mo |
| Supabase | $25/mo |
| Upstash | $10/mo |
| Qdrant Cloud | $25/mo |
| Redpanda | $0 (free tier) |
| Daily.co | $50/mo |
| OpenAI | $200/mo (1k users × 3 sessions × $0.07) |
| AssemblyAI | $200/mo |
| Twilio | $50/mo |
| PostHog | $0 (free tier) |
| Resend | $20/mo |
| Sentry | $0 (free tier) |
| **Tổng** | **~$680/mo** |

Tốt cho ngân sách $3-5k/tháng.

---

## 12. Out of Scope (Design)

- ❌ Native mobile (chỉ PWA)
- ❌ Group sessions / Learning Pods
- ❌ Code editor live
- ❌ AR overlay
- ❌ Skill Passport
- ❌ Premium subscription / Stripe
- ❌ B2B features
- ❌ Multi-language i18n (chỉ VN + EN)
- ❌ Microservices cho toàn bộ (chỉ 2 service tách ra)

---

## 13. Open Questions

| # | Câu hỏi | Owner | Deadline |
|---|----------|-------|----------|
| 1 | Embedding model: OpenAI hay self-hosted (sentence-transformers)? | AI eng | Sprint 0 |
| 2 | Re-ranking: rule-based hay Gradient Boost từ đầu? | AI eng | Sprint 1 |
| 3 | AI Co-Pilot: freemium hay Premium only? | Founder | Sprint 0 |
| 4 | Liveness check (Onfido) cần thiết từ Phase 2 hay đợi Phase 3? | Founder | Sprint 2 |
| 5 | Kafka hay dùng in-memory event (giống Phase 1)? | Backend | Sprint 1 |