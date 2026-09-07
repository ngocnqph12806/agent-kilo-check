# Settings — Account

> **Mục đích:** Quản lý email, password, phone, verification.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[Account]
        H1 --- H2
    end

    subgraph EMAIL[Email]
        E1[Current: mai@acme.vn]
        E2[[Change email]]
        E3[Verified: ✓ Aug 15 2025]
        E1 --- E2
        E2 --- E3
    end

    subgraph PHONE[Phone]
        P1[Current: +84 xxx xxx 123]
        P2[[Verify new phone]]
        P3[Verified: ✓ Aug 20 2025]
        P1 --- P2
        P2 --- P3
    end

    subgraph PWD[Password]
        PW1[Last changed: 30 days ago]
        PW2[[Change password]]
        PW3[[Enable 2FA via authenticator app]]
        PW1 --- PW2
        PW2 --- PW3
    end

    subgraph VER[Verification Level]
        V1[Current level: 2 ID verified]
        V2[Level 3 — Selfie liveness [Upgrade →]]
        V3[Level 4 — Video interview [Forest tier only]]
        V1 --- V2
        V2 --- V3
    end

    subgraph DANGER[Danger Zone]
        D1[[Delete account permanently]]
        D2[This will erase all your data after 30 days grace period]
        D1 --- D2
    end

    HEADER ==> EMAIL
    EMAIL --> PHONE
    PHONE --> PWD
    PWD --> VER
    VER --> DANGER

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class E2,PW2,V2 primary
    class D1 danger
```

**GDPR:** Right to delete với 30-day grace period + soft delete.
