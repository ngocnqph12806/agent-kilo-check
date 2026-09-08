# SkillSeed — Production Monitoring & Alerts

> Reference for the monitoring stack used by SkillSeed in production
> (T-M214). Pairs with `docs/DEPLOYMENT.md` §5.

**Last updated:** 2026-09-08 (Sprint 4)
**Owner:** Tech Lead

---

## 1. What we monitor

| Signal | Tool | Threshold | Alert route |
|---|---|---|---|
| Backend `/actuator/health` | UptimeRobot | every 5 min, fail = 2 in a row | email + Slack `#ops-alerts` |
| Frontend `https://skillseed.app/` | UptimeRobot | every 5 min | email + Slack |
| API p95 latency | Better Stack | > 400 ms for 10 min | Slack `#ops-alerts` |
| Backend errors (5xx) | Better Stack | > 1% over 5 min | Slack + email |
| Crash-free rate | Sentry | < 99% over 1 h | Slack |
| DB connection pool | Better Stack dashboard | > 80% utilised | Slack |
| Wallet drift | Daily SQL job | any non-zero | Slack + email on-call |
| Daily / hourly backups | Supabase dashboard | failed backup | email on-call |
| Cost anomaly | Railway + Vercel billing | > $300/mo | email CFO |

---

## 2. UptimeRobot configuration (T-M214)

| Monitor | Type | URL | Interval | Timeout | Alert contacts |
|---|---|---|---|---|---|
| SkillSeed API | HTTPS | `https://api.skillseed.app/actuator/health` | 5 min | 30 s | `ops@skillseed.app`, `#ops-alerts` Slack |
| SkillSeed Web | HTTPS | `https://skillseed.app/` | 5 min | 30 s | same |
| SkillSeed Frontend Sitemap | HTTPS | `https://skillseed.app/sitemap.xml` | 60 min | 30 s | same |

UptimeRobot status page (public): `https://status.skillseed.app` (free
tier; or self-host with `upptime`).

---

## 3. Better Stack (logs)

1. Create a source: **Node.js HTTP** (or syslog), point Railway's
   container to it via the `BETTERSTACK_SOURCE_TOKEN` env var.
2. Set retention to **30 days** (matches GDPR backup window).
3. Create Live Tail view `prod-backend` → invite the team.
4. Create alerts on:
   - `level=ERROR` count > 20 / 5 min
   - `path=/actuator/health` failed for any reason
   - Wallet-related log entries without a successful next entry

---

## 4. Sentry (errors)

- Frontend: `NEXT_PUBLIC_SENTRY_DSN` injected at build time.
- Backend: `SENTRY_DSN` env, sample rate 20% in prod.
- Source maps uploaded via `@sentry/nextjs` (FE) and
  `io.sentry:sentry-spring-boot-starter` (BE).
- Release tracking: tag deploys with `release=$(git rev-parse --short HEAD)`.

---

## 5. Daily monitoring runbook (T-M223)

The on-call founder runs through this checklist **once per day** for
the first two weeks of beta, then weekly afterwards.

### 5.1. Quick health (5 min)
- [ ] `curl -fsS https://api.skillseed.app/actuator/health | jq` → `status: UP`
- [ ] UptimeRobot status page → all green
- [ ] Better Stack Live Tail → no `ERROR` entries in last 24 h
- [ ] Sentry inbox → no unresolved critical

### 5.2. Latency (5 min)
- [ ] Better Stack query: `p95(latency) where service=backend` < 300 ms
- [ ] Vercel Analytics: p95 page-load < 1.5 s (LCP < 2.5 s)

### 5.3. Background jobs (5 min)
- [ ] `BookingAndWalletJobs.expirePendingBookings` ran in last 5 min
- [ ] `BookingAndWalletJobs.markNoShows` ran in last minute
- [ ] `BookingAndWalletJobs.sendReminders` ran in last 5 min
- [ ] `BookingAndWalletJobs.processSeedExpiry` ran today
- [ ] `RatingAutoRateJob` ran in last 10 min
- [ ] `UserGdprJobs.purgeExpiredDeletedUsers` ran today

Check the log markers: `[jobs] expired N pending bookings` etc. Each
job logs once per run.

### 5.4. Wallet integrity (5 min)
- [ ] SQL: `SELECT SUM(balance_cached) FROM seed_wallets` ≈
  `SELECT SUM(amount) FROM seed_transaction WHERE status='COMPLETED'`
- [ ] No negative balances
- [ ] No expired-but-not-zero transactions

A 5 min job in `apps/SkillseedApplication.java` writes the totals to
Better Stack metrics — alert when the two diverge.

### 5.5. Growth (5 min)
- [ ] DAU / WAU / MAU vs last week
- [ ] Sessions/day vs target (≥ 10/day by week 2)
- [ ] NPS / thumbs-down in feedback channel

### 5.6. Cost (5 min)
- [ ] Railway + Vercel + Supabase + Daily.co + Resend < $250 / mo
- [ ] No idle Redis / DB connections

---

## 6. On-call rotation

Phase 1 is single-founder — the on-call is whoever holds the pager
(OpsGenie free tier). Sprint 4 sets up the alerting plumbing; Sprint 5
will introduce a proper rotation if headcount grows.

---

## 7. Incident playbook (5-minute MTTR target)

| Symptom | First action |
|---|---|
| 5xx spike | Check Better Stack → recent deploy → rollback if culprit |
| `/health` DOWN | Check Railway status → DB connection → Redis pings |
| Latency spike | Check DB slow query log → Redis memory → CDN |
| Wallet drift | Pause writes via `WALLET_WRITES_DISABLED=true` env flag |
| Frontend down | Check Vercel status → rollback to previous deployment |
| Spam / abuse | Enable Cloudflare Bot Fight Mode → ban IP range |

---

## 8. Status page

Public status page lives at `https://status.skillseed.app`. Backed by
UptimeRobot's free status-page feature (T-M214).

Components tracked:
- API (HTTPS health check)
- Web (HTTPS root)
- Sign-in (HTTPS /login)
- Bookings (HTTPS /bookings — authenticated)
- Wallet (HTTPS /wallet — authenticated)