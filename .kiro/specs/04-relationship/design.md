# 04 — Relationships · Design

## 1. Module
```
relationship/
├── controller/{RelationshipController}
├── service/{RelationshipService, RelationshipValidator, SiblingDeriver, CycleDetector}
├── dto/{RelationshipCreateRequest, RelationshipResponse, RelationsByPersonResponse}
├── entity/{Relationship}
├── repository/{RelationshipRepository}
└── strategy/  // một class mỗi type, implement validate()
    ├── SpouseValidator
    ├── ParentChildValidator
    ├── SiblingValidator
    ├── AdoptedValidator
    └── GodparentValidator
```

## 2. Schema
```sql
CREATE TABLE relationships (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  type VARCHAR(30) NOT NULL,
  person_a_id UUID NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
  person_b_id UUID NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
  start_date DATE,
  end_date DATE,
  notes TEXT,
  deleted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE UNIQUE INDEX idx_rel_unique ON relationships(family_id, type, person_a_id, person_b_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_rel_a ON relationships(person_a_id);
CREATE INDEX idx_rel_b ON relationships(person_b_id);
```

## 3. Strategy pattern cho validation
```java
public interface RelationshipValidator {
  RelType supports();
  void validate(Person a, Person b, RelationshipCreateRequest req);
}
```
- Bean `RelationshipValidatorRegistry` map `RelType → Validator`.
- `RelationshipService.create()` gọi registry.validate trước khi persist.

## 4. Cycle detection (PARENT_CHILD)
- BFS từ `personB` lên các ancestor qua parent edges; nếu gặp `personA` → cycle.
- Dùng materialized view cache hoặc in-memory DFS (giới hạn depth 50).
- Check trước create + check trong transaction (SELECT ... FOR UPDATE để chống race).

## 5. Sibling auto-derive
- Khi persist PARENT_CHILD:
  ```sql
  -- tìm các child khác cùng parent
  SELECT pc2.child_id FROM parent_child pc1
  JOIN parent_child pc2 ON pc1.parent_id = pc2.parent_id
  WHERE pc1.child_id = :childId AND pc2.child_id <> :childId;
  ```
- Insert SIBLING(idempotent, dựa vào unique index).

## 6. SPOUSE state machine
- States: `current` (endDate null), `ended` (endDate set).
- Quy tắc:
  - Một person chỉ có 1 `current` spouse.
  - Tạo mới → check existing; nếu có → 409.
  - Khi set `endDate` → spouse cũ chuyển `ended`.

## 7. Generation update
- Khi PARENT_CHILD thay đổi → publish event `PersonGenerationChanged(personId)`.
- Listener: update `persons.generation` của subtree (chỉ tăng/giảm 1 level tại subtree gốc), không scan toàn bộ.

## 8. Read API
- `GET /persons/{id}/relations`:
  - 4 query: parents, children, spouses, siblings.
  - Dùng 1 SQL với UNION ALL và JSON_AGG.
  - Cache 60s Redis (invalidate khi relationship thay đổi).

## 9. Endpoints
| Method | Path | Role |
|---|---|---|
| POST | `/families/{slug}/relationships` | EDITOR+ |
| GET | `/families/{slug}/relationships` | MEMBER |
| GET | `/persons/{id}/relations` | MEMBER |
| PATCH | `/relationships/{id}` | EDITOR+ |
| DELETE | `/relationships/{id}` | EDITOR+ |
