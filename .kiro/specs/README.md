# SkillSeed — Spec-Driven Development (Kiro format)

> **Bộ spec đầy đủ cho dự án SkillSeed, chia theo từng giai đoạn phát triển.**
> **Format:** Theo chuẩn **Kiro spec** — mỗi phase có 3 files: `requirements.md`, `design.md`, `tasks.md`.
> **Triết lý:** Mỗi spec đủ chi tiết để một developer mới onboard có thể triển khai mà không bị rối loạn.

---

## Cấu trúc

```
.kiro/specs/
├── README.md                              # File này
│
├── phase-0-validation/                    # Tuần 1–4
│   ├── requirements.md                    # User stories, FR, NFR, GO/NO-GO criteria
│   ├── design.md                          # Landing page, surveys, ads strategy
│   └── tasks.md                           # Task list theo tuần
│
├── phase-1-mvp/                           # Tháng 2–4
│   ├── requirements.md                    # MVP features: Auth, Profile, Booking, Video, Wallet
│   ├── design.md                          # Monolith Spring Boot + Next.js + Daily.co
│   └── tasks.md                           # 5 sprints × 2 tuần
│
├── phase-2-ai-polish/                     # Tháng 5–8
│   ├── requirements.md                    # AI Matching, AI Co-Pilot, PWA, Multi-criteria rating
│   ├── design.md                          # Matching service (Python), Kafka, Qdrant
│   └── tasks.md                           # 5 sprints × 3 tuần
│
├── phase-3-mobile-scale/                  # Tháng 9–14
│   ├── requirements.md                    # Mobile RN, Premium Stripe, B2B, Pods, Passport v1
│   ├── design.md                          # Microservices + K8s + Mobile
│   └── tasks.md                           # 12 sprints × 2 tuần
│
├── phase-4-regional-expansion/            # Tháng 15–24
│   ├── requirements.md                    # Multi-region, i18n, Blockchain SBT, Public API
│   ├── design.md                          # Multi-region K8s + Polygon + Stripe Connect
│   └── tasks.md                           # 10 sprints × 3 tuần
│
└── phase-5-beyond/                        # Năm 2–3
    ├── requirements.md                    # Voice AI, AR/VR, EU/US, zk-SNARK, Series B
    ├── design.md                          # Vision architecture (Voice + AR + zk)
    └── tasks.md                           # Year/Quarter roadmap (không sprint cứng)
```

---

## Tổng quan từng Phase

| Phase | Thời gian | Mục tiêu chính | Team | Budget/mo | Deliverable |
|-------|-----------|------------------|------|-----------|-------------|
| **Phase 0** | Tuần 1–4 | Validate PMF | 1 founder + designer | $300 | 200+ waitlist, GO/NO-GO |
| **Phase 1** | Tháng 2–4 | MVP live | 2 người | $500–1.500 | 50–100 beta users, 200+ sessions |
| **Phase 2** | Tháng 5–8 | AI Matching + polish | 3–4 người | $3.000–5.000 | 1K–5K MAU, AI matching live |
| **Phase 3** | Tháng 9–14 | Mobile + Scale + B2B | 6–10 người | $15K–30K | 20K–50K MAU, 1K+ Premium, 5+ B2B |
| **Phase 4** | Tháng 15–24 | Regional expansion | 15–25 người | $50K–100K | 200K+ MAU, $1M+ ARR, Series A |
| **Phase 5** | Năm 2–3 | Vision execution | 30–60 người | $200K–500K | 1M+ MAU, $10M+ ARR, Series B / Exit |

---

## Cách sử dụng bộ spec này

### Cho Founder / PM

1. Đọc Phase 0 trước → quyết định GO/NO-GO.
2. Mỗi phase đọc theo thứ tự: `requirements.md` → `design.md` → `tasks.md`.
3. Acceptance Criteria ở cuối mỗi `requirements.md` là checklist đánh giá phase hoàn thành.

### Cho Developer

