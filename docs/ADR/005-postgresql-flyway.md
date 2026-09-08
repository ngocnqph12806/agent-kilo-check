# ADR-005: Postgres + Flyway for schema evolution

- **Status:** Accepted
- **Date:** 2026-08-25
- **Deciders:** Founder, Tech Lead
- **Related:** `backend/src/main/resources/db/migration/`

## Context

We need a relational store with strong typing, transactions, and
reliable schema migrations that work both locally (docker compose) and
on managed Postgres (Supabase).

## Decision

Use **Postgres 16** for the database and **Flyway** for migrations.
All schema changes go through versioned SQL files under
`src/main/resources/db/migration/V{N}__{description}.sql`. No
`ddl-auto: update` in any environment — we always validate.

## Consequences

**Positive**
- Migrations are first-class code — reviewed in PRs, replayable.
- `ddl-auto: validate` catches schema drift between code and DB.
- Easy to roll back manually by writing a `U{N}__{description}.sql`
  undo script.

**Negative**
- Two-step migrations for breaking changes (expand → migrate contract
  → contract).
- Large seed data goes through Flyway too (e.g. `V2__seed_skills.sql`).

**Reversible?** Yes — we just write a new migration to undo.