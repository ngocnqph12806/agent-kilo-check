# Phase 0 — Validation: Tasks

> **Task list triển khai Phase 0 theo tuần.**
> Format: `- [ ] [ID] [Priority] Mô tả — Acceptance criteria`

---

## Tuần 1 — Foundation & Planning

### 1.1. Chuẩn bị hạ tầng & nhân sự

- [ ] [T-V01] **[P0]** Mua domain `skillseed.app` (hoặc `.com` / `.vn`) — Domain hoạt động, DNS trỏ về Vercel.
- [ ] [T-V02] **[P0]** Setup Vercel account + link domain — Vercel project "skillseed-landing" được tạo.
- [ ] [T-V03] **[P0]** Tạo GitHub repo `skillseed/landing-page` — Repo public, có README, branch protection.
- [ ] [T-V04] **[P0]** Tuyển 1 designer freelance (Figma) — Designer onboard, ký NDA nếu cần.
- [ ] [T-V05] **[P0]** Setup Google Workspace (Gmail, Sheets, Drive) — Email `@skillseed.app` hoạt động.

### 1.2. Customer research prep

- [ ] [T-V10] **[P0]** Liệt kê 30 người quen trong 5 persona — Spreadsheet với tên, email, persona tag.
- [ ] [T-V11] **[P0]** Cold message 20 người lạ qua LinkedIn/Facebook (mỗi persona 4 người) — Ít nhất 10 người đồng ý phỏng vấn.
- [ ] [T-V12] **[P0]** Viết script phỏng vấn 30 phút — Document 12–15 câu hỏi (xem design.md section 4.1).
- [ ] [T-V13] **[P0]** Setup Otter.ai hoặc Fireflies.ai free account — Test recording 1 buổi thử.
- [ ] [T-V14] **[P1]** Đặt lịch phỏng vấn 5 người đầu tiên (tuần 2) — Calendar event có Google Meet link.

### 1.3. Landing page prep

- [ ] [T-V20] **[P0]** Designer tạo Figma wireframe landing page — 9 sections, mobile + desktop.
- [ ] [T-V21] **[P1]** Viết copy (Tiếng Anh) cho 9 sections — Google Doc, ~1500 words.
- [ ] [T-V22] **[P1]** Viết Privacy Policy + Terms cơ bản — 2 trang mỗi cái, có consent clause.

---

## Tuần 2 — Landing Page Live & First Interviews

### 2.1. Landing page build & deploy

- [ ] [T-V30] **[P0]** Init Next.js 15 project với TailwindCSS + shadcn/ui — `npm run dev` chạy được local.
- [ ] [T-V31] **[P0]** Implement Hero section (responsive) — Tagline, CTA button, hero image.
- [ ] [T-V32] **[P0]** Implement Problem, Solution, How-it-works sections — Mỗi section theo design.
- [ ] [T-V33] **[P0]** Implement Benefits, FAQ, Final CTA, Footer — Hoàn thiện page.
- [ ] [T-V34] **[P0]** Embed Tally.so waitlist form — Form hiển thị trên hero + cuối trang.
- [ ] [T-V35] **[P0]** Setup Google Analytics 4 + Meta Pixel — Tracking page views, form opens.
- [ ] [T-V36] **[P0]** Deploy lên Vercel production — URL live, Lighthouse score ≥ 90.
- [ ] [T-V37] **[P0]** Test responsive trên 5 devices (iPhone, Android, iPad, Desktop) — Không vỡ layout.
- [ ] [T-V38] **[P0]** Thêm `/privacy` và `/terms` pages — Public, có footer link.
- [ ] [T-V39] **[P1]** Cookie banner với Accept/Decline — Banner hiển thị lần đầu, lưu preference.

### 2.2. Form & email setup

- [ ] [T-V40] **[P0]** Setup Resend account + verify domain `skillseed.app` — API key ready.
- [ ] [T-V41] **[P0]** Tạo email template "Welcome to waitlist" — Email đẹp, có logo, có CTA share.
- [ ] [T-V42] **[P0]** Tạo email template "Thank you for feedback" — Cá nhân hóa theo score.
- [ ] [T-V43] **[P0]** Kết nối Tally → Resend qua Zapier/Make — Mỗi form submission trigger email.
- [ ] [T-V44] **[P0]** Kết nối Tally → Google Sheet "Waitlist" — Mỗi submission là 1 row mới.

