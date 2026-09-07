# AGENTS.md — SkillSeed

> **Hướng dẫn dành cho AI agent (Kilo, Claude, Cursor, …) và developer mới khi làm việc với dự án SkillSeed.**
> Mục tiêu: mọi agent onboard trong **< 10 phút** và làm việc **không gây rối loạn thông tin**.

**Last updated:** 2026-09-07
**Maintainer:** SkillSeed Product Team

---

## 1. Project Snapshot

| Mục | Giá trị |
|---|---|
| Tên | **SkillSeed** — P2P skill-exchange platform |
| Tagline | *Teach what you know. Learn what you love.* |
| Lĩnh vực | EdTech / micro-learning / reputation economy |
| Đơn vị tiền tệ nội bộ | **Seed** (không phải crypto Phase 1; SBT ở Phase 4) |
| Trạng thái hiện tại | **Design / Spec phase** — chưa có source code. Tài liệu là nguồn sự thật. |
| Giai đoạn đang triển khai | **Phase 0 (validation)** → sắp vào **Phase 1 (MVP)** |
| Đối tượng đọc chính | Founder, Backend (Java), Frontend (Next.js), Designer, AI Agent |

**Đọc trước khi làm bất cứ việc gì:**
1. `SKILLSEED.md` — tổng quan sản phẩm & kỹ thuật
2. `.kiro/specs/README.md` — cách đọc bộ spec theo phase
3. `.kiro/specs/phase-{N}-{name}/requirements.md` của phase đang làm

---

## 2. Tech Stack (canonical — Phase 1 MVP)

> Stack này là **chuẩn cho Phase 1**. Phase 2+ sẽ thay đổi (microservices, AI service bằng Python, K8s, blockchain). **Không tự ý thêm tech ngoài danh sách** khi chưa được phê duyệt trong `design.md` của phase tương ứng.

### 2.1. Backend
| Layer | Tech | Version |
|---|---|---|
| Language | **Java** | 21 (LTS) |
| Framework | **Spring Boot** | 3.3.x |
| Persistence | Spring Data JPA + Hibernate | 6.x |
| Migration | **Flyway** | 10.x |
| Validation | Jakarta Bean Validation | 3.x |
| Security | Spring Security + OAuth2 Resource Server | 6.x |
| API Docs | springdoc-openapi | 2.x |
| WebSocket | Spring WebSocket + STOMP | — |
| Cache | Spring Data Redis (Redis 7) | 3.x |
| Build | **Maven** | 3.9.x |
| Test | JUnit 5 + Mockito + Testcontainers | — |

### 2.2. Frontend (Web)
| Layer | Tech |
|---|---|
| Framework | **Next.js 15** (App Router) |
| Language | **TypeScript** 5.x (strict mode) |
| UI | **shadcn/ui** + **TailwindCSS** |
| State | **Zustand** (client state) |
| Server state | **TanStack Query** (React Query) |
| Forms | React Hook Form + **Zod** |
| Realtime | socket.io-client hoặc native WebSocket |
| Video | **Daily.co** React SDK |
| Charts | Recharts |

### 2.3. Data
| Loại | Tech |
|---|---|
| Primary DB | **PostgreSQL 16** |
| Cache | **Redis 7** (Upstash) |
| Search | PostgreSQL FTS (Phase 1); Qdrant (Phase 2+) |
| Object storage | Cloudflare R2 / AWS S3 |

### 2.4. Module map (Phase 1 monolith)
```
spring-boot/
├── auth/          # OAuth2, JWT, session
├── user/          # profile, Skill DNA, onboarding
├── skill/         # skill catalog, user-skill mapping
├── discover/      # search, filters, recommendation (Phase 1: simple)
├── booking/       # schedule, conflict-check
├── session/       # Daily.co room, AI notes stub
├── rating/        # multi-criteria rating
├── wallet/        # Seed balance, transactions
├── notification/  # email + in-app
└── admin/         # moderation, feature flags
```

---

## 3. Repository Map

