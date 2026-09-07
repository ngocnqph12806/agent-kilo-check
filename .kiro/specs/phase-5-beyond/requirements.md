# Phase 5 — Beyond: Requirements

> **Mục tiêu giai đoạn:** Đẩy SkillSeed lên tầm vision — "GitHub of real skills" toàn cầu. Voice-first AI matching, AR/VR sessions cho trải nghiệm immersive, offline community events, EU/US expansion, và chuẩn bị cho Series B / strategic exit.
> **Thời gian:** Năm 2 → Năm 3.
> **Team:** 30–60 người (thêm AR/VR engineer, Voice AI specialist, EU/US country teams, M&A team).
> **Deliverable:** 1M+ MAU, $10M+ ARR, Series B $15-25M hoặc strategic exit.
> **Ngân sách:** $200.000–500.000/tháng.

---

## 1. Bối cảnh & Lý do Phase 5

Phase 4 đã có 200K+ MAU, $1M+ ARR, 4 quốc gia. Phase 5 là giai đoạn **"moonshot"**:
- **Vision execution:** Trở thành "GitHub of real skills" thực sự — global, trusted, verifiable.
- **Tech frontier:** Voice AI, AR/VR là next frontier sau mobile.
- **Market expansion:** EU + US là thị trường lớn nhưng đòi hỏi khác biệt (compliance, language, payment).
- **Strategic options:** Series B scale, hoặc exit (M&A) với strategic acquirer.

---

## 2. User Stories Vision-level

### 2.1. Voice-first AI Matching

- **US-501:** Là người dùng, tôi muốn mở app và **nói** "Tôi muốn học public speaking với người Singapore" — AI hiểu và match ngay.
- **US-502:** Là người dùng, tôi muốn có voice assistant tư vấn skill roadmap qua hội thoại tự nhiên.
- **US-503:** Là người dùng (Accessibility), tôi muốn dùng voice-first thay vì đọc/gõ (cho người khiếm thị hoặc busy).

### 2.2. AR/VR Sessions

- **US-510:** Là người dùng (dạy yoga), tôi muốn AR overlay để học viên thấy form chuẩn ngay trong không gian thật.
- **US-511:** Là người dùng (dạy sửa chữa), tôi muốn AR step-by-step overlay lên thiết bị cần sửa.
- **US-512:** Là người dùng, tôi muốn **VR classroom** — avatar representation, whiteboard 3D, immersive (Meta Horizon, Apple Vision Pro).
- **US-513:** Là người dùng, tôi muốn record AR/VR session để rewatch.

### 2.3. Offline Community Events

- **US-520:** Là người dùng, tôi muốn tham gia offline SkillSeed meetup ở thành phố tôi.
- **US-521:** Là organizer, tôi muốn tạo offline event qua platform (location, time, topic, RSVP).
- **US-522:** Là Premium user, tôi muốn event space booking (hỗ trợ địa điểm coworking).
- **US-523:** Là user, tôi muốn earn Seeds khi tham gia offline event (check-in GPS verified).

### 2.4. EU/US Expansion

- **US-530:** Là user EU, tôi muốn dùng SkillSeed tuân thủ GDPR đầy đủ.
- **US-531:** Là user Mỹ, tôi muốn thanh toán USD qua Apple Pay / Google Pay / Venmo.
- **US-532:** Là user EU, tôi muốn content localized (Tiếng Đức, Pháp, Tây Ban Nha, Ý, Hà Lan).
- **US-533:** Là user Mỹ (creator), tôi muốn monetize qua skill subscription model (giống Substack cho skills).

### 2.5. AI Native Skills Verification

- **US-540:** Là employer (HR), tôi muốn verify candidate's Skill Passport với cryptographic proof (zk-SNARK).
- **US-541:** Là user, tôi muốn selective disclosure — chỉ reveal rating mà không reveal identity.

### 2.6. Network Effects Amplification

- **US-550:** Là user, tôi muốn "Endorse" skill của đồng nghiệp (LinkedIn-style endorsement nhưng on-chain).
- **US-551:** Là user, tôi muốn "follow" mentor để nhận updates về session mới.
- **US-552:** Là Premium user, tôi muốn AI coach cá nhân dài hạn (không chỉ 6 tháng roadmap mà ongoing).

