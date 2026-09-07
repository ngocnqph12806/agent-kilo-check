# Phase 2 — AI & Polish: Tasks

> **Task list triển khai AI & Polish theo sprint (3 tuần/sprint).**
> **5 sprints × 3 tuần = 15 tuần (~ 4 tháng)**

---

## Sprint 5 (Tuần 11–13) — AI Matching Engine v1

### Infrastructure & embedding

- [ ] [T-A01] **[P0]** Setup Qdrant Cloud account + cluster free tier
  - Có endpoint + API key
- [ ] [T-A02] **[P0]** Setup OpenAI account + API key
  - Add credits $200
- [ ] [T-A03] **[P0]** Setup matching-service repo (Python + FastAPI)
  - Folder: `/services/matching`
  - Dockerfile + docker-compose integration
- [ ] [T-A04] **[P0]** Setup API Gateway (Cloudflare Worker hoặc Kong)
  - Route `/api/v1/matches` → matching-service
- [ ] [T-A05] **[P0]** Implement Skill DNA embedding job
  - Khi user cập nhật Skill DNA → tạo embedding text → gọi OpenAI → lưu Qdrant
  - Backfill job cho users hiện tại
- [ ] [T-A06] **[P0]** Implement Qdrant ANN search endpoint (Python)
  - Query: vector + filters (language, country, is_teacher)
  - Return top-200 candidates

### Re-ranking & scoring

- [ ] [T-A10] **[P0]** Implement multi-factor scoring (rule-based)
  - 7 features với weights từ design.md section 3.3
- [ ] [T-A11] **[P0]** Implement cold-start logic
  - User mới → chỉ dùng skill_sim + schedule + boost verif
- [ ] [T-A12] **[P0]** Implement "Why we matched" generator (LLM)
  - Batch 10 explanations per LLM call
  - Cache 7 ngày trong Redis
- [ ] [T-A13] **[P0]** Implement GET `/api/v1/discover` orchestration
  - Frontend gọi → gateway → matching-service
  - Trả top-10 với explanations

### Frontend — Discover AI

- [ ] [T-A20] **[P0]** Update Discover page hiển thị "Why we matched" tooltip
  - Click vào match → show explanation modal
- [ ] [T-A21] **[P0]** Loading skeleton cho AI matching (vì p95 ~1.5s)
- [ ] [T-A22] **[P1]** "Show me more" button → fetch next 10 matches
- [ ] [T-A23] **[P1]** Empty state UI khi không có match
- [ ] [T-A24] **[P1]** Founding Mentor badge cho top 100 users

### Testing & QA

- [ ] [T-A30] **[P0]** Unit tests cho scoring function
- [ ] [T-A31] **[P0]** Integration test matching pipeline
- [ ] [T-A32] **[P0]** Manual test với 10 personas khác nhau
- [ ] [T-A33] **[P1]** A/B test 2 scoring weight sets (qua MLflow)

---

## Sprint 6 (Tuần 14–16) — AI Co-Pilot & Multi-criteria Rating

### AI Co-Pilot — streaming

- [ ] [T-A40] **[P0]** Setup AssemblyAI account + streaming API key
- [ ] [T-A41] **[P0]** Setup ai-copilot-service repo (Spring Boot)
  - WebSocket endpoint `/ws/copilot/{bookingId}`
- [ ] [T-A42] **[P0]** Implement audio capture từ session (frontend MediaRecorder)
  - Stream chunks qua WebSocket
- [ ] [T-A43] **[P0]** Implement AssemblyAI streaming integration (Java SDK hoặc WebSocket client)
  - Forward audio → AssemblyAI → nhận text chunks
- [ ] [T-A44] **[P0]** Implement notes generation (LLM every 5 min)
  - Batch 5 min transcript → GPT-4o-mini summary
  - Lưu vào `session_notes` table
- [ ] [T-A45] **[P0]** Implement final summary generation sau khi session end
  - Full transcript → LLM → summary + action items
