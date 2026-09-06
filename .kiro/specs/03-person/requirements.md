# 03 — Person CRUD · Requirements

### REQ-03-01 — Create Person
- **REQ-03-01-01** WHEN user role `OWNER` hoặc `EDITOR` POST `/api/v1/families/{slug}/persons` với payload hợp lệ THE hệ thống SHALL tạo person mới (sinh UUID, sinh `generation` tự động nếu có parent), trả 201 + PersonResponse.
- **REQ-03-01-02** IF user role < EDITOR THEN THE hệ thống SHALL trả 403; nếu là CONTRIBUTOR SHALL trả 403 kèm `code=NEEDS_APPROVAL`.
- **REQ-03-01-03** IF `birthDate` sau `deathDate` THEN THE hệ thống SHALL trả 400 `INVALID_DATE_RANGE`.

### REQ-03-02 — Read
- **REQ-03-02-01** WHEN member GET `/families/{slug}/persons` với query `?page=&size=&q=&generation=&branch=&living=&gender=` THE hệ thống SHALL trả danh sách phân trang (cursor-based, mặc định 20/page).
- **REQ-03-02-02** WHEN member GET `/persons/{id}` THE hệ thống SHALL trả PersonResponse đầy đủ gồm thông tin cá nhân + media avatar + relationships liên quan + events count.
- **REQ-03-02-03** IF person không thuộc family user là member THEN THE hệ thống SHALL trả 404.

### REQ-03-03 — Update
- **REQ-03-03-01** WHEN user role `OWNER` hoặc `EDITOR` PATCH `/persons/{id}` THE hệ thống SHALL cập nhật partial fields, ghi audit log entry (old→new cho mỗi field thay đổi) và trả PersonResponse mới.
- **REQ-03-03-02** WHEN user thay đổi `familyId` của person sang family khác không phải member THEN THE hệ thống SHALL trả 403.
- **REQ-03-03-03** IF user gửi field không hợp lệ THEN THE hệ thống SHALL trả 400 kèm danh sách field lỗi.

### REQ-03-04 — Delete (soft)
- **REQ-03-04-01** WHEN user role `OWNER` hoặc `EDITOR` DELETE `/persons/{id}` THE hệ thống SHALL soft-delete (`deleted_at`), cascade xóa relationships liên quan và ghi audit `person.deleted`.
- **REQ-03-04-02** IF person có descendant chưa xóa THEN THE hệ thống SHALL trả 409 `HAS_LIVING_DESCENDANTS`.

### REQ-03-05 — Lunar/Solar Date
- **REQ-03-05-01** WHEN user tạo/cập nhật person với `birthDate` + `birthDateLunar=true` THE hệ thống SHALL lưu cả solar và lunar; trả response có cả 2 trường.
- **REQ-03-05-02** WHEN user GET person THE hệ thống SHALL trả `birthDateSolar`, `birthDateLunar`, `deathDateSolar`, `deathDateLunar` cho FE render.

### REQ-03-06 — Biography
- **REQ-03-06-01** WHEN user PATCH `biography` (Markdown, max 50.000 ký tự) THE hệ thống SHALL lưu text + render an toàn phía FE (DOMPurify).
- **REQ-03-06-02** IF biography > 50.000 ký tự THEN THE hệ thống SHALL trả 400.

### REQ-03-07 — Audit
- **REQ-03-07-01** WHEN mọi thao tác CREATE/UPDATE/DELETE trên person THE hệ thống SHALL ghi `audit_logs` (actor, before, after, timestamp).
- **REQ-03-07-02** WHEN member GET `/persons/{id}/history` THE hệ thống SHALL trả danh sách audit entries (20 mới nhất), phân trang nếu cần.

## Acceptance
- Tạo person đầu tiên, generation=1.
- Con của person A tự động generation=2.
- Sửa person → history có entry old→new.
- Xóa person có con → 409.
- Convert ngày âm ↔ dương chính xác (±1 ngày đối với tháng nhuận).
