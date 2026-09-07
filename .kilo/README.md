# `.kilo/command/` — Workflow Commands for SkillSeed

Các slash command dùng trong Kilo CLI cho dự án SkillSeed.

| Command | Mục đích |
|---|---|
| `/new-phase` | Khởi tạo spec cho 1 phase mới (3 file Kiro format) |
| `/new-mockup` | Tạo Mermaid mockup cho 1 UI screen |
| `/new-adr` | Tạo ADR (Architecture Decision Record) |
| `/audit-docs` | Audit toàn bộ docs tìm mâu thuẫn / inconsistency |
| `/check-phase-scope` | Kiểm tra 1 feature có thuộc phase hiện tại không |

**Format:** Mỗi command là 1 file `.md` với YAML frontmatter (`description`, `agent`).

**Đặt tên:** kebab-case, verb-first. File = `/name`.

**Best practice:**
- Mỗi command nên < 50 dòng cho prompt body.
- Reference docs bằng relative path (`AGENTS.md`, `.kiro/specs/...`).
- Không hard-code giá trị phụ thuộc phase — luôn read spec trước.
