# Manual E2E — Video + Rating Flow (Sprint 3, T-M180)

> **When to run:** before opening a feature branch, after merging Sprint 3
> code, and on every release-candidate build. Two participants + a single
> operator.
>
> **Stack prerequisites:** `docker compose up -d` (Postgres + Redis + backend +
> frontend), Daily.co API key set in `.env` (`DAILY_API_KEY=...`), a Daily
> webhook registered to `https://<api-host>/api/v1/webhooks/daily` for
> `meeting.ended`.

---

## 0. Pre-flight checklist

- [ ] Backend up: `curl http://localhost:8080/actuator/health` → `UP`
- [ ] Frontend up: open <http://localhost:3000> → login screen
- [ ] Mail catcher up: <http://localhost:1080> (MailHog) — verify emails land here
- [ ] Two fresh test accounts:
      - `teacher.e2e@skillseed.app` (teacher)
      - `learner.e2e@skillseed.app` (learner)
      Both go through `/register` → verify-email → onboarding. Onboarding
      grants the 30-seed starter pack.
- [ ] Daily webhook signed (Daily → Settings → Webhooks → "secret" copied
      to `DAILY_WEBHOOK_SIGNING_KEY` in `.env`)

---

## 1. Discover & book

1. Sign in as **learner**, navigate to `/discover`.
2. Confirm **teacher** appears in the grid (rating is 0, sessions 0).
3. Click the teacher card → `/users/{teacherId}`.
4. Pick the first offered skill, choose the next free slot, click
   **Book session**, confirm the seed preview ("X seeds remaining").
5. Verify a `pending` booking appears under `/bookings`.
6. Confirm 30 seed grant has been debited (open `/wallet` → balance dropped
   by the session's seed amount).

## 2. Accept & join the room

7. Sign in as **teacher**, open `/bookings`, switch to **Upcoming** filter
   and find the new request.
8. Click **Accept**. Status flips to `confirmed`. Both parties should
   receive a `BOOKING_REQUEST` (teacher) and `BOOKING_ACCEPTED` (learner)
   in-app notification.
9. Learner opens `/bookings/{id}`. The "Join session" button is disabled
   more than 10 min before the start; within the 10-min window it enables.
10. Click **Join session**. The browser will request mic + camera permission.
    Grant it. A Daily.co iframe should mount with the local video tile
    visible.
11. Repeat for the teacher account in another window.

## 3. Session interactions

12. Test controls: **Mute mic**, **Turn camera off**, **Share screen** (T-M160).
13. Open the **chat** panel (right column). Send a message from the teacher
    window — it should appear in the learner window within ~1 s. STOMP
    connected badge should read "Connected".
14. Open the **whiteboard** (T-M161). Draw a couple of strokes with two
    different brush sizes. Clear and draw again to confirm reset works.

## 4. End the session

15. **Teacher** clicks **Mark complete** (or just leave; Daily's
    `meeting.ended` webhook should also flip the booking to `completed`
    within 30 s of both parties leaving).
16. Backend log shows `Marked booking ... complete via Daily webhook`.
17. Both sides receive a `SESSION_COMPLETED` notification followed by
    a `RATING_PROMPT` notification (T-M172).

## 5. Rate

18. **Learner** opens `/bookings/{id}`. The status timeline shows
    `completed` → new **Rate session** button.
19. Click → modal (T-M175) opens. Pick 5 stars, write "Loved the lesson!",
    submit.
20. **Teacher** receives a `RATING_PROMPT` (already had it), opens the
    booking, picks helpfulness 5 + respectfulness 5, submits.
21. The booking status transitions to `RATED`.
22. Sign in as the **teacher**, open the profile `/users/{teacherId}` —
    `rating_avg` and `sessions_completed` should have incremented
    (T-M174).
23. The **Reviews** tab on the profile lists the learner's review
    (T-M176).

## 6. Auto-rate sweep (T-M173)

24. In a Postgres shell:
    ```sql
    UPDATE bookings SET updated_at = now() - interval '8 days' WHERE id = '<bookingId>';
    ```
25. Trigger the job manually (or wait for the 02:00 UTC cron):
    ```bash
    docker compose exec backend \
      java -cp app.jar org.springframework.boot.loader.PropertiesLauncher \
      --spring.profiles.active=dev -Dapp.invoke=ratingJobs.autoRateStaleBookings
    ```
    Production: the cron will run daily — to avoid waiting, you can
    directly call `RatingService.autoRateStaleBookings` from a JMX bean or
    via a temporary admin endpoint.
26. Re-open `/users/{teacherId}` → the missing rating now shows as 5⭐
    with the text "Auto-rated after 7 days without review".

## 7. Failure-mode checks (recommended)

- [ ] Disconnect the network during a session; reconnect within 5 s and
      confirm the chat reconnects (STOMP `reconnectDelay: 5000`).
- [ ] Re-submit a rating (POST `/ratings` twice for the same booking
      from the same side) → 409 `RATING_DUPLICATE`.
- [ ] Cancel a confirmed booking < 24 h before start as the teacher → 50%
      refund row appears in `/wallet/me/transactions`.

## 8. Sign-off

| Step | Operator A | Operator B |
|------|------------|------------|
| 1–6  |            |            |
| 7    |            |            |
| Date |            |            |