- [ ] [T-A46] **[P0]** Implement toggle "AI Co-Pilot" per-session (opt-in)
- [ ] [T-A47] **[P0]** Frontend: Hiển thị real-time notes panel trong session
- [ ] [T-A48] **[P0]** Frontend: Hiển thị summary + action items sau session

### Multi-criteria rating

- [ ] [T-A50] **[P0]** DB migration: thêm columns cho multi-criteria
  - knowledge, clarity, helpfulness, punctuality, friendliness (learner→teacher)
  - preparedness, engagement, respectfulness (teacher→learner)
  - Drop overall_score, thêm generated column
- [ ] [T-A51] **[P0]** Update Rating API để nhận multi-criteria scores
  - Backward-compatible: nếu old payload thì dùng overall_score → map sang 5 tiêu chí (3)
- [ ] [T-A52] **[P0]** Frontend: Rating modal với 5 star pickers (thay vì 1)
- [ ] [T-A53] **[P0]** Backend: GET `/users/{id}/rating-breakdown`
  - Trả multi-dim avg
- [ ] [T-A54] **[P0]** Frontend: Rating breakdown component trên profile

### Reputation tier

- [ ] [T-A60] **[P0]** Implement reputation tier logic (computed view)
  - 5 tiers: SPROUT, SAPLING, TREE, FOREST, LEGEND
- [ ] [T-A61] **[P0]** Frontend: Tier badge component (icon + label)
- [ ] [T-A62] **[P0]** Update profile page + Discover card hiển thị tier

### Testing

- [ ] [T-A70] **[P0]** Load test matching service (200 concurrent users)
- [ ] [T-A71] **[P0]** E2E test AI Co-Pilot flow (mock AssemblyAI)

---

## Sprint 7 (Tuần 17–19) — Mobile PWA + Verification

### PWA setup

- [ ] [T-A80] **[P0]** Setup next-pwa plugin
  - manifest.json với icons (192, 512, maskable)
  - Service worker tự động generate
- [ ] [T-A81] **[P0]** Implement push notification backend
  - VAPID key pair (generate + store)
  - Endpoint POST `/notifications/subscribe`
- [ ] [T-A82] **[P0]** Implement push notification send service
  - Subscribe storage (Postgres table)
  - Trigger từ booking events, reminder jobs
- [ ] [T-A83] **[P0]** Frontend: Push permission prompt
  - Hiển thị banner "Cho phép thông báo?"
- [ ] [T-A84] **[P0]** Frontend: Service worker cho offline mode
  - Cache app shell
  - Cache API responses (user profile, bookings)
- [ ] [T-A85] **[P0]** Frontend: Offline banner + offline page
- [ ] [T-A86] **[P1]** IndexedDB cache cho booking list + profile
- [ ] [T-A87] **[P0]** Test PWA trên iOS Safari + Android Chrome

### Verification nâng cao

- [ ] [T-A90] **[P0]** Setup Twilio account (trial → pay-as-you-go)
- [ ] [T-A91] **[P0]** Implement SMS OTP send endpoint
  - POST `/auth/verify-phone/send`
  - Redis store OTP (TTL 5min)
- [ ] [T-A92] **[P0]** Implement OTP confirm endpoint
  - Validate OTP, update verification_level=1
- [ ] [T-A93] **[P0]** Frontend: Phone verification UI
  - Input phone → receive OTP → enter code
- [ ] [T-A94] **[P1]** Setup Onfido / Persona SDK (liveness check)
  - Backend: webhook handler
  - Frontend: Onfido SDK flow
- [ ] [T-A95] **[P0]** Verified badge UI component
- [ ] [T-A96] **[P0]** Enforce: chỉ user level ≥ 2 mới nhận seeds

### Testing

- [ ] [T-A100] **[P0]** Test PWA install flow iOS + Android
- [ ] [T-A101] **[P0]** Test push notification end-to-end
- [ ] [T-A102] **[P0]** Test offline mode (chrome devtools)

---

## Sprint 8 (Tuần 20–22) — Analytics, Admin & Growth

### Analytics infrastructure

