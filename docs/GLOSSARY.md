# SkillSeed — Glossary

> **Single source of truth for domain terminology.**
> Referenced from `AGENTS.md`, `SKILLSEED.md`, and all phase specs.
> When you introduce a new term, add it here and cross-reference from the spec.

**Last updated:** 2026-09-08
**Owner:** SkillSeed Product Team

---

## A

### Availability
A teacher's weekly recurring free-time slots, stored as `user_availability` rows keyed by `(user_id, day_of_week, start_time, end_time)` in the user's local timezone. Used by the booking module to validate that a learner's requested `scheduled_at` falls inside a slot.

### Auth Provider
The external identity provider attached to a user account: `EMAIL`, `GOOGLE`, or `APPLE`. Persisted on `users.auth_provider`. An `EMAIL` user has a `password_hash`; OAuth users do not.

## B

### Booking
A scheduled learning session between a teacher (skill provider) and a learner (skill consumer). One row in `bookings` per request. Drives the Seed escrow + state-machine transitions (see `docs/STATE_MACHINES.md` §1).

### BookingStatus
The 9-state lifecycle of a booking:
`pending → confirmed → in_progress → completed → rated`, with side branches
`declined`, `cancelled`, `expired`, `no_show`. See STATE_MACHINES §1.

## C

### Cancel Refund Policy
Three-tier policy applied by `BookingService.cancel` based on time remaining before `scheduled_at` and current state:
- **≥ 24 h before scheduled**: learner refund 100%, teacher 0%.
- **< 24 h before scheduled**: learner refund 50%, teacher compensation 50%.
- **In-progress / past scheduledAt**: learner 0%, teacher 100%.

## D

### Daily.co
External video-calling vendor integrated via `session.daily.*` config. Room naming convention: `ss-<bookingIdCompact>` so webhooks can be routed back to the originating booking without a lookup table.

### Discovery
The matching engine that surfaces teachers to learners. Public read-only endpoint `GET /api/v1/discover?skill=&country=&language=&minRating=` returns a ranked list of `DiscoverMatchResponse`.

## E

### EARN
`SeedTransaction.type = 'earn'`. A positive row credited to the teacher's wallet on booking completion. Expires 6 months after creation (configurable).

### Escrow
The seed amount temporarily locked by `escrowDebit` (a `SPEND` row with `status='pending'`). Settled by `captureEscrow` on `completed`, or released by `refundLearnerFull` / `learnerNoShowForfeit` on cancel / no-show.

### EXPIRE
`SeedTransaction.type = 'expire'`. A negative counter-row emitted by `processExpiry` when an `EARN` row's `expires_at` passes. Closes the EARN out of the active balance while preserving the audit trail.

## F

### Feature Branch Naming
Format `{type}/{phase}-{short-desc}` per `AGENTS.md` §5.6. Examples: `feat/M01-booking-flow`, `fix/M02-wallet-double-spend`.

## G

### GRANT
`SeedTransaction.type = 'grant'`. A positive row from an external source (starter pack, promo, NGO gifting). May or may not have an `expires_at`.

## H

### Hold → Capture → Release
The three wallet operations on an escrow row:
- **Hold** = `escrowDebit(booking)` — SPEND row PENDING, balance ↓.
- **Capture** = `captureEscrow(booking)` — SPEND row COMPLETED, teacher +amount EARN.
- **Release** = `refundLearnerFull(booking)` — SPEND row CANCELLED, learner +amount REFUND.

## I

### Idempotency Key
Header `Idempotency-Key` accepted on `POST /bookings` (and any other mutating endpoint where retries are unsafe). Phase 1 accepts the header but does not enforce uniqueness; will be enforced in Phase 2.

## L

### Ledger
The append-only `seed_transactions` table. INVARIANT: rows are NEVER updated or deleted; corrections are emitted as new counter-rows (see `docs/STATE_MACHINES.md` §4 invariant 2).

## M

### Match Reasons
A short list of human-readable strings returned alongside each `DiscoverMatchResponse` to explain why a teacher was surfaced (e.g. `"same timezone"`, `"5-star in Java"`, `"responds in < 24h"`).

## N

### No-show
A confirmed booking where at least one party did not attend. Terminal state in the state machine. Triggered by the `markNoShows` cron (after `scheduled_at + duration + 15 min`) or by `POST /bookings/{id}/no-show` (one party reporting the other absent).

## P

### Pair (Auth Token Pair)
The `(accessToken, refreshToken)` returned by `/auth/login`, `/auth/refresh`, `/auth/oauth/google`, `/auth/oauth/apple`. Access TTL 15 min; refresh TTL 30 days. Refresh token is stored in Redis under the `refresh-token` purpose and mirrored into an HttpOnly cookie.

### Pending Hold
The sum of all SPEND rows with `status='pending'` for a user. Surfaced in `WalletSummaryResponse.pendingHold` so the FE can show "available: 30 seeds (10 in escrow)".

## R

### REFUND
`SeedTransaction.type = 'refund'`. A positive row credited to the learner on cancellation. Issued by `refundEscrow(booking, percent)`.

### Reconciliation
The nightly cron job (`reconcileWalletCache` in `BookingAndWalletJobs`) that walks every wallet, recomputes the active balance from the ledger, and auto-repairs `balance_cached` discrepancies. See STATE_MACHINES §4 invariant 4.

### Room Name
Daily.co room identifier of the form `ss-<bookingIdCompact>` (booking UUID with hyphens stripped). Used by the Daily webhook to map `meeting.ended` events back to the originating booking.

## S

### Seed / Seeds
The internal accounting unit (1 Seed ≠ 1 unit of fiat). All wallet operations deal in integer Seed counts. Display in the FE always carries the suffix `"seeds"` (per VISUAL_FIDELITY §4.2).

### Session
Conceptually 1:1 with a Booking in Phase 1 (no separate `sessions` table). Lifecycle tracked via `bookings.status`:
`confirmed → in_progress → completed`.

### SPEND
`SeedTransaction.type = 'spend'`. A negative row debited from the learner when they create a booking (escrow hold).

### Status Badge
The colored pill rendered next to a booking on FE. Five semantic colors are exposed as Tailwind tokens (`bg-brand-credit-bg text-brand-credit`, `bg-brand-debit-bg text-brand-debit`, `bg-brand-pending-bg text-brand-pending`, etc.) — see `tailwind.config.ts`.

## T

### Tier
A wallet achievement badge derived from `total_earned`: `bronze` / `silver` / `gold` / `platinum`. Surfaced in `WalletSummaryResponse.tier`.

## U

### UserSummaryResponse
The compact user object returned by `/auth/login` and `/auth/refresh`. Fields per `UserSummaryResponse` record: `id`, `email`, `fullName`, `avatarUrl`, `verified`, `verificationLevel`, `onboardingCompleted`, `authProvider`, `countryCode`, `timezone`.

## V

### Visual Fidelity Rule
Mandatory workflow defined in `docs/VISUAL_FIDELITY.md`: every UI / DTO / status-enum change must reference the corresponding SVG mockup in `screens-svg/` before coding. Enforced via AGENTS §5.3 + DoD §7.1.
