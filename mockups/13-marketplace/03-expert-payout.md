# Expert Payout Dashboard

> **Mục đích:** Expert xem earnings + Stripe payout schedule.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Marketplace]
        H2[💰 Payout Dashboard]
        H1 --- H2
    end

    subgraph SUMMARY[This month]
        S1[💵 Gross revenue: $1,240]
        S2[Platform fee 15%: -$186]
        S3[Stripe fee ~3%: -$37]
        S4[Net earnings: $1,017]
        S5[Pending payout: $1,017]
        S6[Next payout: Sep 15 to Visa **** 4242]
        S1 --- S2
        S2 --- S3
        S3 --- S4
        S4 --- S5
        S5 --- S6
    end

    subgraph CHART[Revenue over 6 months]
        CH1[┌──────────────────────────────────────┐<br/>│         ▁▂▃▅▇█▇▆▅              │<br/>│  Apr May Jun Jul Aug Sep            │<br/>│  $0.8K $1.0K $1.1K $1.2K $1.3K $1.2K │<br/>└──────────────────────────────────────┘]
    end

    subgraph RECENT[Recent transactions]
        R1[Sep 10: 1-1 session with John D $80]
        R2[Sep 08: 1-1 session with Maria S $80]
        R3[Sep 05: Premium subscriber: Sarah $15]
        R4[Sep 03: 1-1 session with Mike T $80]
        R5[Sep 01: Subscription renewal: 12 users $180]
        R1 --- R2
        R2 --- R3
        R3 --- R4
        R4 --- R5
    end

    subgraph ACTIONS[Actions]
        A1[[📊 Export earnings PDF]]
        A2[[⚙️ Update payout method]]
        A3[[📄 View tax forms 1099]]
        A1 --- A2
        A2 --- A3
    end

    HEADER ==> SUMMARY
    SUMMARY --> CHART
    CHART --> RECENT
    RECENT --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class A1,A2 primary
```

**Stripe Connect Express:** Required for marketplace payouts.
**Tax compliance:** US 1099 forms for experts earning > $600/year.
