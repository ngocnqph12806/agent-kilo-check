# ADR-0002: PostgreSQL 16 làm Primary Database cho toàn bộ Phase 1–4

- **Status:** Accepted
- **Date:** 2026-09-07
- **Phase:** 1–4
- **Deciders:** SkillSeed Product Team

---

## Context

Cần chọn primary database cho transactional data (user, booking, wallet, rating, …). Cân nhắc giữa SQL và NoSQL.

## Decision

**Chọn PostgreSQL 16** (Supabase free tier cho dev / RDS hoặc self-host cho prod).

## Rationale

1. **ACID:** Wallet / booking / payment cần transaction mạnh. PostgreSQL là lựa chọn hàng đầu.
2. **JSONB:** Lưu Skill DNA (JSON linh hoạt) + query được bằng GIN index → tránh phải dùng MongoDB cho "flexible schema".
3. **Full-Text Search built-in:** Phase 1 không cần Qdrant/Elasticsearch. `tsvector` + `tsquery` đủ cho skill search.
4. **Mature ecosystem:** Flyway, Hibernate, PostGIS (nếu cần), pg_partman (partitioning).
5. **Cloud options:** Supabase (free + nhanh), Neon, RDS, self-host — linh hoạt.
6. **Skillset:** Java backend dev đã quen. Không tốn thời gian học lại.

## Consequences

### Positive
- Single DB cho mọi use case (transaction + FTS + JSON). Đơn giản hoá vận hành.
- Tooling đầy đủ: backup, replication, monitoring, point-in-time recovery.

### Negative
- Vertical scale giới hạn. Khi > 100GB hoặc > 10K write/s phải cân nhắc partitioning / sharding.
- Multi-region write sẽ khó (chỉ giải quyết bằng read replica).

## Alternatives Considered

| Phương án | Tại sao không chọn |
|---|---|
| MySQL | JSON support yếu hơn JSONB; FTS kém hơn; team quen Postgres hơn |
| MongoDB | Không cần cho flexible schema (JSONB đủ); transaction multi-doc yếu hơn |
| Cassandra / DynamoDB | Overkill cho MVP; cost cao |
| SQLite | Không scale; không dùng cho multi-instance |

## When to Revisit

- Khi MAU > 100K và write throughput > 10K/s → cân nhắc sharding hoặc tách hot tables (wallet, booking) sang riêng
- Khi cần multi-region write (Phase 5+) → cân nhắc CockroachDB hoặc Spanner

## References

- `.kiro/specs/phase-1-mvp/design.md` §2.3
- `SKILLSEED_API_AND_DB.md` §8 (DB Schema)
