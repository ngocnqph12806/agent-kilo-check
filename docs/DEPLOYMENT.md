# SkillSeed — Production Deployment Guide

> **One-stop reference for taking SkillSeed from `main` to live traffic.**
> Covers infra provisioning (T-M210), secrets (T-M211), Railway deploy
> (T-M212), Vercel deploy (T-M213), monitoring (T-M214) and CI/CD (T-M215).
>
> **Last updated:** 2026-09-08 (Sprint 4)
> **Owner:** Tech Lead

---

## 1. Environment & secrets (T-M211)

All runtime configuration is read from environment variables — nothing
is committed. Use `backend/.env.example` and `frontend/.env.example` as
your shopping list.

### 1.1. Backend (Railway)

| Variable | Required | Purpose |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | yes | `prod` for production |
| `DATABASE_URL` | yes | JDBC URL (Supabase Postgres) |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | yes | DB creds |
| `REDIS_URL` | yes | `rediss://...` (Upstash) |
| `JWT_SECRET` | yes | ≥ 48 bytes random. Rotate every 90 days. |
| `JWT_ACCESS_TOKEN_TTL_MINUTES` | no (default 30) | Access-token lifetime |
| `JWT_REFRESH_TOKEN_TTL_DAYS` | no (default 30) | Refresh-token lifetime |
| `R2_*` | yes (avatars) | Cloudflare R2 / S3-compatible |
| `DAILY_API_KEY` / `DAILY_DOMAIN` | yes | Daily.co video |
| `RESEND_API_KEY` / `RESEND_FROM` | yes | Transactional email |
| `GOOGLE_OAUTH_CLIENT_ID` / `GOOGLE_OAUTH_CLIENT_SECRET` | optional | Google sign-in |
| `APPLE_OAUTH_CLIENT_ID` / `APPLE_OAUTH_CLIENT_SECRET` | optional | Apple sign-in |
| `APP_CORS_ALLOWED_ORIGINS` | yes | Comma-separated, e.g. `https://skillseed.app` |
| `BETTERSTACK_SOURCE_TOKEN` | optional | Log drain |
| `SENTRY_DSN` | optional | Error tracking |
| `PORT` | no (8080) | Container port (Railway injects) |

**Secrets storage:** use Railway's built-in environment variable UI (it
encrypts at rest). Never paste secrets into GitHub Issues or PR comments.

**GitHub Secrets** (used by `.github/workflows/cd.yml`):

| Secret | Used by |
|---|---|
| `RAILWAY_TOKEN` | Backend deploy job |
| `VERCEL_TOKEN` | Frontend deploy job |

### 1.2. Frontend (Vercel)

| Variable | Required | Purpose |
|---|---|---|
| `NEXT_PUBLIC_APP_URL` | yes | `https://skillseed.app` |
| `NEXT_PUBLIC_API_URL` | yes | `https://api.skillseed.app` |
| `NEXT_PUBLIC_GOOGLE_CLIENT_ID` | optional | Google sign-in |
| `NEXT_PUBLIC_DAILY_DOMAIN` | yes | `skillseed.daily.co` |
| `NEXT_PUBLIC_POSTHOG_HOST` / `NEXT_PUBLIC_POSTHOG_KEY` | optional | Analytics (gated by cookie consent) |
| `NEXT_PUBLIC_SENTRY_DSN` | optional | Error tracking |
| `NEXT_PUBLIC_INTERCOM_APP_ID` | optional | Feedback widget (T-M222) |

Vercel separates Production / Preview / Development — set the prod
values on **Production**, sandbox-friendly values on **Preview**.

### 1.3. Rotation policy

- `JWT_SECRET` — rotate every 90 days. Force-revoke all refresh tokens
  by bumping `JWT_SECRET` (users will be silently logged out and have
  to refresh).
- `DAILY_API_KEY` / `RESEND_API_KEY` — rotate on staff changes.
- All other service keys — rotate annually or on suspected compromise.

---

## 2. Backend → Railway (T-M212)

### 2.1. One-time setup
1. Sign in at <https://railway.app> with GitHub.
2. Create a new project → "Deploy from GitHub repo" → select `ngocnqph12806/agent-kilo-check`.
3. Add a **Postgres** plugin (or use the external Supabase project — see §2.2).
4. Add a **Redis** plugin (or use external Upstash — see §2.3).
5. Set the variables from §1.1 in **Variables** tab.
6. Connect the custom domain `api.skillseed.app` in **Settings → Domains**.

### 2.2. Database (Supabase)
1. Create a Supabase project in the same region as Railway.
2. From **Settings → Database**, copy the **Connection string (JDBC)** —
   transform it into `jdbc:postgresql://...` form.
3. Set `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`.
4. Flyway will auto-migrate on first boot.

