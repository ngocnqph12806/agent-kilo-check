# B2B Reports

> **Mục đích:** Detailed analytics: learning hours, engagement, skills growth.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← ACME Corp]
        H2[📈 Reports]
        H1 --- H2
    end

    subgraph RANGE[Date Range]
        D1[Sep 1 - Sep 30 2026 ▼]
        C1[[Compare with: Aug 2026 ▼]]
        D1 --- C1
    end

    subgraph METRICS[Top Metrics]
        M1[Active members: 78 ↑ 12% vs last month]
        M2[Total sessions: 142 ↑ 23%]
        M3[Learning hours: 89.5 ↑ 18%]
        M4[Avg rating: 4.6 ⭐ = same]
        M5[Skills gained: 24 unique]
        M6[Member satisfaction NPS: 52 ↑ 8]
        M1 --- M2
        M2 --- M3
        M3 --- M4
        M4 --- M5
        M5 --- M6
    end

    subgraph CHART1[Sessions over time]
        CH1[┌──────────────────────────────────────┐<br/>│       ▁▂▃▄▅▆▇█▇▆▅▄▃▂▁             │<br/>│       Mon Tue Wed Thu Fri Sat Sun    │<br/>│                                       │<br/>│  Total sessions last 30 days: 142    │<br/>└──────────────────────────────────────┘]
    end

    subgraph CHART2[Top skills learned]
        SK1[1. Public Speaking ████████████ 32]
        SK2[2. Python        ██████████   26]
        SK3[3. UX Design     ███████      18]
        SK4[4. Data Science  █████        14]
        SK5[5. Spanish       ████         10]
        SK1 --- SK2
        SK2 --- SK3
        SK3 --- SK4
        SK4 --- SK5
    end

    subgraph EXPORT[Export]
        E1[[📊 Export PDF report]]
        E2[[📋 Export CSV raw data]]
        E3[[📧 Schedule monthly email report]]
        E1 --- E2
        E2 --- E3
    end

    HEADER ==> RANGE
    RANGE --> METRICS
    METRICS --> CHART1
    CHART1 --> CHART2
    CHART2 ==> EXPORT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class E1,E2,E3 primary
```

**Real-time:** ClickHouse-backed, refresh every 5 phút.
**Scheduled reports:** Monthly email digest cho leadership.
