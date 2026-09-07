# Session End — Post-call screen

> **Mục đích:** Hiển thị sau khi leave call: summary + rating CTA.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed]
    end

    subgraph DONE[Session Ended]
        D1[(✅)]
        D2[Your session with Mai Tran has ended.]
        D3[Duration: 60 min • 60 seeds released]
        D1 --- D2
        D2 --- D3
    end

    subgraph SUMMARY[AI Summary]
        S1[✨ Session Summary]
        S2[Topics covered:]
        T1[• Opening hook techniques]
        T2[• 3-second rule]
        T3[• Eye contact + body language]
        T4[Key takeaways:]
        T5[• Practice pause before speaking]
        T6[• Personal story > generic intro]
        T7[Action items:]
        T8[• Practice 3-min pitch by Friday]
        T9[• Record yourself and review]
        S1 --- S2
        S2 --- T1
        T1 --- T2
        T2 --- T3
        T3 --- T4
        T4 --- T5
        T5 --- T6
        T6 --- T7
        T7 --- T8
        T8 --- T9
    end

    subgraph RATING[Rate your experience]
        R1[How was your session?]
        R2[[⭐⭐⭐⭐⭐ Rate now]]
        R3[You'll auto-rate 5⭐ after 7 days]
        R1 --- R2
        R2 --- R3
    end

    subgraph ACTIONS[Next]
        A1[[Book again with Mai]]
        A2[[View booking detail]]
        A3[[Browse other mentors →]]
        A1 --- A2
        A2 --- A3
    end

    HEADER ==> DONE
    DONE --> SUMMARY
    SUMMARY --> RATING
    RATING --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class R2,A1 primary
```

**Trigger:** Sau khi Daily.co emit `meeting.ended` event.
**Seed release:** Teacher nhận 60 seeds ngay khi session complete.
