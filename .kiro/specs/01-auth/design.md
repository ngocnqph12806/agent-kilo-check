# 01 — Auth & RBAC · Design

## 1. Kiến trúc

```
Browser ──HTTPS──> Nginx ──> api(/api/v1/*)
                                │
                  ┌─────────────┼─────────────┐
                  ▼             ▼             ▼
            AuthController  OAuth2Controller UsersController
                  │             │             │
                  ▼             ▼             ▼
              AuthService   OAuth2Service  UserService
                  │             │
        ┌─────────┼─────────────┼──────┐
        ▼         ▼             ▼      ▼
  JwtTokenProvider  PasswordService  RateLimitFilter
        │                              │
        ▼                              ▼
     Postgres                       Redis (Bucket4j)
```

## 2. JWT chi tiết

- **Thuật toán:** HS256 với 2-key rotation (current + previous), phân biệt bằng `kid`.
- **Secret:** lưu trong biến môi trường `JWT_SECRET_CURRENT`, `JWT_SECRET_PREVIOUS`.
- **Access TTL:** 15 phút. Refresh TTL: 7 ngày.
- **Claims access:** `sub, email, roles[], fams[{fid,role}], kid, iat, exp, jti`.
- **Claims refresh:** `sub, jti, iat, exp`.
- **Verify order:** parse header `kid` → tìm key tương ứng → verify signature & exp → kiểm tra `jti` không nằm trong Redis blacklist.

## 3. Refresh token rotation

- Mỗi lần `/auth/refresh`, cặp token mới được cấp; token cũ bị blacklist (key `bl:refresh:{jti}`, TTL = exp - now).
- Lưu refresh token metadata (jti, userId, ua, ip, createdAt, expiresAt) vào bảng `refresh_tokens` phục vụ audit + revoke all sessions.

## 4. RBAC model

- Quyền gắn liền với `family`, không phải global.
- JWT chứa cache `fams` (role theo family) với TTL 5 phút; nếu thay đổi role, force refresh token hoặc check DB trực tiếp khi cần quyền nhạy cảm.
- Annotation sử dụng: `@PreAuthorize("@familyGuard.canEdit(#familyId, principal)")`.

### Permission matrix
| Action | OWNER | EDITOR | CONTRIBUTOR | VIEWER |
|---|---|---|---|---|
| Xem family | ✅ | ✅ | ✅ | ✅ |
| CRUD person | ✅ | ✅ | ❌ (gửi yêu cầu) | ❌ |
| CRUD relationship | ✅ | ✅ | ❌ | ❌ |
| Upload media | ✅ | ✅ | ✅ | ❌ |
| Đổi role member | ✅ | ❌ | ❌ | ❌ |
| Mời member | ✅ | ✅ | ❌ | ❌ |
| Xóa family | ✅ | ❌ | ❌ | ❌ |
| Xuất bản | ✅ | ✅ | ❌ | ❌ |

## 5. OAuth2

- Dùng Spring Security OAuth2 Client (authorization_code).
- Provider: Google, Facebook (cấu hình trong `application-oauth.yml`).
- `OAuth2SuccessHandler`: tạo/cập nhật user → cấp JWT → redirect về `${WEB_URL}/oauth/callback?token=...`; FE nhận token và lưu.

## 6. Rate limit

| Endpoint | Limit |
|---|---|
| `POST /auth/login` | 5/15min/IP |
| `POST /auth/register` | 10/hour/IP |
| `POST /auth/forgot-password` | 3/hour/IP |
| `POST /auth/reset-password` | 5/hour/email |
| `POST /auth/refresh` | 30/min/user |

## 7. Bảo mật

- Password hash: BCrypt cost 12.
- Reset token: 32-byte random, hash SHA-256 lưu DB.
- JWT secret rotation: hỗ trợ 2 key cùng lúc.
- Logout phía FE: xóa cookie + gọi `/auth/logout`.
- CORS: chỉ cho phép origin `WEB_URL` (env), credentials true.
- CSRF: tắt cho API (Bearer token), bật cho form web nếu có.

## 8. Schema bổ sung
```sql
-- V2__users.sql
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255),
  full_name VARCHAR(255),
  avatar_url TEXT,
  email_verified BOOLEAN DEFAULT FALSE,
  oauth_provider VARCHAR(50),
  oauth_provider_id VARCHAR(255),
  locale VARCHAR(10) DEFAULT 'vi',
  theme VARCHAR(20) DEFAULT 'system',
  is_disabled BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_users_oauth ON users(oauth_provider, oauth_provider_id);

CREATE TABLE refresh_tokens (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  jti UUID NOT NULL UNIQUE,
  user_agent TEXT,
  ip VARCHAR(45),
  created_at TIMESTAMPTZ DEFAULT now(),
  expires_at TIMESTAMPTZ NOT NULL,
  revoked_at TIMESTAMPTZ
);
CREATE INDEX idx_refresh_user ON refresh_tokens(user_id);

CREATE TABLE password_reset_tokens (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  used_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT now()
);
```

## 9. Email templates
- `welcome.html` — chào mừng sau đăng ký.
- `reset-password.html` — link reset (chứa token plain, FE điền form).
- `password-changed.html` — xác nhận đổi pass.
- `oauth-linked.html` — thông báo link OAuth.
