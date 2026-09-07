# Wallet Expiring Soon

> **Mục đích:** Detail view cho seeds sắp hết hạn + quick actions.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Wallet]
        H2[⚠️ Expiring Seeds]
        H1 --- H2
    end

    subgraph ALERT[Header alert]
        A1[⚠️ You have 30 seeds expiring soon]
        A2[Use them before they expire on Sep 25 2026]
        A1 --- A2
    end

    subgraph EXPIRELIST[Expiring transactions]
        subgraph T1[Expires Sep 25 2026]
            X1[+30 seeds grant Welcome bonus]
            X2[Awarded: Sep 8 2026]
            X3[Expires in: 14 days]
            X4[[Use now → Book session]]
            X1 --- X2
            X2 --- X3
            X3 --- X4
        end

        subgraph T2[Expires Oct 12 2026]
            Y1[+60 seeds earned Mai Tran session]
            Y2[Awarded: Aug 12 2026]
            Y3[Expires in: 31 days]
            Y4[[Use now → Book session]]
            Y1 --- Y2
            Y2 --- Y3
            Y3 --- Y4
        end

        T1 --- T2
    end

    subgraph POLICY[Policy]
        P1[ⓘ Earned seeds expire 6 months after issue]
        P2[ⓘ Purchased Seed Pack seeds never expire]
        P3[ⓘ Refunded seeds follow original source policy]
        P1 --- P2
        P2 --- P3
    end

    subgraph SUGGEST[Quick actions]
        S1[[📚 Browse mentors and use seeds]]
        S2[[🎁 Gift seeds to a friend]]
        S3[[💼 Convert to B2B credits]]
        S4[[⚙️ Manage notifications for expiry]]
        S1 --- S2
        S2 --- S3
        S3 --- S4
    end

    HEADER ==> ALERT
    ALERT --> EXPIRELIST
    EXPIRELIST --> POLICY
    POLICY --> SUGGEST

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class X4,Y4,S1,S2,S3,S4 primary
    class A1 warning
```

**Expiry job:** Cron 01:00 UTC daily check `expires_at < now()`.
**Notification:** Email + push 14, 7, 1 day trước expiry.
