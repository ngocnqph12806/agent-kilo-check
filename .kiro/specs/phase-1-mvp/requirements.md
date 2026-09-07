# Phase 1 — MVP: Requirements

> **Mục tiêu giai đoạn:** Ra mắt sản phẩm MVP cho phép 2 người dùng thực hiện trao đổi kỹ năng qua video call và đánh giá lẫn nhau, với hệ thống Seed Wallet đơn giản.
> **Thời gian:** Tháng 2 → Tháng 4 (sau khi Phase 0 GO).
> **Team:** 1–2 người (Java backend dev + 1 frontend freelancer).
> **Deliverable:** MVP live với 50–100 beta users, 200+ sessions completed.
> **Ngân sách:** $500–1.500/tháng (infra + freelancer).

---

## 1. Personas trong Phase 1

| Persona | Tập trung |
|---------|-----------|
| **Sinh viên "Hạt giống"** (18–24) | Beta user chính — dễ mời, có thời gian, cần kỹ năng |
| **Chuyên gia trẻ "Đa năng"** (25–35) | Beta teacher — sẵn sàng dạy ngắn, kết nối network |
| **Người chuyển nghề** (30–45) | Beta secondary — muốn học nhanh |

---

## 2. User Stories — MVP

### 2.1. Onboarding & Authentication

- **US-101:** Là người dùng mới, tôi muốn đăng ký bằng email + password để có tài khoản.
- **US-102:** Là người dùng mới, tôi muốn đăng ký nhanh bằng Google/Apple ID để không phải nhớ mật khẩu.
- **US-103:** Là người dùng mới, tôi muốn nhận email xác nhận để kích hoạt tài khoản.
- **US-104:** Là người dùng, tôi muốn đăng nhập an toàn bằng JWT + có thể refresh token.
- **US-105:** Là người dùng, tôi muốn quên mật khẩu và reset qua email.
- **US-106:** Là người dùng, tôi muốn tạo profile (Skill DNA) bằng form 7 bước đơn giản trong ~3 phút.

### 2.2. Skill DNA (Profile)

- **US-110:** Là người dùng, tôi muốn thêm kỹ năng tôi có thể dạy (chọn từ taxonomy + tự nhập), kèm level (1–5), mô tả, số năm kinh nghiệm.
- **US-111:** Là người dùng, tôi muốn thêm kỹ năng tôi muốn học (chọn từ taxonomy + tự nhập), kèm priority (1–5), target level.
- **US-112:** Là người dùng, tôi muốn thiết lập lịch rảnh hàng tuần theo timezone của tôi.
- **US-113:** Là người dùng, tôi muốn chọn ngôn ngữ giao tiếp và quốc gia.
- **US-114:** Là người dùng, tôi muốn upload avatar và viết bio ngắn.
- **US-115:** Là người dùng, tôi muốn cập nhật Skill DNA bất kỳ lúc nào.

### 2.3. Discovery & Matching (v1 — đơn giản, không AI)

- **US-120:** Là người dùng, tôi muốn vào tab "Discover" thấy danh sách người có thể dạy kỹ năng tôi muốn học.
- **US-121:** Là người dùng, tôi muốn filter theo kỹ năng, ngôn ngữ, rating, múi giờ.
- **US-122:** Là người dùng, tôi muốn xem profile đầy đủ của người khác (kỹ năng dạy, rating, bio, lịch rảnh).
- **US-123:** Là người dùng, tôi muốn tìm kiếm nhanh theo tên hoặc kỹ năng qua search bar.

### 2.4. Booking & Session

- **US-130:** Là người học, tôi muốn xem lịch rảnh của người dạy và chọn slot 15/30/60 phút.
- **US-131:** Là người học, tôi muốn book session và nhận xác nhận qua email + in-app.
- **US-132:** Là người dạy, tôi muốn nhận thông báo (email + in-app) khi có booking request.
- **US-133:** Là người dạy, tôi muốn accept hoặc decline booking.
- **US-134:** Là người dùng, tôi muốn tích hợp Google Calendar / Outlook để session tự động sync.
- **US-135:** Là người dùng, tôi muốn nhận reminder 24h, 1h trước session.
- **US-136:** Là người dùng, tôi muốn cancel session (với lý do) và được refund seeds.

