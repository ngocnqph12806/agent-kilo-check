# SkillSeed Backend

Java 21 + Spring Boot 3.3 monolith for the SkillSeed MVP (Phase 1).

> **Spec:** `.kiro/specs/phase-1-mvp/`
> **Architecture:** docs/ADR/ — see ADR-001 (monolith), ADR-002
> (Daily.co), ADR-003 (Seed ledger), ADR-004 (Java 21), ADR-005
> (Flyway).
> **Deploy:** see `docs/DEPLOYMENT.md`.

---

## Tech stack

| Layer | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Build | Maven |
| DB | PostgreSQL 16 (via Flyway) |
| Cache / locks | Redis 7 |
| Video | Daily.co |
| Email | Resend |
| Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, AssertJ |

---

## Modules (top-level packages)

| Package | Module | Purpose |
|---|---|---|
| `com.skillseed.shared.*` | cross-cutting | Security, exceptions, config, enums, utils |
| `com.skillseed.auth` | auth | Email/password + OAuth + JWT |
| `com.skillseed.user` | user | Profile, Skill DNA, availability, GDPR |
| `com.skillseed.skill` | skill | Skill catalogue (2 000+ skills) |
| `com.skillseed.discover` | discover | Search + match |
| `com.skillseed.booking` | booking | Bookings + state machine |
| `com.skillseed.wallet` | wallet | Seed ledger |
| `com.skillseed.session` | session | Video room + chat + webhook |
| `com.skillseed.rating` | rating | Post-session reviews + auto-rate |
| `com.skillseed.notification` | notification | In-app + email |

Anything used by **≥ 2 modules** must live in `shared.*` — see
`AGENTS.md` §5.4.

---

## Local development

### Option A — Docker Compose (recommended)

```bash
# from repo root
docker compose up -d
```

- Backend on `:8080`
- Postgres on `:5432`
- Redis on `:6379`

Run the backend in your IDE or via Maven:

```bash
cd backend
mvn spring-boot:run
```

### Option B — Native

Requires Java 21 + Maven 3.9 + a running Postgres + Redis (use
`docker compose up postgres redis`).

```bash
cd backend
mvn spring-boot:run
```

---

## Scripts

| Command | Purpose |
|---|---|
| `mvn compile` | Type-check |
| `mvn test` | Unit tests |
| `mvn verify` | Full gate (unit + integration + checkstyle + jacoco) |
| `mvn checkstyle:check` | Lint |
| `mvn spring-boot:run` | Local dev server |
| `mvn flyway:info` | Migration state |
| `mvn flyway:migrate` | Apply migrations manually |
| `mvn spring-boot:build-image` | Build OCI image |

---

## Environment variables

Copy `backend/.env.example` to `.env` and fill in values. See
`docs/DEPLOYMENT.md` §1.1 for the production list.

Required for local dev (defaults in `application-local.yml`):
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `REDIS_URL`
- `JWT_SECRET` (≥ 256 bits)

---

## API documentation

Springdoc generates the OpenAPI spec at runtime:

- **Swagger UI:** <http://localhost:8080/swagger-ui.html>
- **JSON spec:** <http://localhost:8080/v3/api-docs>

Authoritative contract also lives in `SKILLSEED_API_AND_DB.md` (and the
relevant section in `phase-1-mvp/design.md` §4).

When you change an endpoint, update:
1. The `@Operation` / `@ApiResponse` annotations.
2. `SKILLSEED_API_AND_DB.md`.
3. The corresponding OpenAPI example (if any).

---

## Database

- Schema migrations: `src/main/resources/db/migration/V*.sql`
- ERD visual: `docs/ERD.md`
- State machines (booking / wallet / session): `docs/STATE_MACHINES.md`

Add a new migration:

```bash
touch src/main/resources/db/migration/V$(($(ls src/main/resources/db/migration | tail -1 | sed 's/^V\([0-9]*\)__.*/\1/') + 1))__add_my_field.sql
```

---

## Background jobs

`com.skillseed.*.job.*` runs on cron schedules declared in source.
List at `docs/MONITORING.md` §5.3.

---

## Testing strategy

- Unit tests live alongside services in `src/test/java`.
- Integration tests with `@SpringBootTest` use the CI Postgres +
  Redis services (see `.github/workflows/ci.yml`).
- E2E smoke tests: `src/test/java/com/skillseed/e2e/`.
- Coverage target: **60%** lines for Phase 1 DoD.

---

## Conventions

- Java code style: Google Java Style (`checkstyle.xml` enforces).
- DTOs are Java **records**.
- Entities are **plain classes** — no Lombok (avoids JPA lazy bugs).
- One public class per file. Interface + Impl when mocking is needed.
- Package-by-feature, not package-by-layer.
- Cross-module logic goes in `shared.*`.

See `AGENTS.md` §5 for the full set.

---

## Deploying

See `docs/DEPLOYMENT.md`. Quick summary:

```bash
railway up --service backend --detach
```

CI/CD auto-deploys on merge to `main`. PR previews come from Vercel.

---

## Troubleshooting

| Symptom | First thing to try |
|---|---|
| `FATAL: database "skillseed" does not exist` | `docker compose up -d postgres` |
| `Redis connection refused` | `docker compose up -d redis` |
| `JWT signature does not match` | restart the app — JWT secret rotated |
| `Flyway: validate failed: missing migration` | check `flyway:info` and align local + remote |
| Health endpoint DOWN | `curl -fsS localhost:8080/actuator/health | jq` |