# 11 — Public Share & Embed · Tasks

- [ ] Tạo Flyway `V11__share.sql` (share_tokens, embed_whitelists, page_views).
- [ ] `PublicDataSanitizer` map entity → public DTO.
- [ ] `ShareTokenService` (generate, hash, validate, revoke).
- [ ] `EmbedWhitelistService` (CRUD domain).
- [ ] `PublicFamilyController` (`/p/{slug}` + tree data endpoint).
- [ ] BE rate limit filter cho public route.
- [ ] Cache public tree Redis 10 phút + invalidation.
- [ ] SEO: `next-seo` integration, OG tags, sitemap.xml route.
- [ ] BE unit test sanitizer (đảm bảo không lộ email).
- [ ] BE integration test share token + embed whitelist.
- [ ] e2E: public visibility PUBLIC → SEO crawler render OK; UNLISTED cần token.
- [ ] FE: page `/p/[slug]` với minimal UI (no auth required).
- [ ] FE: page `/p/[slug]?embed=1` layout riêng.
- [ ] FE: Share dialog trong family settings (copy link, set expire, canDownload).
- [ ] FE: Embed config UI (textarea domain, list hiện tại).
- [ ] FE: view analytics chart trong dashboard.
- [ ] Security: CSP header cho embed route.
- [ ] Verify `robots.txt` đúng chuẩn.
