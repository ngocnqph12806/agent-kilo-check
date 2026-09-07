# SkillSeed — User Research Script

> **Bộ công cụ nghiên cứu người dùng hoàn chỉnh cho SkillSeed.**
> **Phạm vi:** PMF validation (Phase 0) → MVP usability (Phase 1) → Continuous discovery (Phase 2–4) → B2B (Phase 3) → Exit interviews (Phase 5).
> **Triết lý:** Mỗi research activity gắn với một **research question** rõ ràng và một **action** sau khi có kết quả.

---

## Mục lục

### Part 1 — Methodology & PMF (Phase 0)
1. [Nguyên tắc nghiên cứu](#1-nguyên-tắc-nghiên-cứu)
2. [Recruiting playbook](#2-recruiting-playbook)
3. [PMF interview — Script 30 phút (5 personas)](#3-pmf-interview--script-30-phút-5-personas)
4. [Survey 5 phút — Landing page](#4-survey-5-phút--landing-page)
5. [Synthesis template](#5-synthesis-template)
6. [Decision framework (GO/NO-GO/PIVOT)](#6-decision-framework-gono-gopivot)

### Part 2 — Usability & Continuous Discovery (Phase 1–4)
7. [Moderated usability test — MVP](#7-moderated-usability-test--mvp)
8. [Unmoderated task test](#8-unmoderated-task-test)
9. [Beta feedback form (in-app)](#9-beta-feedback-form-in-app)
10. [NPS & CSAT surveys](#10-nps--csat-surveys)
11. [B2B customer discovery](#11-b2b-customer-discovery)
12. [Exit interview (churned users)](#12-exit-interview-churned-users)
13. [Diary study (longitudinal)](#13-diary-study-longitudinal)

---

# PART 1 — METHODOLOGY & PMF

## 1. Nguyên tắc nghiên cứu

### 1.1. 6 nguyên tắc vàng

1. **Talk to humans, not assumptions.** Mọi quyết định product phải có voice-of-customer.
2. **Small samples, deep insights.** 5–8 interview chất lượng hơn 50 survey superficial.
3. **No leading questions.** Không hỏi "Bạn có thích ý tưởng X không?" mà hỏi "Kể về lần cuối bạn làm X".
4. **Triangulate.** Kết hợp nhiều methods (interview + survey + analytics).
5. **Bias-aware.** Biết mình bias ở đâu (recency, friend-network, founder bias) và correct.
6. **Action-oriented.** Mỗi research phải dẫn đến quyết định product rõ ràng.

### 1.2. Methods by phase

| Phase | Primary method | Secondary | Sample size | Frequency |
|-------|---------------|-----------|-------------|-----------|
| **Phase 0** | 1:1 interview | Survey (Typeform) | 30 + 200 | Once |
| **Phase 1** | Moderated usability | In-app feedback | 10/sprint | Weekly |
| **Phase 2** | NPS + cohort analysis | User interview | 50/quarter | Monthly |
| **Phase 3** | B2B customer discovery | Diary study | 20 + 10 | Quarterly |
| **Phase 4** | Exit interviews | Multi-region survey | 50/quarter | Quarterly |
| **Phase 5** | Strategic customer advisory | Longitudinal cohort | 30 board/year | Quarterly |

### 1.3. Roles & responsibilities

| Role | Responsibility |
|------|---------------|
| **Founder** | Lead interviews, do synthesis, decide pivots |
| **Researcher (hired Phase 2+)** | Recruit, schedule, conduct interviews, analyze |
| **Designer** | Co-facilitate usability tests, log UX findings |
| **Engineer** | Read research reports, raise questions, implement fixes |
| **Customer Success (Phase 3+)** | Conduct B2B check-ins, log feedback |

### 1.4. Research repository

```
.kiro/research/
├── transcripts/             # Otter/Fireflies raw outputs
├── notes/                   # Founder's handwritten notes
├── synthesis/               # Affinity diagrams, themes
├── recordings/              # Audio (with consent)
├── consent-forms/           # Per-interviewee
└── reports/                 # Per-quarter insights report
```

---

## 2. Recruiting playbook

### 2.1. Persona-targeted outreach templates

#### Template A — Cold LinkedIn (Sinh viên)

```
Hi [Name],

I'm Mai, founder of SkillSeed — a platform where students teach each 
other skills for free (instead of paying $200+ for courses).

I'm researching whether this is actually something students would use. 
Would you have 30 minutes for a quick chat? I'd love to hear about how 
you learn new skills today — no sales pitch, just listening.

If yes, here's my calendar: [Calendly link]

Thanks!
Mai
```

#### Template B — Warm network (mọi persona)

```
Hey [Name],

I'm building something I think you'd find interesting — a way to 
trade skills with other people using time instead of money.

Before I launch, I want to make sure I'm solving a real problem. 
Could I grab 30 min to interview you about how you currently learn/teach?

Totally casual — happy to grab coffee (real or virtual).

When works?
```

#### Template C — B2B (HR Manager — Phase 3)

```
Hi [Name],

I lead a startup building a peer-to-peer learning platform for companies 
to upskill employees at 1/10th the cost of Coursera. We're piloting with 
5 SMEs in SEA.

Would you have 30 min to share how your team currently handles 
cross-training and skill gaps? Happy to share our findings in return.

[Calendly]
```

### 2.2. Screening questionnaire (5 min)

Dùng Google Form hoặc Typeform trước khi book interview:

```
1. What's your age range?
   ( ) 18–24   ( ) 25–34   ( ) 35–44   ( ) 45+

2. What's your current role?
   [free text]

3. In the last 6 months, have you tried to learn a new skill?
   ( ) Yes, completed   ( ) Yes, but stopped   ( ) No

4. How did you learn? (multi-select)
   [ ] Online course (paid)   [ ] YouTube   [ ] Books
   [ ] Friend/colleague       [ ] Mentor    [ ] Trial & error

5. Do you have a skill you'd feel confident teaching others?
   ( ) Definitely   ( ) Maybe   ( ) No

6. How comfortable are you with video calls?
   ( ) Love them   ( ) OK   ( ) Dislike them

7. Which country are you in?
   [dropdown of 50+ countries]

8. Email for follow-up
   [email input]
```

### 2.3. Screening criteria

| Persona | Must-have | Nice-to-have |
|---------|-----------|--------------|
| Sinh viên | Age 18–24, currently studying | English-speaking, has tried online courses |
| Chuyên gia trẻ | Age 25–35, full-time job | Tech role, frequent learner |
| Career switcher | Age 30–45, considering career change | Recently started job hunt |
| Người lớn tuổi | Age 55+, retired or semi-retired | Has lifetime skill (e.g., retired teacher, ex-engineer) |
| Mẹ sau sinh | Age 28–40, has young child | Looking to re-enter workforce |

**Stratification goal:** ≥ 30% người lạ (cold outreach) để tránh friend-bias.

### 2.4. Incentive

- **Phase 0:** Không cần — chỉ "được ảnh hưởng đến sản phẩm tương lai". Offer early access + 30 starter seeds.
- **Phase 1 usability test:** $20–50 gift card (GrabPay, MoMo, Amazon) cho 45-min session.
- **Phase 3 B2B:** Free pilot access hoặc $200 Amazon voucher cho 60-min interview.

---

## 3. PMF interview — Script 30 phút (5 personas)

### 3.0. Setup & logistics

```
Tools:
  - Zoom / Google Meet (record with Otter.ai)
  - Google Doc for live note-taking
  - Figma prototype (if concept-testing)
  - 4 quadrant affinity board

Pre-interview (15 min before):
  - Open Otter.ai, test mic
  - Open persona context card
  - Have consent form ready
  - Open Notion synthesis board

Consent (record at start):
  "I'm going to record this conversation for our research. 
   The recording stays internal and we'll anonymize any quotes 
   in our reports. You can stop at any time. Sound good?"
```

### 3.1. Universal script (áp dụng cho cả 5 personas)

**Mục tiêu phỏng vấn:**
- Hiểu pain points hiện tại
- Test concept SkillSeed (reception + concerns)
- Đánh giá willingness-to-pay & willingness-to-teach
- Phát hiện use cases cụ thể

**Tổng thời gian:** 30 phút (intro 5min + 6 sections × 4min + wrap-up 1min)

---

#### [00:00–05:00] WARM-UP

```
1. Tell me a bit about yourself — what's your day-to-day like?
   [OPEN — listen for: job, hobbies, family, location]

2. How did you spend your weekend?
   [OBSERVATION — energy, social vs solo, learning-related activities]

3. What's something you're really proud of learning recently?
   [WARM — gets them into "learning" mindset, watch enthusiasm]
```

**Listen for:** vocabulary (technical vs casual), engagement level, life stage clues.

---

#### [05:00–09:00] CURRENT LEARNING BEHAVIOR

```
4. Think about the last time you wanted to learn something new. 
   What was it, and what did you do?
   [BEHAVIORAL — past tense, not hypothetical]

5. Walk me through the steps. How did you find resources, who helped, 
   what worked, what didn't?
   [JOURNEY MAP — emotional peaks/valleys]

6. How much did it cost you — money, time, energy?
   [QUANTIFY — get concrete numbers]
```

**Listen for:** actual vs stated behavior. Watch for "I would have done X" vs "I did X".

---

#### [09:00–13:00] PAIN POINTS

```
7. What's the most frustrating part of learning new skills today?
   [EMOTIONAL — dig into feelings]

8. Have you ever felt stuck — like you couldn't find anyone to help 
   you learn what you want? Tell me about that.
   [STORY — get specific incident]

9. How much do you value learning from someone who "gets" you — same 
   industry, same culture, same stage of life?
   [PERSONA FIT — relevance to our value prop]

10. If you could wave a magic wand, what would the perfect learning 
    experience look like?
    [IDEAL STATE — aspirational, don't constrain]
```

**Listen for:** intensity of pain. Frequency. Workarounds.

---

#### [13:00–17:00] TEACHING BEHAVIOR (THE OTHER SIDE)

```
11. Now flip the script — what's something you know that you'd feel 
    confident teaching others?
    [INVENTORY — list skills, watch hesitation]

12. Have you ever taught or mentored anyone before? Formally or 
    informally?
    [HISTORY — get specifics]

13. If I told you "you can teach someone for 30 minutes and get 
    something in return" — what would make that worthwhile?
    [VALUE EXCHANGE — money? credits? karma? network?]
    
14. What would stop you from teaching? Be honest.
    [OBJECTIONS — get all of them on the table]
```

**Listen for:** confidence vs imposter syndrome. Self-perceived value.

---

#### [17:00–24:00] CONCEPT TEST (THE BIG ONE)

**Transition:**
```
"I've been working on something I want to share with you. It's 
called SkillSeed — and the idea is..." [show 1-slide mockup]

[Show this slide on screen share:]
┌─────────────────────────────────────────────────────────────┐
│  🌱 SkillSeed                                               │
│                                                              │
│  Teach what you know. Learn what you love.                  │
│  Pay with your time, not your wallet.                       │
│                                                              │
│  • Học kỹ năng 1-1 qua video call                           │
│  • Dùng "Skill Seeds" (internal currency) thay vì tiền      │
│  • AI matching tìm mentor/mentee phù hợp                    │
│  • Skill Passport chứng minh bạn biết gì                    │
│                                                              │
│  30 free starter seeds để bắt đầu                          │
└─────────────────────────────────────────────────────────────┘
```

```
15. First reaction — what's going through your head?
    [IMMEDIATE — gut reaction, don't analyze yet]

16. Tell me more — what do you think this is? Who is it for?
    [COMPREHENSION — check understanding]

17. Be honest — what concerns come up?
    [OBJECTIONS — get them out in the open]

18. Imagine you're using it tomorrow. Walk me through your first 
    session — what would you do?
    [USE CASE — concrete scenario]
```

**Critical questions to dig into:**

```
19. The "Seeds" idea — what do you think about that? 
    [REACTION TO TOKEN ECONOMY]

20. The AI matching — would you trust it? Any concerns?
    [ALGORITHM TRUST]

21. The Skill Passport on LinkedIn — would you share that? 
    [VERIFIABLE REPUTATION]

22. What would make you try it for the first time?
    [ACTIVATION BARRIER]

23. What would make you stop using it?
    [CHURN TRIGGERS — very important]
```

---

#### [24:00–28:00] PRICING & MONETIZATION

```
24. Right now, online courses cost $200-$2,000. If this was 100% 
    free (you trade time for time), how would that feel?
    [FREE TIER REACTION]

25. If we had a Premium version with AI insights, priority matching, 
    etc., how much would you pay per month?
    [PRICING — get range, watch anchoring]

    Anchor options to consider:
    - $5/mo (Netflix-level)
    - $10/mo (mid)
    - $20/mo (premium)
    
26. Have you paid for any learning platforms? Which, how much, why?
    [PRIOR SPEND — validates ability to pay]
```

**Listen for:** actual willingness vs polite answer. Get specific numbers.

---

#### [28:00–30:00] WRAP-UP

```
27. If you could only remember one thing from our chat today, what 
    would you want me to remember?
    [MOST IMPORTANT — single most powerful insight]

28. Is there anyone else you think I should talk to? Can you intro?
    [REFERRAL — network effect of research]

29. Last question — anything you want to ask me?
    [RECIPROCITY — share briefly about SkillSeed if asked]

"Thank you so much! I'll send you a $10 gift card and your 30 free 
starter seeds when we launch. Mind if I email you in 3 months to 
see if your thoughts have changed?"
```

---

### 3.2. Persona-specific deep-dive questions

#### Persona A — Sinh viên "Hạt giống" (18–24)

```
After section 4 (Pain Points), add:

A1.  Trường bạn có hỗ trợ gì để sinh viên học kỹ năng mới không? 
     (CLB, mentor, workshop, ...) Bạn có dùng không?
A2.  Bạn có biết ai trong trường giỏi X mà bạn muốn học không? 
     Bạn đã nhờ họ chưa? Tại sao có/không?
A3.  Thu nhập thêm — nếu bạn dạy kỹ năng của mình 1h/tuần, bạn 
     thích nhận gì? (tiền mặt / Seeds / quà / giấy khen / khác)
A4.  Bạn có LinkedIn không? Profile có ảnh + bio không? Bạn có 
     care về "online reputation" không?
```

#### Persona B — Chuyên gia trẻ "Đa năng" (25–35)

```
B1.  Trong 6 tháng qua, bạn đã học gì ngoài công việc chính? 
     (Hobby, side skill, certification)
B2.  Bạn có thường xuyên mentor junior không? Có formal hay informal?
B3.  Nếu có 1 mentor giỏi hơn bạn, bạn có sẵn sàng trả bao nhiêu 
     cho 1 session 1h? (Likert: 0 / <50K / 50-200K / >200K VND)
B4.  Bạn dùng app nào cho việc học online? (Zoom, Notion, ChatGPT, ...)
B5.  Bạn có quan tâm đến "Skill Passport" trên LinkedIn? Tại sao?
```

#### Persona C — Người chuyển nghề "Lội ngược dòng" (30–45)

```
C1.  Bạn đang cân nhắc chuyển sang ngành nào? Đã bắt đầu chưa?
C2.  Skill gap lớn nhất của bạn là gì? Bạn đang tìm cách fill nó 
     thế nào?
C3.  Nếu có mentor đã làm công việc bạn muốn — sẵn sàng nói chuyện 
     30 phút — bạn sẵn sàng trả bao nhiêu?
C4.  Time constraint — bạn có bao nhiêu giờ rảnh/tuần để học? 
     Bạn sẵn sàng hy sinh gì?
C5.  Bạn đã từng dùng mentor platform nào chưa? (ADPList, MentorCruise, ...)
```

#### Persona D — Người lớn tuổi "Truyền lửa" (55+)

```
D1.  Bạn nghỉ hưu chưa? Bạn có muốn chia sẻ kinh nghiệm của mình 
     không?
D2.  Bạn có kỹ năng nào mà bạn tự hào nhất? Bạn có nghĩ giới trẻ 
     muốn học nó không?
D3.  Bạn có dùng smartphone thành thạo không? Video call? 
     (Important — đánh giá adoption barrier)
D4.  Bạn cảm thấy thế nào về việc dạy online (không gặp mặt)?
D5.  Bạn có con/cháu dùng app học online không? Bạn nghĩ gì về 
     cách họ đó?
```

#### Persona E — Bà mẹ sau sinh "Tái khởi" (28–40)

```
E1.  Con bạn mấy tuổi? Bạn có đang ở nhà hay đi làm lại?
E2.  Bạn muốn học gì trong 6 tháng tới? Tại sao?
E3.  Thời gian rảnh của bạn rất linh hoạt hay cố định? 
     (Em bé ngủ trưa, tối sau khi bé đi ngủ, ...)
E4.  Bạn có sẵn sàng dạy kỹ năng mình không? (Kinh nghiệm làm mẹ, 
     quản lý Tài chính gia đình, nấu ăn, ...)
E5.  Nếu bạn có thể học 1 thứ MIỄN PHÍ từ 1 mentor giỏi, bạn sẽ 
     chọn học gì?
```

---

### 3.3. Post-interview checklist

```
Ngay sau khi interview (15 min):

□ Tắt recording
□ Ghi notes ngay trong khi còn nhớ:
   - Top 3 insights (1 dòng mỗi cái)
   - 1 quote mạnh nhất
   - 1 concern/objection quan trọng nhất
   - Có nên "would-use" không? (Yes / Maybe / No)
□ Add row mới vào tracking sheet
□ Update Notion synthesis board (sticky note per insight)
□ Gửi thank-you email + $10 gift card (nếu có incentive)

Trong 24 giờ:

□ Transcribe qua Otter.ai (nếu dài > 20 min)
□ Tag transcript theo persona + theme
□ Highlight 5 most powerful quotes
□ Update weekly metrics dashboard
```

---

## 4. Survey 5 phút — Landing page

### 4.1. Survey structure (8 câu, 5 phút)

```
[Intro]
"SkillSeed is a new platform where people teach each other skills 
1-on-1 via video call, using time instead of money. Help us understand 
if this would be useful to you. 5-minute survey, completely anonymous."

---

Q1.  What's your age?
     ( ) Under 18   ( ) 18–24   ( ) 25–34   ( ) 35–44
     ( ) 45–54      ( ) 55+

Q2.  Which country are you in?
     [dropdown — 50+ countries]

Q3.  What's your current role?
     [free text — single line]

Q4.  In the last 6 months, which skills have you tried to learn? 
     (open text — comma separated)
     [_____________________________________________]

Q5.  What skills could you confidently teach to someone else? 
     (open text)
     [_____________________________________________]

Q6.  How willing are you to TEACH a skill for 30 minutes/week, 
     in exchange for time (not money)?
     1 —— 2 —— 3 —— 4 —— 5
     (Not at all)            (Very willing)

Q7.  How willing are you to LEARN a skill 1-on-1 with a stranger 
     via video call?
     1 —— 2 —— 3 —— 4 —— 5
     (Not at all)            (Very willing)

Q8.  Would you use SkillSeed if it launched tomorrow?
     ( ) Definitely
     ( ) Probably
     ( ) Maybe
     ( ) Probably not
     ( ) Definitely not

Q9.  (Optional) Email to get early access + 30 free starter seeds
     [email input]

[Thank you screen]
"Thanks! If you left your email, we'll notify you at launch 
and give you 30 free Skill Seeds to try it out."
```

### 4.2. Survey variants (cho A/B testing landing page)

#### Variant A — Control
```
Q6.  How willing are you to TEACH for 30 minutes/week?
```

#### Variant B — With Seed detail
```
Q6.  You earn 30 Seeds when you teach for 30 minutes. 
     60 Seeds = 1 hour of learning from any mentor.
     How willing are you to teach?
```

#### Variant C — Social proof
```
Q6.  [Testimonial: "I taught Vietnamese for 30 min and learned 
     English conversation in return — felt like magic." — Linh, 27]
     
     How willing are you to teach?
```

---

## 5. Synthesis template

### 5.1. Affinity diagram (Notion board)

Sau mỗi đợt 5–10 interviews, founder dành 2h để cluster insights.

```
Columns (themes — tự sinh từ data):
  ┌─────────────────────────────────────────────┐
  │  PAIN POINTS    │  TEACHING    │  TRUST    │
  │                  │              │           │
  │  [sticky 1]      │  [sticky 7]  │  [sticky 11]│
  │  [sticky 2]      │  [sticky 8]  │  [sticky 12]│
  │  ...             │  ...         │  ...      │
  └─────────────────────────────────────────────┘

Re-arrange cho đến khi clusters ổn định.
```

### 5.2. Per-persona synthesis sheet

```
Persona: Sinh viên "Hạt giống" (n=6)

  WHAT THEY WANT:
    • Học English speaking 1-1 (4/6 mentioned)
    • Học coding từ senior (3/6)
    • Kết nối với peers cùng chuyên ngành

  WHAT FRUSTRATES THEM:
    • Không đủ tiền cho Coursera ($200+)
    • Bạn bè không rảnh / không biết chủ đề
    • YouTube tutorials thiếu cá nhân hoá

  WHAT EXCITES THEM ABOUT SKILLSEED:
    • Free (5/6)
    • AI matching (3/6 — "it would save time")
    • Skill Passport (2/6 — "useful for CV")

  WHAT CONCERNS THEM:
    • Stranger safety (4/6)
    • "Will mentor be qualified?" (3/6)
    • Time zone with global mentors (2/6)

  WILL THEY USE?
    • Yes: 4/6 (67%)
    • Maybe: 1/6
    • No: 1/6 (concerns about stranger safety)

  TOP QUOTE:
    "I would love to learn English from a real person, but I can't 
     afford $20/hour lessons. If I could trade my Vietnamese skills 
     for English conversation, that's perfect." — Hương, 21, Hanoi
```

### 5.3. Cross-persona patterns

```
EMERGING THEMES (after 30 interviews):

1. FREE IS TABLE-STAKES
   • 26/30 say they "can't afford" current options
   • But not "any price = $0" — they value time invested
   
2. TRUST IS THE BIGGEST BARRIER
   • 22/30 ask "what if mentor isn't qualified?"
   • 18/30 ask "what if the stranger is weird?"
   • → Verification system is essential (not optional)

3. AI MATCHING IS A MAGNET
   • 24/30 say AI matching would "save time finding mentor"
   • But need to understand WHY matched ("Why we matched")

4. SEEDS ECONOMY = "FUN" BUT UNCLEAR
   • 19/30 like the idea ("sounds fair")
   • But unclear about expiration, conversion, sustainability
   • → Onboarding must teach this clearly

5. SKILL PASSPORT RESONATES WITH YOUNG
   • Strong among 18–35 (16/22 = 73%)
   • Weak among 55+ (1/5 = 20%)
   • → De-emphasize for older persona initially
```

---

## 6. Decision framework (GO/NO-GO/PIVOT)

### 6.1. Scoring rubric

Sau khi có 30 interviews + 200 survey responses, dùng rubric này:

| Criterion | Weight | Target | Actual | Score |
|-----------|--------|--------|--------|-------|
| % would-use (interview) | 25% | ≥ 50% | ... | ... |
| Waitlist signups | 15% | ≥ 200 | ... | ... |
| Ad conversion rate | 15% | ≥ 8% | ... | ... |
| Cost per email | 10% | ≤ $2 | ... | ... |
| % "willing to teach" ≥ 4/5 | 15% | ≥ 60% | ... | ... |
| Persona coverage (5 personas) | 10% | All 5 | ... | ... |
| Friend-referral ratio | 10% | ≥ 30% | ... | ... |

**Tổng điểm:**
- ≥ 80% → GO, proceed to Phase 1
- 50–79% → PIVOT, adjust positioning + retest 2 tuần
- < 50% → NO-GO, archive + lessons learned

### 6.2. Pivot decision tree

```
If "would-use" < 50%:
  ├─ Specific persona dragging it down?
  │    ├─ Yes → deprioritize that persona, focus on top 2-3
  │    └─ No  → problem is positioning or core concept
  │
  └─ Issue with concept (e.g., "money-less" feels weird)?
       ├─ Yes → test alternative (e.g., "low-cost" vs "free")
       └─ No  → problem with channel/audience (wrong people surveyed)

If conversion < 8%:
  ├─ Landing page copy weak? → A/B test 3 hero variants
  ├─ Audience mismatch? → narrow targeting
  └─ Offer unclear? → clarify "30 free seeds" + benefit
```

### 6.3. Validation report outline

```
SKILLSEED VALIDATION REPORT — [Date]

EXECUTIVE SUMMARY
  • GO/NO-GO recommendation
  • Top 3 insights
  • Top 3 risks

METHODOLOGY
  • Interview count, persona coverage
  • Survey response, source channels
  • Bias acknowledgment

PERSONA FINDINGS
  • One section per persona (5 sections)
  • Top quote per persona
  • Pain points + reactions

QUANTITATIVE RESULTS
  • Survey scores breakdown
  • Conversion funnel metrics
  • Ad performance by channel

KEY THEMES (cross-cutting)
  • 5 emerging patterns
  • Supporting evidence per pattern

RECOMMENDATION
  • Decision: GO / PIVOT / NO-GO
  • If GO: top 3 features for MVP
  • If PIVOT: what to change
  • Kill criteria for Phase 1.5 (next decision point)

APPENDIX
  • Interview transcripts (anonymized)
  • Survey raw data
  • Ad creative screenshots
```

---

**Hoàn thành Part 1 (Methodology + PMF interviews + surveys + synthesis + decision framework).**

**Tiếp theo là Part 2 (Usability tests + NPS + B2B discovery + Exit interviews + Diary studies). Tôi sẽ tạo trong file `user-research-script-2.md` để tách rõ. Bạn muốn tôi tiếp tục ngay không?**