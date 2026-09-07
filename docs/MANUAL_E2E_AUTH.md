# Manual E2E Test Plan — Auth Flow (T-M62)

> **Scope:** verify the full authentication surface (`/api/v1/auth/**`) end-to-end
> against a deployed environment (staging or `docker compose up`).
>
> **Why a manual plan?** End-to-end flows that involve third-party providers (Resend,
> Google, Apple) and real network behaviour (rate-limit windows, cookie TTLs,
> email deliverability) cannot be fully covered by automated integration tests.
> This document is the operator-side QA checklist for `T-M62`.

---

## 0. Prerequisites

| Item | Where to get it | Notes |
|---|---|---|
| `curl`, `jq` | local | for ad-hoc REST calls |
| A staging URL (`https://api.staging.skillseed.app`) **or** local stack (`http://localhost:8080`) | see `docker-compose.yml` | |
| A throwaway email inbox (Mailtrap, Mailsac, or your own `+tag` Gmail alias) | external | to capture the verify-email + reset-password emails |
| `JWT_SECRET` ≥ 32 bytes in the env | ops | `openssl rand -base64 32` |
| `RESEND_API_KEY` set **or** empty (logging fallback) | ops | empty is fine for staging if you read logs |
| `APP_PUBLIC_BASE_URL` set to the FE URL (e.g. `http://localhost:3000`) | env | used to build verification + reset links |
| `GOOGLE_OAUTH_CLIENT_ID` set if you want to exercise Google sign-in | env | Apple needs `APPLE_OAUTH_CLIENT_ID` + `APPLE_OAUTH_ISSUER` |

Confirm the stack is live:

```bash
curl -fsS https://api.staging.skillseed.app/actuator/health | jq .
# {"status":"UP"}
```

Confirm Swagger UI loads:

```
https://api.staging.skillseed.app/swagger-ui.html
```

---

## 1. Registration + Email Verification

### 1.1 Happy path

```bash
EMAIL="alice+$(date +%s)@example.com"

curl -fsS -X POST "$API/auth/register" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"Password1\",\"fullName\":\"Alice QA\"}"
# 201 { "message": "Verification email sent" }
```

- [ ] Response is `201 Created`.
- [ ] DB row created (`SELECT email, verified FROM users WHERE email = '$EMAIL'` → `verified=false`).
- [ ] An email arrives in your inbox (or check logs if `RESEND_API_KEY` is empty) with subject
      `Verify your SkillSeed email` and a link of the form `$APP_PUBLIC_BASE_URL/verify-email/{token}`.

Open the link in a browser (or POST the token directly):

```bash
TOKEN="<token from email>"

curl -fsS -X POST "$API/auth/verify-email" \
  -H 'Content-Type: application/json' \
  -d "{\"token\":\"$TOKEN\"}"
# 200 { "message": "Email verified" }
```

- [ ] Response is `200 OK`.
- [ ] DB row now has `verified=true` (`SELECT verified FROM users WHERE email = '$EMAIL'`).
- [ ] Re-using the same token returns `400 INVALID_TOKEN` (single-use).

### 1.2 Negative cases

| Action | Expected status | Expected `code` |
|---|---|---|
| Re-register the same email | `409` | `EMAIL_ALREADY_EXISTS` |
| Register with invalid email (`"not-an-email"`) | `400` | (Bean Validation, no `code`) |
| Register with weak password (`"short"`) | `400` | (Bean Validation, message "password must contain at least one letter and one digit") |
| Register with empty fullName | `400` | (Bean Validation) |

---

## 2. Login + JWT pair

### 2.1 Happy path

```bash
curl -fsS -X POST "$API/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"Password1\"}"
```

- [ ] `200 OK` with `{ accessToken, refreshToken, expiresInSeconds: 900, tokenType: "Bearer", user: { ... } }`.
- [ ] Decode the access token at <https://jwt.io> — claims contain `sub` (user UUID), `email`, `verificationLevel`, `tokenType=access`, `iat`, `exp`.
- [ ] Decode the refresh token — `tokenType=refresh`, no email claim, `exp ≈ iat + 30 days`.

### 2.2 Wrong password

```bash
curl -fsS -X POST "$API/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"WRONG\"}"
```

- [ ] `401` with `code = INVALID_CREDENTIALS`.

