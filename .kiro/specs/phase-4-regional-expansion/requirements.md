# Phase 4 — Regional Expansion: Requirements

> **Mục tiêu giai đoạn:** Mở rộng ra 3 quốc gia ngoài VN (Singapore, Indonesia, Philippines), triển khai Blockchain Soulbound Token (SBT) cho Skill Passport, Public API & Webhooks, AI matching v2.
> **Thời gian:** Tháng 15 → Tháng 24.
> **Team:** 15–25 người (thêm Localization, AI researcher, DevRel, Blockchain engineer).
> **Deliverable:** 200.000+ MAU, 5.000+ Premium, $1M+ ARR, $1M Series A.
> **Ngân sách:** $50.000–100.000/tháng.

---

## 1. Bối cảnh & Lý do Phase 4

Phase 3 đã có sản phẩm mature ở VN. Phase 4 nhắm vào:
- **TAM expansion:** Tăng addressable market 5–10× (VN → Đông Nam Á).
- **Tech moat deepening:** Blockchain SBT + AI v2 = khó copy.
- **Platform play:** Public API + Webhooks = ecosystem expansion.
- **Network effect:** Multi-country → cross-border skill exchange.

---

## 2. Quốc gia mục tiêu

| Quốc gia | Thị trường | Localization | Pricing | Ưu tiên |
|----------|-----------|--------------|---------|---------|
| **Singapore (SG)** | Tech-savvy, tiếng Anh, $ cao | EN, ZH | USD (SGD) | 1 |
| **Indonesia (ID)** | Lớn nhất Đông Nam Á, tiếng Bahasa | ID | IDR | 2 |
| **Philippines (PH)** | English-proficient, BPO culture | EN | PHP/USD | 3 |
| **India (IN)** | Tech hub, tiếng Anh, scale lớn | EN, HI | INR | 4 (Phase 4.5) |

**Tại sao chọn các nước này:**
- Cùng time zone (GMT+5–8) → dễ vận hành.
- Smartphone penetration > 80%.
- EdTech market tăng trưởng nhanh (15–25%/năm).
- Founder network có sẵn (founder VN có network Đông Nam Á).

---

## 3. User Stories mới

### 3.1. Internationalization (i18n)

- **US-401:** Là người dùng Indonesia, tôi muốn dùng SkillSeed bằng tiếng Bahasa Indonesia.
- **US-402:** Là người dùng Philippines, tôi muốn thanh toán bằng PHP/GrabPay/GCash.
- **US-403:** Là người dùng Singapore, tôi muốn skill taxonomy bao gồm skills phổ biến ở SG (e.g., Mandarin Business, ASEAN Tech).
- **US-404:** Là người dùng, tôi muốn cross-border matching — học từ mentor quốc gia khác nếu lịch phù hợp.

### 3.2. Blockchain Soulbound Token (SBT)

- **US-410:** Là người dùng, sau khi đạt Skill Passport, tôi muốn mint SBT trên Polygon để chứng minh credential vĩnh viễn.
- **US-411:** Là người dùng, tôi muốn SBT có thể verify được bởi bên thứ 3 (HR tech, EdTech).
- **US-412:** Là người dùng, tôi muốn xem SBT trên ví Web3 của tôi (MetaMask, WalletConnect).
- **US-413:** Là user, tôi muốn zk-proof verify (privacy-preserving) khi chia sẻ.

### 3.3. Public API & Webhooks

- **US-420:** Là developer bên thứ 3, tôi muốn REST API + OpenAPI spec để tích hợp Skill Passport vào app HR tech.
- **US-421:** Là developer, tôi muốn Webhooks khi user nhận badge mới.
- **US-422:** Là developer, tôi muốn OAuth2 client_credentials cho B2B partners.
- **US-423:** Là developer, tôi muốn rate limits cao + dedicated support.

### 3.4. AI Matching v2

- **US-430:** Là người dùng, tôi muốn AI matching hiểu context sâu hơn (industry-specific, career-level).
- **US-431:** Là người dùng, tôi muốn re-rank model được train trên data đa quốc gia.
- **US-432:** Là người dùng, tôi muốn AI giải thích match bằng ngôn ngữ của tôi.

### 3.5. Cross-border Features

- **US-440:** Là người dùng VN, tôi muốn học từ mentor Singapore (nếu lịch VN/Asia phù hợp).
- **US-441:** Là người dùng, tôi muốn currency conversion tự động theo tỷ giá thực.
- **US-442:** Là người dùng, tôi muốn xem rating review bằng ngôn ngữ tôi chọn (auto-translate).

