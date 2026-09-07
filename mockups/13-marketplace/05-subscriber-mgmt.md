# Subscriber Management (Creator)

> **Mục đích:** Expert xem danh sách subscribers + manage churn.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Creator Dashboard]
        H2[👥 Subscribers]
        H1 --- H2
    end

    subgraph METRICS[This month]
        M1[Active subscribers: 87]
        M2[New this month: 12]
        M3[Churned: 3]
        M4[MRR: $1,395]
        M5[Avg lifetime: 4.2 months]
        M1 --- M2
        M2 --- M3
        M3 --- M4
        M4 --- M5
    end

    subgraph SEGMENT[Filter]
        S1[[All 87]]
        S2[[Gold tier 18]]
        S3[[Silver 42]]
        S4[[Bronze 27]]
        S5[[Churned 15]]
        S1 --- S2
        S2 --- S3
        S3 --- S4
        S4 --- S5
    end

    subgraph TABLE[Subscribers table]
        subgraph ROW1[Subscriber 1]
            AV1[(👤)] --> N1[John D. Gold $50/mo]
            N1 --> ST1[✓ Active since Mar 2025]
            ST1 --> AC1[[Message] [Pause] [Cancel]]
        end

        subgraph ROW2[Subscriber 2]
            AV2[(👤)] --> N2[Lisa Wong Silver $15/mo]
            N2 --> ST2[✓ Active since Jun 2025]
            ST2 --> AC2[[Message] [Pause] [Cancel]]
        end

        subgraph ROW3[Subscriber 3]
            AV3[(👤)] --> N3[An Pham Bronze $5/mo]
            N3 --> ST3[⚠️ Failed payment Sep 5]
            ST3 --> AC3[[Retry] [Contact] [Cancel]]
        end

        ROW1 --- ROW2
        ROW2 --- ROW3
    end

    subgraph ENGAGE[Engagement]
        E1[📊 Send monthly update]
        E2[📧 Bulk message]
        E3[🎁 Offer exclusive bonus]
        E1 --- E2
        E2 --- E3
    end

    HEADER ==> METRICS
    METRICS --> SEGMENT
    SEGMENT --> TABLE
    TABLE --> ENGAGE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class AC1,AC2,E1,E2,E3 primary
    class ST3 warning
```

**Failed payment:** Stripe Smart Retries + email reminder.
**Churn analysis:** Track reasons to improve retention.
