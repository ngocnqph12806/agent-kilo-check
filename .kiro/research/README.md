# SkillSeed — User Research

> **Bộ công cụ nghiên cứu người dùng hoàn chỉnh.**

## Files

- **`user-research-script.md`** — Part 1: Methodology + PMF interviews (Phase 0)
  - 6 research principles
  - Recruiting playbook (outreach templates + screening)
  - PMF interview script 30 phút (universal + 5 personas)
  - Survey 5 phút + A/B variants
  - Synthesis templates (affinity diagram, persona summary)
  - GO/NO-GO/PIVOT decision framework

- **`user-research-script-2.md`** — Part 2: Continuous discovery (Phase 1–5)
  - Moderated usability test (60-min protocol)
  - Unmoderated task tests
  - In-app beta feedback form
  - NPS & CSAT surveys
  - B2B customer discovery (30-min script)
  - Exit interview (churned users)
  - Diary study (longitudinal)
  - Operations (consent forms, synthesis board, cadence)

## Methods at a glance

| Method | When | Sample | Duration | Output |
|--------|------|--------|----------|--------|
| **PMF interview** | Phase 0 | 30 | 30 min each | GO/NO-GO decision |
| **Survey** | Phase 0 | 200+ | 5 min | Quantitative validation |
| **Moderated usability** | Phase 1+ | 5–8/round | 60 min each | UX fixes (Linear tickets) |
| **Unmoderated task test** | Phase 1+ | 20–50 | 5–10 min | Task success rate |
| **Beta feedback** | Phase 1+ | Continuous | 2 min | Categorized tickets |
| **NPS** | Phase 1+ | All active | 2 min/quarter | Quarterly NPS score |
| **CSAT** | Phase 1+ | Per session | 1 min | Per-feature satisfaction |
| **B2B discovery** | Phase 3 | 8–10 | 30 min | ICP + pricing validation |
| **Exit interview** | Phase 2+ | Churned | 15 min | Churn taxonomy + fixes |
| **Diary study** | Phase 2+ | 10–15 | 2–4 weeks | Habit formation insights |

## Research repository structure

```
.kiro/research/
├── README.md                            (file này)
├── user-research-script.md              (Part 1)
├── user-research-script-2.md            (Part 2)
├── transcripts/                         (Otter.ai raw outputs — tạo khi có data)
├── notes/                               (founder's handwritten notes)
├── synthesis/                           (affinity diagrams, themes)
├── recordings/                          (audio với consent)
├── consent-forms/                       (per-interviewee PDFs)
└── reports/                             (per-quarter insights report)
```

## Cadence by phase

| Phase | Cadence | Sample size per round |
|-------|---------|----------------------|
| Phase 0 (Validation) | Once, 4 weeks | 30 interviews + 200 surveys |
| Phase 1 (MVP) | Weekly | 5 usability tests |
| Phase 2 (AI/Polish) | Bi-weekly | 8 usability + 10 NPS |
| Phase 3 (Mobile/Scale) | Weekly | 5 usability + 20 NPS + 5 B2B |
| Phase 4 (Regional) | Monthly per region | 8 usability + 30 NPS + 5 B2B + 3 exit |
| Phase 5 (Beyond) | Quarterly | 15 advisory board + ongoing |

## Golden rules

1. **Talk to humans, not assumptions.** Mọi quyết định product phải có voice-of-customer.
2. **Small samples, deep insights.** 5–8 interview chất lượng hơn 50 survey superficial.
3. **No leading questions.** Hỏi về behavior (past tense), không hypothetical.
4. **Triangulate.** Kết hợp nhiều methods.
5. **Bias-aware.** Friend-network bias, recency bias, founder bias.
6. **Action-oriented.** Mỗi research phải dẫn đến quyết định product rõ ràng.

---

Xem chi tiết trong từng file `.md` ở trên.