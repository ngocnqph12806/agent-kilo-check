# NGO Gifting Portal

> **Mục đích:** NGO tặng seeds cho user thu nhập thấp (corporate social responsibility).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Admin]
        H2[🎁 NGO Gifting Portal]
        H1 --- H2
    end

    subgraph INTRO[Intro]
        I1[🎁 Gift Seeds to learners in need]
        I2[Partner with SkillSeed to provide access<br/>to underprivileged learners globally.]
        I3[Track impact with detailed reporting.]
        I1 --- I2
        I2 --- I3
    end

    subgraph ORG[Your organization]
        O1[NGO Name: EduForAll Foundation]
        O2[Country: Singapore]
        O3[Tax ID: S74UF1234A]
        O4[Contact: partner@eduforall.org]
        O5[Status: ✓ Verified partner]
        O1 --- O2
        O2 --- O3
        O3 --- O4
        O4 --- O5
    end

    subgraph GIFT[Gift Seeds]
        GS1[Choose amount:]
        GS2[( ) 100 seeds = $5]
        GS3[(●) 500 seeds = $25]
        GS4[( ) 1000 seeds = $50]
        GS5[( ) Custom amount]
        GS6[Quantity: 50 packs]
        GS7[Total: $1,250]
        GS1 --- GS2
        GS2 --- GS3
        GS3 --- GS4
        GS4 --- GS5
        GS5 --- GS6
        GS6 --- GS7
    end

    subgraph RECIP[Recipients]
        R1[Distribution method:]
        R2[(●) Auto to verified low-income users]
        R3[( ) Email codes to specific list]
        R4[( ) Public redemption link]
        R5[Target: students in VN ID PH rural areas]
        R1 --- R2
        R2 --- R3
        R3 --- R4
        R4 --- R5
    end

    subgraph IMPACT[Impact reporting]
        IR1[📊 Sessions funded: 412]
        IR2[👥 Recipients reached: 287]
        IR3[🌍 Countries: 4]
        IR4[📜 Tax receipt: PDF available]
        IR1 --- IR2
        IR2 --- IR3
        IR3 --- IR4
    end

    subgraph PAY[Payment]
        P1[Payment: Wire transfer ✓ received Sep 1]
        P2[Receipt #NGO-2026-042]
        P1 --- P2
        P3[[Generate new gift batch →]]
    end

    HEADER ==> INTRO
    INTRO --> ORG
    ORG --> GIFT
    GIFT --> RECIP
    RECIP --> IMPACT
    IMPACT --> PAY

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class P3 primary
```

**Compliance:** KYC NGO + tax receipt + impact report.
**Distribution:** Auto credit hoặc email code.
