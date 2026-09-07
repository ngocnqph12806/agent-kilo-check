# Expert Profile (Premium)

> **Mục đích:** Detailed expert profile với pricing + booking CTA.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Experts]
    end

    subgraph HERO[Expert Hero]
        AV[Avatar]
        N1[Lisa Wong]
        N2[🌟 Verified Expert • 🏆 Forest+ Tier 4]
        N3[⭐ 4.9 156 sessions]
        N4[🇸🇬 Singapore • EN, ZH]
        AV --- N1
        N1 --- N2
        N2 --- N3
        N3 --- N4
    end

    subgraph BIO[Bio]
        B1["Senior ML Engineer at Google. 10 years experience<br/>teaching Python, system design, and ML.<br/>Passionate about helping career switchers."]
    end

    subgraph SKILLS[Skills + Pricing]
        S1[💻 Python Programming — $60/hour or 150 seeds]
        S2[🏗️ System Design — $100/hour or 250 seeds]
        S3[🤖 Machine Learning — $80/hour or 200 seeds]
        S4[📊 Data Engineering — $70/hour or 175 seeds]
        S1 --- S2
        S2 --- S3
        S3 --- S4
    end

    subgraph CREDS[Credentials]
        C1[✓ Identity verified via Persona]
        C2[✓ Background check passed]
        C3[✓ Stripe Connect verified]
        C4[📜 Google ML Certificate 2018]
        C5[📜 AWS Solutions Architect 2020]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
    end

    subgraph AVAIL[Availability]
        A1[This week: 8 slots available]
        A2[[View calendar →]]
        A1 --- A2
    end

    subgraph REVIEWS[Reviews]
        R1[⭐⭐⭐⭐⭐ "Lisa helped me ace my system design<br/>interview at Meta. Highly recommend!" — John D., 1w]
        R2[⭐⭐⭐⭐⭐ "Clear, patient, structured teaching."<br/>— Maria S., 2w]
        R1 --- R2
        RM[View all 156 reviews →]
    end

    subgraph PASSPORT[Skill Passport]
        P1[✓ Python • ✓ System Design • ✓ ML<br/>View all passports →]
    end

    subgraph CTA[Book CTA]
        CT1[[Book premium session →]]
        CT2[Pay with Stripe card / Apple Pay / Google Pay<br/>or 60-250 seeds]
        CT1 --- CT2
    end

    HEADER ==> HERO
    HERO --> BIO
    BIO --> SKILLS
    SKILLS --> CREDS
    CREDS --> AVAIL
    AVAIL --> REVIEWS
    REVIEWS --> PASSPORT
    PASSPORT ==> CTA

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class CT1 primary
```

**Payment flow:** USD qua Stripe Connect (platform fee 15%) hoặc dùng Seeds.
**Tax:** Stripe Tax auto-calculate per country.
