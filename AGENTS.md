# AGENTS.md — SkillSeed

> **Hướng dẫn cho AI agent (Kilo, Claude, Cursor, …) và developer mới khi làm việc với SkillSeed.**
> Mục tiêu: onboard < 10 phút, làm việc **không trùng / không mâu thuẫn** với spec có sẵn.
>
> **Nguyên tắc vàng:** File này là **navigation layer** — trỏ đến spec gốc. **KHÔNG duplicate nội dung spec.** Khi có conflict, spec thắng.

**Last updated:** 2026-09-07 (Sprint 3 video + rating complete; added docs/MANUAL_E2E_SPRINT3.md cross-link)
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
| **Manual e2e auth test plan (T-M62)** | **`docs/MANUAL_E2E_AUTH.md`** ← sign-off checklist cho staging |
| **Manual e2e Sprint 3 test plan (T-M180)** | **`docs/MANUAL_E2E_SPRINT3.md`** ← video + rating + auto-rate + load test sign-off |

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

### 5.3. Frontend components (tái sử dụng)

> **Nguyên tắc:** Mọi UI element dùng ở **≥ 2 màn hình / chỗ khác nhau** PHẢI được tách thành component tái sử dụng. Không copy-paste JSX giữa các screen.

**Quy tắc bắt buộc:**

- **Vị trí đặt component:**
  - Component **domain-specific** (thuộc 1 module nghiệp vụ): `src/modules/{module}/components/` — ví dụ `src/modules/booking/components/booking-card.tsx`.
  - Component **shared / generic** (dùng chung ≥ 2 module): `src/components/ui/` (shadcn/ui) hoặc `src/components/shared/` — ví dụ `src/components/shared/empty-state.tsx`.
  - **KHÔNG** đặt component tái sử dụng trong `app/` (Next.js route) hay trong file screen/page.

- **Phát hiện trùng lặp:**
  - Khi viết screen mới, **trước khi** tạo block UI → grep `src/modules/` và `src/components/` xem đã có component tương tự chưa.
  - Nếu đã có → import dùng lại. Nếu chưa có nhưng biết sẽ dùng ở ≥ 2 chỗ → tạo component mới ngay từ đầu.

- **API của component:**
  - Props phải **typed đầy đủ** (TypeScript interface), **không** dùng `any` hay `unknown` không giải thích.
  - Tách biệt **data layer** (props) và **presentation**: không fetch trực tiếp trong component trừ khi là page-level.
  - Ưu tiên **composition** (`children`, `render prop`, slots) hơn prop drilling.

- **Styling:**
  - Dùng Tailwind + `cn()` helper, **không** inline style trừ khi dynamic giá trị.
  - Variant / state dùng `cva` (class-variance-authority) — không `if` trải className khắp nơi.
  - Mọi giá trị màu / spacing / radius phải qua **design token** (xem `tailwind.config.ts` + `src/lib/design-tokens.ts` khi có). Không hardcode hex.

- **Quy ước file:**
  - 1 component = 1 file, tên file kebab-case: `booking-card.tsx`.
  - Component export theo **named export** (`export function BookingCard`), trừ Next.js page (default export).
  - Đặt kèm `index.ts` barrel trong mỗi folder `components/` để import gọn: `import { BookingCard } from '@/modules/booking/components'`.

- **Checklist trước khi merge screen mới:**
  - [ ] JSX không có phần nào copy-paste từ screen khác.
  - [ ] Mọi UI element dùng ≥ 2 chỗ đã được tách component.
  - [ ] Component tái sử dụng có story/example trong `src/modules/{module}/components/__examples__/` (khi có Storybook) hoặc ít nhất 1 usage thực tế ở screen khác.
  - [ ] Không có prop thừa / hardcoded text cố định trong component generic.

### 5.4. Backend shared logic (tái sử dụng)

> **Nguyên tắc:** Mọi logic / constant / exception / validator / DTO / helper **dùng ở ≥ 2 module khác nhau** PHẢI được đưa vào lớp shared. Không copy-paste code giữa các module nghiệp vụ.

**Quy tắc bắt buộc:**