### 2.3. Phỏng vấn đợt 1

- [ ] [T-V50] **[P0]** Phỏng vấn 5 người (mix 5 persona) — Transcript lưu trong Google Drive folder.
- [ ] [T-V51] **[P0]** Tag từng interview theo persona trong tracking sheet — 5 rows đầy đủ.
- [ ] [T-V52] **[P0]** Tổng hợp top 3 pain points từ 5 interview đầu — Doc 1 trang Aha-Moments.

### 2.4. Ads setup

- [ ] [T-V60] **[P0]** Tạo Facebook Business Manager + verify — Có quyền chạy ads.
- [ ] [T-V61] **[P0]** Tạo Google Ads account + link GA4 — Conversion tracking hoạt động.
- [ ] [T-V62] **[P0]** Viết 4 Facebook Ad variants + creative — A/B test sẵn 2 variant.
- [ ] [T-V63] **[P0]** Viết 4 Google Ad headlines + descriptions — Approved.
- [ ] [T-V64] **[P1]** Tạo UTM convention doc — Mọi link ads có UTM đúng chuẩn.

---

## Tuần 3 — Scale Traffic & More Interviews

### 3.1. Phỏng vấn đợt 2

- [ ] [T-V70] **[P0]** Phỏng vấn 10 người (mix 5 persona) — 15 cumulative transcripts.
- [ ] [T-V71] **[P0]** Cập nhật tracking sheet với 10 rows mới — Tag would-use, key quotes.
- [ ] [T-V72] **[P0]** Review lại pain points sau 15 interview — Điều chỉnh landing page copy nếu cần.

### 3.2. Ads launch

- [ ] [T-V80] **[P0]** Launch Facebook Ads 4 variants ($50/variant = $200 total) — Ads approved + running.
- [ ] [T-V81] **[P0]** Launch Google Ads 4 keyword groups ($100 total) — Ads running, có impressions.
- [ ] [T-V82] **[P0]** Monitor mỗi ngày: CPM, CTR, CPC, conversion — Ghi vào Sheet 3 (Ad Performance).
- [ ] [T-V83] **[P1]** Tạm dừng ad variant có CTR < 1% sau 3 ngày — Budget chuyển variant tốt hơn.

### 3.3. Waitlist growth

- [ ] [T-V90] **[P0]** Target: ≥ 100 email đăng ký — Sheet Waitlist có ≥ 100 rows.
- [ ] [T-V91] **[P1]** Chia sẻ landing page lên LinkedIn/Twitter cá nhân founder — Reach ≥ 1000 người.
- [ ] [T-V92] **[P1]** Post lên 3 subreddit relevant (r/learnprogramming, ...) — Mỗi post có ≥ 50 upvote.

### 3.4. Mid-point review

- [ ] [T-V100] **[P0]** Đánh giá giữa giai đoạn (Tuần 3) — So sánh với target tuần 3.
- [ ] [T-V101] **[P0]** PIVOT landing page copy nếu conversion < 5% — A/B test hero mới.
- [ ] [T-V102] **[P1]** Nếu persona nào có % would-use < 30%, chạy thêm 5 interview persona đó — Bổ sung data.

---

## Tuần 4 — Final Push & GO/NO-GO Decision

### 4.1. Phỏng vấn đợt 3

- [ ] [T-V110] **[P0]** Phỏng vấn 15 người cuối (mix 5 persona) — 30 cumulative interviews.
- [ ] [T-V111] **[P0]** Final tracking sheet đầy đủ — 30 rows, đủ columns.
- [ ] [T-V112] **[P0]** Phân tích: % would-use theo persona — Bar chart trong Sheet 2.

### 4.2. Ads optimization

- [ ] [T-V120] **[P0]** Pause ad variants cost > $5/email — Reallocate budget.
- [ ] [T-V121] **[P0]** Final ad metrics report — Sheet 3 đầy đủ 4 tuần.

### 4.3. Waitlist final target

