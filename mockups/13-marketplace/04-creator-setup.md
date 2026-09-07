# Creator Subscription Setup (US)

> **Mục đích:** Expert tạo subscription tiers cho fanbase.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Expert Dashboard]
        H2[Create Subscription Tiers]
        H1 --- H2
    end

    subgraph INTRO[Intro]
        I1[Monetize your expertise with subscription tiers]
        I2[Substack model for skills — fans pay monthly for exclusive content]
        I3[You keep 85% we keep 15% platform fee]
        I1 --- I2
        I2 --- I3
    end

    subgraph BRONZE[Bronze Tier]
        B1[Price: $5/month]
        B2[Benefits:]
        B3[• Monthly Q&A in group chat]
        B4[• 1 group session per month]
        B5[• Exclusive newsletter]
        B1 --- B2
        B2 --- B3
        B3 --- B4
        B4 --- B5
        BT[[Save]]
    end

    subgraph SILVER[Silver Tier popular]
        S1[Price: $15/month]
        S2[Benefits:]
        S3[• Everything in Bronze]
        S4[• 2 group sessions per month]
        S5[• Course materials + templates]
        S6[• Direct message priority]
        S1 --- S2
        S2 --- S3
        S3 --- S4
        S4 --- S5
        S5 --- S6
        ST[[Save]]
    end

    subgraph GOLD[Gold Tier]
        G1[Price: $50/month]
        G2[Benefits:]
        G3[• Everything in Silver]
        G4[• 1-1 monthly session]
        G5[• Early access to new content]
        G6[• Exclusive mastermind group]
        G1 --- G2
        G2 --- G3
        G3 --- G4
        G4 --- G5
        G5 --- G6
        GT[[Save]]
    end

    subgraph SETUP[Stripe Connect]
        SC1[Status: ✓ Verified]
        SC2[Express account: ac_xxx]
        SC3[Payout: Weekly to Visa **** 4242]
        SC1 --- SC2
        SC2 --- SC3
    end

    HEADER ==> INTRO
    INTRO --> BRONZE
    BRONZE --> SILVER
    SILVER --> GOLD
    GOLD --> SETUP

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef popular fill:#FEF3C7,stroke:#F59E0B,color:#92400E
    class BT,ST,GT primary
    class SILVER popular
```

**Stripe Billing:** Recurring subscriptions qua Stripe Connect.
**Auto-renewal:** Monthly, cancel anytime by subscriber.
