# Marketplace — Verified Expert List

> **Mục đích:** Browse verified experts selling premium sessions USD.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Discover]
        H2[🌟 Verified Experts]
        H1 --- H2
    end

    subgraph FILTER[Filter Bar]
        F1[Skill ▼]
        F2[Country ▼]
        F3[Price range $20-$200]
        F4[Rating 4.5+ ▼]
        F5[Sort: Top rated ▼]
        F1 --- F2
        F2 --- F3
        F3 --- F4
        F4 --- F5
    end

    subgraph EXPERTLIST[Expert Cards]
        subgraph E1[Expert Card 1]
            AV1[Avatar]
            N1[Lisa Wong Verified Expert 🌟]
            N2[Teaches: Python, System Design, ML]
            N3[💰 $80/hour • 200 seeds]
            N4[⭐ 4.9 156 sessions • 🏆 Forest+]
            N5[🇸🇬 Singapore • EN, ZH]
            AV1 --- N1
            N1 --- N2
            N2 --- N3
            N3 --- N4
            N4 --- N5
            CTA1[[Book premium session →]]
        end

        subgraph E2[Expert Card 2]
            AV2[Avatar]
            M1[Carlos Verified Expert 🌟]
            M2[Teaches: Spanish, Latin Business]
            M3[💰 $50/hour • 125 seeds]
            M4[⭐ 4.8 89 sessions • 🌳 Tree+]
            M5[🇲🇽 Mexico • EN, ES]
            AV2 --- M1
            M1 --- M2
            M2 --- M3
            M3 --- M4
            M4 --- M5
            CTA2[[Book premium session →]]
        end

        E1 --- E2
    end

    subgraph PAGINATION[Pagination]
        P1[← Prev  1 2 3 ... 12  Next →]
    end

    HEADER ==> FILTER
    FILTER ==> EXPERTLIST
    EXPERTLIST --> PAGINATION

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef expert fill:#FEF3C7,stroke:#F59E0B,color:#92400E
    class CTA1,CTA2 primary
    class E1,E2 expert
```

**Verified Expert badge:** Tier 4 (Forest+) + identity verified + Stripe Connect.
**Commission:** Platform takes 15% qua Stripe Connect.
