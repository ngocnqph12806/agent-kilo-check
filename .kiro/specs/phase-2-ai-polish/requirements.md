# Phase 2 — AI & Polish: Requirements

> **Mục tiêu giai đoạn:** Thêm AI Matching Engine để cá nhân hoá trải nghiệm Discover, polish UX, nâng cấp verification và analytics.
> **Thời gian:** Tháng 5 → Tháng 8.
> **Team:** 3–4 người (Founder/Java dev + AI/ML engineer + Frontend dev + Marketing).
> **Deliverable:** 1.000–5.000 MAU, 500+ sessions/tuần, AI matching v1 chạy ổn định.
> **Ngân sách:** $3.000–5.000/tháng.

---

## 1. Bối cảnh & Lý do Phase 2

Phase 1 (MVP) đã chứng minh được demand: ≥ 200 sessions, ≥ 50 users. Phase 2 tập trung vào:
- **Differentiation:** AI matching là lợi thế cạnh tranh cốt lõi.
- **Retention:** Nâng cấp UX để giữ user (D7 retention target 35%).
- **Quality:** Verification nâng cao + multi-criteria rating → trust cao hơn.

---

## 2. User Stories mới

### 2.1. AI Matching

- **US-201:** Là người dùng, tôi muốn tab "Discover" hiển thị top 10 matches mỗi ngày, được AI cá nhân hoá dựa trên Skill DNA của tôi.
- **US-202:** Là người dùng, tôi muốn thấy lý do AI ghép cặp ("Why we matched: Bạn 92% hợp với Mai vì...").
- **US-203:** Là người dùng, tôi muốn có nút "Show me more" để xem thêm matches ngoài top 10.
- **US-204:** Là người dùng mới (cold start), tôi muốn được match với "Founding Mentor" (early adopters).

### 2.2. AI Co-Pilot trong Session

- **US-210:** Là người dùng, trong session tôi muốn có AI ghi chú tự động theo thời gian thực.
- **US-211:** Là người dùng, sau session tôi muốn nhận summary + action items.
- **US-212:** Là người dùng, trong session tôi muốn AI đề xuất follow-up topics.
- **US-213:** Là người dùng (Premium), tôi muốn AI highlight "Aha moments" trong buổi học.

### 2.3. Mobile PWA

- **US-220:** Là người dùng mobile, tôi muốn install SkillSeed như native app từ browser.
- **US-221:** Là người dùng mobile, tôi muốn nhận push notification cho booking, reminder, match mới.
- **US-222:** Là người dùng mobile, tôi muốn dùng app offline (xem booking, profile, đã lưu notes).

### 2.4. Verification nâng cao

- **US-230:** Là người dùng, tôi muốn verify phone qua SMS OTP.
- **US-231:** Là người dùng (muốn dạy), tôi muốn verify identity bằng selfie AI liveness.
- **US-232:** Là người dùng, tôi muốn thấy badge "Verified" trên profile của người dạy.

### 2.5. Rating & Reputation nâng cao

- **US-240:** Là người học, sau session tôi muốn đánh giá 5 tiêu chí (kiến thức, truyền đạt, hữu ích, đúng giờ, thân thiện) thay vì 1 overall.
- **US-241:** Là người dùng, tôi muốn thấy multi-dimensional breakdown trên profile người dạy.
- **US-242:** Là người dùng, tôi muốn có tier hệ thống (Sprout → Sapling → Tree → Forest → Legend).

### 2.6. Discovery nâng cao

- **US-250:** Là người dùng, tôi muốn filter nâng cao (giọng nói, quốc gia, ngành nghề).
- **US-251:** Là người dùng, tôi muốn xem "Top mentors tuần này" ở mỗi kỹ năng.
- **US-252:** Là người dùng, tôi muốn saved searches và notifications khi có mentor mới.

### 2.7. Analytics & Admin

- **US-260:** Là admin, tôi muốn dashboard với MAU, sessions/day, conversion funnel.
- **US-261:** Là admin, tôi muốn phát hiện anomaly (rating rings, spam booking).
- **US-262:** Là admin, tôi muốn tools để moderate content (skills custom, reviews).

---

## 3. Functional Requirements