1. Đọc `requirements.md` để hiểu WHAT cần build (user stories, FR, NFR).
2. Đọc `design.md` để hiểu HOW sẽ build (architecture, schema, API).
3. Đọc `tasks.md` để biết WHEN và thứ tự (sprint, ưu tiên).

### Cho Designer / FE

1. `requirements.md` để hiểu UI/UX cần thiết kế.
2. `design.md` để hiểu data flow và tech constraints.
3. `tasks.md` để biết deadline cho mỗi feature.

### Cho Investor / Advisor

1. Phase 0 + Phase 1 để hiểu go-to-market validation.
2. Phase 2-3 để hiểu product roadmap.
3. Phase 4-5 để hiểu vision và exit options.

---

## Mapping với tài liệu gốc

| Source doc | Spec phases |
|------------|-------------|
| `SKILLSEED.md` (Product & Tech spec) | Tất cả phases — high-level |
| `SKILLSEED_API_AND_DB.md` (API & DB) | Phase 1 (chi tiết), Phase 2–4 (mở rộng) |
| `SKILLSEED_CODE_SKELETON.md` | Phase 1 (backend skeleton), Phase 3 (microservices) |
| `SKILLSEED_CLOUD_COST.md` | Phase 2 (~$3K), Phase 3 (~$15K), Phase 4 (~$50K) |
| `SKILLSEED_PITCH_DECK.md` | Phase 0 (validation), Phase 3 (seed raise), Phase 4 (Series A) |

---

## Nguyên tắc viết spec (theo Kiro)

Mỗi spec tuân theo:

1. **requirements.md** — WHAT
   - User Stories (US-XXX) theo personas
   - Functional Requirements (FR-X##) đánh priority MUST/SHOULD
   - Non-Functional Requirements (NFR-X##) với tiêu chí đo lường
   - Acceptance Criteria rõ ràng
   - Out of Scope (đẩy sang phase sau)
   - Dependencies & Risks

2. **design.md** — HOW
   - High-level architecture (diagrams ASCII)
   - Tech stack chi tiết
   - Database schema (DDL chính)
   - API design (endpoints + payload chính)
   - Module/Service boundaries
   - Deployment topology
   - Out of Scope design
   - Open Questions

3. **tasks.md** — WHEN
   - Sprint-based task list (Phase 1–4) hoặc quarter-based (Phase 5)
   - Task ID format: `[T-{Phase}{Serial}]`
   - Priority: P0 (must), P1 (should), P2 (nice-to-have)
   - Definition of Done cho mỗi phase
   - Effort estimation
   - Risk register

---

## Conventions

- **Task IDs:** `T-{PhaseLetter}{Number}` — VD: `T-V01` (Phase 0), `T-M01` (Phase 1 MVP), `T-A01` (Phase 2 AI), `T-S01` (Phase 3 Scale), `T-X01` (Phase 4 eXpansion), `T-V01` (Phase 5 Vision — xin lỗi trùng Phase 0, sẽ dùng prefix `T-5-`).
- **FR/NFR IDs:** `FR-{PhaseCode}##` — VD: `FR-M01` (Phase 1 MVP).
- **US IDs:** `US-{PhaseCode}##` — VD: `US-101` (Phase 1).

---

## Quy trình cập nhật

Mỗi spec là **living document**:
- Review cuối mỗi sprint.
- Update nếu có thay đổi lớn (pivot, thêm feature, bỏ feature).
- Versioning: thêm "Last updated: YYYY-MM-DD" ở đầu file.
- Khi phase done → archive spec vào `.kiro/specs/archive/phase-{N}-{name}-{date}/`.

---

> **Ghi chú cuối:** Bộ spec này là bản đồ chi tiết cho hành trình 2–3 năm của SkillSeed. Mỗi phase đã được cân nhắc về technical feasibility, market timing, và resource requirements. Tuy nhiên, thực tế triển khai sẽ có những thay đổi — hãy xem spec như **compass**, không phải **contract**.

> **Maintainer:** SkillSeed Product Team
> **Last updated:** 2026-09-07