### 2.5. Video Call (Real-time)

- **US-140:** Là người dùng, tôi muốn click "Join Session" trong app và mở video call ngay (không cần cài tool ngoài).
- **US-141:** Là người dùng, tôi muốn chia sẻ màn hình và dùng whiteboard cơ bản.
- **US-142:** Là người dùng, tôi muốn chat text trong session (fallback khi audio fail).
- **US-143:** Là người dùng, tôi muốn thấy chất lượng kết nối (network indicator) và có nút "Report issue".

### 2.6. Rating & Reputation v1

- **US-150:** Là người học, sau session, tôi muốn đánh giá người dạy bằng form 1 overall score (1–5) + comment ngắn.
- **US-151:** Là người dạy, sau session, tôi muốn đánh giá người học (helpfulness, respectfulness).
- **US-152:** Là người dùng, tôi muốn thấy rating trung bình và số buổi đã hoàn thành trên profile mình.

### 2.7. Seed Wallet

- **US-160:** Là người dùng mới, tôi muốn được tặng 30 Free Starter Seeds khi đăng ký.
- **US-161:** Là người dạy, sau session hoàn thành, tôi muốn nhận Seeds (1 seed/phút dạy) cộng vào wallet.
- **US-162:** Là người học, khi book session, tôi muốn Seeds bị trừ khỏi wallet.
- **US-163:** Là người dùng, tôi muốn xem số dư Seeds và lịch sử giao dịch.
- **US-164:** Là người dùng, tôi muốn thấy Seeds sắp hết hạn (trong 30 ngày tới).

---

## 3. Functional Requirements

### 3.1. Authentication & Onboarding

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-M01** | Hệ thống hỗ trợ đăng ký bằng email + password (min 8 chars, có chữ + số). | MUST |
| **FR-M02** | Hệ thống hỗ trợ OAuth2 Google và Apple ID. | MUST |
| **FR-M03** | Email xác nhận gửi qua SendGrid/Resend, có link kích hoạt (TTL 24h). | MUST |
| **FR-M04** | JWT access token (TTL 15 phút) + refresh token (TTL 30 ngày, httpOnly cookie). | MUST |
| **FR-M05** | Password hash bằng BCrypt cost 12. | MUST |
| **FR-M06** | Rate limit: 5 lần đăng nhập sai/IP/15 phút → block. | MUST |
| **FR-M07** | Form Onboarding 7 bước với progress bar, autosave mỗi step. | MUST |
| **FR-M08** | Có 30 "Free Starter Seeds" tặng cho user mới khi hoàn thành onboarding. | MUST |

### 3.2. User Profile & Skill DNA

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-M10** | User có thể thêm/sửa/xoá kỹ năng muốn dạy (offered) với: skill (từ taxonomy), level (1–5), years_experience, description, hourly_seed_rate. | MUST |
| **FR-M11** | User có thể thêm/sửa/xoá kỹ năng muốn học (wanted) với: skill, priority (1–5), target_level, notes. | MUST |
| **FR-M12** | User có thể thiết lập lịch rảnh: mỗi ngày trong tuần (day_of_week 0–6), start_time, end_time, timezone. | MUST |
| **FR-M13** | User có thể cập nhật avatar (upload ảnh ≤ 5MB), bio (max 500 chars), country_code, languages (multi-select). | MUST |
| **FR-M14** | Khi đăng ký, user nhận 30 free starter seeds, expires trong 6 tháng. | MUST |
| **FR-M15** | Có API GET `/users/me/profile` trả về đầy đủ Skill DNA để frontend render. | MUST |

### 3.3. Skills Taxonomy

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-M20** | Seed sẵn 2.000+ skills phổ biến trong bảng `skills` (categories: tech/business/art/language/life/health/...). | MUST |
| **FR-M21** | Mỗi skill có slug, name, category, parent_id (cho skill phân cấp). | MUST |
| **FR-M22** | User có thể tạo custom skill (đánh dấu `is_custom=true`), admin sẽ review. | MUST |
| **FR-M23** | API GET `/skills?query=...&category=...` để search + autocomplete. | MUST |