### 3.1. AI Matching Engine v1

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-A01** | Hệ thống embedding Skill DNA của user (skills + level + bio + goals) thành vector 1536-dim, lưu vào Qdrant. | MUST |
| **FR-A02** | Khi user vào Discover, gọi AI matching pipeline trả top 10 users. | MUST |
| **FR-A03** | Pipeline: (1) Embed query, (2) Qdrant ANN search top 200, (3) Hard filter (timezone, language, level), (4) Re-rank bằng multi-factor scoring. | MUST |
| **FR-A04** | Multi-factor scoring: Skill Match (0.30), Schedule Overlap (0.20), Rating (0.15), Response Rate (0.10), Personality (0.10), Language (0.10), Diversity (0.05). | MUST |
| **FR-A05** | Generate "Why we matched" explanation bằng LLM (GPT-4o-mini) cho mỗi top-10 match. | MUST |
| **FR-A06** | Cold start: user mới match dựa trên Skill similarity + Schedule overlap + boost "Founding Mentor". | MUST |
| **FR-A07** | Re-rank model re-train mỗi tuần dựa trên booking outcomes. | SHOULD |
| **FR-A08** | A/B test scoring weights qua experiment framework. | SHOULD |

### 3.2. AI Co-Pilot trong Session

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-A10** | Trong session, AI stream audio → transcript real-time (AssemblyAI hoặc Deepgram streaming). | MUST |
| **FR-A11** | Auto-generate session notes theo từng chunk (5 phút/lần) bằng LLM. | MUST |
| **FR-A12** | Sau session, generate summary + action items bằng LLM. | MUST |
| **FR-A13** | (Premium) Detect "Aha moments" dựa trên sentiment + keyword. | SHOULD |
| **FR-A14** | (Premium) Suggest follow-up topics qua LLM. | SHOULD |
| **FR-A15** | Notes/summary lưu vào DB và hiển thị trong booking detail. | MUST |
| **FR-A16** | User có thể tắt AI Co-Pilot per-session. | MUST |

### 3.3. Mobile PWA

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-A20** | Web app là PWA với manifest.json + service worker. | MUST |
| **FR-A21** | User có thể "Add to Home Screen" trên iOS/Android. | MUST |
| **FR-A22** | Push notification qua Web Push API (VAPID) — booking, reminder, new match. | MUST |
| **FR-A23** | Service worker cache app shell + API responses cho offline mode. | MUST |
| **FR-A24** | Offline banner hiển thị khi mất mạng. | MUST |
| **FR-A25** | Critical screens (booking list, profile) hoạt động offline. | SHOULD |

### 3.4. Verification nâng cao

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-A30** | Verify phone qua SMS OTP (Twilio hoặc MessageBird). | MUST |
| **FR-A31** | Verify identity bằng selfie AI liveness (Onfido hoặc Persona). | SHOULD |
| **FR-A32** | Verification level tăng dần (0→1→2→3). | MUST |
| **FR-A33** | User dạy cần level ≥ 2 mới nhận seeds. | MUST |
| **FR-A34** | Hiển thị badge "Verified" trên profile. | MUST |

### 3.5. Rating & Reputation nâng cao

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-A40** | Learner rate teacher với 5 tiêu chí: knowledge, clarity, helpfulness, punctuality, friendliness (mỗi cái 1–5). | MUST |
| **FR-A41** | `overall_score` = AVG(5 tiêu chí) (computed/generated column). | MUST |
| **FR-A42** | Teacher rate learner với 3 tiêu chí: preparedness, respectfulness, engagement. | MUST |
| **FR-A43** | Multi-dimensional breakdown hiển thị trên profile. | MUST |
| **FR-A44** | Reputation tier system: Sprout → Sapling → Tree → Forest → Legend (computed từ sessions_completed + rating). | MUST |
| **FR-A45** | Tier badges hiển thị trên profile + Discover card. | MUST |

### 3.6. Discovery nâng cao

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-A50** | Filter mới: giọng nói (giọng Bắc/Nam cho VN), quốc gia, ngành nghề (skill category). | MUST |
| **FR-A51** | "Top mentors this week" ở mỗi kỹ năng (cached query, refresh mỗi 6h). | MUST |
| **FR-A52** | Saved searches (tối đa 5/user) + notification khi có mentor mới match. | SHOULD |
| **FR-A53** | Empty state cho Discover: "Chưa có match, mở rộng filter?". | MUST |