### 2.3 Rate limit (5 attempts / 15 minutes / IP)

Send 5 bad attempts in a row (use the same `X-Forwarded-For` to simulate one IP):

```bash
for i in 1 2 3 4 5; do
  curl -fsS -X POST "$API/auth/login" \
    -H 'Content-Type: application/json' \
    -H 'X-Forwarded-For: 203.0.113.7' \
    -d "{\"email\":\"$EMAIL\",\"password\":\"WRONG\"}" || true
done

# 6th attempt
curl -i -X POST "$API/auth/login" \
  -H 'Content-Type: application/json' \
  -H 'X-Forwarded-For: 203.0.113.7' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"Password1\"}"
```

- [ ] First 5 → `401 INVALID_CREDENTIALS`.
- [ ] 6th → `429 RATE_LIMITED` with message containing the retry-after seconds.
- [ ] Trying from a different `X-Forwarded-For` is **not** limited (per-IP isolation).

### 2.4 OAuth-only account

Try to log in with email/password on an account that was created via Google sign-in
(skip if you don't have a Google-verified user yet).

- [ ] `401 OAUTH_ONLY_ACCOUNT` (not `INVALID_CREDENTIALS` — we deliberately disambiguate).

---

## 3. Refresh + rotation

```bash
RESP=$(curl -fsS -X POST "$API/auth/login" -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"Password1\"}")

OLD_REFRESH=$(echo "$RESP" | jq -r '.refreshToken')

# First refresh
NEW=$(curl -fsS -X POST "$API/auth/refresh" \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"$OLD_REFRESH\"}")
NEW_REFRESH=$(echo "$NEW" | jq -r '.refreshToken')
echo "old=$OLD_REFRESH new=$NEW_REFRESH"
# old ≠ new (rotation)

# Replay the OLD refresh
curl -i -X POST "$API/auth/refresh" \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"$OLD_REFRESH\"}"
```

- [ ] First refresh returns a new `accessToken` + new `refreshToken`.
- [ ] Old refresh token returns `401 INVALID_TOKEN` (rotation enforced).
- [ ] Garbage / tampered JWT returns `401 INVALID_TOKEN`.

---

## 4. Forgot / Reset password

### 4.1 Known email

```bash
curl -i -X POST "$API/auth/forgot-password" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\"}"
# 200 { "message": "If the email exists, a reset link has been sent" }
```

- [ ] `200 OK` with the generic message (never leaks account existence).
- [ ] Email arrives with link `$APP_PUBLIC_BASE_URL/reset-password/{token}` (subject: `Reset your SkillSeed password`).

### 4.2 Unknown email

```bash
curl -i -X POST "$API/auth/forgot-password" \
  -H 'Content-Type: application/json' \
  -d '{"email":"nobody-here@example.com"}'
```

- [ ] `200 OK` with the **same** generic message.

### 4.3 Reset flow

```bash
RESET_TOKEN="<token from email>"

curl -fsS -X POST "$API/auth/reset-password" \
  -H 'Content-Type: application/json' \
  -d "{\"token\":\"$RESET_TOKEN\",\"newPassword\":\"NewPass99\"}"
# 200 { "message": "Password updated" }
```

- [ ] `200 OK`.
- [ ] Logging in with the **new** password works.
- [ ] Logging in with the **old** password returns `401 INVALID_CREDENTIALS`.
- [ ] Re-using the same reset token returns `400 INVALID_TOKEN` (single-use).

### 4.4 Reset an OAuth-only account

- [ ] `400 OAUTH_ONLY_ACCOUNT` (cannot set a password on a Google/Apple-only user).

---

## 5. Logout

```bash
curl -fsS -X POST "$API/auth/logout" \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"$NEW_REFRESH\"}"
# 200 { "message": "Logged out" }
```

- [ ] `200 OK`.
- [ ] Subsequent `/auth/refresh` with that token returns `401 INVALID_TOKEN`.
- [ ] Calling `/auth/logout` with a token that was already revoked returns `200` (idempotent).

---

## 6. Google Sign-In

> Only run if `GOOGLE_OAUTH_CLIENT_ID` is set on the backend.

```bash
GOOGLE_ID_TOKEN="<id_token from Google Identity Services frontend flow>"

curl -fsS -X POST "$API/auth/oauth/google" \
  -H 'Content-Type: application/json' \
  -d "{\"idToken\":\"$GOOGLE_ID_TOKEN\"}"
```

- [ ] `200 OK` with JWT pair.
- [ ] User has `authProvider=google`, `verified=true`, `passwordHash=null`.
- [ ] Repeat with the same Google email → links the existing account, doesn't create a duplicate.
- [ ] Calling `/auth/login` (email+password) on that account → `401 OAUTH_ONLY_ACCOUNT`.

### 6.1 Provider not configured

If `GOOGLE_OAUTH_CLIENT_ID` is empty in the BE env:

```bash
curl -i -X POST "$API/auth/oauth/google" \
  -H 'Content-Type: application/json' \
  -d '{"idToken":"anything"}'
```

- [ ] `400 OAUTH_PROVIDER_DISABLED`.

---

## 7. Apple Sign-In

> Only run if `APPLE_OAUTH_CLIENT_ID` + `APPLE_OAUTH_ISSUER` are set on the backend.
> Apple requires generating the `id_token` through Apple's JS SDK on iOS — manual
> testing here requires an iOS test device or a Sign in with Apple JWT mock.

```bash
curl -i -X POST "$API/auth/oauth/apple" \
  -H 'Content-Type: application/json' \
  -d '{"idToken":"<apple id_token>"}'
```

- [ ] `200 OK` with JWT pair, OR `400 INVALID_OAUTH_TOKEN` if the token is bad,
      OR `400 OAUTH_PROVIDER_DISABLED` if not configured.

---

## 8. Protected route guard (sanity check from the FE)

Open `https://app.staging.skillseed.app` (or `http://localhost:3000`).

- [ ] Unauthenticated visit to `/discover` redirects to `/login?next=/discover`.
- [ ] After login, the user is redirected back to `/discover`.
- [ ] Logged-in visit to `/login` or `/register` redirects to `/discover`.
- [ ] `/verify-email/[token]` with a valid token shows "Email verified" then links to `/login?verified=1`.
- [ ] `/verify-email/[token]` with an invalid token shows "Verification failed" with a retry link.
- [ ] `/reset-password?token=...` shows the new-password form; missing token shows a warning.
- [ ] Sign-out from `/discover` clears the session and returns to `/login`.

---

## 9. Sign-off template

```
Date:               ____________
Tester:             ____________
Environment:        (staging | docker compose | other)
Build / commit:     ____________

[ ] 1.1 Register happy path
[ ] 1.2 Register negative cases
[ ] 2.1 Login happy path
[ ] 2.2 Wrong password
[ ] 2.3 Rate limit (5/15min/IP)
[ ] 2.4 OAuth-only account 401
[ ] 3   Refresh rotation + replay rejection
[ ] 4.1 Forgot password (known email)
[ ] 4.2 Forgot password (unknown email)
[ ] 4.3 Reset password happy path
[ ] 4.4 Reset on OAuth-only account 400
[ ] 5   Logout revocation
[ ] 6   Google sign-in
[ ] 6.1 OAuth provider disabled 400
[ ] 7   Apple sign-in (optional)
[ ] 8   Frontend route guards

Notes / defects found:
__________________________________________________________
__________________________________________________________

Approver:           ____________
```

---

## 10. Known gaps / follow-ups

| Gap | Why | Plan |
|---|---|---|
| Apple Sign-In UI not yet shipped on FE | Sign in with Apple JS SDK requires Service ID + redirect URI setup | T-M55 ships the React Query mutation hook; UI lands in T-M130s polish phase |
| Real email deliverability check | Requires a production Resend API key | Re-run §1.1 + §4 with `RESEND_API_KEY` populated on staging |
| Multi-tab refresh race | If two tabs refresh simultaneously, the second tab's refresh may 401 | Document in `docs/GOTCHAS.md` once observed; mitigation = silent re-login |
| Cookie domain / SameSite=None over HTTPS | Current FE stores refresh in a cookie via `document.cookie` with `SameSite=Lax`; production should switch to httpOnly cookie set by BE | Follow-up task after BE exposes `Set-Cookie` on `/auth/login` |
| CORS for staging FE origin | `cors.allowed-origins` must include the FE origin | Ops checklist, not a code change |