- [ ] [T-A110] **[P0]** Setup PostHog Cloud (free tier)
- [ ] [T-A111] **[P0]** Implement frontend analytics tracking
  - post-hoc-js SDK
  - Events: signup, onboarding_complete, view_match, book_session, complete_session, rate_session
- [ ] [T-A112] **[P0]** Setup Kafka cluster (Redpanda Cloud free tier)
- [ ] [T-A113] **[P0]** Implement Kafka producer trong monolith
  - Topic: user-events, booking-events, session-events, rating-events
- [ ] [T-A114] **[P0]** Implement analytics consumer service
  - Subscribe to topics → forward to PostHog (server-side)
- [ ] [T-A115] **[P0]** Sentry setup (frontend + backend)
  - Source maps upload, error tracking

### Admin dashboard

- [ ] [T-A120] **[P0]** Backend admin endpoints
  - GET `/admin/stats` — MAU, DAU, sessions/day
  - GET `/admin/users` — list + search + ban
  - GET `/admin/bookings` — list + force-cancel
  - GET `/admin/reports` — anomaly detection
- [ ] [T-A121] **[P0]** Auth: role-based access (Spring Security `@PreAuthorize("hasRole('ADMIN')")`)
- [ ] [T-A122] **[P0]** Frontend: Admin pages (route group `(admin)`)
  - Dashboard, Users, Bookings, Reports, Skills review
- [ ] [T-A123] **[P0]** Charts: MAU line, sessions bar, top skills pie
- [ ] [T-A124] **[P1]** Anomaly detection algorithm
  - Rating rings (mutual 5⭐ + low sessions)
  - Spam booking (>5 bookings/day)
  - Fake profile (no avatar + bio = suspicious)

### Marketing & Growth

- [ ] [T-A130] **[P0]** Implement referral program
  - Unique referral code per user
  - Both inviter + invitee get 30 seeds (cap 3 invites/month)
- [ ] [T-A131] **[P0]** Public share page `/u/{userId}`
  - Open Graph tags + Twitter cards
  - Show tier, top skills, rating
- [ ] [T-A132] **[P0]** SEO landing pages `/learn/{skill-slug}`
  - SSR Next.js
  - Schema.org markup
  - 30+ skill pages (top skills first)
- [ ] [T-A133] **[P1]** Blog (Notion + embed hoặc MDX)
- [ ] [T-A134] **[P0]** Email drip campaign cho user mới
  - 5 emails trong 14 ngày (welcome, tips, match highlights, success stories, invite)
  - Dùng Resend templates

---

## Sprint 9 (Tuần 23–25) — Polish, Scale, Beta to Public

### Discovery nâng cao

- [ ] [T-A140] **[P0]** Advanced filters: giọng nói, quốc gia, ngành nghề
- [ ] [T-A141] **[P0]** "Top mentors this week" endpoint
  - Cached query, refresh 6h
- [ ] [T-A142] **[P1]** Saved searches + notification cho new matches
- [ ] [T-A143] **[P0]** Discovery feed pagination (cursor-based)

### Polish & bug fixes

- [ ] [T-A150] **[P0]** Bug bash: rà soát critical bugs từ beta feedback
- [ ] [T-A151] **[P0]** Performance optimization
  - DB query review (slow query log)
  - Redis cache tuning
  - Frontend bundle size (< 250KB initial)
- [ ] [T-A152] **[P0]** Accessibility audit (WCAG 2.1 AA)
- [ ] [T-A153] **[P0]** i18n setup cơ bản (VN + EN)
- [ ] [T-A154] **[P0]** SEO meta tags đầy đủ (per page)
- [ ] [T-A155] **[P0]** Error tracking dashboard (Sentry)
- [ ] [T-A156] **[P0]** Uptime monitoring + on-call rotation

### Scale preparation

- [ ] [T-A160] **[P0]** Setup read replica cho Postgres (nếu MAU > 2K)
- [ ] [T-A161] **[P0]** Connection pool tuning (HikariCP max 20)
- [ ] [T-A162] **[P0]** Rate limiting cho API (Bucket4j)
- [ ] [T-A163] **[P0]** Setup CDN (Cloudflare) cho static assets
- [ ] [T-A164] **[P0]** Backup strategy (daily + weekly offsite)

