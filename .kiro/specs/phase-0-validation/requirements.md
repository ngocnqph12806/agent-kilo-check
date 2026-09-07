# Phase 0 — Validation: Requirements

> **Mục tiêu giai đoạn:** Xác nhận Product-Market Fit (PMF) trước khi viết một dòng code backend nào.
> **Thời gian:** Tuần 1 → Tuần 4
> **Owner:** Founder + 1 freelancer thiết kế
> **Quyết định cuối giai đoạn:** GO (tiếp tục Phase 1) hoặc NO-GO (pivot/dừng).

---

## 1. User Stories

### 1.1. Founder (PMF Researcher)

- **US-V01:** Là founder, tôi muốn có một danh sách 30 người thuộc 5 nhóm persona để bắt đầu phỏng vấn.
- **US-V02:** Là founder, tôi muốn có một bộ câu hỏi phỏng vấn 30 phút để so sánh kết quả giữa các nhóm.
- **US-V03:** Là founder, tôi muốn có một landing page thu thập email + 2 câu hỏi định lượng để đo mức độ quan tâm.
- **US-V04:** Là founder, tôi muốn chạy quảng cáo $200 để đo conversion rate thực tế từ cold traffic.
- **US-V05:** Là founder, tôi muốn một dashboard thu thập dữ liệu (số phỏng vấn, số email, conversion) để ra quyết định GO/NO-GO.
- **US-V06:** Là founder, tôi muốn một Validation Report PDF để chia sẻ với co-founder potential, advisor và nhà đầu tư giai đoạn pre-seed.

### 1.2. Người được phỏng vấn (Customer Interviewee)

- **US-V07:** Là người quan tâm đến việc học kỹ năng mới, tôi muốn trả lời một bản khảo sát ngắn (<5 phút) để thể hiện nhu cầu.
- **US-V08:** Là người có kỹ năng muốn chia sẻ, tôi muốn đăng ký waitlist để được ưu tiên khi sản phẩm ra mắt.

### 1.3. Nhà đầu tư tiềm năng (Pre-seed investor)

- **US-V09:** Là angel investor, tôi muốn xem một Validation Report rõ ràng để quyết định tham gia vòng pre-seed.

---

## 2. Functional Requirements

### 2.1. Khảo sát & Phỏng vấn

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-V01** | Hệ thống phải cung cấp script phỏng vấn 30 phút gồm 12–15 câu hỏi mở, bao phủ 5 vấn đề cốt lõi: pain hiện tại, willingness-to-pay, willingness-to-teach-free, kênh tìm mentor hiện tại, phản ứng với concept SkillSeed. | MUST |
| **FR-V02** | Mỗi phỏng vấn phải được ghi âm (sau khi có consent) và lưu transcript dạng text. | MUST |
| **FR-V03** | Kết quả phỏng vấn phải được tag theo persona (5 nhóm) và đánh dấu "would-use" / "would-not-use". | MUST |
| **FR-V04** | Phải tạo được Google Form hoặc Typeform khảo sát 5 phút gồm 8 câu hỏi (multiple choice + Likert scale + 2 câu mở). | MUST |

### 2.2. Landing Page & Waitlist

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-V10** | Landing page phải tải trong <2 giây (LCP) trên 3G mobile. | MUST |
| **FR-V11** | Phải có Hero section với tagline, giải thích vấn đề, giải pháp, "How it works" 3 bước, social proof placeholder. | MUST |
| **FR-V12** | Phải có form đăng ký email + 2 câu hỏi: (1) "Bạn có sẵn sàng dạy miễn phí kỹ năng X không?", (2) "Bạn sẵn sàng học miễn phí kỹ năng Y không?". | MUST |
| **FR-V13** | Phải tích hợp với email service (Resend hoặc Brevo) để gửi email xác nhận + auto-responder. | MUST |
| **FR-V14** | Phải có tracking pixel Google Analytics 4 + Meta Pixel cho quảng cáo. | MUST |
| **FR-V15** | Phải có trang `/privacy` và `/terms` cơ bản để tuân thủ GDPR/PDPA khi thu thập email. | MUST |
| **FR-V16** | Phải responsive trên mobile (iPhone SE trở lên) và desktop. | MUST |

