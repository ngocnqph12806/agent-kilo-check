# Wallet Transactions

> **Mục đích:** Lịch sử giao dịch với filter chi tiết.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Wallet]
        H2[Transactions]
        H1 --- H2
    end

    subgraph SUMMARY[Summary this month]
        S1[Balance: 75 seeds]
        S2[Earned: +90]
        S3[Spent: -60]
        S4[Expiring: 30 seeds in 14d]
        S1 --- S2
        S2 --- S3
        S3 --- S4
    end

    subgraph FILTER[Filter]
        F1[All ▼]
        F2[Date range ▼]
        F3[Type: earn spend expire grant refund]
        F4[[Export CSV]]
        F1 --- F2
        F2 --- F3
        F3 --- F4
    end

    subgraph TX[Transactions]
        subgraph T1[Sep 11]
            X1[18:45 ✓ Session completed with Mai Tran]
            X2[+60 seeds earned expires Mar 11 2027]
            X3[X1 --- X2]
        end

        subgraph T2[Sep 11]
            X4[18:45 Session with Mai Tran]
            X5[-60 seeds spent escrow release]
            X4 --- X5
        end

        subgraph T3[Sep 10]
            X6[20:15 ✗ Booking cancelled within 24h]
            X7[+30 seeds refund 50%]
            X6 --- X7
        end

        subgraph T4[Sep 08]
            X8[09:00 Welcome bonus]
            X9[+30 seeds grant expires Mar 8 2027]
            X8 --- X9
        end

        subgraph T5[Sep 05]
            X10[14:20 ⚠️ Expired seeds]
            X11[-20 seeds expire from old session]
            X10 --- X11
        end

        T1 --- T2
        T2 --- T3
        T3 --- T4
        T4 --- T5
    end

    subgraph PAGINATION[Pagination]
        P1[← Prev  1 2 3 ... 28  Next →]
    end

    HEADER ==> SUMMARY
    SUMMARY --> FILTER
    FILTER --> TX
    TX --> PAGINATION

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class F4 primary
    class X10 warning
```

**Source:** All from `seed_transactions` table (ledger).
**Immutable:** Không cho edit/delete transactions (audit trail).