### 3.4. Discovery & Search

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-M30** | API GET `/discover` trả về danh sách users có skill match với wanted skills của current user. | MUST |
| **FR-M31** | Filter theo: skill_id, language, country, min_rating, timezone_offset. | MUST |
| **FR-M32** | Sort theo: rating, sessions_completed, recent_activity. | MUST |
| **FR-M33** | Pagination (offset/limit), max 50 results/page. | MUST |
| **FR-M34** | API GET `/users/{id}` trả về public profile + offered skills + rating. | MUST |
| **FR-M35** | API GET `/users/{id}/availability` trả về lịch rảnh upcoming (7 ngày tới). | MUST |

### 3.5. Booking

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-M40** | API POST `/bookings` tạo booking: teacher_id, skill_id, scheduled_at, duration_minutes. | MUST |
| **FR-M41** | Hệ thống check lịch rảnh của teacher — không cho book trùng slot. | MUST |
| **FR-M42** | Status workflow: pending → confirmed (teacher accept) → in_progress → completed → rated. Hoặc cancelled/no_show. | MUST |
| **FR-M43** | Booking confirmation gửi email cho cả teacher & learner. | MUST |
| **FR-M44** | API POST `/bookings/{id}/accept` và `/decline` cho teacher. | MUST |
| **FR-M45** | API POST `/bookings/{id}/cancel` với cancellation_reason (teacher_unavailable, learner_unavailable, technical_issue, other). | MUST |
| **FR-M46** | Auto-expire booking ở status `pending` sau 24h nếu teacher không phản hồi. | MUST |
| **FR-M47** | Idempotency key cho POST `/bookings` để chống double-submit. | MUST |
| **FR-M48** | Tích hợp Google Calendar & Outlook qua OAuth2 (gửi event invite 2 chiều). | SHOULD |
| **FR-M49** | Reminder job: 24h và 1h trước session. | MUST |

### 3.6. Video Call (Real-time)

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-M50** | Video call dùng WebRTC peer-to-peer (DTLS-SRTP encrypted). | MUST |
| **FR-M51** | Signaling qua WebSocket (Spring Boot + STOMP). | MUST |
| **FR-M52** | TURN server fallback (coturn self-hosted hoặc Twilio Network Traversal). | MUST |
| **FR-M53** | Tích hợp screen sharing (chromeAPI/getDisplayMedia). | MUST |
| **FR-M54** | Whiteboard cơ bản (canvas + brush/eraser/colors). | SHOULD |
| **FR-M55** | Chat text trong session (WebSocket, lưu DB). | MUST |
| **FR-M56** | Network quality indicator (4 mức: excellent/good/poor/bad). | SHOULD |
| **FR-M57** | Nút "Report issue" tạo incident ticket. | MUST |

**Ghi chú:** MVP dùng **Daily.co embedded SDK** hoặc **Jitsi Meet iframe** để giảm độ phức tạp. Chuyển sang WebRTC native ở Phase 2.

### 3.7. Rating & Reputation v1

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-M60** | Sau khi session chuyển status = completed, cả 2 bên nhận form đánh giá. | MUST |
| **FR-M61** | Learner đánh giá teacher với: overall_score (1–5) + review_text (max 500 chars). | MUST |
| **FR-M62** | Teacher đánh giá learner với: helpfulness_score (1–5), respectfulness_score (1–5). | MUST |
| **FR-M63** | Teacher có rating_avg và sessions_completed_count hiển thị trên profile. | MUST |
| **FR-M64** | Sau khi cả 2 đánh giá → status chuyển `rated`. | MUST |
| **FR-M65** | Nếu sau 7 ngày mà không đánh giá → auto-rate 5⭐ (default) + status = rated. | MUST |