---

## 4. Functional Requirements

### 4.1. i18n & Localization

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-I01** | Hỗ trợ 5 ngôn ngữ UI: Tiếng Việt, English, Bahasa Indonesia, Filipino, Chinese (Simplified). | MUST |
| **FR-I02** | Translation workflow: Crowdin hoặc in-house + freelancer. | MUST |
| **FR-I03** | Locale-aware content: dates, numbers, currency, timezones. | MUST |
| **FR-I04** | Skill taxonomy mở rộng per-region (skills phổ biến địa phương). | MUST |
| **FR-I05** | Auto-translate rating reviews (LLM) — show original + translated. | SHOULD |
| **FR-I06** | Skill DNA onboarding localized (câu hỏi, options). | MUST |
| **FR-I07** | Marketing pages localized + SEO (hreflang tags). | MUST |
| **FR-I08** | Support 24/7 với team đa timezone (Singapore, Manila). | SHOULD |

### 4.2. Multi-region Payment

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-R01** | Stripe Connect cho từng quốc gia (USD, SGD, IDR, PHP, VND). | MUST |
| **FR-R02** | Local payment methods: GrabPay, GCash, OVO (Indonesia), DANA, MoMo (VN), PayNow (SG). | MUST |
| **FR-R03** | Tax compliance: GST (SG), VAT (ID), BIR (PH). | MUST |
| **FR-R04** | Pricing theo PPP (Purchasing Power Parity) — Premium ID $2.9, PH $3.9, SG $9.9. | MUST |
| **FR-R05** | Currency conversion real-time (cached daily). | MUST |
| **FR-R06** | Payout local currency cho Expert (USD bank hoặc local e-wallet). | MUST |

### 4.3. Cross-border Matching

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-CB01** | User có thể bật "Cross-border matching" preference. | MUST |
| **FR-CB02** | Timezone-aware matching (match nếu overlap ≥ 2h/day). | MUST |
| **FR-CB03** | Currency conversion display (e.g., "30 seeds ≈ $3 USD"). | MUST |
| **FR-CB04** | Filter theo region (only local, only global, both). | MUST |
| **FR-CB05** | Language requirement: must overlap language. | MUST |

### 4.4. Blockchain SBT (Polygon)

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-BC01** | Smart contract SkillSBT.sol trên Polygon (ERC-5114 Soulbound). | MUST |
| **FR-BC02** | Mint SBT khi Skill Passport được issue (event listener). | MUST |
| **FR-BC03** | SBT metadata: skill, sessions_count, rating_avg, issued_at, signature. | MUST |
| **FR-BC04** | Verification endpoint `/verify/{txHash}` trả về chi tiết SBT. | MUST |
| **FR-BC05** | Wallet connection (MetaMask, WalletConnect) trên user profile. | MUST |
| **FR-BC06** | Display SBT trên profile với link tới PolygonScan. | MUST |
| **FR-BC07** | zk-proof verification (off-chain, privacy-preserving). | SHOULD |
| **FR-BC08** | Revoke SBT nếu user xóa account hoặc detected fraud. | MUST |
| **FR-BC09** | Multi-chain support (Polygon, Base, Optimism). | SHOULD |
| **FR-BC10** | Audit trail: mọi mint/revoke log vào DB + on-chain event. | MUST |

### 4.5. Public API & Webhooks

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-PA01** | REST API + OpenAPI 3.1 spec published. | MUST |
| **FR-PA02** | OAuth2 client_credentials flow cho partners. | MUST |
| **FR-PA03** | Endpoints: GET /passports/{id}, GET /users/{id}/public-profile, GET /skills/search. | MUST |
| **FR-PA04** | Webhooks: passport.issued, passport.revoked, user.skill_updated. | MUST |
| **FR-PA05** | Rate limit tiers: 1K/10K/100K req/day theo plan. | MUST |
| **FR-PA06** | Dedicated sandbox environment cho partners. | MUST |
| **FR-PA07** | SDK: TypeScript, Python, Java (OpenAPI generator). | MUST |
| **FR-PA08** | Developer portal (docs.skillseed.app). | MUST |
| **FR-PA09** | Partner tier: Free, Standard, Premium (custom pricing). | MUST |

