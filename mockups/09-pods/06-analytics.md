# Pod Analytics (Creator Dashboard)

> **Mục đích:** Pod creator xem engagement metrics + member activity.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Public Speaking Club VN]
        H2[📊 Pod Analytics]
        H1 --- H2
    end

    subgraph METRICS[This month]
        M1[Active members: 6/8 75%]
        M2[Sessions hosted: 4]
        M3[Avg attendance: 5/6 per session]
        M4[Forum posts: 23]
        M5[Engagement rate: 78%]
        M1 --- M2
        M2 --- M3
        M3 --- M4
        M4 --- M5
    end

    subgraph CHART[Sessions attendance trend]
        CH1[┌────────────────────────────────┐<br/>│  ▃▅▇▆▅▇▆▇▅▆              │<br/>│  Week 1-8                        │<br│└────────────────────────────────┘]
    end

    subgraph TOPMEM[Top contributors]
        T1[1. Mai Tran Creator<br/>   12 posts 4 sessions hosted]
        T2[2. Duc Nguyen<br/>   8 posts]
        T3[3. Linh Tran<br/>   5 posts]
        T4[4. An Pham<br/>   3 posts]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph AT-RISK[At-risk members 2]
        A1[⚠️ An Pham — no activity 21 days]
        A2[⚠️ Hung Nguyen — never attended]
        A3[[Send nudge →]]
        A1 --- A2
        A2 --- A3
    end

    subgraph ACTIONS[Actions]
        AC1[[📅 Schedule next session]]
        AC2[[📢 Post announcement]]
        AC3[[✏️ Edit Pod info]]
        AC4[[⚙️ Manage members]]
        AC1 --- AC2
        AC2 --- AC3
        AC3 --- AC4
    end

    HEADER ==> METRICS
    METRICS --> CHART
    CHART --> TOPMEM
    TOPMEM --> AT-RISK
    AT-RISK --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class A3,AC1,AC2,AC4 primary
    class A1,A2 warning
```

**Permissions:** Chỉ Creator + Moderator mới thấy.
**Retention insights:** Predict churn risk dựa trên activity decline.
