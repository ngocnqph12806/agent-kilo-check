# 02 — Family Management · Tasks

- [ ] Tạo Flyway `V3__families.sql` (families, family_members, family_invites).
- [ ] Tạo entities + repositories + MapStruct mapper.
- [ ] Implement `SlugService` (slugify + collision check) với Redis cache.
- [ ] Implement `FamilyService` (create, list, detail, update, soft-delete).
- [ ] Implement `MembershipService` (add, change role, remove, last-owner check).
- [ ] Implement `InviteService` (token sinh, hash lưu DB, email template).
- [ ] Implement `FamilyGuard` SpEL helper.
- [ ] Implement controllers + DTO + validation.
- [ ] Implement `PublicFamilyController` (`/p/{slug}` + share token).
- [ ] BE unit tests cho SlugService (collisions, unicode).
- [ ] BE integration tests cho create/update/delete + RBAC.
- [ ] FE: trang `/families` (danh sách + nút Create).
- [ ] FE: trang `/families/[slug]/settings` (form update, members table, invite dialog).
- [ ] FE: `/invites/accept` page (gọi BE rồi redirect).
- [ ] FE: `/p/[slug]` (public tree view — chỉ skeleton ở epic này, render thật ở epic 06).
- [ ] Email templates: `invite.html`, `added-to-family.html`.
- [ ] Cache invalidation: pub/sub Redis cho membership change.
- [ ] Verify RBAC matrix bằng test matrix.
