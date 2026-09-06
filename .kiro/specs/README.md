# GiaPhaOnline — Kiro Spec Index

> Tài liệu gốc: [../../SPEC.md](../../SPEC.md)
> Format: **Kiro spec** (requirements.md + design.md + tasks.md) theo từng epic.

## Quy ước

- **EARS** (Easy Approach to Requirements Syntax) cho requirements:
  - `WHEN <trigger> THE <system> SHALL <behavior>`
  - `WHILE <state> THE <system> SHALL <behavior>`
  - `WHERE <feature> THE <system> SHALL <behavior>`
  - `IF <condition> THEN THE <system> SHALL <behavior>`
- Mỗi epic có 3 file: `requirements.md`, `design.md`, `tasks.md`.
- Mỗi `tasks.md` có checklist `- [ ]` để tracking.

## Roadmap tổng

| # | Epic | Phụ thuộc | Sprint |
|---|---|---|---|
| 00 | Foundation | — | S0 |
| 01 | Auth & RBAC | 00 | S1 |
| 02 | Family Management | 01 | S1 |
| 03 | Person CRUD | 02 | S2 |
| 04 | Relationships | 03 | S2 |
| 05 | Media | 02 | S2 |
| 06 | Tree Visualization | 03,04 | S3 |
| 07 | Events & Calendar | 03 | S4 |
| 08 | Search & Filter | 03 | S4 |
| 09 | Export | 03,04 | S5 |
| 10 | Notifications | 07 | S4–S5 |
| 11 | Public Share & Embed | 02,03 | S5 |
| 12 | Admin & Audit | 01,03,04 | S6 |

## Danh sách epic

- [00-foundation](./00-foundation/) — Monorepo, Docker Compose, CI, skeleton FE/BE, Flyway.
- [01-auth](./01-auth/) — Đăng ký/đăng nhập, JWT refresh, OAuth2, phân quyền.
- [02-family](./02-family/) — CRUD family, mời thành viên, role management.
- [03-person](./03-person/) — CRUD person, ngày âm/dương, timeline, audit.
- [04-relationship](./04-relationship/) — Quan hệ cha–mẹ–con, vợ–chồng, sibling.
- [05-media](./05-media/) — Upload, MinIO, thumbnail, EXIF, album.
- [06-tree](./06-tree/) — React Flow, layout, filter, mini-map, PNG/SVG export.
- [07-events-calendar](./07-events-calendar/) — Events, lịch âm/dương.
- [08-search](./08-search/) — Full-text search, filter nâng cao.
- [09-export](./09-export/) — PDF, GEDCOM, JSON.
- [10-notifications](./10-notifications/) — In-app, email, realtime WebSocket.
- [11-public-share](./11-public-share/) — Public tree view, embed iframe, share token.
- [12-admin](./12-admin/) — Dashboard, backup/restore, audit log.