### 2.3. Paid Ads Experiment

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-V20** | Phải chạy được Facebook Ads và Google Ads với tổng budget $200. | MUST |
| **FR-V21** | Mỗi ad variant phải có UTM parameter riêng để tracking conversion theo kênh. | MUST |
| **FR-V22** | Phải đo được các chỉ số: CPM, CTR, CPC, Conversion Rate (visitor→waitlist), Cost per Email. | MUST |

### 2.4. Validation Dashboard

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-V30** | Phải có Google Sheet dashboard cập nhật tự động (qua Zapier/Make) từ: (a) form submissions, (b) CRM, (c) ad platforms. | MUST |
| **FR-V31** | Dashboard phải hiển thị: tổng interviews, % would-use, số waitlist, conversion rate, CAC ước tính. | MUST |

### 2.5. Validation Report

| ID | Mô tả | Ưu tiên |
|----|-------|--------|
| **FR-V40** | Phải xuất được Validation Report PDF 8–12 trang gồm: Executive Summary, Persona Insights, Quantitative Results, Key Findings, GO/NO-GO Recommendation. | MUST |

---

## 3. Non-Functional Requirements

| ID | Mô tả | Tiêu chí đo |
|----|-------|------------|
| **NFR-V01** | Landing page uptime | ≥ 99.5% trong 4 tuần |
| **NFR-V02** | Privacy compliance | Có consent checkbox + link Privacy Policy |
| **NFR-V03** | Cost control | Tổng chi phí ads ≤ $200, tools ≤ $50/tháng |
| **NFR-V04** | Time-to-launch | Landing page live trong tuần 2 |
| **NFR-V05** | Data integrity | Không mất dữ liệu khảo sát kể cả khi nhà cung cấp form sập (export CSV backup hàng tuần) |

---

## 4. Acceptance Criteria (GO/NO-GO)

Phase 0 được xem là **THÀNH CÔNG (GO)** khi đồng thời đạt **≥ 4/5 tiêu chí** sau:

| # | Tiêu chí | Mức GO | Mức NO-GO |
|---|----------|--------|-----------|
| 1 | Số phỏng vấn hoàn thành | ≥ 30 | < 20 |
| 2 | % người nói "có thể sẽ dùng" (trong phỏng vấn) | ≥ 50% | < 30% |
| 3 | Số email đăng ký waitlist | ≥ 200 | < 100 |
| 4 | Conversion rate (ad visitor → email) | ≥ 8% | < 3% |
| 5 | Cost per Email (CAC proxy) | ≤ $2 | > $5 |

**Nếu đạt 4/5 → GO, scale nhẹ Phase 1.**
**Nếu đạt 2-3/5 → PIVOT: thay đổi positioning/persona và chạy lại 2 tuần.**
**Nếu đạt 0-1/5 → NO-GO, dừng dự án.**

---

## 5. Out of Scope (Phase 0)

- ❌ Xây dựng backend / database.
- ❌ Xây dựng app đầy cuối (web/mobile).
- ❌ AI matching engine.
- ❌ Video call integration.
- ❌ Thanh toán (Stripe).
- ❌ Bất kỳ tính năng product nào ngoài landing page.

---

## 6. Dependencies

| Phụ thuộc | Loại | Trạng thái |
|----------|------|-----------|
| Domain `skillseed.app` (hoặc tương đương) | Mua sẵn | Owner mua tuần 1 |
| Vốn pre-seed cá nhân ($500–$1.000) | Tài chính | Owner tự cấp |
| 30 người quen trong 5 persona | Mạng lưới | Founder tự chuẩn bị tuần 1 |
| Designer freelance | Nhân sự | Tuyển tuần 1 |

---

## 7. Risks & Mitigation

| Rủi ro | Xác suất | Tác động | Mitigation |
|--------|----------|----------|------------|
| Không tuyển được 30 người phỏng vấn | Thấp | Cao | Founder chủ động cold message trên LinkedIn/Facebook; mở rộng network qua bạn bè |
| Landing page không convert | Trung bình | Trung bình | A/B test 3 variant hero + CTA trong 2 tuần |
| Ads bị reject (Facebook) | Trung bình | Thấp | Dự phòng Google Ads + Reddit organic |
| Phỏng vấn thiên kiến (chỉ hỏi người quen) | Cao | Cao | Bắt buộc 30% người lạ (qua cold outreach) |