```
/
├── AGENTS.md                          ← BẠN ĐANG Ở ĐÂY
├── README.md                          ← Tổng quan public
│
├── SKILLSEED.md                       ← Product & Tech spec (master doc)
├── SKILLSEED_API_AND_DB.md            ← API contract + DB schema (single source of truth cho data)
├── SKILLSEED_CODE_SKELETON.md         ← Code skeleton (Phase 1 backend + FE)
├── SKILLSEED_CLOUD_COST.md            ← Cost projection theo phase
├── SKILLSEED_PITCH_DECK.md            ← Pitch materials
│
├── .kiro/
│   ├── specs/                         ← Spec theo Kiro format (requirements/design/tasks per phase)
│   │   ├── phase-0-validation/
│   │   ├── phase-1-mvp/               ← MVP — phase hiện tại ưu tiên
│   │   ├── phase-2-ai-polish/
│   │   ├── phase-3-mobile-scale/
│   │   ├── phase-4-regional-expansion/
│   │   └── phase-5-beyond/
│   ├── wireframes/                    ← ASCII wireframes chi tiết
│   └── research/                      ← User research, market data
│
├── mockups/                           ← 130 UI mockups (Mermaid flowchart)
│   ├── 00-marketing/ 01-auth/ ... 99-special-states/
│   └── README.md                      ← Mockup index + convention
│
├── screens-svg/                       ← SVG render của mockups (optional visual)
│
├── docs/                              ← Bổ sung (xem §10)
│   ├── GLOSSARY.md
│   ├── ARCHITECTURE.md
│   ├── ADR/                           ← Architecture Decision Records
│   └── GOTCHAS.md
│
├── src/                               ← Source code (CHƯA TỒN TẠI — sẽ sinh ra theo SKILLSEED_CODE_SKELETON.md)
│
└── .kilo/                             ← Kilo-specific config
    ├── command/                       ← Workflow/macro lệnh
    ├── agent/                         ← Agent definitions (nếu có)
    └── kilo.json                      ← Kilo project config
```

---

## 4. Commands

> **Hiện tại KHÔNG có source code** — các lệnh dưới đây là **mục tiêu**. Khi `src/` được khởi tạo, agent phải cập nhật section này.

### 4.1. Spec / docs (luôn chạy được)
```bash
# Render Mermaid mockup
code mockups/04-booking/01-booking-modal.md       # VS Code + extension Markdown Preview Mermaid
# hoặc copy nội ```mermaid vào https://mermaid.live

# Đọc spec theo phase
cat .kiro/specs/phase-1-mvp/requirements.md
cat .kiro/specs/phase-1-mvp/design.md
cat .kiro/specs/phase-1-mvp/tasks.md
```

### 4.2. Khi backend Spring Boot tồn tại
```bash
mvn clean install                    # build + test
mvn test                             # chỉ unit test
mvn verify                           # full CI gate (test + integration + checkstyle)
mvn spring-boot:run                  # local dev (cần Postgres + Redis local)
mvn spring-boot:run -Dspring-boot.run.profiles=local
mvn flyway:migrate                   # apply migration
mvn flyway:info                      # xem trạng thái migration
mvn checkstyle:check                 # style check
```

### 4.3. Khi frontend Next.js tồn tại
```bash
pnpm install                         # hoặc npm install
pnpm dev                             # local dev
pnpm build                           # production build
pnpm lint                            # ESLint
pnpm typecheck                       # tsc --noEmit
pnpm test                            # Vitest / Jest
pnpm e2e                             # Playwright
```

### 4.4. Khi chưa có lệnh cho task
Agent phải **hỏi người dùng** thay vì tự đoán. Không chạy lệnh chưa được verify.

---

## 5. Conventions

### 5.1. Spec format (Kiro)
Mỗi phase có đúng 3 file:
- `requirements.md` — **WHAT**: User Stories (`US-{PhaseCode}##`), Functional Requirements (`FR-{PhaseCode}##`), NFR, Acceptance Criteria, Out of Scope
- `design.md` — **HOW**: Architecture, schema, API, module boundaries, deployment
- `tasks.md` — **WHEN**: Sprint-based task list, IDs `T-{PhaseLetter}{Serial}`, priority P0/P1/P2

