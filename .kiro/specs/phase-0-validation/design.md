# Phase 0 — Validation: Design

> **Tài liệu thiết kế kỹ thuật cho giai đoạn Validation.**
> **Triết lý:** "Don't build what you can validate." Toàn bộ Phase 0 dùng no-code/low-code tools để ra quyết định nhanh nhất.

---

## 1. High-Level Architecture

```
┌────────────────────────────────────────────────────────────────────┐
│                         PHASE 0 STACK                                │
└────────────────────────────────────────────────────────────────────┘

   Internet Users
       │
       ├──► Facebook Ads ───┐
       ├──► Google Ads ─────┤
       └──► Organic ────────┤
                            ▼
                ┌──────────────────────┐
                │   Landing Page       │  ◄── Next.js 15 (static export)
                │   (Vercel/Netlify)   │      + shadcn/ui + TailwindCSS
                └──────────┬───────────┘
                           │
                           ▼
                ┌──────────────────────┐
                │  Waitlist Form       │  ◄── Tally.so / Typeform embed
                └──────────┬───────────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │ Resend/  │  │  Google  │  │  Zapier  │
        │ Brevo    │  │  Sheets  │  │  / Make  │
        │ (email)  │  │ (CRM)    │  │ (auto)   │
        └──────────┘ └──────────┘ └──────────┘
                           │
                           ▼
                ┌──────────────────────┐
                │  Validation Report   │  ◄── Notion + Export PDF
                └──────────────────────┘
```

**Nguyên tắc thiết kế:**
1. **Zero backend code** trong Phase 0 — dùng SaaS no-code để ra quyết định trong 4 tuần.
2. **Mọi metric đo được** (events, conversion, CAC) phải có sẵn tracking từ ngày 1.
3. **Không tích hợp phức tạp** — nếu giải pháp cần >2 service tích hợp, đơn giản hóa.

---

## 2. Tech Stack

| Layer | Công nụ | Lý do |
|-------|---------|-------|
| **Landing page** | Next.js 15 (App Router, static export) | React quen thuộc, SEO tốt, deploy free trên Vercel |
| **UI components** | shadcn/ui + TailwindCSS | Nhanh, đẹp, customizable |
| **Form** | Tally.so (free tier) hoặc Typeform | Free, embed được, webhook tốt |
| **Email** | Resend (free 3.000 email/tháng) | Developer-friendly, dễ tích hợp |
| **CRM** | Google Sheets + Notion | Free, đủ dùng cho 200 email |
| **Automation** | Zapier free tier hoặc Make.com | 100 tasks/tháng free, đủ dùng |
| **Analytics** | Google Analytics 4 + Meta Pixel | Chuẩn ngành, free |
| **Ads** | Facebook Ads + Google Ads | Đa dạng kênh để test |
| **Recording** | Otter.ai hoặc Fireflies.ai (free tier) | Auto-transcript phỏng vấn |
| **Survey analysis** | Notion AI hoặc ChatGPT | Tag & phân tích transcript |

**Chi phí ước tính:**
- Domain: $12/năm
- Vercel: $0 (free tier)
- Tally: $0 (free)
- Resend: $0 (free)
- Ads: $200 (budget cố định)
- **Tổng:** ~$212 cho toàn bộ Phase 0

---

## 3. Landing Page Design

### 3.1. Page Structure (Above the Fold)

```
┌─────────────────────────────────────────────────────────────┐
│  [LOGO SkillSeed]                  [Login (disabled)]       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   Teach what you know.                                       │
│   Learn what you love.                                       │
│   Pay with your time, not your wallet.                       │
│                                                              │
│   [Get Early Access →]   ← Hero CTA                         │
│                                                              │
│   🌱 30 free starter seeds when you join                     │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 3.2. Full Page Sections (chi tiết)

| # | Section | Mục đích | Conversion Goal |
|---|---------|----------|-----------------|
| 1 | **Hero** | Tagline + CTA đầu trang | Email signup |
| 2 | **Problem** | 3 pain points (đắt đỏ, cô đơn, khó tìm mentor) | Empathy |
| 3 | **Solution** | 3 trụ cột (Money-less, AI-matched, Verified) | Differentiation |
| 4 | **How it works** | 3 bước: Tạo profile → AI match → Book session | Clarity |
| 5 | **Benefits** | Grid 6 ô (cho Learner & Teacher) | Value prop |
| 6 | **Social proof** | Placeholder cho testimonial (chưa có user) | Trust |
| 7 | **FAQ** | 6 câu hỏi thường gặp | Objection handling |
| 8 | **Final CTA** | Email form lặp lại | Conversion |
| 9 | **Footer** | Privacy, Terms, Contact, Social | Compliance |

### 3.3. Conversion Form

```html
<form>
  <input type="email" placeholder="your@email.com" required />
  
  <select name="can_teach">
    <option>Bạn có sẵn sàng dạy miễn phí kỹ năng của bạn?</option>
    <option value="5">Rất sẵn sàng (5⭐)</option>
    <option value="4">Sẵn sàng</option>
    <option value="3">Có thể</option>
    <option value="2">Không chắc</option>
    <option value="1">Không</option>
  </select>
  
  <select name="can_learn">
    <option>Bạn sẵn sàng học miễn phí?</option>
    <option value="5">Rất sẵn sàng (5⭐)</option>
    <option value="4">Sẵn sàng</option>
    <option value="3">Có thể</option>
    <option value="2">Không chắc</option>
    <option value="1">Không</option>
  </select>
  
  <button>Join Waitlist →</button>
