# SkillSeed — Beta Launch Operations

> **Operational runbook for the closed beta (T-M220, T-M221, T-M222, T-M223).**
> Read this top-to-bottom on launch day. Pair with `docs/MONITORING.md`
> for the on-call checklist.

**Last updated:** 2026-09-08 (Sprint 4)
**Owner:** Founder / Tech Lead

---

## 1. Beta user invitation (T-M220)

### 1.1. Cohort
- **Size:** 50–100 members from the Phase 0 waitlist.
- **Geography:** primarily Vietnam + Singapore for first two weeks;
  invite English-speaking EU/US users from week 3.
- **Profile mix:** 60% learners / 40% teachers (we want both sides of
  the marketplace active).

### 1.2. Selection criteria
1. Signed up on the landing page before 2026-08-01.
2. Completed the waitlist profile (`country_code`, `timezone`,
   `learning_style`).
3. Not a competitor / scraper (manual review of domain).

### 1.3. Outreach
- **Channel:** Personal email from the founder (no bulk blast — beta
  vibe matters).
- **Subject:** "You're in — early access to SkillSeed 🌱"
- **Send from:** `hello@skillseed.app` (Resend domain verified).

### 1.4. Welcome kit
- One-pager PDF (`docs/beta/welcome-kit.pdf`, generated from
  `docs/beta/welcome-kit.md`) covering:
  - What SkillSeed is (60-second pitch)
  - What to try in week 1
  - Where to ask for help
  - How to leave feedback

### 1.5. Beta tracking sheet
- Shared Google Sheet `Beta cohort Q3` — columns: `email`, `invited_at`,
  `signed_up_at`, `first_booking_at`, `last_active_at`, `feedback_score`.
- Update daily for the first two weeks.

---

## 2. Tutorial videos (T-M221)

We publish two 3-minute videos on launch day. Recording happens in
advance with a script; the video file lives on Cloudflare R2 and is
embedded in the in-app onboarding banner.

### 2.1. Script — "How to use SkillSeed in 3 minutes"

```
00:00 — Hook
   "Hey! I'm going to show you how to teach what you know and learn
    what you love on SkillSeed — in under three minutes."

00:15 — Step 1: Add skills you can teach (15s)
   - Open /onboarding
   - Pick "Java tutoring"
   - Set level "Expert" + years experience
   - Set your hourly Seed rate (default 60)

01:00 — Step 2: Add skills you want to learn (15s)
   - Same flow, choose category
   - Add "Public speaking"

01:30 — Step 3: Set weekly availability (15s)
   - Pick Monday 18:00–20:00 (Asia/Ho_Chi_Minh)
   - Pick Friday 09:00–11:00

02:00 — Step 4: Book your first session (30s)
   - Search "Python" on /discover
   - Filter by language English
   - Pick a teacher, hit "Book session"
   - Confirm — 60 Seeds deducted from wallet (you got 60 free on
     onboarding)

02:45 — Step 5: Join the call (15s)
   - At scheduled time, open the booking → "Join session"
   - Daily.co room opens in the in-app video surface
   - Whiteboard + chat available

03:00 — Outro
   "That's it. Book, teach, learn. Enjoy 🌱"
```

### 2.2. Recording checklist
- [ ] Screen captured at 1920×1080, 30 fps
- [ ] Captions burned in (Vercel-hosted player supports captions too)
- [ ] Royalty-free lo-fi music under voice-over (low volume)
- [ ] Final cut ≤ 3:15
- [ ] Upload to R2 bucket `skillseed-marketing` (public read)

---

## 3. Feedback channel (T-M222)

Two channels run in parallel:

### 3.1. In-app Intercom widget
- App ID: `NEXT_PUBLIC_INTERCOM_APP_ID` (Vercel env).
- Trigger conditions:
  - User has been active ≥ 7 days, OR
  - User just submitted a rating < 3 stars, OR
  - User just cancelled a booking.
- Initial message: "👋 Got 30 seconds? Tell us what's working and what isn't."

### 3.2. Email fallback
- Address: `feedback@skillseed.app` (forwarded to a shared inbox).
- Auto-responder: "Thanks — we read every message. Expect a reply within
  24 hours Mon–Fri."
- Triage cadence: founder skims inbox daily, replies publicly in
  `#beta-feedback` Slack channel.

### 3.3. Weekly digest
- Every Friday, founder posts a digest to `#beta-feedback`:
  - Top 3 issues reported (severity, count, status)
  - Top 3 feature requests
  - NPS from the in-app widget (if available)

---

## 4. Two-week daily monitoring (T-M223)

The detailed checklist lives in `docs/MONITORING.md` §5. Summary:

| Day | Focus |
|---|---|
| Day 1 | All hands on deck. Watch the deploy, check `/actuator/health` every hour |
| Day 2–3 | Reply to every email. Triage Intercom. Patch any "blocker" bugs |
| Day 4–7 | Daily monitoring checklist (MONITORING.md §5). Begin weekly digest |
| Day 8–14 | Move from daily to every-other-day monitoring |

After week 2, monitoring switches to weekly.

---

## 5. Success criteria (cross-reference)

`Sprint 4` DoD (`tasks.md` §Business):
- [ ] ≥ 50 beta users registered
- [ ] ≥ 200 sessions completed
- [ ] Show-up rate ≥ 30%
- [ ] Avg rating ≥ 4.0

Track each in the cohort sheet; surface in the Friday digest.

---

## 6. Roll-back to private alpha

If the launch exposes a critical bug, founder runs:

```bash
# 1. Pause new sign-ups via feature flag
railway variables set SIGNUP_ENABLED=false --service backend

# 2. Notify cohort
# (use the email template in §7.1)

# 3. Patch + redeploy
git commit -m "fix: ..."
git push origin main
# auto-deploys via CD
```

---

## 7. Email templates

### 7.1. Launch day — "You're in!"
```
Subject: You're in — early access to SkillSeed 🌱
To: <first name>

Hi <first name>,

Thanks for signing up on our waitlist. You're one of <N> people
getting early access to SkillSeed today.

👉 Get started: https://skillseed.app/register?ref=beta-q3

Your account gets:
  • 60 free Seeds (≈ 1 hour of teaching)
  • Priority access to live sessions
  • Direct line to the team via the in-app chat icon

A few things to try in week 1:
  1. Finish onboarding (add 1 skill you teach + 1 you want to learn).
  2. Book your first session — even a 15-minute "hello".
  3. Leave us feedback via the chat icon. We read everything.

Stuck? Reply to this email or ping me on the in-app widget.

Happy trading,
< founder >
```

### 7.2. Day 4 — "How's it going?"
```
Subject: Quick check-in from SkillSeed 🌱
To: <first name>

Hey <first name>,

You've been on SkillSeed for a few days now. Got 60 seconds to tell us
how it's going?

  👍 What's working?
  👎 What's broken / missing?
  ❓ Any questions?

Just reply to this email — I read every message.

< founder >
```