- **Vị trí đặt theo loại shared logic:**

  | Loại | Vị trí | Ví dụ |
  |---|---|---|
  | **Cross-module service / business logic** | `com.skillseed.shared.{domain}` — chia nhỏ theo domain: `shared.payment`, `shared.notification`, `shared.audit` | `SharedPricingService`, `SharedNotificationService` |
  | **Utility / static helper** | `com.skillseed.shared.util` | `DateUtils`, `SeedMath`, `StringUtils` |
  | **Custom exception** | `com.skillseed.shared.exception` (+ `GlobalExceptionHandler` ở `com.skillseed.shared.web`) | `WalletInsufficientException`, `BookingStateException` |
  | **Validator / annotation** | `com.skillseed.shared.validation` | `@ValidSeedAmount`, `@ValidTimezone` |
  | **DTO chung / page wrapper** | `com.skillseed.shared.dto` | `PageResponse<T>`, `ErrorResponse`, `AuditDto` |
  | **Constants / enums** | `com.skillseed.shared.constants` hoặc `com.skillseed.shared.enums` | `SeedTransactionType`, `ApiPaths` |
  | **Common config (Spring)** | `com.skillseed.shared.config` | `RedisConfig`, `SecurityConfig`, `OpenApiConfig` |
  | **Interceptor / filter / aspect** | `com.skillseed.shared.web` | `RequestLoggingFilter`, `RateLimitInterceptor` |
  | **Mapper chung (entity ↔ DTO)** | `com.skillseed.shared.mapper` (chỉ khi map entity thuộc nhiều module) | `UserMapper` |

- **Phân biệt rõ 3 lớp:**
  - **Module-specific** (`com.skillseed.{module}.*`) — chỉ module đó dùng. KHÔNG để module khác import trực tiếp.
  - **Shared** (`com.skillseed.shared.*`) — ≥ 2 module dùng. Mọi module đều được phép import.
  - **Common / cross-cutting** (config, exception handler, filter) — áp dụng toàn hệ thống, thường là `@Component` / `@Configuration` autoloaded.

- **Phát hiện trùng lặp:**
  - Khi viết service / controller mới, **trước khi** tạo method mới → grep `com.skillseed` (`rg "methodName"` hoặc mở IDE outline) xem đã có chỗ nào làm chưa.
  - Nếu 2 module cùng pattern → extract vào `shared.{domain}` ngay từ đầu, đừng để "duplicate-cân-nhắc-sau".

- **API của shared service:**
  - Phải **interface + implementation** khi có khả năng thay thế / mock: `interface SharedPricingService` + `SharedPricingServiceImpl`.
  - Constructor injection, **không** `@Autowired` field.
  - Method phải **idempotent** nếu có thể, hoặc document rõ side-effect trong JavaDoc (nhưng KHÔNG thêm comment trừ khi được yêu cầu — xem §5.2).
  - Transaction boundary: ghi rõ `@Transactional` ở service layer, không lan xuống shared util.

- **Quy ước file:**
  - 1 public class / interface = 1 file, tên file = class name (`PascalCase`).
  - Interface **không** prefix `I` (chuẩn Java + Spring): `PricingService`, không `IPricingService`.
  - Implementation suffix `Impl`: `PricingServiceImpl`.
  - KHÔNG để business logic trong `controller/`, `repository/`, `entity/` — chỉ giữ ở `service/`.
  - DTO **immutable** (Java record khi có thể), không Lombok `@Data` trên entity.

- **Checklist trước khi merge module mới:**
  - [ ] Không có method / constant / exception copy-paste từ module khác.
  - [ ] Mọi logic dùng ≥ 2 module đã được tách vào `com.skillseed.shared.*`.
  - [ ] Shared service dùng constructor injection + interface (khi cần mock).
  - [ ] Không có shared logic phụ thuộc vào entity của 1 module cụ thể (shared phải module-agnostic, hoặc tách sub-domain `shared.{domain}`).
  - [ ] Test cho shared service có ở `src/test/java/com/skillseed/shared/...` (độc lập với test module).

### 5.5. Branch & commit
- **Branch:** `{type}/{phase}-{short-desc}` — `feat/M01-booking-flow`, `fix/M02-wallet-double-spend`
- **Commit:** Conventional Commits với **scope** khi thuộc 1 module — `feat(booking): cancel refund logic`.
- **PR:** Title = Conventional Commit. Body: link issue + mô tả + cách test + ảnh/video (nếu UI).

### 5.6. Mockup file naming
```
mockups/{category}/{NN}-{screen-name}.md
```
- `category`: `00-marketing` → `99-special-states`
- `NN`: 2 chữ số
- `screen-name`: kebab-case
- Convention đầy đủ: `mockups/README.md`

### 5.7. Spec ID pattern (Kiro)
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
