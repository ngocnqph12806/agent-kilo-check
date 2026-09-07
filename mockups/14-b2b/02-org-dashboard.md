# B2B Org Dashboard

> **Mục đích:** HR Manager xem overview org: MAU, sessions, skills gap.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🏢 ACME Corp]
        H2[Mai Tran HR Manager]
        H3[[Logout]]
        H1 --- H2
        H2 --- H3
    end

    subgraph TABS[Sidebar]
        TB1[[📊 Overview] active]
        TB2[[👥 Members]]
        TB3[[🎓 Skills]]
        TB4[[📈 Reports]]
        TB5[[⚙ Settings]]
        TB1 --- TB2
        TB2 --- TB3
        TB3 --- TB4
        TB4 --- TB5
    end

    subgraph METRICS[This month]
        M1[Active members: 78/120 65%]
        M2[Sessions completed: 142]
        M3[Skills exchanged: 24 unique]
        M4[Avg rating: 4.6 ⭐]
        M1 --- M2
        M2 --- M3
        M3 --- M4
    end

    subgraph HEATMAP[Skills gap heatmap]
        HM[         Eng  Design  Product  Sales  Ops<br/>Junior  🟢    🟡     🟡       🟢    🟢<br/>Mid     🟢    🟢     🔴       🟡    🟢<br/>Senior  🟢    🟢     🟢       🔴    🟡]
        LG[🟢 ≥5 mentors  🟡 1-4  🔴 gap]
        HM --- LG
    end

    subgraph TOPCONTRIB[Top contributors]
        T1[1. An Nguyen — 12 sessions taught]
        T2[2. Duc Mai — 10 sessions]
        T3[3. Linh Tran — 9 sessions]
        T4[+ see all 24 contributors]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph RECS[Recommended actions]
        R1[• 3 members haven't logged in 30 days → nudge]
        R2[• Product-Mid gap → consider external mentors]
        R3[• Onboard 5 new members from invite link]
        R1 --- R2
        R2 --- R3
    end

    subgraph EXPORT[Export]
        EX1[[Export PDF report]]
        EX2[[Schedule 1-1 with SkillSeed CSM]]
        EX1 --- EX2
    end

    HEADER ==> TABS
    TABS ==> METRICS
    METRICS --> HEATMAP
    HEATMAP --> TOPCONTRIB
    TOPCONTRIB --> RECS
    RECS ==> EXPORT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class EX1,EX2 primary
```

**Permission:** Chỉ ORG_ADMIN + ORG_OWNER mới truy cập.
**Real-time data:** Metrics refresh mỗi 5 phút.
