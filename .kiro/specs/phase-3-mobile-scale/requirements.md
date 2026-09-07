# Phase 3 — Mobile + Scale: Requirements

> **Mục tiêu giai đoạn:** Mobile native app (iOS + Android), B2B workspace MVP, Stripe Premium, Learning Pods, Skill Passport v1, multi-currency, AI Personality Insights.
> **Thời gian:** Tháng 9 → Tháng 14.
> **Team:** 6–10 người (thêm Mobile dev, DevOps, Designer full-time, PM, B2B Sales).
> **Deliverable:** 20.000–50.000 MAU, 1.000+ Premium subs, 5+ B2B clients.
> **Ngân sách:** $15.000–30.000/tháng.
> **Fundraising target:** Seed round $500K–$1M trước/sau giai đoạn này.

---

## 1. Bối cảnh & Lý do Phase 3

Phase 2 đã chứng minh AI matching + PWA hoạt động với 1K+ MAU. Phase 3 tập trung vào:
- **Mobile native:** User thị trường mục tiêu (Đông Nam Á) dùng mobile 90%, cần app store presence.
- **Monetization:** Premium subscription + B2B contracts.
- **Differentiation sâu hơn:** Learning Pods, Skill Passport, AI Personality.
- **Scale infrastructure:** Từ monolith sang microservices có chọn lọc + Kubernetes.

---

## 2. User Stories mới

### 2.1. Mobile Native App

- **US-301:** Là người dùng mobile, tôi muốn download app SkillSeed từ App Store / Google Play.
- **US-302:** Là người dùng mobile, tôi muốn biometric login (Face ID / Touch ID / Fingerprint).
- **US-303:** Là người dùng mobile, tôi muốn camera tích hợp để chụp avatar + verify identity ngay trong app.
- **US-304:** Là người dùng mobile, tôi muốn push notification tốt hơn (rich media, action buttons).
- **US-305:** Là người dùng mobile, tôi muốn widget trên home screen hiển thị session sắp tới.

### 2.2. Premium Subscription

- **US-310:** Là người dùng, tôi muốn đăng ký Premium ($9.9/tháng) để unlock: unlimited booking, AI insights, priority matching, no ads.
- **US-311:** Là người dùng, tôi muốn quản lý subscription (upgrade, downgrade, cancel).
- **US-312:** Là người dùng, tôi muốn thanh toán qua Stripe (card, Apple Pay, Google Pay, Momo, VNPay).
- **US-313:** Là gia đình (4 người), tôi muốn mua Family Plan ($14.9/tháng) để share seed pool.

### 2.3. B2B Workspace

- **US-320:** Là HR Manager, tôi muốn dashboard để xem skill map của công ty.
- **US-321:** Là HR Manager, tôi muốn tạo "skill pool" nội bộ cho team/department.
- **US-322:** Là Employee, tôi muốn join công ty tôi qua SSO + domain email.
- **US-323:** Là HR Manager, tôi muốn báo cáo (skills gap, learning velocity, engagement).
- **US-324:** Là HR Manager, tôi muốn SSO (SAML/OIDC) + audit log cho compliance.

### 2.4. Learning Pods

- **US-330:** Là người dùng, tôi muốn tạo Pod 4–8 người cùng chủ đề (e.g., "Public Speaking Club VN").
- **US-331:** Là người dùng, tôi muốn AI gợi ý Pod trending theo khu vực + sở thích của tôi.
- **US-332:** Là Pod creator, tôi muốn schedule session nhóm (multi-participant video).
- **US-333:** Là Pod member, tôi muốn discussion forum trong Pod.
- **US-334:** Là Pod creator (Premium), tôi muốn charge phí tham gia Pod (mastermind model).

### 2.5. Skill Passport v1

- **US-340:** Là người dùng, sau 5+ buổi học rating ≥ 4.5, tôi muốn nhận Skill Passport cho kỹ năng đó.
- **US-341:** Là người dùng, tôi muốn public link `/passport/{id}` để chia sẻ trên LinkedIn.
- **US-342:** Là người dùng, tôi muốn Open Graph image đẹp khi share.
- **US-343:** Là người dùng, tôi muốn download PDF Skill Passport.

### 2.6. Multi-modal Sessions

- **US-350:** Là người dùng (dạy lập trình), tôi muốn code editor live (Monaco/CodeMirror) trong session.
- **US-351:** Là người dùng (dạy yoga/nấu ăn), tôi muốn camera sau + AR overlay cơ bản.
- **US-352:** Là người dùng, tôi muốn interactive quiz mid-session (giáo viên tạo, học viên trả lời).

### 2.7. AI Personality Insights (Premium)

- **US-360:** Là Premium user, tôi muốn xem personality profile chi tiết (learning style, pace, strengths).
- **US-361:** Là Premium user, tôi muốn nhận lộ trình 6 tháng cá nhân hoá (AI-generated).
- **US-362:** Là Premium user, tôi muốn so sánh ẩn danh với cohort cùng nhóm.

