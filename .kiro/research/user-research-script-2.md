# SkillSeed — User Research Script (Part 2)

> **Tiếp nối Part 1 — bao gồm usability tests, NPS/CSAT, B2B discovery, exit interviews, diary studies.**
> **Phạm vi:** Phase 1 (MVP usability) → Phase 4 (Continuous discovery + churn analysis).
> **Triết lý:** Mỗi phương pháp có 1 use case rõ ràng và 1 output action.

---

## Mục lục

7. [Moderated usability test — MVP](#7-moderated-usability-test--mvp)
8. [Unmoderated task test](#8-unmoderated-task-test)
9. [Beta feedback form (in-app)](#9-beta-feedback-form-in-app)
10. [NPS & CSAT surveys](#10-nps--csat-surveys)
11. [B2B customer discovery](#11-b2b-customer-discovery)
12. [Exit interview (churned users)](#12-exit-interview-churned-users)
13. [Diary study (longitudinal)](#13-diary-study-longitudinal)

---

## 7. Moderated usability test — MVP

### 7.1. Use case & timing

**Khi nào dùng:** Phase 1 (Sprint 3–4), khi MVP đủ feature để test core flow.
**Tần suất:** 1 round/sprint, 5–8 participants/round.
**Mục tiêu:** Phát hiện UX blockers, đo task success rate, hiểu mental model.

### 7.2. Recruitment

```
Target: 5–8 users matching target persona (recruited from beta waitlist)

Screening (Typeform):
  - Đã dùng SkillSeed beta (registered)
  - Có webcam + microphone
  - 60 phút available trong tuần
  - $30 gift card incentive

Diversity:
  - ≥ 3 personas represented
  - Mix gender
  - Mix tech-savviness (advanced, intermediate, beginner)
```

### 7.3. Setup

```
Tools:
  - Zoom (record với consent)
  - Lookback.io hoặc Hotjar recordings
  - Staging environment URL (skillseed.app staging)
  - Test data: pre-seeded 5 fake mentors + 3 sample bookings
  - Task list printed out
  - Notion observation log (per-participant row)

Roles:
  - Moderator (1): hướng dẫn, không gợi ý
  - Observer (1-2): ghi notes, watch reactions
  - Participant: thực hiện task, "think aloud"
```

### 7.4. Pre-session (15 min)

```
Welcome email sent 24h before:
  Subject: Your SkillSeed usability session tomorrow
  Body:
    Hi [Name],
    
    Thanks for joining tomorrow! Quick logistics:
    - Time: [Time] GMT+7
    - Link: [Zoom URL]
    - Duration: 60 minutes
    - You'll receive a $30 [GrabPay/MoMo/Amazon] voucher after.
    
    What to prepare:
    - A laptop with Chrome browser (mobile testing separately)
    - A quiet space
    - Your webcam + mic working
    
    What we'll do:
    - I'll ask you to try 5-6 tasks on SkillSeed
    - I'll observe (no judging!) and ask follow-up questions
    - You can stop anytime
    
    See you tomorrow!
    Mai
    
[Calendly auto-reminder at 1h before]
```

### 7.5. Session protocol (60 min)

#### [00:00–05:00] WARM-UP + CONSENT

```
Moderator script:

"Hi [Name], thanks so much for joining! I'm Mai, founder of SkillSeed.

Before we start, I want to record this session so my team can review 
it later. Is that okay? The recording stays internal, and we'll 
anonymize any quotes.

Great. So today, I'll ask you to try out a few tasks on SkillSeed — 
it's an early prototype, so some things might not work perfectly. 
That's totally fine — we want to learn from that.

The most important rule: there are NO wrong answers. If something is 
confusing, that's our fault, not yours. Please be brutally honest.

I'll ask you to 'think aloud' — just narrate what you're doing and 
what you're thinking. Like 'okay, I'm clicking here because...'

Sound good? Any questions before we start?"

[Start recording]

"Great. Can you share your screen and open Chrome? Navigate to 
[staging URL]. Cool, you're in."
```

#### [05:00–55:00] TASKS (6 tasks × 8 min each)

**Task 1 — Sign up & onboarding**

```
"Imagine you just heard about SkillSeed from a friend. Sign up 
with your email and walk me through what you see."

[OBSERVE]
  - Where do they hesitate?
  - Do they read the value props?
  - Do they pick the easy options or thoughtful ones?
  - Do they upload a photo? (rate %)

[PROMTS if stuck > 60s]
  - "What are you thinking right now?"
  - "What would you expect to see next?"

[AFTER task]
  - "How was that? Easy, hard, or weird?"
  - "Did anything surprise you?"
  - "On a scale of 1-7, how confident are you that you completed it correctly?"
```

**Task 2 — Find a mentor**

```
"Now imagine you want to learn Public Speaking. Find a mentor 
on SkillSeed who could teach you."

[OBSERVE]
  - Do they use search or browse?
  - Do they apply filters?
  - Do they read profiles fully or skim?
  - How do they decide?

[PROMTS]
  - "What are you looking for in a mentor?"
  - "What info is missing that you'd want?"
```

**Task 3 — Book a session**

```
"Pick the mentor you liked best. Try to book a 30-minute session 
with them for tomorrow evening."

[OBSERVE]
  - Do they understand the seed cost?
  - Do they check availability?
  - Do they hesitate at the confirm step?
  - What questions come up?

[AFTER]
  - "How confident are you the booking went through?"
  - "Anything that felt off?"
```

**Task 4 — Join video session**

```
"You've been matched with a mentor who's ready for the call. 
Join the video session."

[Use 2-person test setup: researcher on other side joins]

[OBSERVE]
  - Can they find the join button?
  - Camera/mic permission flow?
  - First-impression of video UI?

[AFTER]
  - "How did that feel? Natural or awkward?"
  - "What would make this better?"
```

**Task 5 — Rate the session**

```
"You just finished the session. Rate your experience."

[OBSERVE]
  - Do they understand the multi-criteria rating? (Phase 2)
  - Do they write a review?

[AFTER]
  - "Did anything about the rating feel unfair or unclear?"
```

**Task 6 — Free exploration (5 min)**

```
"Now you have 5 minutes. Explore SkillSeed freely — discover any 
feature you want, click around. Tell me what you find or try."

[OBSERVE]
  - What features do they gravitate to?
  - What do they ignore?
  - Where do they get confused?

[AFTER]
  - "What was the most interesting thing you found?"
  - "What's still unclear?"
  - "If you could change ONE thing, what would it be?"
```

#### [55:00–60:00] WRAP-UP

```
"Thank you so much! Two last questions:

1. Overall, on a scale of 1-7, how easy was SkillSeed to use? 
   1 = very hard, 7 = very easy.

2. Would you recommend SkillSeed to a friend? 
   0 = definitely not, 10 = definitely yes.
   
   Why or why not?

[Stop recording]

"Thanks! Your $30 voucher will arrive within 24 hours. 
Mind if we email you in 3 weeks for a follow-up?"
```

### 7.6. Observer's note template

```
PARTICIPANT: P03 — Hương, 24, Sinh viên IT
DATE: 2026-09-12
TASKS COMPLETED: 5/6 (Task 4 had technical issue)

TASK 1 (Sign up + Onboarding): ✅ SUCCESS (4 min, easy)
  - Hesitated at "level" — didn't know if 3 was low or high
  - Skipped photo upload (cited privacy)
  - Question: "Should I list skills I'm learning too?"

TASK 2 (Find mentor): ⚠️ PARTIAL (6 min, struggled)
  - Used search but typed Vietnamese instead of English skill name
  - 0 results — gave up, scrolled Discover
  - Found mentor Mai Tran but confused why "Forest tier" matters
  - Quote: "I want to see who teaches what quickly, not badges"

TASK 3 (Book session): ✅ SUCCESS (3 min, easy)
  - Liked the seed cost transparency
  - Question: "What if I cancel — do I get all seeds back?"

TASK 4 (Join session): ❌ FAILED (tech issue, not UX)
  - Daily.co room URL didn't open on Chrome
  - Tried 2x, gave up
  - ACTION: bug filed — Daily.co integration issue

TASK 5 (Rate): ✅ SUCCESS (2 min, easy)
  - "5 stars for all — Mai was great"
  - Skipped review text — "takes too long to write"

OVERALL SUS SCORE: 5.4/7
NPS-LIKE: 8/10 ("Cool idea, hope video works better")

CRITICAL ISSUES IDENTIFIED:
1. Search needs Vietnamese language support (P0)
2. Tier system explanation missing (P1)
3. Cancellation policy needs to be more prominent (P1)

POSITIVE OBSERVATIONS:
- Onboarding felt "fast and friendly"
- Seed economy was intuitive
- "Why we matched" resonated
```

### 7.7. Synthesis & action

After each round (5–8 participants):

```
1. Affinity board — cluster observations by theme
2. Severity rating (Jakob Nielsen):
   0 = not a usability problem
   1 = cosmetic only
   2 = minor usability problem
   3 = major usability problem (fix ASAP)
   4 = usability catastrophe (fix before launch)
3. Top 3 issues → create Linear tickets
4. Share 5-minute summary with team
5. Update design system / component library
```

---

## 8. Unmoderated task test

### 8.1. When to use

- High-volume, low-cost: test 20–50 users asynchronously
- For specific micro-tasks (e.g., "Find your wallet balance")
- When scheduling moderated tests is impractical

### 8.2. Tools

- **UserTesting.com** (paid, $50/participant)
- **Maze.co** (continuous research, free tier)
- **Hotjar** (session recordings + on-site surveys)

### 8.3. Template (Maze.co)

```
Welcome screen:
"Hi! We're testing a new feature. This will take 5-10 minutes. 
You'll need Chrome browser and headphones."

[Consent checkbox]
[Demographics: age, role, country]
```

### 8.4. Task examples

```
Task 1: "Find and book a 30-minute session with someone who can 
        teach you Python."
        Success criteria: Reaches booking confirmation page
        
Task 2: "Cancel your upcoming session and get a refund."
        Success criteria: Sees cancellation confirmation
        
Task 3: "Check how many Skill Seeds you have."
        Success criteria: Reports correct balance (visible on screen)
```

### 8.5. Success metrics

| Metric | Target |
|--------|--------|
| Task success rate | ≥ 75% |
| Time on task | ≤ 3 min (simple), ≤ 8 min (complex) |
| Misclicks | ≤ 2 per task |
| Drop-off rate | ≤ 20% |

---

## 9. Beta feedback form (in-app)

### 9.1. Placement & timing

- **Permanent widget:** bottom-right "💬 Feedback" button → modal
- **Trigger after actions:**
  - After completing onboarding
  - After first session
  - After 1 week of usage
  - After cancellation/no-show

### 9.2. Modal structure

```
┌─────────────────────────────────────┐
│  ✕ Help us improve!                 │
├─────────────────────────────────────┤
│                                      │
│  What's on your mind?                │
│                                      │
│  ( ) 💡 Suggestion                   │
│  ( ) 🐛 Bug report                   │
│  ( ) ❓ Question                      │
│  ( ) 💬 Other                         │
│                                      │
│  Tell us more:                       │
│  ┌─────────────────────────────┐   │
│  │                              │   │
│  │                              │   │
│  │                              │   │
│  └─────────────────────────────┘   │
│                                      │
│  📸 Screenshot (optional)            │
│  [Drop image here or click]          │
│                                      │
│  📧 Email (optional, để follow-up):  │
│  [email input]                       │
│                                      │
│  ┌─────────────────────────────┐   │
│  │  Submit                     │   │
│  └─────────────────────────────┘   │
│                                      │
└─────────────────────────────────────┘
```

### 9.3. Auto-categorization (PostHog)

```javascript
// Tag feedback automatically
trackEvent('beta_feedback', {
    category: 'suggestion',  // from radio
    content_length: 234,     // chars
    has_screenshot: true,
    page: window.location.pathname,
    user_segment: getUserSegment(),
});
```

### 9.4. Routing rules

| Category | Route to | SLA |
|----------|----------|-----|
| Bug | Linear (P1/P2) | Triage 24h |
| Suggestion | Notion feedback board | Review weekly |
| Question | Help center docs | Auto-reply 1h |
| Other | Founder | Weekly digest |

---

## 10. NPS & CSAT surveys

### 10.1. NPS (Net Promoter Score) — quarterly

**Trigger:** Send email sau 14 ngày kể từ session completed, mỗi quý.

**Question structure:**

```
Hi [Name],

You've been using SkillSeed for a while. Quick question:

"On a scale of 0 to 10, how likely are you to recommend 
SkillSeed to a friend or colleague?"

○ 0  ○ 1  ○ 2  ○ 3  ○ 4  ○ 5  ○ 6  ○ 7  ○ 8  ○ 9  ○ 10

[Follow-up question, conditional]

If score ≤ 6:
  "Sorry to hear that. What's the main reason for your score?"
  [open text]

If score = 7-8:
  "What could we improve to earn a higher score?"
  [open text]

If score = 9-10:
  "Awesome! What do you love most about SkillSeed?"
  [open text]
```

### 10.2. NPS calculation

```
Promoters (9-10):  X / total respondents = P%
Passives (7-8):    Y / total respondents = Pa%
Detractors (0-6):  Z / total respondents = D%

NPS = P% - D%
```

**Target NPS:**
- Phase 1: ≥ 30
- Phase 2: ≥ 50
- Phase 3: ≥ 60

### 10.3. CSAT (Customer Satisfaction) — per session

**Trigger:** After each completed session.

```
"How satisfied are you with this session?"

○ ⭐ (1) Very unsatisfied
○ ⭐⭐ (2) Unsatisfied  
○ ⭐⭐⭐ (3) Neutral
○ ⭐⭐⭐⭐ (4) Satisfied
○ ⭐⭐⭐⭐⭐ (5) Very satisfied

[Optional follow-up]
"Any specific feedback?"
[open text]
```

### 10.4. Other in-product surveys

| Trigger | Question | Use |
|---------|----------|-----|
| After onboarding | "How was the onboarding experience?" (1-5) | Measure onboarding UX |
| After Discover view | "Did you find what you're looking for?" (Yes/No + reason) | Measure matching quality |
| Before session join | "Are you ready for the session?" + status | Reduce no-shows |
| After cancellation | "Why are you cancelling?" (multi-choice) | Reduce churn |

---

## 11. B2B customer discovery

### 11.1. Use case

**Khi nào:** Phase 3 trước khi build B2B features. Xác nhận HR managers sẽ mua.

### 11.2. ICP (Ideal Customer Profile)

```
- Company size: 50–500 employees
- Industry: Tech, consulting, BPO
- Region: VN, SG, ID, PH (Phase 4)
- Pain: high turnover, slow skill development
- Decision maker: HR Manager, L&D Lead, People Director
- Budget: $5K–$50K/year for L&D tools
```

### 11.3. Outreach (LinkedIn + cold email)

```
Subject: Quick question about your L&D budget

Hi [Name],

I noticed [Company] has been hiring aggressively in [role]. 
Congrats on the growth!

Quick question — when a new hire needs to learn [specific skill], 
what's your current approach? I've been talking to 20 HR leaders 
in [industry] and hearing a consistent pain point.

Would you have 20 minutes this week to share your view? 
No pitch — I'm just researching.

[Calendly link]

Thanks,
Mai
SkillSeed (we're building peer-to-peer learning for companies)
```

### 11.4. Discovery interview script (30 min)

#### Section 1 — Current state (10 min)

```
1. Walk me through how a new employee at [Company] learns the 
   skills they need to do their job well.
   
2. What's working well in your current approach?

3. What's the biggest pain point in employee development right now?

4. Roughly, what % of new hires struggle in their first 90 days? 
   What do they struggle with?

5. How much do you currently spend per employee on L&D per year?
   (Tools, courses, books, conferences)
```

#### Section 2 — Existing solutions (5 min)

```
6. What tools/platforms do you currently use for L&D?
   (LinkedIn Learning, Coursera, internal LMS, etc.)

7. What's the adoption rate among employees?
   (Estimate % who actively use it)

8. What's your biggest complaint about these tools?

9. Have you tried peer-to-peer learning before?
   (e.g., internal mentoring programs)
```

#### Section 3 — Concept test (10 min)

```
10. [Show concept: "SkillSeed for Companies"]
    
    Imagine a tool where:
    - Employees teach each other skills 1-on-1
    - AI matches based on Skill DNA
    - You (HR) see dashboard with skill gap heatmap
    - Employees earn internal reputation (Skill Passport)
    - Cost: $99/user/year (vs $500+ for LinkedIn Learning)
    
    First reaction?

11. Who would be the internal champion for this? Why?

12. What concerns come up?

13. If we ran a 3-month free pilot with 30 employees, would you 
    try it? What's the success metric for you?

14. What's the minimum we'd need to deliver for you to recommend 
    it to your CEO?
```

#### Section 4 — Pricing & close (5 min)

```
15. We estimate $99/user/year. How does that compare to your 
    current L&D spend per employee?

16. If we bundled with SSO + dedicated success manager, would 
    $200/user/year work for 50+ seats?

17. [If interested] Can I introduce you to our head of B2B 
    sales for a 30-min discovery call?
```

### 11.5. Output

```
B2B DISCOVERY SUMMARY

ICP confirmed: 6/8 interviews
  - 4/6 say "yes, would pilot"
  - 2/6 say "maybe, need CFO approval"
  - 0/6 say "no"

Top pain points:
  1. Generic courses don't fit specific needs (5/6)
  2. Low engagement with self-paced tools (5/6)
  3. No visibility into skill gaps (4/6)

Pricing signal:
  - Median acceptable: $80-120/user/year
  - Premium tier ($200+) needs SSO + dedicated CSM

Feature priorities for pilot:
  1. SSO + dashboard (5/6)
  2. Internal matching within company (4/6)
  3. Skill gap heatmap (4/6)

Next steps:
  - Build pilot deck
  - Recruit 3 design partners
  - Run 3-month pilot
```

---

## 12. Exit interview (churned users)

### 12.1. When to use

- For users who stopped using after ≥ 3 sessions or after 90 days
- Quarterly review of churn patterns
- Find "saveable" churn (fixable) vs "unsaveable" (product-market mismatch)

### 12.2. Trigger criteria

```
A user is "churned" if:
  - Last session > 60 days ago
  - OR hasn't logged in > 30 days after ≥ 1 completed session
  - OR account deactivated
```

### 12.3. Outreach (email + $20 incentive)

```
Subject: We'd love to learn from your experience

Hi [Name],

You tried SkillSeed a few months ago and we noticed you haven't 
been back. That's totally okay — but we'd really value 15 minutes 
of your honest feedback.

What's working? What's not? Your answers directly shape what we build next.

As a thank you, we'll send you a $20 [GrabPay/MoMo] voucher, 
regardless of what you say.

[Calendly link]

Thanks for being part of our journey,
Mai
```

### 12.4. Exit interview script (15 min)

```
1. Can you walk me through your SkillSeed journey? When did you 
   sign up, what did you do first, how did it go?

2. What made you stop using it?
   [OPEN — let them talk]

3. Was there a specific moment or feature that turned you off?
   [SPECIFIC INCIDENT]

4. What would have made you stay?
   [FUTURE-PROOFING]

5. Did you try similar products (Preply, italki, ADPList, etc.)? 
   Why did you choose (or not) them instead?

6. If you could change ONE thing about SkillSeed, what would it be?

7. Would you try SkillSeed again if we fixed that thing?

8. Anything else we should know?
```

### 12.5. Categorization

| Reason | Action |
|--------|--------|
| Found a mentor they love offline | Don't recover (acceptable) |
| Sessions didn't meet expectations | Improve matching quality |
| Cost too high (premium) | Pricing experiment |
| Got busy / life change | Win-back campaign later |
| Bad mentor experience | Quality control + reporting |
| Tech issues | Bug fix priority |
| Privacy / safety concern | Trust signals + verification |

### 12.6. Win-back campaigns

```
For "life change" churners (segment):
  - Email after 90 days: "Still interested? Here's 50 free seeds"
  - Email after 180 days: "We've added X, Y, Z — check it out"

For "bad experience" churners:
  - Personal email from founder: "Sorry about X. We've fixed it. 
    Want to try again with a free Premium month?"
```

---

## 13. Diary study (longitudinal)

### 13.1. Use case

- Understand natural usage over time (vs single-session tests)
- Capture emotional arc (excitement → habit → drop-off)
- Identify "magic moments" + "near-miss moments"

### 13.2. Setup (Phase 2+)

```
Recruit: 10–15 active users (mix of personas)
Duration: 2–4 weeks
Tools:
  - Notion journal (or Slack channel)
  - Daily 2-min survey via email/SMS
  - Weekly 15-min video check-in
Incentive: $100 total + premium features
```

### 13.3. Daily micro-survey (2 min)

```
[Sent at user's typical session time]

"Did you use SkillSeed today?"
  ( ) Yes
  ( ) No
  ( ) Tried but something went wrong

[If Yes]
"How was it?" (1 emoji)
😞 😐 🙂 😃 🤩

"One word to describe today:"
[open text]

[If No]
"What stopped you?"
[open text]
```

### 13.4. Weekly reflection (15 min call)

```
1. Walk me through your week with SkillSeed.

2. Tell me about the BEST moment this week.

3. Tell me about the WORST moment.

4. Did you discover any feature you didn't know about?

5. Did anything surprise you?

6. Are you forming a habit yet? (Yes / Somewhat / No)
   If yes, what triggers it?

7. If you had to describe SkillSeed to a friend in 1 sentence, 
   what would you say?
```

### 13.5. Output

```
DIARY STUDY SYNTHESIS

Engagement arc over 4 weeks:
  Week 1: High curiosity, high usage, some confusion
  Week 2: First "magic moment" (great session)
  Week 3: Plateau, mixed feelings
  Week 4: Either habit-forming OR drop-off risk

Habit triggers identified:
  - Calendar reminder (Wed 7pm = "session night")
  - Notification when matched (powerful)
  - Streak (3 sessions/week)

Drop-off risks:
  - Poor mentor match → immediate disengagement
  - No reply from mentor > 24h → frustration
  - Wallet confusion (seeds expiring)

Recommendations:
  - Add session reminders (P0)
  - Auto-match if no reply > 24h (P1)
  - Wallet transparency (P0)
```

---

## Phụ lục — Research operations

### A. Calendar management

```
Tools:
  - Calendly (free tier, 1 event type)
  - Cal.com (open source, better long-term)
  
Event types:
  - PMF interview (30 min)
  - Usability test (60 min)
  - B2B discovery (30 min)
  - Exit interview (15 min)
  - Diary study check-in (15 min)
```

### B. Consent form template

```
SKILLSEED RESEARCH CONSENT FORM

Project: [Project name]
Date: [Date]
Researcher: Mai Tran, Founder, SkillSeed

PURPOSE: To understand user needs, behaviors, and feedback for 
SkillSeed product development.

WHAT WE'LL DO: 
- 30-60 minute interview via video call
- Record audio and video (with your permission)
- Take notes during the conversation
- Follow up via email if needed

YOUR RIGHTS:
- Participation is voluntary
- You can stop at any time without consequence
- You can decline any question
- You can request deletion of your data after the session

DATA HANDLING:
- Recordings are stored securely and accessed only by the SkillSeed team
- Transcripts are anonymized for internal reports
- Recordings deleted within 12 months unless you consent otherwise
- You can request your data at any time via research@skillseed.app

INCENTIVE: [Gift card amount + delivery method]

By signing below, you consent to participate under these terms.

Participant: ______________________  Date: __________
Researcher:  ______________________  Date: __________
```

### C. Synthesis board template (Notion)

```
.kiro/research/synthesis/[date]/

├── Raw notes
│   ├── P01 - Hương, 24, Sinh viên
│   ├── P02 - Duc, 28, Dev
│   └── ...
│
├── Affinity diagram (screenshot)
│
├── Theme clusters
│   ├── Theme 1: Trust & verification
│   ├── Theme 2: Seed economy confusion
│   └── Theme 3: AI matching expectations
│
├── Top quotes (anonymized)
│
├── Recommendations
│   ├── Must-fix (P0): 3 items
│   ├── Should-fix (P1): 5 items
│   └── Nice-to-fix (P2): 7 items
│
└── Follow-up actions
    ├── Linear tickets created: 5
    ├── Design changes queued: 3
    └── Next research: ___________
```

### D. Per-phase research cadence

| Phase | Cadence | Sample size per round |
|-------|---------|----------------------|
| Phase 0 (Validation) | Once, 4 weeks | 30 interviews + 200 surveys |
| Phase 1 (MVP) | Weekly | 5 usability tests |
| Phase 2 (AI/Polish) | Bi-weekly | 8 usability + 10 NPS |
| Phase 3 (Mobile/Scale) | Weekly | 5 usability + 20 NPS + 5 B2B |
| Phase 4 (Regional) | Monthly per region | 8 usability + 30 NPS + 5 B2B + 3 exit |
| Phase 5 (Beyond) | Quarterly | 15 advisory board + ongoing |

---

**Hoàn thành toàn bộ User Research Script (Part 1 + Part 2).**

Tóm tắt deliverables:
- **Part 1:** 6 nguyên tắc + Recruiting + PMF interview script (5 personas) + Survey + Synthesis + Decision framework
- **Part 2:** Moderated & unmoderated usability + Beta feedback + NPS/CSAT + B2B discovery + Exit interview + Diary study

**Bạn muốn tôi commit & push lên git ngay bây giờ không?**