### 2.3. Redis (Upstash)
1. Create an Upstash Redis database (TLS enabled).
2. Copy the **Redis URL** (starts with `rediss://`).
3. Set `REDIS_URL`.

### 2.4. Health check
- Path: `/actuator/health` (proxied through Spring Actuator).
- Defined in `backend/railway.toml` (`healthcheckPath`).
- A `200` response means the DB + Redis + JWT verifier are all healthy.

### 2.5. Deploy commands
- Auto-deploy: configured via `.github/workflows/cd.yml` (T-M215).
- Manual redeploy: `railway up --service backend --detach`.

---

## 3. Frontend → Vercel (T-M213)

### 3.1. One-time setup
1. Sign in at <https://vercel.com> with GitHub.
2. "Add New… → Project" → import `ngocnqph12806/agent-kilo-check`,
   set **Root Directory** = `frontend`.
3. Framework preset: **Next.js** (auto-detected).
4. Override build/install commands if needed (defaults match `frontend/package.json`).
5. Set the variables from §1.2 in **Settings → Environment Variables**.
6. Connect the custom domain `skillseed.app`.

### 3.2. Custom headers
Security headers are applied via `frontend/vercel.json`:
`X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`,
`Permissions-Policy`.

### 3.3. Preview environments
Every PR gets its own preview URL (T-M215). The backend rewrite targets
`https://api.skillseed.app` (prod) by default; for preview-safe testing,
override `NEXT_PUBLIC_API_URL` to a staging API.

---

## 4. Custom domains (T-M212, T-M213)

| Domain | Service | DNS record |
|---|---|---|
| `skillseed.app` | Vercel (frontend) | `CNAME` → Vercel |
| `www.skillseed.app` | Vercel (frontend) | `CNAME` → Vercel |
| `api.skillseed.app` | Railway (backend) | `CNAME` → Railway |

Set the apex `skillseed.app` redirect from `www` (or vice-versa) in
Vercel.

---

## 5. Observability (T-M214)

### 5.1. Logs
- Backend stdout is shipped to **Better Stack** via the
  `BETTERSTACK_SOURCE_TOKEN` log drain.
- Vercel captures Next.js build + runtime logs natively.

### 5.2. Uptime monitoring
- **UptimeRobot** monitors:
  - `https://api.skillseed.app/actuator/health` (every 5 min)
  - `https://skillseed.app/` (every 5 min)
- Alerts route to `ops@skillseed.app` and the `#ops-alerts` Slack channel.

### 5.3. Error tracking (optional)
- **Sentry** DSN injected via `SENTRY_DSN` (backend) and
  `NEXT_PUBLIC_SENTRY_DSN` (frontend).

### 5.4. Daily monitoring checklist (T-M223)
- [ ] No 5xx in last 24 h
- [ ] p95 latency < 300 ms
- [ ] Wallet balance drift = 0
- [ ] Reminder / auto-rate jobs ran successfully
- [ ] Beta-user signups vs target

---

## 6. CI/CD pipeline (T-M215)

| Trigger | Workflow | Result |
|---|---|---|
| PR / push to `main`/`develop` | `.github/workflows/ci.yml` | Backend test + checkstyle + frontend lint/typecheck/build + docs lint |
| Push to `main` | `.github/workflows/cd.yml` | Backend deploy to Railway + frontend deploy to Vercel + smoke test |
| Manual dispatch | same `cd.yml` | Re-deploy to `staging` or `production` |

PR previews: Vercel automatically builds every PR. The bot comments the
preview URL on the PR.

Promote to production: merge to `main`. Both services auto-deploy.

---

## 7. First-time deploy checklist (T-M210, T-M220)

- [ ] Railway project created
- [ ] Supabase DB provisioned + migrations applied
- [ ] Upstash Redis provisioned
- [ ] Cloudflare R2 bucket created (public read for avatars)
- [ ] Daily.co domain `skillseed.daily.co` provisioned
- [ ] Resend domain verified (`skillseed.app`)
- [ ] Vercel project created + `skillseed.app` domain attached
- [ ] UptimeRobot monitor + alerts
- [ ] Better Stack log source created
- [ ] Smoke test: register → onboard → discover → book → join call → rate

---

## 8. Rollback playbook

| Symptom | Action |
|---|---|
| 5xx spike after deploy | Railway → Deployments → click previous → "Rollback" |
| Bad FE bundle | Vercel → Deployments → "Promote to Production" on previous |
| DB migration broke prod | Flyway repair + restore from nightly Supabase backup |
| Wallet ledger drift | Pause writes (set feature flag), reconcile, replay |

---

## 9. Cost guardrails

See `SKILLSEED_CLOUD_COST.md`. Phase 1 monthly run-rate target: < USD 250.
Alert thresholds configured in UptimeRobot when bills exceed $300.