# 11 — Public Share & Embed · Design

## 1. Module
```
share/
├── controller/{PublicFamilyController, ShareTokenController, EmbedController}
├── service/{PublicFamilyService, ShareTokenService, EmbedWhitelistService}
├── dto/{PublicFamilyResponse, PublicTreeNode, PublicTreeEdge, ShareTokenRequest, EmbedConfig}
├── entity/{ShareToken, EmbedWhitelist}
└── sanitizer/{PublicDataSanitizer}
```

## 2. Schema (Flyway V11)
```sql
CREATE TABLE share_tokens (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  token_hash VARCHAR(255) NOT NULL UNIQUE,
  created_by UUID REFERENCES users(id),
  expires_at TIMESTAMPTZ,
  revoked_at TIMESTAMPTZ,
  can_download BOOLEAN DEFAULT FALSE,
  view_count BIGINT DEFAULT 0,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE embed_whitelists (
  family_id UUID REFERENCES families(id) ON DELETE CASCADE,
  domain VARCHAR(255) NOT NULL,
  PRIMARY KEY (family_id, domain)
);
```

## 3. PublicDataSanitizer
- Loại bỏ fields: email, phone, address chi tiết, media gốc (chỉ thumbnail), biography nếu private.
- Map entity → DTO public với field allowlist.

## 4. ShareTokenService
- Generate: `SecureRandom 32 byte → base64url`.
- Hash: `SHA-256(token)` lưu DB.
- Validate: lookup by hash, check expiresAt, revokedAt.
- View count: increment khi hit `/p/{slug}?token=`.

## 5. Embed flow
```
GET /p/{slug}?embed=1
  → render layout "embed" (no chrome)
  → set CSP/X-Frame-Options dựa trên whitelist
  → nếu domain request không whitelisted → 403
```

## 6. SEO
- `next-seo` cho OG tags.
- Dynamic `sitemap.xml` route `/sitemap.xml` (chỉ PUBLIC families).
- `robots.txt`: allow public, disallow `/p/*?token=` và dashboard.

## 7. Endpoints
| Method | Path | Auth |
|---|---|---|
| GET | `/p/{slug}` | public (token tùy visibility) |
| GET | `/p/{slug}/tree` | public |
| POST | `/families/{slug}/share-tokens` | OWNER |
| DELETE | `/share-tokens/{id}` | OWNER |
| GET | `/families/{slug}/embed-config` | OWNER |
| PUT | `/families/{slug}/embed-config` | OWNER |

## 8. Caching
- Public tree data cache Redis 10 phút, key `public:tree:{slug}:{hash(filter)}`.
- Invalidate khi family update visibility, person/relationship thay đổi.

## 9. Analytics
- Log view event `{familyId, ip, ua, referer, ts}` vào bảng `page_views` (Flyway V11).
- Dashboard OWNER hiển thị chart.
