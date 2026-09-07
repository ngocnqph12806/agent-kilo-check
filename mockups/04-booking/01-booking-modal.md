# Booking Modal

> **Mục đích:** Confirm trước khi tạo booking — chọn skill, date, time, duration.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[× Book a session with Mai Tran]
    end

    subgraph SKILL[Skill]
        SK[┌──────────────────────────────┐<br/>│ Public Speaking       ▼       │<br/>└──────────────────────────────┘]
    end

    subgraph DATE[Date]
        D[┌──────────────────────────────┐<br/>│ 📅 Thursday, Sep 11         │<br/>└──────────────────────────────┘]
    end

    subgraph TIMESLOT[Time slot]
        T1[● 18:00 - 18:30 30 min, 30 sd]
        T2[○ 18:30 - 19:00 30 min, 30 sd]
        T3[○ 19:00 - 20:00 60 min, 60 sd]
        T4[○ 20:00 - 21:00 60 min, 60 sd]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph DUR[Duration]
        D1[( ) 15 min]
        D2[( ) 30 min]
        D3[(●) 60 min]
        D1 --- D2
        D2 --- D3
    end

    subgraph MSG[Message to mentor optional]
        M[┌──────────────────────────────┐<br/>│ Hi Mai, I'd love to learn...  │<br/>└──────────────────────────────┘]
    end

    subgraph COST[Cost summary]
        C1[Cost: 60 seeds]
        C2[Your balance: 75 seeds ✓]
        C3[ⓘ Cancellation policy:<br/>• Cancel ≥ 24h before: 100% refund<br/>• Cancel < 24h before: 50% refund<br/>• No-show: 0% refund]
        C1 --- C2
        C2 --- C3
    end

    subgraph ACTION[Action]
        A1[Confirm booking 60 seeds →]
    end

    HEADER ==> SKILL
    SKILL --> DATE
    DATE --> TIMESLOT
    TIMESLOT --> DUR
    DUR --> MSG
    MSG --> COST
    COST --> ACTION

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef selected fill:#10B981,stroke:#047857,color:#fff
    class A1,T1,D3 primary
```

**Validation:** Phải có slot selected + user balance ≥ cost.
**Idempotency:** Sử dụng Idempotency-Key để chống double-submit.