### 4.6. AI Matching v2

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-AM01** | Multi-region training: re-train với data đa quốc gia. | MUST |
| **FR-AM02** | Industry-specific embeddings (tech, business, creative, health). | SHOULD |
| **FR-AM03** | Career-level signals (junior, mid, senior, executive). | SHOULD |
| **FR-AM04** | Cross-language embeddings (multilingual model). | MUST |
| **FR-AM05** | Match explanations localized (LLM prompt per locale). | MUST |
| **FR-AM06** | A/B test framework mature (multiple variants simultaneously). | MUST |
| **FR-AM07** | Continuous learning pipeline (weekly retrain). | MUST |
| **FR-AM08** | Bias detection & fairness monitoring (avoid regional bias). | MUST |

### 4.7. Data Residency & Compliance

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-DC01** | Data residency per region: SG data ở ap-southeast-1, ID ở ap-southeast-3 (Jakarta nếu có), PH ở ap-southeast-5 (Manila). | SHOULD |
| **FR-DC02** | GDPR-EU compliance (cho future EU expansion). | SHOULD |
| **FR-DC03** | Local data protection law compliance (UU PDP Indonesia, DPA Philippines). | MUST |
| **FR-DC04** | Cookie consent per region (different vendors). | MUST |
| **FR-DC05** | Data export + delete APIs localize theo region. | MUST |
| **FR-DC06** | SOC 2 Type II audit (B2B Enterprise). | MUST |

---

## 5. Non-Functional Requirements

| ID | Mô tả | Tiêu chí |
|----|-------|----------|
| **NFR-X01** | Multi-region latency (p95) | < 200ms cho user ở region của họ |
| **NFR-X02** | Public API uptime | ≥ 99.95% |
| **NFR-X03** | SBT mint cost | < $0.01 per mint (Polygon cheap) |
| **NFR-X04** | Translation coverage | ≥ 95% strings translated |
| **NFR-X05** | Mobile app store rating | ≥ 4.5/5 |
| **NFR-X06** | Customer support response | < 24h (B2C), < 4h (B2B, Premium) |
| **NFR-X07** | Concurrent users | ≥ 50.000 |
| **NFR-X08** | Sessions/day capacity | ≥ 100.000 |

---

## 6. Out of Scope (Phase 4)

- ❌ AR/VR sessions (Phase 5)
- ❌ Voice-first AI matching (Phase 5)
- ❌ Offline community events (Phase 5)
- ❌ M&A integration (Phase 5)
- ❌ EU/US expansion (Phase 5+)
- ❌ Crypto payments / Web3 native features khác ngoài SBT

---

## 7. Acceptance Criteria (Phase 4 Done)

| # | Tiêu chí | Mức đạt |
|---|----------|---------|
| 1 | Live ở ≥ 4 quốc gia | VN + SG + ID + PH |
| 2 | i18n coverage ≥ 95% | VN, EN, ID, FIL, ZH |
| 3 | Local payment methods per region | ≥ 1 per region |
| 4 | MAU | ≥ 200.000 |
| 5 | Premium subs | ≥ 5.000 |
| 6 | B2B clients | ≥ 50 |
| 7 | ARR | ≥ $1M |
| 8 | Skill Passports minted (lifetime) | ≥ 50.000 |
| 9 | SBT minted (lifetime) | ≥ 10.000 |
| 10 | Public API partners | ≥ 20 |
| 11 | NPS | ≥ 55 |
| 12 | App store rating | ≥ 4.5 |
| 13 | SOC 2 Type II audit | Passed |
| 14 | Series A raised | $3-5M closed |

---

## 8. Dependencies

| Phụ thuộc | Loại | Trạng thái |
|----------|------|-----------|
| Phase 3 Done | Quyết định | ✅ Required |
| Series A $3-5M | Funding | Trước tháng 15 |
| Polygon/Mumbai RPC access | External | Setup tháng 15 |
| Translation agency (Crowdin) | External | Contract tháng 15 |
| Local entity in SG/ID/PH | Legal | Setup tháng 16 |
| Local payment processor | External | Stripe Connect + local |
| AI researcher | Nhân sự | Tuyển tháng 16 |
| Blockchain engineer | Nhân sự | Tuyển tháng 17 |
| Localization manager | Nhân sự | Tuyển tháng 16 |
| Country GM x 3 (SG, ID, PH) | Nhân sự | Tuyển tháng 17 |
| DevRel | Nhân sự | Tuyển tháng 18 |