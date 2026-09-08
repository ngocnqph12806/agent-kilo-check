# SkillSeed API Reference

> **Authoritative source:** Swagger UI at <https://api.skillseed.app/swagger-ui.html>
> (and locally at <http://localhost:8080/swagger-ui.html>).
> **Machine-readable spec:** `/v3/api-docs` (JSON) and
> `/v3/api-docs.yaml` (YAML).
>
> **Static contract:** `SKILLSEED_API_AND_DB.md` mirrors this and is the
> source-of-truth referenced from `.kiro/specs/phase-1-mvp/design.md`.
>
> **Maintainer:** SkillSeed Product Team · **Last updated:** 2026-09-08

---

## How to read this file

| Section | What it covers |
|---|---|
| §1 Quickstart | curl / fetch examples for the most common flows |
| §2 Auth | Token lifecycle |
| §3 Modules | Endpoint map by module |
| §4 Errors | Standard error envelope |
| §5 Versioning | How we cut breaking changes |
| §6 Changelog | Per-sprint endpoint diffs |

---

## 1. Quickstart

```bash
# 1. Register
curl -X POST https://api.skillseed.app/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"hunter22hunter22","fullName":"Alice"}'

# 2. Verify email — link is in the welcome email; hits:
curl -X POST https://api.skillseed.app/api/v1/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{"token":"<email-token>"}'

# 3. Log in → access token
curl -X POST https://api.skillseed.app/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"hunter22hunter22"}'

# 4. Authenticated request
curl https://api.skillseed.app/api/v1/users/me \
  -H "Authorization: Bearer <access-token>"
```

---

## 2. Auth

- All authenticated endpoints expect `Authorization: Bearer <jwt>`.
- Access tokens live **30 minutes**; refresh tokens **30 days**.
- Refresh: `POST /api/v1/auth/refresh` with the refresh token.
- Logout: `POST /api/v1/auth/logout` revokes the refresh token.
- Soft-deleted users cannot refresh — they get 401 and must re-register.

OAuth (Google / Apple):
- `POST /api/v1/auth/oauth/google` `{ idToken }`
- `POST /api/v1/auth/oauth/apple` `{ idToken }`

---

## 3. Modules

| Module | Base path | Highlights |
|---|---|---|
| auth | `/api/v1/auth/*` | register, login, refresh, verify-email, oauth/* |
| users | `/api/v1/users/*` | me, {id}, availability, skills offered/wanted, **me/export**, **DELETE me** |
| skills | `/api/v1/skills/*` | catalogue search |
| discover | `/api/v1/discover/*` | search + match |
| bookings | `/api/v1/bookings/*` | create, get, list, cancel, complete |
| wallet | `/api/v1/wallet/*` | balance, ledger, top-up webhook |
| session | `/api/v1/session/*` | Daily room + chat history |
| rating | `/api/v1/ratings/*` | create, list, auto-rate |
| notifications | `/api/v1/notifications/*` | list, mark-read |

Sprint 4 additions (T-M200, T-M201):
- `DELETE /api/v1/users/me` — soft-delete (anonymise PII + 30-day purge).
- `GET /api/v1/users/me/export` — full JSON export (profile, skills,
  wallet, bookings, ratings, transactions).

---

## 4. Errors

Every non-2xx response carries an `application/problem+json` envelope:

```json
{
  "code": "WALLET_INSUFFICIENT",
  "message": "Not enough Seeds to confirm this booking",
  "status": 409,
  "errors": null,
  "traceId": "9b6f7c40..."
}
```

Common codes: `VALIDATION_FAILED` (400), `UNAUTHORIZED` (401),
`FORBIDDEN` (403), `NOT_FOUND` (404), `CONFLICT` (409),
`RATE_LIMITED` (429), `INTERNAL_ERROR` (500).

---

## 5. Versioning

- Today: `/api/v1/*`. Breaking changes → bump to `/api/v2/*`.
- Non-breaking additions (new optional fields, new endpoints) ship in
  v1.
- We keep v1 alive for **6 months** after v2 ships.

---

## 6. Changelog

### Sprint 4 (2026-09)
- **NEW** `DELETE /api/v1/users/me` (T-M200) — GDPR right-to-delete.
- **NEW** `GET /api/v1/users/me/export` (T-M201) — GDPR data export.
- **CHANGED** `GET /api/v1/users/me/availability` — now accepts
  `?days=1..60`.

### Sprint 3 (2026-09)
- **NEW** `/api/v1/session/{bookingId}/chat` (websocket) (T-M155).
- **NEW** `/api/v1/ratings/auto-rate` (internal cron) (T-M173).
- **NEW** `POST /api/v1/session/{bookingId}/recording` (T-M156).

### Sprint 2 (2026-08)
- **NEW** `/api/v1/bookings/*` (T-M100..T-M110).
- **NEW** `/api/v1/wallet/*` (T-M120..T-M126).

### Sprint 1 (2026-07)
- **NEW** `/api/v1/skills/*`, `/api/v1/discover/*`, `/api/v1/users/{id}*`
  (T-M60..T-M82).

### Sprint 0 (2026-06)
- **NEW** `/api/v1/auth/*`, `/api/v1/users/me` (T-M10..T-M32).

---

## 7. Tools

- **Swagger UI:** <https://api.skillseed.app/swagger-ui.html>
- **JSON spec:** <https://api.skillseed.app/v3/api-docs>
- **YAML spec:** <https://api.skillseed.app/v3/api-docs.yaml>
- **Postman collection:** (auto-generated via
  `openapi-to-postman` — uploaded to internal wiki).

---

## 8. Reporting issues

API bug or unexpected status code? Open a ticket at
`api-feedback@skillseed.app` with the `traceId` from the error
envelope.