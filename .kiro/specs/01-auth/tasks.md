# 01 — Auth & RBAC

> Đăng ký/đăng nhập (email + OAuth2), JWT access + refresh, RBAC theo family.

## Requirements (EARS)

### REQ-01-01 — Registration
- **REQ-01-01-01** WHEN user POST `/api/v1/auth/register` với email hợp lệ và password ≥10 ký tự (1 hoa, 1 thường, 1 số, 1 đặc biệt) THE hệ thống SHALL tạo user mới với role mặc định `USER` và trả 201 + JWT pair.
- **REQ-01-01-02** IF email đã tồn tại THEN THE hệ thống SHALL trả 409 `EMAIL_TAKEN`.
- **REQ-01-01-03** IF payload không hợp lệ THEN THE hệ thống SHALL trả 400 với danh sách field lỗi.

### REQ-01-02 — Login
- **REQ-01-02-01** WHEN user POST `/auth/login` với email+password đúng THE hệ thống SHALL trả access token (TTL 15 phút) và refresh token (TTL 7 ngày).
- **REQ-01-02-02** IF credentials sai THEN THE hệ thống SHALL trả 401 `INVALID_CREDENTIALS`.
- **REQ-01-02-03** WHILE user bị soft-delete THEN THE hệ thống SHALL từ chối login trả 403 `ACCOUNT_DISABLED`.

### REQ-01-03 — Refresh & Logout
- **REQ-01-03-01** WHEN access token hết hạn THE FE SHALL gọi `/auth/refresh` với refresh token còn hiệu lực và nhận cặp token mới.
- **REQ-01-03-02** IF refresh token hết hạn hoặc nằm trong blacklist THEN THE hệ thống SHALL trả 401 `REFRESH_INVALID` và FE xóa local state.
- **REQ-01-03-03** WHEN user POST `/auth/logout` THE hệ thống SHALL blacklist refresh token (Redis) với TTL = thời gian còn lại.

### REQ-01-04 — OAuth2 (Google, Facebook)
- **REQ-01-04-01** WHEN user bấm "Login with Google" THE hệ thống SHALL redirect sang Google OAuth flow với scope `openid email profile`.
- **REQ-01-04-02** WHEN OAuth callback thành công THE hệ thống SHALL tìm user theo `oauth_provider + oauth_provider_id`; nếu chưa có SHALL tạo mới với email verified.
- **REQ-01-04-03** IF email từ OAuth trùng email đã đăng ký bằng password THEN THE hệ thống SHALL link account và trả token (không yêu cầu password).

### REQ-01-05 — Forgot / Reset Password
- **REQ-01-05-01** WHEN user POST `/auth/forgot-password` với email đã đăng ký THE hệ thống SHALL gửi email chứa token reset (TTL 1 giờ) và luôn trả 200 (không lộ email tồn tại).
- **REQ-01-05-02** WHEN user POST `/auth/reset-password` với token hợp lệ và password mới đạt policy THEN THE hệ thống SHALL đổi password_hash, blacklist tất cả refresh token hiện tại.

### REQ-01-06 — Authorization (RBAC)
- **REQ-01-06-01** WHEN endpoint yêu cầu role THE hệ thống SHALL kiểm tra JWT chứa `userId`, `roles`, `familyMemberships[]` (cached 5 phút).
- **REQ-01-06-02** IF user không có quyền trên family THEN THE hệ thống SHALL trả 403 `FORBIDDEN`.
- **REQ-01-06-03** WHILE request BE xử lý THE hệ thống SHALL log audit `actor`, `familyId`, `route`, `decision (allow/deny)`.

### REQ-01-07 — Account Security
- **REQ-01-07-01** WHEN login thất bại ≥5 lần trong 15 phút từ 1 IP THEN THE hệ thống SHALL block IP đó trong 30 phút (Bucket4j).
- **REQ-01-07-02** WHEN password được đổi THE hệ thống SHALL gửi email thông báo tới email đăng ký.
- **REQ-01-07-03** WHERE JWT được ký THE hệ thống SHALL dùng thuật toán HS256 với secret xoay vòng (key id `kid`) để hỗ trợ rotation.

