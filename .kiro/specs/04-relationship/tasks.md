# 04 — Relationships · Tasks

- [ ] Tạo Flyway `V5__relationships.sql`.
- [ ] Entity `Relationship` với soft delete.
- [ ] Repository + custom queries (parents/children/spouses/siblings per person).
- [ ] Implement `RelationshipValidator` interface + 5 concrete validators.
- [ ] `RelationshipValidatorRegistry` (Spring autowire list → map).
- [ ] `CycleDetector` (DFS giới hạn 50, dùng `SELECT FOR UPDATE`).
- [ ] `SiblingDeriver` (tự sinh SIBLING khi PARENT_CHILD).
- [ ] `RelationshipService` (create/read/update/softDelete + publish events).
- [ ] DTO + MapStruct mapper + validation.
- [ ] Controllers + Swagger.
- [ ] Event `PersonGenerationChanged` + listener update subtree generation.
- [ ] Cache Redis `relations:{personId}` (60s TTL, invalidate on write).
- [ ] BE unit test cho từng validator (cases: too young, cycle, already married, cross-family).
- [ ] BE integration test full flow: create family → A → B(child of A) → C(child of A) → assert SIBLING(B,C).
- [ ] FE: `RelationshipForm` (chọn type + 2 person selectors + date pickers).
- [ ] FE: hiển thị relations trong trang person detail.
- [ ] FE: bulk-action "Set spouse" và "Add parent".
- [ ] e2E: tạo couple → thêm 2 child → assert SIBLING tự sinh.
