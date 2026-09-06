# 02 — Family Management · Design

## 1. Module
```
family/
├── controller/{FamilyController, FamilyMemberController, PublicFamilyController}
├── service/{FamilyService, MembershipService, InviteService, SlugService}
├── dto/{FamilyCreateRequest, FamilyUpdateRequest, FamilyResponse, InviteRequest, InviteAcceptRequest, MemberResponse, ChangeRoleRequest}
├── entity/{Family, FamilyMember, FamilyInvite}
├── repository/{FamilyRepository, FamilyMemberRepository, FamilyInviteRepository}
└── guard/{FamilyGuard}  // @PreAuthorize SpEL
```

## 2. Schema (Flyway V3)
```sql
CREATE TABLE families (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug VARCHAR(120) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  origin_location VARCHAR(255),
  visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
  cover_image_url TEXT,
  founder_id UUID,
  created_by UUID NOT NULL REFERENCES users(id),
  deleted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_families_creator ON families(created_by) WHERE deleted_at IS NULL;

CREATE TABLE family_members (
  family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role VARCHAR(20) NOT NULL,
  joined_at TIMESTAMPTZ DEFAULT now(),
  PRIMARY KEY (family_id, user_id)
);
CREATE INDEX idx_family_members_user ON family_members(user_id);

CREATE TABLE family_invites (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  email VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  token_hash VARCHAR(255) NOT NULL,
  invited_by UUID REFERENCES users(id),
  expires_at TIMESTAMPTZ NOT NULL,
  used_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT now()
);
```

## 3. Slug generation
- B1: lowercase, NFKD strip dấu, thay đường kẻ và space bằng `-`, cắt còn [a-z0-9-], collapse `-`.
- B2: query `SELECT 1 FROM families WHERE slug=?`; nếu tồn tại append `-2`, `-3`...
- B3: cache check trong Redis `slug:{slug}` 60s để tránh race condition (DB unique constraint là fallback).

## 4. Invite flow
```
OWNER POST /families/{slug}/members {email, role}
  → nếu email đã có user → MembershipService.addMember()
  → nếu chưa có user → InviteService.createInvite(token)
                            → email template invite.html
User bấm link → FE /invites/accept?token=xxx
  → BE POST /invites/accept {token} → đăng ký (nếu chưa) + auto join
```

## 5. RBAC guard
```java
@Component("familyGuard")
public class FamilyGuard {
  public boolean canEdit(UUID familyId, AuthPrincipal p) {
    return p.familyRole(familyId).map(r ->
      r == Role.OWNER || r == Role.EDITOR).orElse(false);
  }
  public boolean isOwner(UUID familyId, AuthPrincipal p) {
    return p.familyRole(familyId).map(r -> r == Role.OWNER).orElse(false);
  }
}
```

## 6. Soft delete
- `families.deleted_at` set thời điểm hiện tại.
- Tất cả query trong `FamilyRepository.findAll()` filter `WHERE deleted_at IS NULL`.
- Cascade: KHÔNG xóa persons, relationships, events (giữ cho khôi phục).
- Audit log: `family.deleted` với payload `{deletedBy, slug}`.

## 7. Cache invalidation
- Khi đổi role / xóa member → publish Redis pub/sub `family:membership:update:{userId}`.
- Tất cả instance BE subscribe; xóa cache local `principalCache:{userId}` để buộc re-fetch từ DB.