### 3.8. Seed Wallet (v1 — đơn giản)

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-M70** | Mỗi user có 1 wallet (`seed_wallets`). | MUST |
| **FR-M71** | Mọi thay đổi số dư phải qua `seed_transactions` (ledger pattern, KHÔNG update balance trực tiếp). | MUST |
| **FR-M72** | Transaction types: `earn` (dạy), `spend` (học), `grant` (free starter), `expire`, `refund`. | MUST |
| **FR-M73** | Balance = SUM(amount) của tất cả transactions của wallet (cache trong Redis). | MUST |
| **FR-M74** | Khi book session → escrow: tạo transaction `spend` với status `pending` (chưa trừ balance). | MUST |
| **FR-M75** | Khi session complete → release escrow: teacher nhận `earn` = duration_minutes × 1 seed. | MUST |
| **FR-M76** | Nếu session cancelled trước 24h → refund 100% cho learner. | MUST |
| **FR-M77** | Nếu session cancelled trong 24h → refund 50% cho learner. | MUST |
| **FR-M78** | API GET `/wallet/me` trả về balance + 10 transaction gần nhất. | MUST |
| **FR-M79** | Scheduled job mỗi ngày 01:00 UTC: tìm transactions có `expires_at < now()` → tạo transaction `expire`. | MUST |

---

## 4. Non-Functional Requirements

| ID | Mô tả | Tiêu chí |
|----|-------|----------|
| **NFR-M01** | API response time (p95) | < 300ms cho các endpoint thường, < 1s cho search |
| **NFR-M02** | API availability | ≥ 99.5% (cho phép downtime ~3.6h/tháng) |
| **NFR-M03** | Video call success rate | ≥ 90% (P2P thành công) |
| **NFR-M04** | Database backup | Daily backup, retention 7 ngày |
| **NFR-M05** | HTTPS only | TLS 1.3, HSTS enabled |
| **NFR-M06** | GDPR compliance | Right to delete + data export API |
| **NFR-M07** | Logging | Structured JSON logs (Logback + Loki) |
| **NFR-M08** | Cost control | Total infra cost ≤ $200/tháng (AWS free tier + Railway/Render) |
| **NFR-M09** | Lighthouse score (web) | ≥ 80 mobile, ≥ 90 desktop |

---

## 5. Out of Scope (Phase 1 — đẩy sang Phase 2+)

- ❌ AI matching engine (chỉ filter-based)
- ❌ Multi-criteria rating (chỉ overall + comment)
- ❌ Liveness check / selfie AI
- ❌ Government ID verification
- ❌ Skill Passport (chỉ rating count)
- ❌ AI Co-Pilot trong session
- ❌ Mobile app (chỉ responsive web)
- ❌ Multi-modal session (code editor, AR)
- ❌ Group pods
- ❌ B2B dashboard
- ❌ Stripe payment
- ❌ Premium subscription
- ❌ Blockchain / Soulbound Token
- ❌ Push notification (chỉ email)

---

## 6. Acceptance Criteria (MVP Done)

Phase 1 được xem là **THÀNH CÔNG** khi:

| # | Tiêu chí | Mức đạt |
|---|----------|---------|
| 1 | Tất cả FR-M MUST đã implement + tested | 100% |
| 2 | Backend API docs (Swagger) đầy đủ | Có |
| 3 | Frontend responsive trên mobile + desktop | Có |
| 4 | ≥ 50 beta users đăng ký | ≥ 50 |
| 5 | ≥ 200 sessions completed | ≥ 200 |
| 6 | Show-up rate (sessions/MAU) | ≥ 30% |
| 7 | Crash-free rate | ≥ 99% |
| 8 | Lighthouse performance | ≥ 80 mobile |
| 9 | Code coverage (backend) | ≥ 60% |
| 10 | Documentation (README, API docs) | Đầy đủ |

---

## 7. Dependencies

| Phụ thuộc | Loại | Trạng thái |
|----------|------|-----------|
| Phase 0 GO decision | Quyết định | ✅ Required |
| Backend dev (Java/Spring) | Nhân sự | ✅ Founder + hire 1 nếu cần |
| Frontend freelancer (Next.js) | Nhân sự | Tuyển sau Phase 0 |
| AWS Free Tier hoặc Railway | Infrastructure | Setup tuần 1 |
| Daily.co account (video) | SaaS | Đăng ký tuần 2 |
| SendGrid/Resend | SaaS | Setup tuần 1 |
| Google OAuth credentials | External | Apply tuần 1 |
| Apple Developer account ($99/năm) | External | Required cho Apple Sign In |
| Domain + Vercel | Existing | Có sẵn từ Phase 0 |