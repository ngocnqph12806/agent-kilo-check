# ADR-008 — Table naming convention: plural

- **Status:** Accepted
- **Date:** 2026-09-09
- **Sprint:** Sprint 5 (T-M420)
- **Supersedes:** §5.1 rule "DB tables số ít" (AGENTS.md cũ)

## Context

AGENTS.md §5.1 (2026-05-20 bản đầu) yêu cầu table name **số ít** (`user`, `booking`, `seed_transaction`). Tuy nhiên, toàn bộ 12 Flyway migrations (`V1` → `V12`) đều dùng **số nhiều** (`users`, `bookings`, `seed_transactions`, `idempotency_keys`, `waitlists`, …).

Audit ngày 2026-09-09 phát hiện:
- **Spec ↔ code drift 100%** trên rule này — không có một table nào tuân theo §5.1.
- Rule cũ đã bị ignore một cách có hệ thống từ migration đầu tiên.
- Spring Data JPA + Hibernate không enforce số ít/nhiều — `@Table(name = "users")` và `@Table(name = "user")` đều hợp lệ.
- Nhiều shop chuẩn (Rails default, Spring Data default, DDD practical) đều dùng plural.

## Decision

Đổi rule thành **snake_case, số nhiều**:
- `users`, `bookings`, `seed_transactions`, `skills`, `sessions`, `ratings`, `notifications`, `idempotency_keys`, `waitlists`, `user_wallets`.
- Tên bảng pivot/join vẫn số nhiều (`user_skills`, `booking_sessions`).
- Tên bảng internal/lookup vẫn số nhiều (`enum_*` tables cũng đổi từ `enum_value` → `enum_values` nếu có; hiện không có).

## Alternatives considered

### Option A (đã chọn) — Sửa rule, giữ schema hiện tại
- Effort: 0.1 ngày (1 dòng AGENTS.md + file ADR này).
- Ưu: Không migration, không downtime, khớp thực tế 100%, tên bảng đọc tự nhiên hơn khi query SQL thủ công.
- Nhược: Phải document lại lý do đổi (file này).

### Option B — Migration V16 đổi tất cả về số ít
- Effort: 3+ ngày, gồm:
  - 1 file SQL rename 11 tables + recreate indexes/constraints.
  - Update 16 repository JPQL (`@Query("SELECT u FROM User u")` không đổi, nhưng native queries + `@Table` annotations 10 entity phải đổi).
  - Update test fixtures (`@Sql` scripts, `Testcontainers` init SQL).
  - Update `docs/SKILLSEED_API_AND_DB.md §8`.
- Nhược: Downtime cao (rename table cần lock), không tương thích với backup restore cũ, không có ROI rõ ràng — singular chỉ "đẹp hơn" về mặt lý thuyết.

### Option C — Bỏ rule hoàn toàn (cho phép cả hai)
- Effort: 0.05 ngày.
- Nhược: Gây nhầm lẫn cho contributor mới, dẫn đến 2 table cùng concept (`user` vs `users`) trong tương lai.

## Consequences

**Positive:**
- Đồng bộ rule ↔ code (zero drift).
- SQL query thủ công đọc tự nhiên: `SELECT * FROM users WHERE email = ?` chuẩn Rails/Postgres convention.
- Dễ debug — không phải nhớ "table này thực ra là số nhiều dù rule nói số ít".
- Tương thích tool: `pg_dump`, `psql`, `pgAdmin` đều hiển thị đẹp với plural.

**Negative:**
- Phải giải thích rule mới trong onboarding (file này + §5.1 update).
- Nếu sau này có migration mới, contributor mới phải biết "rule mới là plural, không phải singular cũ".

## Migration plan

Không cần migration — rule chỉ formalize hoá trạng thái đã tồn tại.

## Validation

- ✅ `mvn flyway:validate` — confirm 12 migrations hiện tại đều dùng plural.
- ✅ `mvn test` — repositories không thay đổi JPQL.
- ✅ Lint/manual: grep `@Table(name = "` trong entity package — tất cả đều khớp plural.

## References

- AGENTS.md §5.1 (post-update 2026-09-09)
- Flyway migrations: `backend/src/main/resources/db/migration/V1__*.sql` → `V12__*.sql`
- Audit report 2026-09-09 (Sprint 5 entry)