- [ ] [T-V130] **[P0]** Target: ≥ 200 email đăng ký — Sheet Waitlist ≥ 200 rows.
- [ ] [T-V131] **[P1]** Personal email "Thank you" tới 50 email có score cao nhất — Tạo relationship sớm.

### 4.4. Validation Report & Decision

- [ ] [T-V140] **[P0]** Viết Validation Report (8–12 trang PDF) — Executive Summary, Persona Insights, Quantitative Results, Key Findings, Recommendation.
- [ ] [T-V141] **[P0]** Tạo slide deck 10 slides (Notion hoặc Pitch) — Dùng cho pitch pre-seed.
- [ ] [T-V142] **[P0]** Tổ chức Decision Meeting (founder + 2 advisors) — Output: GO / PIVOT / NO-GO.
- [ ] [T-V143] **[P0]** Nếu GO: tạo backlog Phase 1 (list features MVP) — Notion board có ≥ 30 items.
- [ ] [T-V144] **[P0]** Nếu GO: tuyển 1 Java backend dev (full-time hoặc founder chính) — Có offer trong 2 tuần sau.
- [ ] [T-V145] **[P0]** Nếu GO: tuyển 1 frontend freelancer — Shortlist 3 candidates.
- [ ] [T-V146] **[P0]** Nếu PIVOT: chạy lại Phase 0 với positioning mới — Loop 2 tuần.
- [ ] [T-V147] **[P0]** Nếu NO-GO: archive docs, lessons learned — Doc "Post-mortem Phase 0".

---

## Cross-cutting tasks (song song cả 4 tuần)

- [ ] [T-V200] **[P1]** Daily standup 15 phút với designer — Update progress, blockers.
- [ ] [T-V201] **[P1]** Weekly review mỗi Chủ nhật — Cập nhật dashboard, đánh giá OKR.
- [ ] [T-V202] **[P1]** Backup mọi data (CSV export Sheets, audio recordings) — Backup hàng tuần lên Google Drive.
- [ ] [T-V203] **[P1]** Track time & cost — Spreadsheet để budget control.
- [ ] [T-V204] **[P1]** Network với 2–3 potential advisor EdTech — Coffee chat 30 phút.
- [ ] [T-V205] **[P2]** Research 3 đối thủ (TimeRepublik, Simbi, ADPList) — Doc 1 trang mỗi đối thủ.

---

## Definition of Done — Phase 0

Phase 0 hoàn thành khi **TẤT CẢ** điều sau đúng:

- [ ] Landing page live với ≥ 200 email waitlist
- [ ] ≥ 30 phỏng vấn hoàn thành + transcript lưu trữ
- [ ] Validation Report PDF xuất bản
- [ ] Decision Meeting có output rõ ràng (GO/PIVOT/NO-GO)
- [ ] Nếu GO: Phase 1 backlog đã tạo + team đã tuyển (hoặc có kế hoạch tuyển)
- [ ] Tất cả data backup an toàn
- [ ] Lessons learned document hoàn thành

---

## Effort Estimation (T-shirt sizing)

| Tuần | Focus | Effort (founder + designer) |
|------|-------|------------------------------|
| Tuần 1 | Setup + research prep | 40h (founder) + 10h (designer) |
| Tuần 2 | Landing page + first interviews | 50h (founder) + 20h (designer) |
| Tuần 3 | Ads + scale interviews | 60h (founder) + 5h (designer) |
| Tuần 4 | Final push + report + decision | 50h (founder) + 10h (designer) |
| **Tổng** | | **200h founder + 45h designer** |

---

## Risks & Blockers (cần theo dõi)

| Risk | Trigger | Mitigation |
|------|---------|------------|
| Designer không ship đúng hạn | Wireframe trễ > 3 ngày | Founder dùng TailwindUI template làm backup |
| Ads bị Facebook reject | Policy violation | Test 2 creative thay thế |
| Phỏng vấn < 30 người | < 20 sau tuần 3 | Cold outreach mở rộng qua alumni groups |
| Landing page không convert | Conv. rate < 3% sau 1 tuần ads | A/B test 3 hero variant mới |
| Founder burnout | > 60h/tuần liên tục 2 tuần | Giảm scope ads, tập trung interviews |