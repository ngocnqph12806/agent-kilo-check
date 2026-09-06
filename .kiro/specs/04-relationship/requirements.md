# 04 — Relationships · Requirements

### REQ-04-01 — Relationship types
- **REQ-04-01-01** THE hệ thống SHALL hỗ trợ các loại quan hệ: `SPOUSE`, `PARENT_CHILD`, `SIBLING`, `ADOPTED_PARENT_CHILD`, `GODPARENT_GODCHILD`.
- **REQ-04-01-02** IF relationship type không hợp lệ THEN THE hệ thống SHALL trả 400 `INVALID_REL_TYPE`.

### REQ-04-02 — Create
- **REQ-04-02-01** WHEN user role `EDITOR+` POST `/families/{slug}/relationships` với `{type, personAId, personBId, startDate?, endDate?, notes?}` THE hệ thống SHALL tạo quan hệ, validate type-specific rules, ghi audit và trả 201.
- **REQ-04-02-02** IF cùng cặp (personA, personB, type) đã tồn tại chưa xóa THEN THE hệ thống SHALL trả 409 `RELATIONSHIP_EXISTS`.
- **REQ-04-02-03** IF personA hoặc personB thuộc family khác THE hệ thống SHALL trả 400 `CROSS_FAMILY_NOT_ALLOWED`.

### REQ-04-03 — Type-specific rules
- **REQ-04-03-01** WHEN tạo `SPOUSE` THE hệ thống SHALL: yêu cầu cả hai person đều ≥ 14 tuổi (theo `birthDate`); chỉ cho phép 1 SPOUSE current (endDate=null); nếu đã có SPOUSE hiện tại → trả 409 `ALREADY_MARRIED` trừ khi set `endDate` cho cái cũ.
- **REQ-04-03-02** WHEN tạo `PARENT_CHILD` THE hệ thống SHALL: parent phải ≥ 14 tuổi hơn child (warn nhưng không chặn), parent phải thuộc generation ≤ child.generation − 1; không cho phép tạo cycle (A→parent B, B→parent A) → trả 400 `CYCLE_DETECTED`.
- **REQ-04-03-03** WHEN tạo `SIBLING` THE hệ thống SHALL: yêu cầu 2 person chia sẻ ≥1 cha/mẹ trong DB; nếu chưa có TH hệ thống SHALL tự động tạo SIBLING dựa trên parent chung (best-effort).
- **REQ-04-03-04** WHEN tạo `ADOPTED_PARENT_CHILD` THE hệ thống SHALL tương tự PARENT_CHILD nhưng generation được tính theo dòng nuôi.

### REQ-04-04 — Read
- **REQ-04-04-01** WHEN member GET `/persons/{id}/relations` THE hệ thống SHALL trả danh sách quan hệ phân loại `{parents, children, spouses, siblings, godparents, godchildren}`.
- **REQ-04-04-02** WHEN member GET `/families/{slug}/relationships` THE hệ thống SHALL trả tất cả quan hệ trong family, hỗ trợ filter `?type=`.

### REQ-04-05 — Update / Delete
- **REQ-04-05-01** WHEN user PATCH `/relationships/{id}` với `{endDate?, notes?}` THE hệ thống SHALL cập nhật và audit.
- **REQ-04-05-02** WHEN user DELETE `/relationships/{id}` THE hệ thống SHALL soft-delete, nếu là parent-child cuối cùng của một child TH vẫn cho xóa nhưng child sẽ không có parent record (UI cảnh báo).
- **REQ-04-05-03** IF user không phải EDITOR+ THEN THE hệ thống SHALL trả 403.

### REQ-04-06 — Graph integrity
- **REQ-04-06-01** WHEN tạo/xóa PARENT_CHILD THE hệ thống SHALL tự động tạo/xóa SIBLING giữa các child cùng cha mẹ (idempotent).
- **REQ-04-06-02** WHEN hai SPOUSE có chung child (qua PARENT_CHILD) THE hệ thống SHALL cho phép nhưng audit log ghi `complex_family`.

## Acceptance
- Tạo parent A, child B → generation B = A+1.
- Tạo B và C cùng parent A → tự động SIBLING(B, C).
- Cố tạo cycle A→parent B, B→parent A → 400.
- Person có SPOUSE hiện tại, tạo SPOUSE mới → 409 ALREADY_MARRIED.
- Xóa parent A → các child vẫn tồn tại nhưng generation không auto-update.
