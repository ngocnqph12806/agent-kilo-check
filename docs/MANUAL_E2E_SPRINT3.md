# Manual E2E Test Plan — Sprint 3 Video + Rating (T-M180, T-M181)

> **Scope:** verify the full Sprint 3 happy path against a deployed
> environment (staging or `docker compose up`):
>
> 2 users → register → onboarding → discover → book → join video →
> complete → rate → wallet update → auto-rate (T-M173).
>
> **Why a manual plan?** Video call quality, real-time chat, and
> load behaviour cannot be fully covered by automated integration
> tests in a CI sandbox. This document is the operator-side QA
> checklist for `T-M180` and `T-M181`.

---

## 0. Prerequisites

| Item | Where to get it | Notes |
|---|---|---|
| `curl`, `jq` | local | for ad-hoc REST calls |
| Two staging accounts (learner + teacher) | see `docs/MANUAL_E2E_AUTH.md` | re-use the auth manual's flow |
| `DAILY_API_KEY` ≥ dev tier | Daily.co dashboard | needed for T-M150 / T-M151 |
| `DAILY_WEBHOOK_SIGNING_KEY` (optional) | Daily.co dashboard | required for signed payloads |
| A staging URL **or** local stack (`docker compose up`) | see `docker-compose.yml` | |
| `k6` or `vegeta` for T-M181 load test | local | see §6 |

---

## 1. Video room creation (T-M151)

| # | Step | Expected |
|---|---|---|
| 1 | `curl -X POST /api/v1/sessions/{bookingId}/room` as the teacher of a CONFIRMED booking | `200` with `{ roomUrl, roomName, token, role: "owner", expiresAt, scheduledAt, durationMinutes }` |
| 2 | Re-run the same call for the same booking | Same `roomName` returned (re-use) |
| 3 | Hit the same call as a non-participant | `403 NOT_PARTICIPANT` |
| 4 | Hit the call for a PENDING booking | `422 BOOKING_NOT_READY` |
| 5 | `roomUrl` opens in a browser while signed-in to Daily as a known user | Lands on a Daily.co room page (or the stub URL in dev profile) |

---

## 2. Daily webhook (T-M156)

| # | Step | Expected |
|---|---|---|
| 1 | Use the Daily.co dashboard (or `curl`) to send a `meeting.ended` webhook with payload `{ type: "meeting.ended", payload: { room: { name: "ss-<bookingCompact>" } } }` | `204 No Content` |
| 2 | `GET /api/v1/bookings/{bookingId}` afterwards | `status = "completed"` |
| 3 | Re-send the same webhook | Still `204`, booking remains `completed` (idempotent) |
| 4 | Send `meeting.started` instead | `204`, no state change |
| 5 | Send payload without `room.name` | `400 Bad Request` |

---

## 3. Frontend join surface (T-M153, T-M154, T-M160)

| # | Step | Expected |
|---|---|---|
| 1 | Visit `/bookings/{id}` as the teacher of a CONFIRMED booking ≥ 10 min before `scheduledAt` | "Join session" button is **disabled** with explanatory tooltip |
| 2 | Refresh at T-10 min | Button becomes enabled |
| 3 | Click "Join session" | VideoCall full-screen overlay mounts, controls visible, mic + camera off initially |
| 4 | Mute / unmute / camera off / on | UI state updates, icon flips |
| 5 | Click "Share screen" (browser prompt) | Browser picker appears; on accept, the participant tile shows the screen share |
| 6 | Open second tab signed in as the learner | Both parties see each other's tiles |
| 7 | Click "Whiteboard" → draw a few strokes → "Hide whiteboard" | Panel slides in / out without unmounting the call |

---

## 4. In-session chat (T-M155)

| # | Step | Expected |
|---|---|---|
| 1 | With both parties in the call, send a chat message from one side | The other side receives it within ~1 s |
| 2 | Send a blank message | No broadcast |
| 3 | Have a 3rd user join `/topic/sessions/{bookingId}` over `wscat` | They do **not** see messages (not authenticated) |
| 4 | Disconnect and reconnect with an expired token | Stomp server rejects with `ERROR` frame |

---

## 5. Rating flow (T-M170–T-M176)

| # | Step | Expected |
|---|---|---|
| 1 | As the learner, complete a booking (or have a webhook do it) | `GET /bookings/{id}` → `completed` |
| 2 | Bell icon | A new `rating_prompt` notification appears for both participants |
| 3 | `/bookings/{id}` shows "Rate session" button | Modal opens |
| 4 | Pick 5 stars + comment + submit | Modal closes, list re-renders |
| 5 | `POST /api/v1/ratings` again with the same booking | `409 RATING_EXISTS` |
| 6 | Visit `/users/{teacherId}` as another user | New review visible in the Reviews tab |
| 7 | Open the ratee profile in a fresh tab | `rating_avg` updated, `sessions_completed` incremented |
| 8 | Repeat as the teacher rating the learner | Same rules, no 409 because different (booking, rater) |

---

## 6. Auto-rate job (T-M173)

| # | Step | Expected |
|---|---|---|
| 1 | Backdate one COMPLETED booking's `updated_at` to 8 days ago, leave it unrated | `rating.auto-rate.cron` overrides for the test: e.g. set `RATING_AUTO_RATE_CRON="*/30 * * * * *"` |
| 2 | Wait ≤ 1 minute | A row appears in `ratings` with `auto_rated = true`, `overall_score = 5`, `rater_id = learner_id` |
| 3 | Re-trigger the job (or wait another tick) | No second row created (idempotent) |
| 4 | Set `rating.auto-rate.enabled=false` and rerun | No row inserted |

---

## 7. Load test (T-M181)

### Goal
Show that the backend can open **50 concurrent video rooms in parallel**
without exceeding p95 latency SLOs.

### Tooling
`k6` is preferred (single-binary, easy to script). Example script in
`infra/loadtest/open-rooms.js` (added in this sprint).

### Targets

| Metric | Target | Hard fail |
|---|---|---|
| `POST /api/v1/sessions/{id}/room` p95 latency | < 500 ms | > 1 s |
| 5xx rate | < 1 % | > 2 % |
| Concurrent VUs sustained for 5 min | 50 | any crash |
| DB CPU (RDS proxy / Supabase free) | < 70 % | > 90 % |

### Notes
- **Daily handles the actual video bandwidth** — we only validate that
  the backend can mint 50 rooms/min without back-pressuring.
- The token request is the slow part; pre-warm a single room and have
  VUs `POST /sessions/{id}/room` for distinct bookings.

### Run
```bash
k6 run --vus 50 --duration 5m infra/loadtest/open-rooms.js
```

A successful run is a prerequisite for the Sprint 3 demo sign-off.