</form>
```

### 3.4. Performance Requirements

| Metric | Target |
|--------|--------|
| Lighthouse Performance | ≥ 90 |
| LCP (Largest Contentful Paint) | < 2.0s |
| FID (First Input Delay) | < 100ms |
| CLS (Cumulative Layout Shift) | < 0.1 |
| Mobile responsive | ✅ iPhone SE+ |

---

## 4. Survey Design

### 4.1. Phỏng vấn sâu (30 phút, 12–15 câu hỏi)

**Phần 1 — Background (3 câu)**
1. Bạn đang làm gì hiện tại? Học về/Học gì?
2. Kỹ năng nào bạn tự tin nhất có thể dạy người khác?
3. Kỹ năng nào bạn đang muốn học trong 6 tháng tới?

**Phần 2 — Pain points (3 câu)**
4. Lần cuối bạn học một kỹ năng mới là khi nào? Trả bao nhiêu?
5. Khó khăn lớn nhất khi tìm người dạy/học cùng là gì?
6. Bạn đã thử những cách nào để học? (Udemy, YouTube, bạn bè, mentor, ...)

**Phần 3 — Concept test (4 câu)**
7. Nếu có app trao đổi kỹ năng bằng thời gian (không cần tiền), bạn có dùng không? Tại sao?
8. Bạn có sẵn sàng dạy miễn phí 30 phút/tuần không? Điều gì khiến bạn ngại?
9. Bạn có quan tâm đến "Skill DNA" (hồ sơ năng lực đa chiều) không? Hay thông tin cơ bản là đủ?
10. Bạn tin tưởng hệ thống đánh giá/reputation thế nào? (1-5)

**Phần 4 — Pricing (2 câu)**
11. Nếu có 1 mentor chất lượng cao, bạn sẵn sàng trả bao nhiêu/giờ?
12. Bạn có quan tâm đến "Skill Passport" chia sẻ được trên LinkedIn không?

**Phần 5 — Channel (2 câu)**
13. Bạn tìm mentor qua kênh nào hiện tại?
14. Bạn dùng app gì cho việc học online? (Zoom, Google Meet, ...)

**Phần 6 — Wrap-up (1 câu)**
15. Có điều gì khác bạn muốn chia sẻ không?

### 4.2. Khảo sát ngắn (5 phút, 8 câu — Google Form)

1. Tuổi của bạn? (18–24, 25–34, 35–44, 45+)
2. Bạn thuộc nhóm nào? (Sinh viên, Chuyên gia trẻ, Người chuyển nghề, Người lớn tuổi, Bà mẹ sau sinh)
3. Bạn có kỹ năng nào muốn dạy không? (Text input)
4. Bạn muốn học kỹ năng gì trong 3 tháng tới? (Text input)
5. Bạn sẵn sàng dạy miễn phí 30 phút/tuần không? (Likert 1–5)
6. Bạn có sẵn sàng học từ người lạ qua video call không? (Likert 1–5)
7. Bạn quan tâm đến chứng chỉ/huy hiệu chia sẻ được trên LinkedIn? (Likert 1–5)
8. Email liên hệ (optional, để nhận early access)

---

## 5. Data Flow & Storage

### 5.1. Waitlist Data Schema (Google Sheets)

| Column | Type | Source |
|--------|------|--------|
| `email` | string | Form |
| `submitted_at` | datetime | Auto |
| `can_teach_score` | int (1-5) | Form |
| `can_learn_score` | int (1-5) | Form |
| `skills_to_teach` | string | Form/Survey |
| `skills_to_learn` | string | Form/Survey |
| `persona` | string | Tag thủ công |
| `utm_source` | string | URL param |
| `utm_campaign` | string | URL param |
| `utm_medium` | string | URL param |
| `converted_to_user` | boolean | (cho Phase 1+) |

### 5.2. Interview Tracking Sheet

| Column | Type |
|--------|------|
| `interviewee_id` | string |
| `name` | string |
| `persona` | enum |
| `date_interviewed` | date |
| `duration_min` | int |
| `transcript_url` | URL |
| `would_use` | boolean |
| `key_quotes` | text |
| `pain_points` | text |
| `concerns` | text |
| `tags` | comma-separated |

---

## 6. Ads Strategy

### 6.1. Facebook Ads

| Variant | Audience | Headline | Budget |
|---------|----------|----------|--------|
| **A1** | Sinh viên VN, 18-24 | "Học kỹ năng mới MIỄN PHÍ từ chuyên gia thực" | $50 |
| **A2** | Chuyên gia 25-35, VN | "Dạy kỹ năng của bạn, nhận 30 phút học MIỄN PHÍ" | $50 |
| **B1** | Career switchers 30-45 | "Muốn chuyển nghề? Học 1-1 miễn phí từ người đi trước" | $50 |
| **B2** | All Vietnam 18-45 | "SkillSeed - Teach what you know, learn what you love" | $50 |

### 6.2. Google Ads

| Keyword | Type | Budget |
|---------|------|--------|
| "học lập trình miễn phí" | Search | $30 |
| "học tiếng anh 1-1 miễn phí" | Search | $30 |
| "tìm mentor miễn phí" | Search | $20 |
| "online skill exchange" | Search | $20 |

### 6.3. UTM Convention

```
?utm_source=facebook&utm_medium=cpc&utm_campaign=phase0_students_v1&utm_content=hero_cta
?utm_source=google&utm_medium=cpc&utm_campaign=phase0_programming&utm_content=form_cta
```

---

## 7. Validation Dashboard (Google Sheets)

### 7.1. Sheet 1 — KPIs

| Metric | Tuần 1 | Tuần 2 | Tuần 3 | Tuần 4 | Target |
|--------|--------|--------|--------|--------|--------|
| Interviews completed | 5 | 12 | 25 | 30 | ≥ 30 |
| % would-use | - | 40% | 45% | 50% | ≥ 50% |
| Waitlist emails | 20 | 100 | 180 | 220 | ≥ 200 |
| Ad impressions | - | 5K | 15K | 25K | - |
| CTR | - | 2% | 2.5% | 3% | ≥ 2% |
| Conv. rate | - | 5% | 7% | 9% | ≥ 8% |
| Cost per email | - | $3 | $1.5 | $1.2 | ≤ $2 |

### 7.2. Sheet 2 — Persona Breakdown

| Persona | Interviews | Would-use | Top skill want | Top skill can teach |
|---------|------------|-----------|----------------|---------------------|
| Sinh viên | 8 | 5 | English, Coding | Math, Music |
| Chuyên gia trẻ | 7 | 5 | Public speaking | Coding, Design |
| Career switcher | 6 | 3 | ... | ... |
| Người lớn tuổi | 5 | 3 | ... | ... |
| Mẹ sau sinh | 4 | 2 | ... | ... |

### 7.3. Sheet 3 — Ad Performance

| Campaign | Spend | Impressions | Clicks | CTR | Signups | CAC |
|----------|-------|-------------|--------|-----|---------|-----|
| FB Students | $50 | ... | ... | ... | ... | ... |
| FB Pros | $50 | ... | ... | ... | ... | ... |
| ... | ... | ... | ... | ... | ... | ... |

---

## 8. Validation Report Template

Cấu trúc PDF (8–12 trang):

1. **Cover Page** — Logo, Title "SkillSeed Validation Report", Date
2. **Executive Summary** (1 trang) — GO/NO-GO decision, key numbers
3. **Methodology** (1 trang) — Cách thu thập data, sample size, biases
4. **Persona Insights** (2–3 trang) — Quote từ từng persona + insights
5. **Quantitative Results** (2 trang) — Bảng số liệu, charts
6. **Key Findings** (2 trang) — Top 5 insights, surprises, red flags
7. **Recommendation** (1 trang) — GO với điều kiện gì, hoặc PIVOT/NO-GO
8. **Appendix** — Script phỏng vấn, raw data links

---

## 9. Privacy & Compliance

| Yêu cầu | Triển khai |
|---------|------------|
| **GDPR consent** | Checkbox "I agree to receive updates" trước khi submit form |
| **Privacy Policy** | Trang `/privacy` giải thích: data thu thập, mục đích, quyền user, cách xóa |
| **Cookie banner** | Banner "We use cookies for analytics. Accept / Decline" |
| **Data retention** | Chỉ giữ email trong Google Sheet; voice recording xóa sau 30 ngày |
| **Right to delete** | Email liên hệ `privacy@skillseed.app` để xóa data |

---

## 10. Out of Scope (Design)

- ❌ Authentication system (sẽ có trong Phase 1)
- ❌ Database thực (chỉ Google Sheets)
- ❌ Real-time features
- ❌ Mobile app
- ❌ AI/LLM integration

---

## 11. Open Questions

| # | Câu hỏi | Owner | Deadline |
|---|----------|-------|----------|
| 1 | Có nên test thêm TikTok/Instagram organic không? | Founder | Tuần 2 |
| 2 | Landing page có cần Tiếng Việt + Tiếng Anh không? | Founder | Tuần 1 |
| 3 | Nên có blog/content SEO ngay từ Phase 0 không? | Founder | Tuần 2 |
| 4 | Có nên launch trên Product Hunt ngay từ đầu không? | Founder | Tuần 4 (nếu GO) |