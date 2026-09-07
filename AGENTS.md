# AGENTS.md — SkillSeed

> **Hướng dẫn cho AI agent (Kilo, Claude, Cursor, …) và developer mới khi làm việc với SkillSeed.**
> Mục tiêu: onboard < 10 phút, làm việc **không trùng / không mâu thuẫn** với spec có sẵn.
>
> **Nguyên tắc vàng:** File này là **navigation layer** — trỏ đến spec gốc. **KHÔNG duplicate nội dung spec.** Khi có conflict, spec thắng.

**Last updated:** 2026-09-07
**Maintainer:** SkillSeed Product Team

---

## 1. Dự án là gì

SkillSeed là **P2P skill-exchange platform** — trao đổi buổi học 15–60 phút, dùng đơn vị nội bộ **Seed** thay vì tiền fiat. Stack chính:

- **Backend:** Java 21 + Spring Boot 3.3 (monolith Phase 1, microservices Phase 2+)
- **Frontend:** Next.js 15 + TypeScript + shadcn/ui + TailwindCSS
- **Data:** PostgreSQL 16 + Redis 7
- **Video:** Daily.co

**Trạng thái hiện tại:** Spec phase (chưa có source code). Tài liệu là nguồn sự thật.

Chi tiết tổng quan: **`SKILLSEED.md`** (đọc §1 + §6).

---

## 2. Đọc gì trước khi làm bất cứ việc gì

```
1. SKILLSEED.md                    ← Tổng quan sản phẩm & kỹ thuật (master doc)
2. .kiro/specs/README.md           ← Cách đọc bộ spec theo phase
3. .kiro/specs/phase-{N}/          ← Spec phase hiện tại
   ├── requirements.md             ←   WHAT (US, FR, NFR, Acceptance)
   ├── design.md                   ←   HOW (architecture, schema, API)
   └── tasks.md                    ←   WHEN (sprint tasks)
```

**Phase codes:** `V` (Phase 0) · `M` (Phase 1 MVP) · `A` (Phase 2 AI) · `S` (Phase 3 Scale) · `X` (Phase 4 eXpansion) · `5-` (Phase 5 Vision).

---

## 3. Source of Truth — Tra cứu nhanh

| Cần biết gì | Đọc ở đâu |
|---|---|
| Tech stack (BE/FE/Data) | `.kiro/specs/phase-{N}/design.md` §2 |
| Kiến trúc / diagram | `.kiro/specs/phase-{N}/design.md` §1 |
| DB schema / ERD / DDL | `SKILLSEED_API_AND_DB.md` §8 + `.kiro/specs/phase-{N}/design.md` §3 |
| API contract (endpoints + payload) | `SKILLSEED_API_AND_DB.md` + `.kiro/specs/phase-{N}/design.md` §4 |
| State machine (booking, wallet, session) | `SKILLSEED.md` §10–11 + `.kiro/specs/phase-{N}/design.md` §6 |
| Thuật ngữ domain (Seed, Skill DNA, Pod, …) | `SKILLSEED.md` §1–5 |
| User stories & acceptance | `.kiro/specs/phase-{N}/requirements.md` |
| Sprint tasks | `.kiro/specs/phase-{N}/tasks.md` |
| UI / mockup flow | `mockups/README.md` → file trong `mockups/{category}/{NN}-{name}.md` |
| Wireframe ASCII chi tiết | `.kiro/wireframes/ui-flow.md` |
| Code skeleton gợi ý (file/folder) | `SKILLSEED_CODE_SKELETON.md` |
| Chi phí cloud theo phase | `SKILLSEED_CLOUD_COST.md` |
| Pitch / narrative | `SKILLSEED_PITCH_DECK.md` |
| **CI/CD pipeline / lint / test** | **`docs/CI_CD.md`** ← xem mục 4 (CI gate) |
| **DB schema visual (ERD Mermaid)** | **`docs/ERD.md`** ← bảng, quan hệ, indexes |
| **Booking / Wallet / Session state machine** | **`docs/STATE_MACHINES.md`** ← transitions hợp lệ, anti-patterns |

**Quy tắc xung đột:** Phase spec hiện tại thắng master doc. Phát hiện mâu thuẫn → flag trong `tasks.md` của phase, KHÔNG tự sửa.

---

## 4. Repo Map

