# SkillSeed — Architecture

> **Diagram tổng quan + luồng dữ liệu end-to-end của SkillSeed.**
> **Last updated:** 2026-09-07

> ⚠️ File này đang ở dạng **skeleton**. Sẽ populate đầy đủ sau khi Phase 1 design stable.

---

## 1. High-Level (Phase 1 MVP)

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

**Lý do chọn Monolith (không Microservice):** Xem `docs/ADR/0001-phase-1-monolith.md`.

---

## 2. Module Boundaries (Phase 1)

```
com.skillseed
├── auth          → JWT issue/verify, OAuth2 callback, refresh token
├── user          → Profile CRUD, Skill DNA (Phase 1: JSON), onboarding
├── skill         → Skill catalog, user-skill mapping (teach/learn)
├── discover      → Search + filter (Phase 1: PG FTS, không AI matching)
├── booking       → Schedule, state machine, conflict check
├── session       → Daily.co room CRUD, token issue, AI notes stub
├── rating        → Multi-criteria rating, aggregated score
├── wallet        → Seed balance, transaction ledger, expiration
├── notification  → Email (SendGrid) + in-app notification
└── admin         → User moderation, skill review, feature flags
```

**Cross-module rules:**
- `wallet` chỉ được ghi vào từ `booking`, `session`, `admin` qua API nội bộ.
- `booking` phụ thuộc `user` + `skill`, không gọi `wallet` trực tiếp — dùng event.
- `admin` đọc tất cả module nhưng **không sửa** dữ liệu người dùng thường (chỉ moderate).

---

## 3. Data Flow — Booking Happy Path

```
[FE: Next.js]
   │  1. User chọn slot → POST /api/v1/bookings
   ▼
[BE: booking module]
   │  2. Validate: slot available? balance đủ? user verify?
   │  3. Insert booking (status=confirmed)
   │  4. Emit event: BookingConfirmed
   ▼
[BE: wallet module]    ← lắng nghe event
   │  5. Hold Seed: -duration_minutes (status=HELD)
   │
   │  ... time passes, session bắt đầu ...
   │
[BE: session module]
   │  6. POST /api/v1/sessions/{bookingId}/start → tạo Daily room
   │
   │  ... session kết thúc ...
   │
[BE: session module]
   │  7. POST /api/v1/sessions/{bookingId}/end
   │  8. Emit event: SessionCompleted
   ▼
[BE: wallet module]    ← lắng nghe event
   │  9. Settle: HELD → Teacher (+duration_minutes), Platform fee (-X%)
   │
   ▼
[BE: rating module]
   │ 10. Trigger rating prompt → FE gọi /api/v1/ratings
   ▼
[BE: passport] (Phase 1: append record; Phase 4: mint SBT)
   │ 11. Append Session record → user_skill_passport
```

---

## 4. Phases Evolution

| Phase | Thay đổi kiến trúc lớn |
|---|---|
| 1 (MVP) | Monolith Spring Boot + Next.js + Postgres + Redis + Daily.co |
| 2 (AI) | Tách `matching` service (Python) + Kafka + Qdrant. Recommendation chuyển từ FTS → embedding. |
| 3 (Scale) | Tách `notification`, `media`, `wallet` thành service. K8s. Thêm CDN. RN mobile app. Stripe Connect. |
| 4 (Regional) | Multi-region K8s. Blockchain (Polygon) cho SBT. Public API. |
| 5 (Vision) | Voice AI service. AR/VR runtime. zk-SNARK cho privacy. |

**Quy tắc:** Không thêm dependency Phase X+ vào Phase 1 trừ khi có ADR phê duyệt.

---

## 5. Open Questions (cần giải quyết trước khi code)

- [ ] Auth: dùng session cookie hay JWT thuần? (hiện tại spec: JWT + refresh)
- [ ] i18n: chốt 1 framework (next-intl? FormatJS?) trước khi viết FE
- [ ] Timezone: lưu UTC trong DB, render theo user TZ — đã chốt chưa?
- [ ] Wallet expiration: grace period bao lâu sau khi Seed hết hạn?
- [ ] Notification transport: WebSocket (STOMP) hay SSE hay polling?
