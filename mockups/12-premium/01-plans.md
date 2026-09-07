# Premium Plans

> **Mục đích:** Show 3 plans (Free / Premium / Family) với feature comparison.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed]
        H2[[Login]]
    end

    subgraph HERO[Hero]
        T1[Unlock more with Premium]
        T2[Unlimited booking • AI insights • Priority matching • No ads]
        T1 --- T2
    end

    subgraph PLANS[3 Plans side-by-side]
        subgraph FREE[Free $0/mo]
            F1[✓ 10 buổi/tháng với non-expert]
            F2[✓ 60 starter seeds]
            F3[✓ Basic AI matching]
            F4[✗ Unlimited booking]
            F5[✗ AI insights]
            F6[✗ Priority matching]
            F1 --- F2
            F2 --- F3
            F3 --- F4
            F4 --- F5
            F5 --- F6
            FB[Current plan]
        end

        subgraph PREMIUM[Premium $9.9/mo popular]
            P1[✓ Everything in Free]
            P2[✓ Unlimited booking]
            P3[✓ AI insights & roadmap]
            P4[✓ Priority matching]
            P5[✓ No ads]
            P6[✓ AI Co-Pilot unlimited]
            P1 --- P2
            P2 --- P3
            P3 --- P4
            P4 --- P5
            P5 --- P6
            PB[[Upgrade now →]]
        end

        subgraph FAMILY[Family $14.9/mo]
            FA1[✓ Everything in Premium]
            FA2[✓ 4 members]
            FA3[✓ Share seed pool]
            FA4[✓ Family dashboard]
            FA1 --- FA2
            FA2 --- FA3
            FA3 --- FA4
            FAB[Choose Family]
        end

        FREE --- PREMIUM
        PREMIUM --- FAMILY
    end

    subgraph PAYMENT[Payment methods]
        PM[Card • Apple Pay • Google Pay • MoMo • VNPay • PayNow • GCash]
    end

    subgraph FAQ[FAQ]
        Q1[▸ Can I cancel anytime?]
        Q2[▸ Refund policy?]
        Q3[▸ Family plan — how to add members?]
        Q1 --- Q2
        Q2 --- Q3
    end

    HEADER ==> HERO
    HERO ==> PLANS
    PLANS --> PAYMENT
    PAYMENT --> FAQ

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef popular fill:#FEF3C7,stroke:#F59E0B,color:#92400E
    class PB primary
    class PREMIUM popular
```

**Currency:** Display theo locale (USD/VND/SGD/IDR/PHP).
**PPP pricing:** ID $2.9, PH $3.9, SG $9.9, VN ~$10.