---

## 3. Functional Requirements

### 3.1. Voice-first AI

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-V01** | Voice input (mic) → STT → LLM intent → action. | MUST |
| **FR-V02** | Voice output (TTS) cho AI assistant responses. | MUST |
| **FR-V03** | Streaming realtime (không đợi full utterance). | MUST |
| **FR-V04** | Multilingual voice (EN, VI, ID, FIL, ZH, DE, FR, ES, IT, NL). | MUST |
| **FR-V05** | Conversation context persistence (across sessions). | MUST |
| **FR-V06** | Voice biometric cho authentication (optional, opt-in). | SHOULD |
| **FR-V07** | Wake word "Hey SkillSeed" (optional cho mobile). | SHOULD |

### 3.2. AR/VR Sessions

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-AR01** | AR overlay cho camera mobile (WebXR + ARKit/ARCore). | MUST |
| **FR-AR02** | Multi-user AR session (2+ người cùng nhìn vào AR scene). | MUST |
| **FR-AR03** | Anchor sharing (cùng coordinate system). | MUST |
| **FR-AR04** | AR annotation tools (draw, text, arrow). | MUST |
| **FR-AR05** | Apple Vision Pro native app (visionOS). | MUST |
| **FR-AR06** | Meta Quest app (Horizon Worlds hoặc native). | SHOULD |
| **FR-AR07** | 3D models library (cho fitness, cooking, repair, ...). | MUST |
| **FR-AR08** | VR avatar representation (full body, face tracking). | SHOULD |
| **FR-AR09** | Immersive whiteboard 3D. | SHOULD |
| **FR-AR10** | Recording + replay cho AR/VR session. | MUST |

### 3.3. Offline Events Platform

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-OF01** | Event entity: title, description, datetime, capacity, location, topic. | MUST |
| **FR-OF02** | RSVP + waitlist. | MUST |
| **FR-OF03** | GPS check-in verification (geofencing). | MUST |
| **FR-OF04** | Event chat (participants). | MUST |
| **FR-OF05** | Photo/video upload từ event. | MUST |
| **FR-OF06** | Seed earning cho attendance (5 seeds/event). | MUST |
| **FR-OF07** | Event discover page với map view. | MUST |
| **FR-OF08** | Partnership với coworking spaces (booking API). | SHOULD |
| **FR-OF09** | QR check-in (alternative cho GPS). | MUST |
| **FR-OF10** | Event analytics cho organizer. | SHOULD |

### 3.4. EU/US Compliance

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-EU01** | GDPR đầy đủ (Right to be forgotten, data portability, DPIA). | MUST |
| **FR-EU02** | EU data residency (region: eu-west-1, eu-central-1). | MUST |
| **FR-EU03** | Cookie consent theo GDPR (granular). | MUST |
| **FR-EU04** | DPO (Data Protection Officer) hire. | MUST |
| **FR-EU05** | Privacy by design audit. | MUST |
| **FR-EU06** | CCPA compliance (California). | MUST |
| **FR-EU07** | State-specific compliance cho US (CCPA, VCDPA, CPA, ...). | MUST |
| **FR-EU08** | ADA accessibility (WCAG 2.1 AA). | MUST |

### 3.5. EU/US Localization

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-L01** | Thêm 6 ngôn ngữ EU: DE, FR, ES, IT, NL, PL. | MUST |
| **FR-L02** | Thêm EN-US variant (date/time/number formats). | MUST |
| **FR-L03** | Local skill taxonomy (e.g., "Vinification" cho Pháp, "Bavarian" cooking cho Đức). | MUST |
| **FR-L04** | Local payment: SEPA, iDEAL, Klarna, Bancontact, Giropay. | MUST |
| **FR-L05** | Venmo, Cash App, Zelle cho US. | MUST |
| **FR-L06** | Country-specific marketing pages. | MUST |

### 3.6. Creator Subscription (US)

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-CR01** | Expert có thể tạo subscription tier ($5/$15/$50/tháng). | MUST |
| **FR-CR02** | Subscriber nhận exclusive content (group sessions, materials, Q&A). | MUST |
| **FR-CR03** | Stripe Billing Subscription cho creator tier. | MUST |
| **FR-CR04** | Expert dashboard: subscribers, MRR, churn. | MUST |
| **FR-CR05** | Discovery surface cho premium experts. | SHOULD |

