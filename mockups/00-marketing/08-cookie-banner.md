# Cookie Consent Banner

> **Mục đích:** GDPR/PDPA compliant cookie consent.

```mermaid
flowchart TB
    subgraph APP[App Background]
        A[Any page]
    end

    subgraph BANNER[Bottom banner]
        H1[🍪 We value your privacy]
        H2[We use cookies to enhance your experience.<br/>Analytics help us improve SkillSeed.]
        H3[Customize settings ▼]
        H1 --- H2
        H2 --- H3
    end

    subgraph ACTIONS[Actions]
        AC1[[Decline non-essential]]
        AC2[[Accept all]]
        AC3[[Save preferences]]
        AC1 --- AC2
        AC2 --- AC3
    end

    A --> BANNER
    BANNER --> ACTIONS

    subgraph CUSTOMIZE[Customize modal opens]
        C1[Essential cookies ⚠️ required always]
        C2[[✓] Strictly necessary auth session CSRF]
        C3[[✓] Functional remember language preference]
        C4[[✓] Analytics PostHog help us improve]
        C5[[✓] Marketing Meta Pixel retargeting]
        C6[[✓] Personalization AI matching quality]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
        C5 --- C6
        C7[[Save preferences]]
        C6 --- C7
    end

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class AC2,AC3,C7 primary
```

**GDPR compliant:** Granular consent, easy to withdraw.
**Persistence:** Choice lưu vào cookie/localStorage 12 tháng.