## Design

### BE modules
```
auth/
├── controller/{AuthController, OAuth2Controller, PasswordController}
├── service/{AuthService, TokenService, OAuth2Service, PasswordService}
├── dto/{RegisterRequest, LoginRequest, TokenResponse, RefreshRequest, ResetPasswordRequest}
├── security/{JwtAuthFilter, JwtTokenProvider, AuthPrincipal, RateLimitFilter}
└── repository/{UserRepository, RefreshTokenRepository}
```

### Token shape
```json
// Access
{
  "sub": "uuid-user",
  "email": "a@b.c",
  "roles": ["USER"],
  "fams": [{ "fid": "uuid", "role": "EDITOR" }],
  "iat": 0, "exp": 0, "kid": "v1"
}
// Refresh
{ "sub": "uuid-user", "jti": "uuid", "iat": 0, "exp": 0 }
```

### Storage
- `users.password_hash` (BCrypt cost 12).
- Refresh token: lưu `jti` + `userId` + `expiresAt` trong Postgres; cache blacklist trong Redis (key `bl:refresh:{jti}`).

### Endpoints
| Method | Path | Auth |
|---|---|---|
| POST | `/auth/register` | public |
| POST | `/auth/login` | public |
| POST | `/auth/refresh` | public (refresh token) |
| POST | `/auth/logout` | user |
| POST | `/auth/forgot-password` | public |
| POST | `/auth/reset-password` | public (reset token) |
| GET | `/auth/oauth2/{provider}` | public |
| GET | `/auth/oauth2/{provider}/callback` | public |
| GET | `/users/me` | user |
| PATCH | `/users/me` | user |
| PATCH | `/users/me/password` | user |

### FE flow
- `AuthProvider` đọc token từ httpOnly cookie + zustand cho UI state.
- Axios interceptor: 401 access → refresh → retry 1 lần; refresh fail → logout.

## Tasks

- [ ] Tạo Flyway `V2__users.sql` (bảng `users`, indexes).
- [ ] Tạo entity `User`, repository, MapStruct mapper.
- [ ] Implement `JwtTokenProvider` (ký/verify với `kid`, parse claims).
- [ ] Implement `JwtAuthFilter` + `SecurityConfig` (stateless, CORS, CSRF off cho API).
- [ ] Implement `AuthService.register/login/refresh/logout`.
- [ ] Implement controller + DTO + validation (Bean Validation).
- [ ] Implement `RateLimitFilter` (Bucket4j Redis backend).
- [ ] Implement `OAuth2Service` + controller cho Google/Facebook (Spring Security OAuth2 Client).
- [ ] Implement `PasswordService` + email template (Thymeleaf) + token table.
- [ ] Implement `GET /users/me`, `PATCH /users/me`, `PATCH /users/me/password`.
- [ ] Tạo Flyway `V3__family_members_seed.sql` (chưa cần family module — tạm thời mỗi user có 1 "personal" family stub cho OAuth flow).
- [ ] FE: tạo `lib/api/client.ts` (axios + interceptor refresh).
- [ ] FE: tạo `store/auth.ts` (zustand).
- [ ] FE: trang `/login`, `/register`, `/forgot-password`, `/reset-password`.
- [ ] FE: `AuthProvider` + middleware route guard.
- [ ] Test: unit `AuthService`, `JwtTokenProvider`.
- [ ] Test: integration `/auth/register`, `/auth/login`, `/auth/refresh` với Testcontainers Postgres + Redis.
- [ ] Test: e2E Playwright flow đăng ký → login → truy cập `/dashboard`.
- [ ] Verify OWASP ASVS level 1: password policy, rate limit, JWT claim đầy đủ.