### 3.7. Advanced Reputation

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-REP01** | zk-SNARK proof cho Skill Passport (privacy-preserving verification). | MUST |
| **FR-REP02** | Endorsement graph (user A endorse user B cho skill X). | MUST |
| **FR-REP03** | Endorsement impact lên reputation score. | MUST |
| **FR-REP04** | Selective disclosure (reveal "Public Speaking ≥ 4.5 stars" mà không show identity). | MUST |
| **FR-REP05** | Reputation score formula mở, công khai (algorithmic transparency). | SHOULD |

### 3.8. Series B / Strategic Exit Preparation

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-EX01** | Technical audit cho due diligence. | MUST |
| **FR-EX02** | IP portfolio review (patents, trademarks). | MUST |
| **FR-EX03** | Financial audit (Big 4). | MUST |
| **FR-EX04** | Customer reference calls. | MUST |
| **FR-EX05** | M&A materials (strategic acquirers list). | MUST |
| **FR-EX06** | Strategic acquirer outreach (LinkedIn, Coursera, Udemy, Pearson, Byju's). | MUST |

---

## 4. Non-Functional Requirements

| ID | Mô tả | Tiêu chí |
|----|-------|----------|
| **NFR-V01** | Voice latency (STT + LLM + TTS) | < 1.5s end-to-end |
| **NFR-V02** | Voice accuracy (multilingual) | ≥ 95% WER |
| **NFR-V03** | AR/VR frame rate | ≥ 60 FPS |
| **NFR-V04** | AR/VR session stability | ≥ 95% session uptime |
| **NFR-V05** | Multi-region (8+ regions) p95 | < 200ms in-region |
| **NFR-V06** | Concurrent users | ≥ 200.000 |
| **NFR-V07** | Sessions/day capacity | ≥ 500.000 |
| **NFR-V08** | API uptime | ≥ 99.99% |
| **NFR-V09** | zk-proof generation time | < 5s |
| **NFR-V10** | zk-proof verification time | < 1s |

---

## 5. Out of Scope (Phase 5 — đẩy sang Phase 6+)

- ❌ IPO preparation (Phase 6)
- ❌ Decentralized autonomous organization (DAO) governance (Phase 6)
- ❌ Brain-computer interface (BCI) experiments (Phase 6)
- ❌ AGI tutor integration (Phase 6)
- ❌ Full offline-first PWA (Phase 6)

---

## 6. Acceptance Criteria (Phase 5 Done)

| # | Tiêu chí | Mức đạt |
|---|----------|---------|
| 1 | Voice-first matching live | ≥ 20% users sử dụng voice monthly |
| 2 | AR/VR session capability | 3+ verticals (fitness, cooking, repair) có AR templates |
| 3 | Offline events platform | ≥ 1.000 events hosted/year |
| 4 | EU live (5+ countries) | DE, FR, NL, IT, ES, PL |
| 5 | US live | Có payment + US tax + ADA |
| 6 | MAU | ≥ 1.000.000 |
| 7 | Premium subs | ≥ 30.000 |
| 8 | ARR | ≥ $10M |
| 9 | B2B clients | ≥ 200 |
| 10 | Creator subscribers | ≥ 5.000 |
| 11 | Total Skill Passports | ≥ 200.000 |
| 12 | zk-proof verify working | Live + adopted by 3+ HR tech partners |
| 13 | Series B raised | $15-25M closed OR strategic exit deal signed |
| 14 | NPS | ≥ 60 |

---

## 7. Dependencies

| Phụ thuộc | Loại | Trạng thái |
|----------|------|-----------|
| Phase 4 Done | Quyết định | ✅ Required |
| Series B $15-25M | Funding | Đầu năm 2 |
| Voice AI expertise (Realtime API) | Hiring | Voice AI engineer |
| AR/VR expertise | Hiring | 2-3 specialists |
| Apple Vision Pro dev kit | External | Apply to Apple |
| Meta Quest partnership | External | Outreach |
| EU legal counsel | External | Hire |
| US legal counsel | External | Hire |
| zk-SNARK expertise | Hiring | 1-2 cryptographers |