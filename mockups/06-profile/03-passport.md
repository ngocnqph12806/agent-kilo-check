# Skill Passport Public Page

> **Mục đích:** Public verification page cho Skill Passport + SBT.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed Passport]
        H2[[Verify on PolygonScan →]]
        H1 --- H2
    end

    subgraph HERO[Passport Hero]
        BG[Background: gradient emerald-indigo]
        AV[(👤 Avatar)]
        N1[Mai Tran]
        N2[🇻🇳 Vietnam]
        N3[✓ Verified identity]
        N4[Member since Aug 2025]
        BG --- AV
        AV --- N1
        N1 --- N2
        N2 --- N3
        N3 --- N4
    end

    subgraph SKILL[Skill Mastery]
        S1[🎯 Public Speaking ⭐⭐⭐⭐⭐ Mastery 4.9]
        S2[24 sessions completed]
        S3[Last verified: 2 weeks ago]
        S1 --- S2
        S2 --- S3
    end

    subgraph SBT[SBT Details]
        BT1[Blockchain: Polygon Mainnet]
        BT2[Contract: 0x1234...5678]
        BT3[Token ID: 42]
        BT4[Block: 51234567]
        BT5[Minted: Sep 5 2026]
        BT1 --- BT2
        BT2 --- BT3
        BT3 --- BT4
        BT4 --- BT5
    end

    subgraph RECOG[Recognized by]
        R1[✓ ACME Corp]
        R2[✓ Globex Inc]
        R3[✓ 12 partner companies]
        R1 --- R2
        R2 --- R3
    end

    subgraph SHARE[Share]
        SH1[📋 Copy link]
        SH2[🔗 Share on LinkedIn]
        SH3[🐦 Share on Twitter]
        SH4[📥 Download PDF]
        SH1 --- SH2
        SH2 --- SH3
        SH3 --- SH4
    end

    subgraph VERIFY[Verify button]
        V1[[✓ Verify this passport]]
        V2[Anyone can verify authenticity]
        V1 --- V2
    end

    HEADER ==> HERO
    HERO --> SKILL
    SKILL --> SBT
    SBT --> RECOG
    RECOG --> SHARE
    SHARE --> VERIFY

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class SH1,SH2,SH4,V1 primary
```

**SEO:** Public page có Open Graph image đẹp khi share trên LinkedIn.
**Verify:** Anyone (employer, HR tech) có thể verify authenticity.