### 2.8. Marketplace mở rộng

- **US-370:** Là Verified Expert, tôi muốn bán buổi premium bằng USD ($20–$200/session).
- **US-371:** Là user, tôi muốn mua Seed Pack ($4.9/$19.9/$49.9) để học Expert.
- **US-372:** Là NGO, tôi muốn tặng Seeds cho user thu nhập thấp.
- **US-373:** Là công ty, tôi muốn mua bulk credits cấp cho nhân viên.

---

## 3. Functional Requirements

### 3.1. Mobile Native App (React Native)

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-M01** | iOS app buildable + submitable lên App Store. | MUST |
| **FR-M02** | Android app buildable + submitable lên Google Play. | MUST |
| **FR-M03** | Single codebase (React Native + Expo) — share 70% logic với web. | MUST |
| **FR-M04** | Biometric login (Face ID, Touch ID, Fingerprint). | MUST |
| **FR-M05** | Push notification qua APNs/FCM. | MUST |
| **FR-M06** | In-app camera (avatar capture, ID verification). | MUST |
| **FR-M07** | Deep linking (`skillseed://booking/{id}`). | MUST |
| **FR-M08** | Home screen widget (Android) / Live Activity (iOS 16+). | SHOULD |
| **FR-M09** | Offline mode cải tiến (xem booking, profile, saved notes). | MUST |
| **FR-M10** | Same backend API với web (không cần backend riêng cho mobile). | MUST |

### 3.2. Premium Subscription & Payments

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-P01** | Stripe Customer tạo khi user đăng ký Premium. | MUST |
| **FR-P02** | Stripe Subscription cho Premium ($9.9/tháng) và Family ($14.9/tháng). | MUST |
| **FR-P03** | Payment methods: card, Apple Pay, Google Pay, Momo, VNPay. | MUST |
| **FR-P04** | Webhook handler cho Stripe events (invoice.paid, subscription.deleted, ...). | MUST |
| **FR-P05** | User có thể upgrade/downgrade/cancel qua `/billing` portal. | MUST |
| **FR-P06** | Free tier giới hạn 10 buổi/tháng với non-expert mentor. | MUST |
| **FR-P07** | Premium features gating ở backend (không chỉ frontend). | MUST |

### 3.3. B2B Workspace

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-B01** | Organization entity (id, name, plan, billing_email, sso_config). | MUST |
| **FR-B02** | User có thể join org qua SSO (Google Workspace, Microsoft Entra) hoặc invite email. | MUST |
| **FR-B03** | Org Admin quản lý members (add, remove, assign role). | MUST |
| **FR-B04** | Skill Pool: org-level list of skills + assigned experts. | MUST |
| **FR-B05** | Internal matching: tìm đồng nghiệp có thể dạy kỹ năng bạn muốn học. | MUST |
| **FR-B06** | Dashboard cho org admin: MAU, skills gap heatmap, learning velocity. | MUST |
| **FR-B07** | Reporting API: export CSV/PDF. | MUST |
| **FR-B08** | SSO (SAML 2.0 + OIDC) cho Enterprise. | MUST |
| **FR-B09** | Audit log cho mọi action (GDPR/SOC2 compliance). | MUST |
| **FR-B10** | Custom branding (logo, primary color) cho Enterprise. | SHOULD |

### 3.4. Learning Pods

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-PD01** | Tạo Pod: name, topic, description, max_members (4–8), is_public. | MUST |
| **FR-PD02** | Join/leave Pod. | MUST |
| **FR-PD03** | Pod session: multi-participant video (max 8 người). | MUST |
| **FR-PD04** | Pod discussion forum (text posts, replies, reactions). | MUST |
| **FR-PD05** | AI suggestions: gợi ý Pod trending theo khu vực + interests. | MUST |
| **FR-PD06** | Pod analytics (engagement, members activity) cho creator. | SHOULD |
| **FR-PD07** | Premium Pod: charge phí tham gia ($10–$100/tháng). | SHOULD |
| **FR-PD08** | Pod discover page `/pods` với filter. | MUST |

### 3.5. Skill Passport v1

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-S01** | Auto-issue Skill Passport khi user đạt: 5+ sessions với skill X, rating avg ≥ 4.5. | MUST |
| **FR-S02** | Skill Passport entity: user_id, skill_id, sessions_count, avg_rating, issued_at, revoked. | MUST |
| **FR-S03** | Public page `/passport/{id}` — đẹp, có OG image. | MUST |
| **FR-S04** | PDF download (qua Puppeteer hoặc React-PDF). | MUST |
| **FR-S05** | LinkedIn share button (manual copy link). | MUST |
| **FR-S06** | Passport hiển thị trên profile + Discover card. | MUST |

