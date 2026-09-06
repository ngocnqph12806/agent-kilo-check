# 11 — Public Share & Embed · Requirements

### REQ-11-01 — Visibility levels
- **REQ-11-01-01** THE hệ thống SHALL hỗ trợ 3 visibility: `PRIVATE` (chỉ member), `UNLISTED` (cần share token), `PUBLIC` (không cần auth, có thể SEO).
- **REQ-11-01-02** WHEN user đổi visibility THE hệ thống SHALL audit log + clear cache public page.

### REQ-11-02 — Public tree route
- **REQ-11-02-01** WHEN GET `/p/{slug}` THE hệ thống SHALL trả public tree view nếu visibility=PUBLIC.
- **REQ-11-02-02** THE hệ thống SHALL loại bỏ mọi thông tin nhạy cảm (email, phone, địa chỉ chính xác) trong public response.
- **REQ-11-02-03** THE hệ thống SHALL trả 404 nếu family không tồn tại hoặc PRIVATE.
- **REQ-11-02-04** IF family UNLISTED THE hệ thống SHALL yêu cầu `?token=` hợp lệ; sai → 404.

### REQ-11-03 — Share token
- **REQ-11-03-01** WHEN OWNER POST `/families/{slug}/share-tokens` với `{expiresAt?, canDownload?}` THE hệ thống SHALL sinh token ngẫu nhiên (32 byte base64url) lưu hash, trả URL kèm token.
- **REQ-11-03-02** WHEN user dùng token để xem UNLISTED THE hệ thống SHALL đếm view (analytics).
- **REQ-11-03-03** WHEN OWNER DELETE `/share-tokens/{id}` THE hệ thống SHALL revoke.

### REQ-11-04 — Embed
- **REQ-11-04-01** THE hệ thống SHALL cung cấp iframe snippet: `<iframe src="https://app/p/{slug}?embed=1" width="800" height="600" />`.
- **REQ-11-04-02** WHEN `embed=1` query THE hệ thống SHALL render minimal UI (no header/footer), enable `X-Frame-Options: ALLOWALL` chỉ cho domain trong whitelist.
- **REQ-11-04-03** THE hệ thống SHALL cho phép OWNER cấu hình allowed embed domains.

### REQ-11-05 — SEO
- **REQ-11-05-01** WHEN family visibility=PUBLIC THE hệ thống SHALL render Open Graph meta tags (og:title, og:image, og:description) cho crawler.
- **REQ-11-05-02** THE hệ thống SHALL sinh `sitemap.xml` cho families PUBLIC.
- **REQ-11-05-03** THE hệ thống SHALL set `noindex` cho PRIVATE/UNLISTED.

### REQ-11-06 — Rate limit
- **REQ-11-06-01** THE hệ thống SHALL rate limit public route: 60 req/min/IP (token-bucket).

## Acceptance
- Family PRIVATE → `/p/{slug}` trả 404.
- Family UNLISTED + token → view OK; sai token → 404.
- Family PUBLIC → Open Graph render đúng.
- Embed iframe load được, không hiển thị header/footer.
- Share token hết hạn → 404.
