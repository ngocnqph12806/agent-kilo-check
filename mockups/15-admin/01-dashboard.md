# Admin Dashboard

> **Mục đích:** Admin monitor health metrics + handle moderation.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed Admin]
        H2[[Logout]]
        H1 --- H2
    end

    subgraph SIDEBAR[Sidebar]
        SB1[[📊 Dashboard] active]
        SB2[[👥 Users]]
        SB3[[📅 Bookings]]
        SB4[[⭐ Ratings]]
        SB5[[🎓 Skills]]
        SB6[[🚩 Reports]]
        SB7[[⚙ Settings]]
        SB1 --- SB2
        SB2 --- SB3
        SB3 --- SB4
        SB4 --- SB5
        SB5 --- SB6
        SB6 --- SB7
    end

    subgraph TODAY[Today]
        T1[DAU: 1,234]
        T2[New users: 87]
        T3[Sessions: 78]
        T4[Errors: 2]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph WEEK[This week]
        W1[Sessions: 567]
        W2[New bookings: 412]
        W3[Revenue: $890]
        W4[Churn: 3.2%]
        W1 --- W2
        W2 --- W3
        W3 --- W4
    end

    subgraph CHART[Sessions over time]
        CH[┌────────────────────────────────────┐<br/>│  ▁▂▃▄▅▆▇█▇▆▅▄▃▂▁               │<br/>│  Mon Tue Wed Thu Fri Sat Sun      │<br/>└────────────────────────────────────┘]
    end

    subgraph TOPSKILLS[Top skills this week]
        SK1[1. Public Speaking ████████████ 124]
        SK2[2. Python         ██████████   98]
        SK3[3. UX Research    ███████      67]
        SK4[4. Yoga           ████         34]
        SK1 --- SK2
        SK2 --- SK3
        SK3 --- SK4
    end

    subgraph ANOMALIES[Anomalies 3]
        A1[⚠️ User abc123 booked 12 sessions/1h → flagged]
        A2[⚠️ Rating ring detected: 2 users mutually 5⭐]
        A3[⚠️ 5 custom skills pending review]
        A1 --- A2
        A2 --- A3
        AR1[[Review queue →]]
    end

    HEADER ==> SIDEBAR
    SIDEBAR ==> TODAY
    TODAY --> WEEK
    WEEK --> CHART
    CHART --> TOPSKILLS
    TOPSKILLS --> ANOMALIES

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class AR1 primary
    class A1,A2,A3 warning
```

**Role:** SUPER_ADMIN only.
**Real-time alerts:** Slack/email khi error rate > 5% hoặc anomaly detected.