### 3.6. Multi-modal Sessions

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-MS01** | Code editor live (Monaco) cho sessions với skill category = tech. | MUST |
| **FR-MS02** | Code editor có shared cursor + realtime updates. | MUST |
| **FR-MS03** | Camera switch (front/back) + AR overlay cơ bản (WebXR). | SHOULD |
| **FR-MS04** | Interactive quiz: teacher tạo câu hỏi, learner trả lời trong session. | MUST |
| **FR-MS05** | Quiz results lưu vào DB, hiển thị trong summary. | MUST |

### 3.7. AI Personality Insights (Premium)

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-AI01** | Phân tích learning behavior từ lịch sử session (pace, style, strength). | MUST |
| **FR-AI02** | Tạo 6-month learning roadmap (LLM). | MUST |
| **FR-AI03** | Ẩn danh comparison với cohort (e.g., "Bạn học nhanh hơn 70% users cùng nhóm"). | SHOULD |
| **FR-AI04** | Insights cập nhật mỗi tháng. | MUST |

### 3.8. Marketplace

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-MK01** | Expert có thể set "premium price" cho session (USD $20–$200). | MUST |
| **FR-MK02** | User mua bằng Stripe (USD). | MUST |
| **FR-MK03** | Platform commission 15–20% tự động trừ qua Stripe Connect. | MUST |
| **FR-MK04** | Seed Pack purchases ($4.9/$19.9/$49.9) cộng seeds vào wallet. | MUST |
| **FR-MK05** | NGO gifting portal (admin create credits cho user). | SHOULD |
| **FR-MK06** | Corporate bulk credit purchase (invoiced). | MUST |

### 3.9. Multi-currency

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-C01** | Hỗ trợ USD, VND, SGD, IDR, INR cho Premium subscription. | MUST |
| **FR-C02** | Hiển thị giá theo locale của user. | MUST |
| **FR-C03** | Backend lưu canonical currency (USD), frontend convert. | MUST |
| **FR-C04** | Exchange rate cập nhật daily (cached). | MUST |

---

## 4. Non-Functional Requirements

| ID | Mô tả | Tiêu chí |
|----|-------|----------|
| **NFR-S01** | Mobile app size (download) | < 50MB |
| **NFR-S02** | Mobile app cold start | < 2s |
| **NFR-S03** | Backend p95 latency | < 300ms |
| **NFR-S04** | Matching p95 latency | < 1.5s |
| **NFR-S05** | Uptime | ≥ 99.9% |
| **NFR-S06** | Concurrent users (peak) | ≥ 10.000 |
| **NFR-S07** | API rate limit | 1000 req/min cho Premium, 10.000 cho B2B API key |
| **NFR-S08** | B2B SSO login time | < 3s |
| **NFR-S09** | Data residency (B2B Enterprise) | Configurable per region (APAC, EU, US) |
| **NFR-S10** | SOC 2 Type II readiness (B2B Enterprise) | Required cho deals > $100K |

---

## 5. Out of Scope (Phase 3 — đẩy sang Phase 4+)

- ❌ Blockchain Soulbound Token (Phase 4)
- ❌ Public API & Webhooks cho third-party (Phase 4)
- ❌ i18n đầy đủ 5 ngôn ngữ (Phase 4)
- ❌ AI matching v2 deep personalization (Phase 4)
- ❌ Regional expansion ngoài VN (Phase 4)
- ❌ AR/VR sessions (Phase 5)
- ❌ Voice-first AI matching (Phase 5)

---

## 6. Acceptance Criteria (Phase 3 Done)

| # | Tiêu chí | Mức đạt |
|---|----------|---------|
| 1 | iOS + Android app live trên stores | Có |
| 2 | ≥ 50% mobile users (downloaded app) | ≥ 50% |
| 3 | MAU | ≥ 20.000 |
| 4 | Premium subs | ≥ 1.000 |
| 5 | B2B clients | ≥ 5 |
| 6 | Stripe MRR | ≥ $10.000 |
| 7 | Sessions/month | ≥ 10.000 |
| 8 | D30 retention | ≥ 25% |
| 9 | Skill Passports issued (lifetime) | ≥ 5.000 |
| 10 | Pods created (lifetime) | ≥ 100 |
| 11 | NPS | ≥ 50 |
| 12 | B2B churn | < 5% monthly |

---

## 7. Dependencies

| Phụ thuộc | Loại | Trạng thái |
|----------|------|-----------|
| Phase 2 Done | Quyết định | ✅ Required |
| Seed round $500K–$1M | Funding | Trước tháng 9 |
| Apple Developer account ($99/yr) | Existing | Có |
| Google Play Developer ($25 one-time) | External | Đăng ký |
| Stripe account | External | Apply tháng 9 |
| Mobile dev (React Native) | Nhân sự | Tuyển tháng 8 |
| DevOps engineer | Nhân sự | Tuyển tháng 9 |
| Designer full-time | Nhân sự | Tuyển tháng 9 |
| B2B Sales | Nhân sự | Tuyển tháng 10 |
| Customer Success | Nhân sự | Tuyển tháng 11 |