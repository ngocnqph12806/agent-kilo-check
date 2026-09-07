# Pricing Page (Public)

> **Mục đích:** Marketing pricing page cho Premium plans.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed]
        H2[[Login] [Get Started]]
        H1 --- H2
    end

    subgraph HERO[Hero]
        HE1[Plans for every learner and teacher]
        HE2[Start free upgrade when you need more]
        HE1 --- HE2
    end

    subgraph PLANS[4 Tiers]
        subgraph FREE[Free]
            F1[$0/mo]
            F2[10 sessions/mo non-expert]
            F3[60 starter seeds]
            F4[Basic matching]
            F5[[Current default]]
        end

        subgraph PREMIUM[Premium]
            P1[$9.9/mo]
            P2[Unlimited booking]
            P3[AI Co-Pilot unlimited]
            P4[Priority matching]
            P5[No ads]
            P6[[Upgrade →]]
        end

        subgraph FAMILY[Family]
            FA1[$14.9/mo]
            FA2[4 members]
            FA3[Shared seed pool]
            FA4[Family dashboard]
            FA5[[Choose Family →]]
        end

        subgraph ORG[B2B Org]
            O1[$99/user/year]
            O2[Unlimited company-wide]
            O3[Skill gap heatmap]
            O4[SSO + audit log]
            O5[[Contact sales →]]
        end

        FREE --- PREMIUM
        PREMIUM --- FAMILY
        FAMILY --- ORG
    end

    subgraph SEEDPACKS[Seed Packs]
        SP1[100 seeds $4.9]
        SP2[500 seeds $19.9 save 19%]
        SP3[1500 seeds $49.9 save 32%]
        SP1 --- SP2
        SP2 --- SP3
        SP4[[Buy seeds →]]
    end

    subgraph COMPARISON[Comparison link]
        CL[[See full feature comparison →]]
    end

    subgraph FAQ[FAQ]
        F1[▸ Can I cancel anytime?]
        F2[▸ Refund policy?]
        F3[▸ Family plan details?]
        F4[▸ B2B custom pricing?]
        F1 --- F2
        F2 --- F3
        F3 --- F4
    end

    HEADER ==> HERO
    HERO --> PLANS
    PLANS --> SEEDPACKS
    SEEDPACKS --> COMPARISON
    COMPARISON --> FAQ

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class P6,FA5,O1 primary
    class SP4,CL primary
```

**PPP pricing:** Adjust theo region (ID $2.9, PH $3.9, etc).