**Phase codes:**
| Phase | Code |
|---|---|
| 0 — Validation | `V` (VD: `FR-V01`, `T-V01`) |
| 1 — MVP | `M` |
| 2 — AI | `A` |
| 3 — Scale | `S` |
| 4 — eXpansion | `X` |
| 5 — Vision (Beyond) | `5-` (tiền tố `T-5-` cho task) |

### 5.2. Naming
- **Files (FE):** kebab-case — `booking-modal.tsx`, `use-wallet-balance.ts`
- **Files (BE):** PascalCase cho class, camelCase cho package private — `BookingService.java`
- **DB tables:** snake_case, **số ít** — `user`, `booking`, `seed_transaction` (KHÔNG `users`)
- **DB columns:** snake_case — `created_at`, `user_id`
- **API routes:** REST, kebab-case, **số nhiều** — `/api/v1/bookings`, `/api/v1/skill-seeds`
- **JSON fields:** camelCase — `firstName`, `createdAt`
- **Env vars:** UPPER_SNAKE — `DATABASE_URL`, `DAILY_API_KEY`
- **Java packages:** `com.skillseed.{module}` — `com.skillseed.booking`
- **TS namespaces:** `@/modules/{module}` — `@/modules/booking`

### 5.3. Code style
- **Java:** Google Java Style + Spring Boot best practices. **Không** dùng Lombok ở entity (gây bug với JPA lazy). Có thể dùng Lombok cho DTO/builder.
- **TS:** ESLint + Prettier config (sẽ có ở `src/`). **Strict mode bắt buộc.** Không `any` trừ khi đã comment lý do.
- **Imports:** Tuyệt đối KHÔNG dùng wildcard (`import java.util.*`). Group theo thứ tự: builtin → external → internal → relative.
- **Comments:** **KHÔNG** thêm comment trừ khi được yêu cầu hoặc comment giải thích quyết định phức tạp.

### 5.4. Branch & commit
- **Branch:** `{type}/{phase}-{short-desc}` — VD: `feat/M01-booking-flow`, `fix/M02-wallet-double-spend`, `docs/M03-update-spec`
- **Commit:** Conventional Commits — `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`. Có **scope** khi liên quan 1 module — `feat(booking): cancel refund logic`.
- **PR:** Title = Conventional Commit. Body phải có: link issue, mô tả thay đổi, cách test, ảnh/video (nếu UI).

### 5.5. Mockup file naming
```
mockups/{category}/{NN}-{screen-name}.md
```
- `category`: `00-marketing`, `01-auth`, …, `99-special-states`
- `NN`: số thứ tự 2 chữ số
- `screen-name`: kebab-case

---

## 6. Source of Truth — Tra cứu nhanh

| Cần biết gì | Đọc ở đâu |
|---|---|
| **API contract / OpenAPI** | `SKILLSEED_API_AND_DB.md` (master); chi tiết phase → `.kiro/specs/phase-N/design.md` |
| **DB schema / ERD** | `SKILLSEED_API_AND_DB.md` (mục 8); chi tiết → `.kiro/specs/phase-N/design.md` |
| **State machine** (booking, wallet, session) | `SKILLSEED.md` §10–11; chi tiết phase → `design.md` |
| **UI flow / màn hình** | `mockups/README.md` → file `mockups/{category}/{NN}-{name}.md` |
| **Wireframe ASCII chi tiết** | `.kiro/wireframes/ui-flow.md` |
| **User stories & acceptance** | `.kiro/specs/phase-N/requirements.md` |
| **Kiến trúc & tech choice** | `.kiro/specs/phase-N/design.md` |
| **Công việc theo sprint** | `.kiro/specs/phase-N/tasks.md` |
| **Code skeleton (file/folder gợi ý)** | `SKILLSEED_CODE_SKELETON.md` |
| **Chi phí cloud** | `SKILLSEED_CLOUD_COST.md` |
| **Thuật ngữ domain** | `docs/GLOSSARY.md` (xem §10) |
| **ADR (tại sao chọn tech X)** | `docs/ADR/` (xem §10) |
| **Lỗi thường gặp / gotchas** | `docs/GOTCHAS.md` (xem §10) |

