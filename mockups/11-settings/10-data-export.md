# Data Export (GDPR)

> **Mục đích:** User export toàn bộ data cá nhân theo GDPR Right to Access.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Privacy]
        H2[📦 Export My Data]
        H1 --- H2
    end

    subgraph INTRO[Intro]
        I1[Download all your SkillSeed data]
        I2[Per GDPR Article 15 + Article 20 Right to Data Portability]
        I3[We'll prepare a ZIP file with all your data<br/>in JSON format. Usually ready within 1 hour.]
        I1 --- I2
        I2 --- I3
    end

    subgraph WHAT[What's included]
        W1[✓ Account info — email phone name]
        W2[✓ Profile + Skill DNA]
        W3[✓ Availability + preferences]
        W4[✓ All bookings + sessions history]
        W5[✓ All ratings given and received]
        W6[✓ Wallet transactions]
        W7[✓ Chat messages + forum posts]
        W8[✓ Notifications history]
        W9[✓ Skill Passports + SBT metadata]
        W10[✗ Other users' data not yours]
        W1 --- W2
        W2 --- W3
        W3 --- W4
        W4 --- W5
        W5 --- W6
        W6 --- W7
        W7 --- W8
        W8 --- W9
        W9 --- W10
    end

    subgraph FORMAT[Format options]
        F1[(●) ZIP JSON machine-readable]
        F2[( ) PDF human-readable]
        F3[( ) Both]
        F1 --- F2
        F2 --- F3
    end

    subgraph WHERE[Where to send]
        WH1[(●) Download link via email]
        WH2[Email: mai@acme.vn]
        WH3[Link expires in 7 days]
        WH1 --- WH2
        WH2 --- WH3

        WH4[( ) Encrypted USB drive — $25]
    end

    subgraph SUBMIT[Submit]
        SU1[[Request data export]]
        SU2[We'll email when ready]
        SU1 --- SU2
    end

    HEADER ==> INTRO
    INTRO --> WHAT
    WHAT --> FORMAT
    FORMAT --> WHERE
    WHERE --> SUBMIT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class SU1 primary
```

**GDPR compliance:** Free export, no friction.
**Encryption:** ZIP password-protected for sensitive data.
