# 08 — Search · Requirements

### REQ-08-01 — Text search
- **REQ-08-01-01** WHEN user GET `/families/{slug}/search?q=...` THE hệ thống SHALL trả danh sách persons khớp với q (full-text trên `given_name, middle_name, surname, alias, occupation, biography`).
- **REQ-08-01-02** THE hệ thống SHALL dùng Postgres `tsvector` + `pg_trgm` để hỗ trợ fuzzy match (Levenshtein distance ≤2).
- **REQ-08-01-03** IF q < 2 ký tự THEN THE hệ thống SHALL trả 400 `QUERY_TOO_SHORT`.

### REQ-08-02 — Filter
- **REQ-08-02-01** THE hệ thống SHALL hỗ trợ filter kết hợp: `generation`, `branch`, `gender`, `living`, `birthYearFrom`, `birthYearTo`, `hasMedia`.
- **REQ-08-02-02** WHEN nhiều filter áp dụng THE hệ thống SHALL AND các điều kiện.

### REQ-08-03 — Sort & pagination
- **REQ-08-03-01** THE hệ thống SHALL cho phép sort: `relevance` (default khi có q), `name`, `birthDate`, `generation`.
- **REQ-08-03-02** THE hệ thống SHALL phân trang cursor-based `?cursor=...&size=20`.

### REQ-08-04 — Performance
- **REQ-08-04-01** THE hệ thống SHALL đảm bảo P95 latency < 300ms với dataset 10.000 persons và filter hợp lý.
- **REQ-08-04-02** THE hệ thống SHALL sử dụng GIN index trên `tsvector` và B-tree trên các filter column.

### REQ-08-05 — Suggest
- **REQ-08-05-01** WHEN user gõ vào search box ≥2 ký tự THE hệ thống SHALL gọi `/families/{slug}/search/suggest?q=` trả tối đa 10 gợi ý autocomplete.
- **REQ-08-05-02** THE hệ thống SHALL debounce 200ms trước khi gọi.

### REQ-08-06 — Cross-family global search
- **REQ-08-06-01** WHEN user GET `/search?q=...` (no family scope) THE hệ thống SHALL tìm trên tất cả family user là member, group kết quả theo family.

## Acceptance
- Search "Nguyễn Văn A" trả đúng person dù viết không dấu "nguyen van a".
- Filter generation=2 + gender=MALE + living=true → AND kết quả.
- Suggest trả < 200ms.
- Dataset 10k persons, search P95 < 300ms (k6 perf test).
