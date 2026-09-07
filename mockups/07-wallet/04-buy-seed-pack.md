# Buy Seed Pack

> **Mục đích:** User mua Seed Pack qua Stripe.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Wallet]
        H2[Buy Seed Pack]
        H1 --- H2
    end

    subgraph INTRO[Intro]
        I1[Need more seeds? Pick a pack:]
        I2[All packs never expire.]
        I1 --- I2
    end

    subgraph PACKS[3 Packs]
        subgraph P1[Starter]
            PP1[🌱 100 seeds]
            PP2[$4.9 USD]
            PP3[Best for 1-2 sessions]
            PP4[[Buy now]]
        end

        subgraph P2[Growth popular]
            PPP1[🌱 500 seeds]
            PPP2[$19.9 USD save 19%]
            PPP3[Best for monthly learner]
            PPP4[[Buy now] highlighted]
        end

        subgraph P3[Pro]
            PPPP1[🌱 1500 seeds]
            PPPP2[$49.9 USD save 32%]
            PPPP3[Best for power user]
            PPPP4[[Buy now]]
        end

        P1 --- P2
        P2 --- P3
    end

    subgraph CHECKOUT[Stripe checkout]
        C1[Card / Apple Pay / Google Pay]
        C2[Country: Vietnam ▼]
        C3[Tax 10%: $X.XX]
        C4[Total: $X.XX]
        C5[[Confirm purchase →]]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
    end

    subgraph SUBSCRIBE[Subscribe & save]
        S1[[✓ Enable monthly auto-recharge 10% off]]
        S2[500 seeds/mo at $17.9 cancel anytime]
        S1 --- S2
    end

    HEADER ==> INTRO
    INTRO --> PACKS
    PACKS --> CHECKOUT
    CHECKOUT --> SUBSCRIBE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef popular fill:#FEF3C7,stroke:#F59E0B,color:#92400E
    class PP4,PPPP4,C5 primary
    class P2,PPP4 popular
```

**Stripe one-time payment:** Seed Pack purchase = single transaction.
**Never expire:** Bỏ qua expiry rule cho purchased seeds (different from earned seeds).