**Quy tắc vàng:** Nếu 2 nguồn mâu thuẫn → **Spec của phase hiện tại thắng** (vì spec là versioned, master doc có thể cũ). Khi phát hiện mâu thuẫn → flag vào `tasks.md` của phase.

---

## 7. Agent Boundaries (Quyền & Giới hạn)

### 7.1. Mặc định được phép
- Đọc toàn bộ repo (`.md`, source, config).
- Chạy lệnh read-only: `ls`, `cat`, `grep`, `mvn test`, `pnpm test`, `pnpm lint`, `mvn checkstyle:check`.
- Tạo/sửa file trong `mockups/`, `screens-svg/`, `.kiro/specs/**/requirements.md`, `.kiro/specs/**/tasks.md` (spec nội bộ).
- Scaffold code theo `SKILLSEED_CODE_SKELETON.md` khi được yêu cầu rõ.

### 7.2. Cần xác nhận trước khi làm
- Tạo/sửa file ngoài `src/**` (config infra, CI, deploy).
- Sửa `.kiro/specs/**/design.md` (thay đổi kiến trúc).
- Thêm dependency mới vào `pom.xml` / `package.json`.
- Đổi DB schema (cần migration + review).

### 7.3. Tuyệt đối KHÔNG được
- Commit secrets (API key, password, JWT secret, mnemonic, private key). **Không bao giờ.**
- Push trực tiếp lên `main` / `master`.
- Sửa `SKILLSEED_API_AND_DB.md` mà không cập nhật ngược `phase-N/design.md`.
- Xoá file trong `mockups/` — nếu sai, sửa hoặc archive.
- Force push, `git reset --hard` lên branch shared, skip hooks.
- Tự ý merge PR.
- Chạy lệnh `DROP`, `TRUNCATE`, `DELETE FROM` không có `WHERE` trên DB thật.

### 7.4. Khi không chắc chắn
→ **Hỏi người dùng.** Tốt hơn 1 câu hỏi còn hơn 1 commit hỏng.

---

## 8. Definition of Done (DoD)

Một task được coi là **DONE** khi **TẤT CẢ** điều sau thoả mãn:

### 8.1. Cho code change
- [ ] Code đúng theo spec (`requirements.md` + `design.md` của phase)
- [ ] Lint pass (`mvn checkstyle:check` / `pnpm lint`)
- [ ] Type check pass (`mvn compile` / `pnpm typecheck`)
- [ ] Test pass: unit + integration mới viết đều có, coverage không giảm
- [ ] Nếu thêm/sửa API → cập nhật OpenAPI + `SKILLSEED_API_AND_DB.md`
- [ ] Nếu đổi DB schema → có Flyway migration + test rollback
- [ ] Nếu đổi UI → mockup tương ứng đã update (nếu cần)
- [ ] Không có TODO / FIXME / commented-out code còn sót
- [ ] Không có `console.log` / `System.out.println` debug
- [ ] PR description đầy đủ + review được approve

### 8.2. Cho spec / docs change
- [ ] Đúng format Kiro (nếu trong `.kiro/specs/`)
- [ ] Có "Last updated" date
- [ ] Cross-link với các doc liên quan còn đúng
- [ ] Acceptance Criteria (nếu `requirements.md`) đã cập nhật
- [ ] Task ID mới (nếu có) đúng pattern `T-{Phase}{Serial}` và chưa trùng

### 8.3. Cho mockup change
- [ ] Render OK trên Mermaid (test trên mermaid.live hoặc VS Code preview)
- [ ] Có `classDef` color tokens thống nhất (xem `mockups/README.md`)
- [ ] Có annotations: validation rules, state variants, tracking events
- [ ] Tên file theo convention `{NN}-{screen-name}.md`
- [ ] Update `mockups/README.md` nếu thêm/sửa số lượng

---

## 9. Workflow cho Agent khi nhận task

```
1. ĐỌC  → Spec của phase hiện tại (.kiro/specs/phase-N/{requirements,design,tasks}.md)
2. TRA  → Source of Truth (§6) để xác nhận API/DB/UI liên quan
3. PLAN → Liệt kê file cần sửa/tạo + lệnh sẽ chạy
4. HỎI → Nếu mơ hồ hoặc vượt quyền (§7) → hỏi user
5. LÀM  → Thay đổi nhỏ, có thể revert
6. KIỂM → Lint + typecheck + test
7. ĐỐI CHIẾU → DoD (§8) đầy đủ chưa
8. BÁO  → Tóm tắt: file đã sửa, lệnh đã chạy, còn lại gì cần user
```