### 3.7. Analytics & Monitoring

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-A60** | Track events: signup, onboarding_complete, view_match, book_session, complete_session, rate_session. | MUST |
| **FR-A60** | PostHog (self-host) hoặc Plausible Analytics cho product analytics. | MUST |
| **FR-A61** | Admin dashboard: MAU, DAU, sessions/day, funnel conversion, top skills, top countries. | MUST |
| **FR-A62** | Anomaly detection: rating rings (mutual 5⭐), spam booking, fake profiles. | SHOULD |
| **FR-A63** | Moderation queue: custom skills, reported reviews, flagged sessions. | MUST |
| **FR-A64** | Real-time alerts: API error rate > 5%, booking failure > 10%. | MUST |

### 3.8. Marketing & Growth

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-A70** | Referral program: invite friend → cả 2 nhận 30 seeds. | MUST |
| **FR-A71** | Public share page cho profile: `/u/{userId}` với Open Graph tags. | MUST |
| **FR-A72** | SEO landing pages cho top skills: `/learn/{skill-slug}` (e.g., `/learn/public-speaking`). | MUST |
| **FR-A73** | Blog CMS (Notion hoặc MDX) cho content marketing. | SHOULD |
| **FR-A74** | Email drip campaign cho user mới (5 email trong 14 ngày). | MUST |

---

## 4. Non-Functional Requirements

| ID | Mô tả | Tiêu chí |
|----|-------|----------|
| **NFR-A01** | AI matching p95 latency | < 1.5 giây |
| **NFR-A02** | Qdrant query latency | < 200ms |
| **NFR-A03** | PWA Lighthouse score | ≥ 85 |
| **NFR-A04** | Push notification delivery | ≥ 90% trong 5 phút |
| **NFR-A05** | LLM API cost per user/month | < $0.50 |
| **NFR-A06** | Admin dashboard load time | < 3 giây |
| **NFR-A07** | Data retention (analytics) | 13 tháng |
| **NFR-A08** | Uptime | ≥ 99.9% |

---

## 5. Out of Scope (Phase 2 — đẩy sang Phase 3+)

- ❌ Native mobile app (chỉ PWA)
- ❌ Group pods (Learning Pods v1)
- ❌ Multi-modal session (code editor, AR overlay)
- ❌ B2B workspace
- ❌ Skill Passport (chỉ rating)
- ❌ Stripe / Premium subscription
- ❌ Soulbound Token / Blockchain
- ❌ Multi-currency
- ❌ Public API
- ❌ Microservices (vẫn monolith modular + 1 sidecar Python)

---

## 6. Acceptance Criteria (Phase 2 Done)

| # | Tiêu chí | Mức đạt |
|---|----------|---------|
| 1 | Tất cả FR-A MUST đã implement | 100% |
| 2 | AI matching deployed + serving production traffic | Có |
| 3 | "Why we matched" explanations được sinh ra cho > 80% top-10 | ≥ 80% |
| 4 | MAU | ≥ 1.000 |
| 5 | Sessions completed (lifetime) | ≥ 500 |
| 6 | D7 retention | ≥ 35% |
| 7 | Avg rating | ≥ 4.3/5 |
| 8 | Show-up rate | ≥ 85% |
| 9 | API p95 latency | < 500ms |
| 10 | Cost per MAU | ≤ $1/tháng |

---

## 7. Dependencies

| Phụ thuộc | Loại | Trạng thái |
|----------|------|-----------|
| Phase 1 Done | Quyết định | ✅ Required |
| OpenAI API key + credit | External | Mua credits tháng 5 |
| Qdrant Cloud account | SaaS | Free tier → $25/mo |
| AI/ML engineer | Nhân sự | Tuyển tháng 5 |
| Twilio account (SMS) | SaaS | Free trial → $20/mo |
| PostHog Cloud | SaaS | Free → $0 (self-host) |
| Frontend dev (React) | Nhân sự | Tuyển tháng 5 |