# B2B Internal Matching

> **Mục đích:** Tìm đồng nghiệp trong cùng org có kỹ năng cần học.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← ACME Corp]
        H2[🔍 Internal Match]
        H1 --- H2
    end

    subgraph SEARCH[Search bar]
        S1[🔍 Search colleagues by skill or name]
        S2[Filter: Department ▼  Level ▼  Available now]
        S1 --- S2
    end

    subgraph AI[AI match suggestions]
        AI1[🤖 Based on your goals:]
        AI2[Want to learn System Design?]
        AI3[Top matches within ACME Corp:]
    end

    subgraph COLLEAGUES[Colleagues]
        subgraph C1[Colleague 1]
            AV1[(👤)] --> N1[An Nguyen Senior Engineer]
            N1 --> SK1[System Design, Microservices, AWS]
            N1 --> R1[⭐ 4.8 12 sessions given]
            N1 --> AV1B[Available: Mon-Fri 6-8 PM]
            N1 --> AC1[[Book 1-1 →]]
        end

        subgraph C2[Colleague 2]
            AV2[(👤)] --> N2[Duc Mai Engineering Manager]
            N2 --> SK2[System Design, Leadership, Strategy]
            N2 --> R2[⭐ 4.9 28 sessions given]
            N2 --> AV2B[Available: Tue-Thu 7-9 PM]
            N2 --> AC2[[Book 1-1 →]]
        end

        subgraph C3[Colleague 3]
            AV3[(👤)] --> N3[Linh Tran Staff Engineer]
            N3 --> SK3[Distributed Systems, Microservices]
            N3 --> R3[⭐ 4.7 19 sessions given]
            N3 --> AV3B[Available: Wed 12-1 PM]
            N3 --> AC3[[Book 1-1 →]]
        end

        C1 --- C2
        C2 --- C3
    end

    subgraph CTA[Book via 1-1]
        CT1[Uses internal Skill Seeds budget]
        CT2[Auto-logged to your team's hours]
        CT3[Reimbursement: ✓ company pays]
        CT1 --- CT2
        CT2 --- CT3
    end

    HEADER ==> SEARCH
    SEARCH --> AI
    AI --> COLLEAGUES
    COLLEAGUES ==> CTA

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class AC1,AC2,AC3 primary
```

**Privacy:** Internal matches respect SSO domain — không hiển thị external users.
**Budget:** Org set seed allocation per employee.
