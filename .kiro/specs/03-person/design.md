# 03 — Person CRUD · Design

## 1. Module
```
person/
├── controller/{PersonController, PersonMediaController}
├── service/{PersonService, PersonAuditService, GenerationResolver, LunarDateService}
├── dto/{PersonCreateRequest, PersonUpdateRequest, PersonResponse, PersonListItem, LunarDateDto}
├── entity/{Person, PersonAudit}
├── repository/{PersonRepository}
└── mapper/PersonMapper (MapStruct)
```

## 2. GenerationResolver
- Mặc định = max(generation của parents) + 1; nếu không có parent = 1.
- Implement: query JPA `SELECT MAX(generation) FROM persons WHERE id IN (parents)` sau khi attach parent (tạo relationship xong mới có parent chính thức).
- Cache `(personId → generation)` trong Redis 5 phút.

## 3. Lunar/Solar conversion
- Dùng thư viện `com.github.nbbrd.java-statistical-datetime-utils` hoặc custom convert dựa trên bảng lunar (1900–2100).
- Service `LunarDateService`:
  ```java
  SolarDate toSolar(LocalDate lunar, int leapMonth); // -> LocalDate
  LunarDate toLunar(LocalDate solar);               // -> {day,month,year,leap}
  ```
- Lưu DB: 2 cột `*_solar` và `*_lunar_json` (JSONB chứa `{day,month,year,leap}`).
- Edge case: tháng nhuận trả 2 giá trị hợp lệ → BE lưu leap flag, FE chọn.

## 4. Audit
- Bảng `person_audits`:
  ```sql
  CREATE TABLE person_audits (
    id BIGSERIAL PRIMARY KEY,
    person_id UUID NOT NULL,
    actor_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL,
    diff JSONB NOT NULL,        // {"field": {"old": x, "new": y}}
    created_at TIMESTAMPTZ DEFAULT now()
  );
  CREATE INDEX idx_person_audits_person ON person_audits(person_id, created_at DESC);
  ```
- AOP `@Audited("Person")` chụp diff bằng cách compare entity trước/sau flush.

## 5. Soft delete
- Cột `persons.deleted_at`.
- Custom `@Where(clause = "deleted_at IS NULL")` trên entity.
- Custom repository methods cho admin restore.
- Cascade: xóa relationship rows where person_a OR person_b = id (FK ON DELETE CASCADE nếu cứng; ở đây ưu tiên soft, set `relationships.deleted_at`).

## 6. Pagination
- Cursor-based: `?cursor=<personId>&size=20`, sort by `(generation ASC, surname ASC, given_name ASC, id ASC)`.
- Response: `{ items: [...], nextCursor: "..." | null }`.

## 7. Media attachment
- `persons.avatar_media_id` FK → `media_assets.id`.
- Upload flow xem epic 05; Person PATCH `{avatarMediaId: "..."}`.

## 8. Validation rules
- `givenName`: 1–120 ký tự, required.
- `surname`: ≤120.
- `gender`: enum MALE/FEMALE/OTHER/UNKNOWN.
- `birthDate`, `deathDate`: trong khoảng năm 1500 đến hiện tại +1.
- `biography`: ≤50000 ký tự.
- `avatarMediaId`: phải thuộc cùng family.

## 9. API endpoints
| Method | Path | Role |
|---|---|---|
| GET | `/families/{slug}/persons` | MEMBER |
| POST | `/families/{slug}/persons` | EDITOR+ |
| GET | `/persons/{id}` | MEMBER |
| PATCH | `/persons/{id}` | EDITOR+ |
| DELETE | `/persons/{id}` | EDITOR+ |
| GET | `/persons/{id}/history` | MEMBER |
| POST | `/persons/{id}/restore` | OWNER (admin) |
