# Marketplace — Expert Profile (Premium Booking)

> **Mục đích:** User xem premium expert profile + book session qua USD.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Marketplace]
        H2[Lisa Wong]
        H1 --- H2
    end

    subgraph HERO[Hero]
        AV[(👤)]
        N1[Lisa Wong]
        N2[🌟 Verified Expert • 🏆 Forest+]
        N3[Senior ML Engineer @ Google]
        N4[⭐ 4.9 156 sessions • 95% rebook rate]
        N5[🇸🇬 Singapore • EN ZH]
        AV --- N1
        N1 --- N2
        N2 --- N3
        N3 --- N4
        N4 --- N5
    end

    subgraph SKILLS[Skills + Pricing]
        S1[💻 Python Programming<br/>$60/hour or 150 seeds<br/>⏱ 60 min session]
        S2[🏗️ System Design Interview Prep<br/>$100/hour or 250 seeds<br/>⏱ 90 min recommended]
        S3[🤖 Machine Learning 1-1<br/>$80/hour or 200 seeds<br/>⏱ 60 min]
        S1 --- S2
        S2 --- S3
        S4[Booked 12 times this week 🔥]
    end

    subgraph CREDS[Credentials]
        C1[✓ Identity verified Persona]
        C2[✓ Background check passed]
        C3[✓ Stripe Connect verified]
        C4[📜 Google ML Certificate 2018]
        C5[📜 AWS Solutions Architect 2020]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
    end

    subgraph AVAIL[Availability next 7 days]
        A1[Today: 3 slots available]
        A2[Tomorrow: 5 slots]
        A3[This week: 18 slots]
        A4[[View calendar →]]
        A1 --- A2
        A2 --- A3
        A3 --- A4
    end

    subgraph REVIEWS[Reviews 156]
        R1[⭐⭐⭐⭐⭐ "Lisa helped me ace system<br/>design interview at Meta!" — John D., 1w]
        R2[⭐⭐⭐⭐⭐ "Patient, clear, structured."<br/>— Maria S., 2w]
        R3[[View all 156 reviews →]]
        R1 --- R2
        R2 --- R3
    end

    subgraph CTA[Book CTA sticky right]
        CT1[Ready to learn?]
        CT2[Skill: System Design ▼]
        CT3[Date: Today ▼]
        CT4[Duration: 90 min ▼]
        CT5[Price: $150 USD or 375 seeds]
        CT6[[Book premium session →]]
        CT7[💳 Pay with Stripe]
        CT8[or 🌱 Pay with Seeds]
        CT1 --- CT2
        CT2 --- CT3
        CT3 --- CT4
        CT4 --- CT5
        CT5 --- CT6
        CT6 --- CT7
        CT7 --- CT8
    end

    HEADER ==> HERO
    HERO --> SKILLS
    SKILLS --> CREDS
    CREDS --> AVAIL
    AVAIL --> REVIEWS
    REVIEWS ==> CTA

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef expert fill:#FEF3C7,stroke:#F59E0B,color:#92400E
    class CT6,CT7,CT8 primary
    class HERO expert
```

**Payment flow:** Stripe Checkout (USD) hoặc dùng Seeds từ wallet.
**Tax:** Stripe Tax tự động tính GST/VAT theo country.
