# 02 — Family Management · Requirements

### REQ-02-01 — Create Family
- **REQ-02-01-01** WHEN user đã đăng nhập POST `/api/v1/families` với `{name, description?, originLocation?, visibility?}` THE hệ thống SHALL tạo family mới, slug tự sinh từ name (lowercase, không dấu, unique), creator trở thành `OWNER` và trả 201.
- **REQ-02-01-02** IF name trống hoặc > 120 ký tự THEN THE hệ thống SHALL trả 400.
- **REQ-02-01-03** IF slug đã tồn tại THEN THE hệ thống SHALL append suffix `-2`, `-3`... cho tới khi unique.

### REQ-02-02 — List & Detail
- **REQ-02-02-01** WHEN user GET `/families` THE hệ thống SHALL trả danh sách family mà user là member kèm role của mình.
- **REQ-02-02-02** WHEN user GET `/families/{slug}` THE hệ thống SHALL trả chi tiết (id, slug, name, description, visibility, coverImageUrl, stats {persons, photos}, creator, createdAt).
- **REQ-02-02-03** IF user không phải member và visibility=PRIVATE THEN THE hệ thống SHALL trả 404.

### REQ-02-03 — Update
- **REQ-02-03-01** WHEN user role `OWNER` PATCH `/families/{slug}` THE hệ thống SHALL cập nhật được các field `name, description, originLocation, visibility, coverImageUrl` và trả detail mới.
- **REQ-02-03-02** WHEN user role < `OWNER` THEN THE hệ thống SHALL trả 403.

### REQ-02-04 — Delete
- **REQ-02-04-01** WHEN user role `OWNER` DELETE `/families/{slug}` THE hệ thống SHALL soft-delete family, ghi audit log, đóng các session của member trên family đó (revoke membership cache) và trả 204.
- **REQ-02-04-02** IF user không phải OWNER THEN THE hệ thống SHALL trả 403.

### REQ-02-05 — Membership
- **REQ-02-05-01** WHEN OWNER hoặc EDITOR POST `/families/{slug}/members` với `{email, role}` THE hệ thống SHALL thêm user vào family với role tương ứng; nếu user chưa tồn tại SHALL gửi email mời kèm link đăng ký + invite token.
- **REQ-02-05-02** WHEN OWNER PATCH `/families/{slug}/members/{userId}` với `{role}` hợp lệ THE hệ thống SHALL đổi role và invalidate cache JWT của user đó.
- **REQ-02-05-03** WHEN OWNER DELETE `/families/{slug}/members/{userId}` THE hệ thống SHALL xóa membership, nếu user là OWNER cuối cùng SHALL trả 409 `LAST_OWNER`.
- **REQ-02-05-04** WHERE có invite token THE hệ thống SHALL validate signature, TTL 14 ngày, single-use.

### REQ-02-06 — Visibility
- **REQ-02-06-01** WHEN user GET `/p/{slug}` (public route) THE hệ thống SHALL trả family tree view-only nếu visibility=PUBLIC.
- **REQ-02-06-02** WHEN visibility=UNLISTED THE hệ thống SHALL chỉ trả cho request có đúng token share trong URL.
- **REQ-02-06-03** WHEN visibility=PRIVATE THE hệ thống SHALL trả 404 cho mọi request không phải member.

## Acceptance
- Tạo family → slug sinh đúng theo quy tắc.
- Mời member bằng email nhận được mail có link invite.
- Đổi role OWNER cho EDITOR không thể đổi role của người khác.
- Soft-delete family; record vẫn còn trong DB nhưng API trả 404.
- Public route `/p/{slug}` với family PUBLIC hiển thị được, PRIVATE trả 404.