```
/
├── AGENTS.md                          ← BẠN ĐANG Ở ĐÂY
├── README.md                          ← Tổng quan public
│
├── SKILLSEED.md                       ← Master product & tech doc
├── SKILLSEED_API_AND_DB.md            ← API contract + DB schema
├── SKILLSEED_CODE_SKELETON.md         ← Code skeleton (BE + FE)
├── SKILLSEED_CLOUD_COST.md            ← Cost projection per phase
├── SKILLSEED_PITCH_DECK.md            ← Pitch materials
│
├── .kiro/
│   ├── specs/                         ← Spec theo Kiro format (3 file/phase)
│   ├── wireframes/                    ← ASCII wireframe UI flow
│   └── research/                      ← User research, market data
│
├── mockups/                           ← 130 UI mockups (Mermaid)
│   └── README.md                      ← Index + convention
├── screens-svg/                       ← SVG render của mockups
│
├── src/                               ← Source code (chưa tồn tại — xem SKILLSEED_CODE_SKELETON.md)
│
└── .kilo/                             ← Kilo-specific config (commands, agents, kilo.json)
```

---

## 5. Conventions (project-wide)

> Specs KHÔNG định nghĩa phần này — đây là rule chung cho mọi phase.

### 5.1. Naming
- **Files (FE):** kebab-case — `booking-modal.tsx`, `use-wallet-balance.ts`
- **Files (BE):** PascalCase class — `BookingService.java`
- **DB tables:** snake_case, **số ít** — `user`, `booking`, `seed_transaction` (KHÔNG `users`)
- **DB columns:** snake_case — `created_at`, `user_id`
- **API routes:** REST, kebab-case, **số nhiều** — `/api/v1/bookings`, `/api/v1/skill-seeds`
- **JSON fields:** camelCase — `firstName`, `createdAt`
- **Env vars:** UPPER_SNAKE — `DATABASE_URL`, `DAILY_API_KEY`
- **Java packages:** `com.skillseed.{module}` — `com.skillseed.booking`
- **TS namespaces:** `@/modules/{module}` — `@/modules/booking`

### 5.2. Code style
- **Java:** Google Java Style + Spring Boot best practices. **KHÔNG** dùng Lombok trên entity (gây bug với JPA lazy). Có thể dùng cho DTO/builder.
- **TS:** ESLint + Prettier (sẽ có ở `src/`). **Strict mode bắt buộc.** Không `any` trừ khi đã comment lý do.
- **Imports:** KHÔNG wildcard (`import java.util.*`). Group: builtin → external → internal → relative.
- **Comments:** KHÔNG thêm comment trừ khi được yêu cầu hoặc giải thích quyết định phức tạp.

### 5.3. Branch & commit
- **Branch:** `{type}/{phase}-{short-desc}` — `feat/M01-booking-flow`, `fix/M02-wallet-double-spend`
- **Commit:** Conventional Commits với **scope** khi thuộc 1 module — `feat(booking): cancel refund logic`.
- **PR:** Title = Conventional Commit. Body: link issue + mô tả + cách test + ảnh/video (nếu UI).

### 5.4. Mockup file naming
```
mockups/{category}/{NN}-{screen-name}.md
```
- `category`: `00-marketing` → `99-special-states`
- `NN`: 2 chữ số
- `screen-name`: kebab-case
- Convention đầy đủ: `mockups/README.md`

