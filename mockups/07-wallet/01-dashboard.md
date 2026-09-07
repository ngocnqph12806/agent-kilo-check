# My Wallet — Dashboard

> **Mục đích:** Xem balance, total earned/spent, mua Seed Pack.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Wallet]
    end

    subgraph BALANCE[Balance Card]
        B1[🌱 75 seeds]
        B2[≈ $7.50 USD value or 1 hour of teaching]
        B3[Total earned: 145 • Total spent: 70]
        B4[[Buy Seed Pack] [Get free seeds →]]
        B1 --- B2
        B2 --- B3
        B3 --- B4
    end

    subgraph EXPIRING[Expiring Soon Alert]
        E1[⚠️ 30 seeds expiring in 14 days Sep 25]
        E2[[Use them →]]
        E1 --- E2
    end

    subgraph HISTORY[Transaction history]
        F[Filter: [All ▼] [Earn] [Spend] [Expiring]]

        subgraph TX[Transactions]
            T1[Sep 11 ✓ Session completed with Mai Tran<br/>+60 seeds earned expires Mar 11 2027]
            T2[Sep 11 Session with Mai Tran<br/>-60 seeds spent]
            T3[Sep 10 ✗ Booking cancelled within 24h<br/>+30 seeds refund 50%]
            T4[Sep 08 Welcome bonus<br/>+30 seeds grant expires Mar 8 2027]
            T1 --- T2
            T2 --- T3
            T3 --- T4
        end

        LOAD[Load more]
        F --> TX
        TX --> LOAD
    end

    HEADER ==> BALANCE
    BALANCE --> EXPIRING
    EXPIRING --> HISTORY

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class B4,E2 primary
    class E1 warning
```

**Ledger pattern:** Mọi transaction lưu DB, balance = SUM.
**Cache:** Redis cache balance, invalidate khi có transaction mới.
**Expiry job:** Cron 01:00 UTC daily — tìm expires_at < now → tạo expire transaction.