---

## 10. Tài liệu bổ sung (tạo khi cần)

Các file này **chưa tồn tại** — sẽ được tạo khi dự án phát triển:

| File | Mục đích | Khi nào tạo |
|---|---|---|
| `docs/GLOSSARY.md` | Định nghĩa thuật ngữ domain (Seed, Skill DNA, Passport, Pod, SBT, zk-Proof, …) | Ngay khi bắt đầu code |
| `docs/ARCHITECTURE.md` | Diagram tổng quan + luồng dữ liệu end-to-end | Khi Phase 1 design đã stable |
| `docs/ADR/0001-{title}.md` | Architecture Decision Records (VD: chọn monolith Phase 1, chọn PostgreSQL, …) | Mỗi quyết định kiến trúc quan trọng |
| `docs/GOTCHAS.md` | Lỗi / workaround thường gặp | Sau 1 sprint đầu tiên |

---

## 11. Glossary nhanh (top terms)

> Glossary đầy đủ sẽ ở `docs/GLOSSARY.md`. Tạm thời:

| Thuật ngữ | Nghĩa |
|---|---|
| **Seed** | Đơn vị giá trị nội bộ, sinh ra khi dạy người khác. 1 Seed ≈ 1 phút dạy. |
| **Skill DNA** | Vector đa chiều mô tả năng lực + cá tính + lịch rảnh của user. Dùng cho matching. |
| **Passport** | Bản ghi verifiable về các buổi đã học/đã dạy. Phase 4+ dùng SBT. |
| **Pod** | Nhóm học tập nhiều người (multi-participant session). |
| **Booking** | Lịch hẹn giữa Learner ↔ Teacher. |
| **Session** | Buổi học thực tế (qua video call). |
| **Premium** | Gói thuê bao trả phí (USD), không dùng Seed. |
| **Marketplace** | Khu vực cho Verified Expert bán khoá học/khuyên (Expert = teacher chuyên nghiệp, có tiền thật). |
| **B2B** | Workspace cho doanh nghiệp dùng SkillSeed nội bộ (SSO, audit log, …). |
| **Verified Expert** | User đã verify identity + skill, có thể bán dịch vụ trên Marketplace. |

---

## 12. Common Gotchas (cảnh báo nhanh)

> Section sẽ được populate vào `docs/GOTCHAS.md` sau sprint đầu. Tạm thời vài lưu ý:

- **Phase 1 dùng monolith, KHÔNG microservices.** Đừng tách service khi chưa có lý do.
- **Phase 1 KHÔNG có blockchain/SBT** — Passport chỉ là record trong DB. Phase 4 mới có Polygon.
- **Phase 1 KHÔNG có AI matching thật** — chỉ filter cơ bản. Phase 2 mới có Qdrant + matching service Python.
- **Seed ≠ crypto.** Phase 1 là ledger đơn giản. Không tự ý thêm smart contract.
- **shadcn/ui** chứ KHÔNG phải Material UI / Ant Design — tuân theo component đã có trong skeleton.
- **Daily.co** là SDK video duy nhất Phase 1. Không tự thêm Twilio/Agora/WebRTC raw.
- **Migration: LUÔN tạo file Flyway mới**, không sửa file migration cũ đã chạy.

---

## 13. Khi phát hiện mâu thuẫn tài liệu

1. **Không tự ý chọn bên thắng** — flag cho user.
2. Ghi rõ: nguồn A nói X, nguồn B nói Y, bạn đề xuất Z, lý do.
3. Đề xuất fix trong `tasks.md` của phase tương ứng (task mới với priority P1).

---

> **Kết:** File này là **living document**. Cập nhật mỗi khi:
> - Phase mới bắt đầu
> - Tech stack thay đổi
> - Có quy ước mới cần enforce
> - Phát hiện gotcha mới đáng ghi lại
