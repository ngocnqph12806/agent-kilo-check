# 01 — Auth & RBAC · Requirements

> EARS-format requirements cho epic Authentication & Role-Based Access Control.

## Glossary
- **JWT pair** = access token + refresh token.
- **RBAC** = Role-Based Access Control theo family.

---

### REQ-01-01 — Registration
- **REQ-01-01-01** WHEN user POST `/api/v1/auth/register` với email hợp lệ và password đạt policy (≥10 ký tự, có 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt) THE hệ thống SHALL tạo user mới với `role=USER`, hash password bằng BCrypt cost 12, trả 201 cùng JWT pair.
- **REQ-01-01-02** IF email đã tồn tại THEN THE hệ thống SHALL trả 409 với `code=EMAIL_TAKEN`.
- **REQ-01-01-03** IF payload thiếu field hoặc validation fail THEN THE hệ thống SHALL trả 400 với danh sách lỗi từng field.
- **REQ-01-01-04** WHERE đăng ký thành công THE hệ thống SHALL gửi email welcome.

### REQ-01-02 — Login
- **REQ-01-02-01** WHEN user POST `/auth/login` với email và password khớp user trong DB THE hệ thống SHALL trả access token (TTL 15 phút) và refresh token (TTL 7 ngày), đồng thời set refresh token vào httpOnly cookie `rt` (Secure, SameSite=Lax).
- **REQ-01-02-02** IF credentials sai THEN THE hệ thống SHALL trả 401 `INVALID_CREDENTIALS` và ghi `failed_login` audit log.
- **REQ-01-02-03** WHILE user `is_disabled=true` THEN THE hệ thống SHALL từ chối login trả 403 `ACCOUNT_DISABLED`.

### REQ-01-03 — Refresh & Logout
- **REQ-01-03-01** WHEN access token hết hạn FE gọi `/auth/refresh` với refresh token hợp lệ THE hệ thống SHALL revoke refresh token cũ (jti → Redis blacklist) và cấp JWT pair mới.
- **REQ-01-03-02** IF refresh token hết hạn, đã revoke, hoặc nằm trong blacklist THEN THE hệ thống SHALL trả 401 `REFRESH_INVALID`.
- **REQ-01-03-03** WHEN user POST `/auth/logout` THE hệ thống SHALL thu hồi refresh token hiện tại (Redis blacklist + DB `revoked_at`) và trả 204.
- **REQ-01-03-04** IF request logout nhưng không có refresh token THEN THE hệ thống SHALL trả 204 (idempotent).

### REQ-01-04 — OAuth2 (Google, Facebook)
- **REQ-01-04-01** WHEN user truy cập `GET /auth/oauth2/google` THE hệ thống SHALL redirect sang Google OAuth2 authorization endpoint với scope `openid email profile` và `state` CSRF token.
- **REQ-01-04-02** WHEN Google/ Facebook callback thành công THE hệ thống SHALL tìm user theo `(oauth_provider, oauth_provider_id)`; nếu chưa tồn tại SHALL tự tạo user mới với `email_verified=true`.
- **REQ-01-04-03** IF email OAuth trùng email đã đăng ký bằng password THEN THE hệ thống SHALL liên kết OAuth vào user hiện tại (không yêu cầu password) và trả JWT pair.
- **REQ-01-04-04** WHERE OAuth flow hoàn tất THE hệ thống SHALL redirect về `${WEB_URL}/oauth/callback?token=<jwt>` để FE xử lý tiếp.

### REQ-01-05 — Forgot / Reset Password
- **REQ-01-05-01** WHEN user POST `/auth/forgot-password` với email THE hệ thống SHALL luôn trả 200 (không lộ email tồn tại); nếu email có user hợp lệ SHALL tạo reset token (TTL 1 giờ, hash SHA-256 lưu DB) và gửi email chứa link reset.
- **REQ-01-05-02** WHEN user POST `/auth/reset-password` với token hợp lệ và password mới đạt policy THEN THE hệ thống SHALL đổi `password_hash`, đánh dấu token đã dùng, revoke toàn bộ refresh token hiện tại của user và gửi email `password-changed`.
- **REQ-01-05-03** IF reset token không tồn tại, hết hạn, hoặc đã dùng THEN THE hệ thống SHALL trả 400 `RESET_TOKEN_INVALID`.

### REQ-01-06 — Authorization (RBAC)
- **REQ-01-06-01** WHEN request đến endpoint có annotation `@PreAuthorize` THE hệ thống SHALL parse JWT, xác thực signature bằng `kid`, kiểm tra `exp`, nạp `Authentication` chứa `userId`, `roles[]`, `familyMemberships[]`.
- **REQ-01-06-02** IF user không có role phù hợp trên `familyId` THEN THE hệ thống SHALL trả 403 `FORBIDDEN`.
- **REQ-01-06-03** WHILE xử lý request BE SHALL ghi audit log `{ actor, familyId, route, method, decision }`.

### REQ-01-07 — Account Security
- **REQ-01-07-01** WHEN login thất bại ≥5 lần trong 15 phút từ 1 IP THEN THE hệ thống SHALL block IP đó khỏi `/auth/*` trong 30 phút (Bucket4j Redis backend) và trả 429 `TOO_MANY_REQUESTS`.
- **REQ-01-07-02** WHEN password thay đổi thành công THE hệ thống SHALL gửi email thông báo tới email đăng ký trong vòng 5 phút.
- **REQ-01-07-03** WHERE JWT được ký THE hệ thống SHALL dùng HS256 với cơ chế key rotation: header `kid` chọn key trong tập `{current, previous}` từ env `JWT_SECRET_CURRENT` / `JWT_SECRET_PREVIOUS`.

### REQ-01-08 — User Profile
- **REQ-01-08-01** WHEN user đã đăng nhập gọi `GET /users/me` THE hệ thống SHALL trả profile (id, email, fullName, avatarUrl, locale, theme, createdAt).
- **REQ-01-08-02** WHEN user PATCH `/users/me` với `{fullName?, avatarUrl?, locale?, theme?}` THE hệ thống SHALL cập nhật và trả profile mới.
- **REQ-01-08-03** WHEN user PATCH `/users/me/password` với currentPassword đúng và newPassword đạt policy THE hệ thống SHALL đổi password, revoke refresh token và trả 204.

## Acceptance
- E2E flow: register → login → truy cập `/users/me` → refresh sau 16 phút → logout → không truy cập được nữa.
- OAuth Google: callback thành công tạo user mới nếu email chưa tồn tại.
- RBAC: 2 user trong cùng family, EDITOR không thể đổi role OWNER.
- Rate limit: brute-force `/auth/login` bị block IP.
- Audit log có entry cho mỗi login/logout/permission_denied.