### 5.5. Spec ID pattern (Kiro)
- **FR / NFR:** `FR-{PhaseCode}##` — `FR-M01` (Phase 1, FR #1)
- **US:** `US-{PhaseCode}##`
- **Task:** `T-{PhaseLetter}{Serial}` — `T-M01`, `T-A12`, `T-5-01`

---

## 6. Agent Boundaries (Quyền & Giới hạn)

### 6.1. Mặc định được phép
- Đọc toàn bộ repo (`.md`, source, config).
- Chạy lệnh read-only: `ls`, `cat`, `grep`, `git status`, `git diff`, `git log`.
- Khi `src/` tồn tại: `mvn test`, `mvn checkstyle:check`, `npm test`, `npm run lint`, `npm run typecheck`. Xem `docs/CI_CD.md` §4 để biết lệnh đầy đủ.
- Tạo/sửa file trong `mockups/`, `screens-svg/`, `.kiro/specs/**/requirements.md`, `.kiro/specs/**/tasks.md`.
- Scaffold code theo `SKILLSEED_CODE_SKELETON.md` khi được yêu cầu rõ.

### 6.2. Cần xác nhận trước khi làm
- Sửa `.kiro/specs/**/design.md` (thay đổi kiến trúc).
- Sửa `SKILLSEED_API_AND_DB.md`, `SKILLSEED.md`, `SKILLSEED_CODE_SKELETON.md`.
- Thêm dependency mới vào `pom.xml` / `package.json`.
- Đổi DB schema (cần migration + review).
- File ngoài `src/**` (config infra, CI, deploy).

### 6.3. Tuyệt đối KHÔNG được
- Commit secrets (API key, password, JWT secret, mnemonic, private key). **Không bao giờ.**
- Push trực tiếp lên `main` / `master`.
- Xoá file trong `mockups/` — sửa hoặc archive.
- Force push, `git reset --hard` lên branch shared, skip hooks.
- Tự ý merge PR.
- Chạy `DROP`, `TRUNCATE`, `DELETE FROM` không `WHERE` trên DB thật.
- Sửa master doc mà không sync lại phase spec tương ứng.

### 6.4. Khi không chắc chắn
→ **Hỏi người dùng.** Tốt hơn 1 câu hỏi còn hơn 1 commit hỏng.

---

## 7. Definition of Done (DoD)

### 7.1. Cho code change (khi `src/` tồn tại)
- [ ] Code đúng theo `.kiro/specs/phase-N/{requirements,design}.md`
- [ ] Lint pass (`mvn checkstyle:check` / `npm run lint`)
- [ ] Type check pass (`mvn compile` / `npm run typecheck`)
- [ ] Test pass: unit + integration, coverage không giảm
- [ ] API mới/sửa → OpenAPI + `SKILLSEED_API_AND_DB.md` đã update
- [ ] DB schema đổi → Flyway migration mới + test rollback
- [ ] UI đổi → mockup tương ứng đã update
- [ ] Không còn TODO/FIXME/debug log
- [ ] PR description đầy đủ + review approved

### 7.2. Cho spec / docs change
- [ ] Đúng format Kiro (nếu trong `.kiro/specs/`)
- [ ] Có "Last updated" date
- [ ] Cross-link còn đúng
- [ ] Acceptance Criteria (nếu `requirements.md`) đã cập nhật
- [ ] Task ID mới (nếu có) đúng pattern `T-{Phase}{Serial}` và chưa trùng

### 7.3. Cho mockup change
- [ ] Render OK trên Mermaid
- [ ] Có `classDef` color tokens (xem `mockups/README.md`)
- [ ] Có annotations: validation rules, state variants, tracking events
- [ ] Tên file đúng convention `{NN}-{screen-name}.md`
- [ ] Update `mockups/README.md` nếu thêm/sửa số lượng

---

## 8. Workflow cho Agent khi nhận task

```
1. ĐỌC  → Spec phase hiện tại (.kiro/specs/phase-N/{requirements,design,tasks}.md)
2. TRA  → Source of Truth (§3) để xác nhận API/DB/UI liên quan
3. PLAN → Liệt kê file cần sửa/tạo + lệnh sẽ chạy
4. HỎI → Nếu mơ hồ hoặc vượt quyền (§6) → hỏi user
5. LÀM  → Thay đổi nhỏ, có thể revert
6. KIỂM → Lint + typecheck + test (khi có src/)
7. ĐỐI CHIẾU → DoD (§7) đầy đủ chưa
8. BÁO  → Tóm tắt: file đã sửa, lệnh đã chạy, còn lại gì cần user
```

---

## 9. Khi phát hiện mâu thuẫn tài liệu

1. **Không tự ý chọn bên thắng** — flag cho user.
2. Ghi rõ: nguồn A nói X, nguồn B nói Y, đề xuất Z, lý do.
3. Đề xuất fix trong `tasks.md` của phase tương ứng (task mới, priority P1).

Dùng command `/audit-docs` để quét toàn bộ docs tìm mâu thuẫn.

---

## 10. Phát hiện spec đã có sẵn → KHÔNG tạo lại

Trước khi tạo file mới, **luôn check xem spec đã có chưa**:

| Cần tạo | Đã có ở | Hành động |
|---|---|---|
| Tech stack Phase X | `.kiro/specs/phase-X/design.md` §2 | **Trỏ vào**, không viết lại |
| API endpoint | `SKILLSEED_API_AND_DB.md` | **Trỏ vào**, không define lại |
| DB schema | `SKILLSEED_API_AND_DB.md` §8 | **Trỏ vào**, không viết lại |
| ERD | `.kiro/specs/phase-X/design.md` §3 | **Trỏ vào**, không vẽ lại |
| Thuật ngữ domain | `SKILLSEED.md` §1–5 | **Trỏ vào**, không tạo glossary mới |
| ADR về tech đã chốt | Có thể chưa — nếu chưa → **tạo trong `docs/ADR/`** | Hỏi user trước |
| Gotcha / lỗi | Sau 1 sprint đầu → `docs/GOTCHAS.md` | Tạo khi có dữ liệu thật |

**Nguyên tắc:** 1 nguồn sự thật (single source of truth). Nếu phải tạo file mới vì chưa có → OK, nhưng phải reference lại từ `AGENTS.md`.

---

> **Kết:** File này là **living document** nhưng nên **mỏng**. Khi specs lớn lên, nhiều phần ở đây có thể bị xoá vì đã có trong spec. Mục tiêu cuối: `AGENTS.md` chỉ chứa **agent-specific rules** (Boundaries, DoD, Workflow, Conventions) — phần còn lại đều là pointer.