### Public launch

- [ ] [T-A170] **[P0]** Product Hunt launch
- [ ] [T-A171] **[P0]** Hacker News Show HN post
- [ ] [T-A172] **[P0]** LinkedIn founder post announcing Phase 2
- [ ] [T-A173] **[P0]** Press kit (logos, screenshots, fact sheet)
- [ ] [T-A174] **[P0]** Community channels: Discord + Facebook group
- [ ] [T-A175] **[P0]** Daily monitoring trong 2 tuần sau launch

---

## Cross-cutting tasks (song song 15 tuần)

- [ ] [T-A200] **[P0]** Daily standup (founder + AI eng + FE)
- [ ] [T-A201] **[P0]** Weekly sprint review
- [ ] [T-A202] **[P0]** Code review mọi PR (2 reviewers)
- [ ] [T-A203] **[P0]** Track metrics: MAU, D7 retention, sessions/day, AI match CTR
- [ ] [T-A204] **[P1]** User interview với 5–10 user mỗi tuần
- [ ] [T-A205] **[P1]** Content marketing: 3 user stories + 1 technical blog post/tháng
- [ ] [T-A206] **[P1]** Partnership outreach (5 trường ĐH + 3 coworking)

---

## Definition of Done — Phase 2

### Functional

- [ ] 100% FR-A MUST đã implement và pass QA
- [ ] AI matching serving production (top-10 cho > 80% users)
- [ ] AI Co-Pilot working cho opt-in users (≥ 30% sessions)
- [ ] PWA installable + push notification
- [ ] Multi-criteria rating + tier system
- [ ] Admin dashboard functional

### Non-Functional

- [ ] API p95 < 500ms (matching < 1.5s)
- [ ] Uptime ≥ 99.9%
- [ ] PWA Lighthouse ≥ 85
- [ ] Cost per MAU ≤ $1

### Business

- [ ] MAU ≥ 1.000
- [ ] Sessions completed (lifetime) ≥ 500
- [ ] D7 retention ≥ 35%
- [ ] NPS ≥ 30
- [ ] Avg rating ≥ 4.3
- [ ] Show-up rate ≥ 85%

### Operational

- [ ] CI/CD pipeline xanh
- [ ] Monitoring + alerts configured
- [ ] Documentation cập nhật
- [ ] Team có thể maintain independently

---

## Effort Estimation

| Sprint | Focus | Effort |
|--------|-------|--------|
| Sprint 5 | AI Matching v1 | 120h (AI eng 80h + founder 40h) |
| Sprint 6 | AI Co-Pilot + Rating | 100h (AI eng 60h + founder 40h) |
| Sprint 7 | PWA + Verification | 80h (FE 60h + founder 20h) |
| Sprint 8 | Analytics + Admin + Growth | 90h (FE 50h + founder 40h) |
| Sprint 9 | Polish + Launch | 60h (FE 30h + founder 30h) |
| **Tổng** | | **~450h** (~ 15 tuần × 30h/người × 1 người, hoặc 4 người × ~28h/tuần) |

---

## Risk Register

| Risk | Trigger | Mitigation |
|------|---------|------------|
| AI matching quality thấp | CTR < 30% | Tăng data, thêm features, manual re-rank cho top users |
| OpenAI cost vượt budget | > $500/mo | Dùng GPT-4o-mini, cache explanations, fallback rule-based |
| AssemblyAI latency cao | Streaming lag > 5s | Buffer trước khi forward, fallback Deepgram |
| PWA install rate thấp | < 5% mobile users | A/B test prompt timing, in-app nudge |
| Push notification spam complaints | Unsubscribe > 30% | Smart frequency cap, user preferences |
| Tier system bị gaming | Fake high ratings | Anomaly detection, velocity check |
| Admin dashboard không stable | Frequent crashes | Test với data thật từ beta, load test |
| Founder burnout | > 50h/tuần | Hire thêm 1 người, phân chia trách nhiệm |