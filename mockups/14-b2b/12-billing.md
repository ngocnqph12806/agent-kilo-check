# B2B Billing

> **Mục đích:** Org quản lý subscription + invoices + payment.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← ACME Corp Settings]
        H2[💳 Billing]
        H1 --- H2
    end

    subgraph PLAN[Current plan]
        PL1[🎓 ACME Corp Business Plan]
        PL2[$99/user/year]
        PL3[Active members: 78/120]
        PL4[Annual cost: $7,722]
        PL5[Next invoice: Dec 1 2026]
        PL6[Status: ✓ Active]
        PL1 --- PL2
        PL2 --- PL3
        PL3 --- PL4
        PL4 --- PL5
        PL5 --- PL6
    end

    subgraph PAYMENT[Payment method]
        PM1[💳 Corporate Visa **** 4242]
        PM2[Expires: 12/2027]
        PM3[[Update payment method]]
        PM4[[Add backup card]]
        PM1 --- PM2
        PM2 --- PM3
        PM3 --- PM4
    end

    subgraph SEATS[Seat management]
        SM1[Active seats: 78/120]
        SM2[Unused seats: 42]
        SM3[[+ Add 10 seats $990/yr]]
        SM4[[- Reduce seats]]
        SM1 --- SM2
        SM2 --- SM3
        SM3 --- SM4
    end

    subgraph INVOICES[Invoices]
        I1[📄 INV-2026-09 — Sep 1 $7,722 — Paid]
        I2[📄 INV-2025-09 — Sep 1 2025 $7,722 — Paid]
        I3[[📋 Download all invoices CSV]]
        I1 --- I2
        I2 --- I3
    end

    subgraph TAX[Tax & compliance]
        T1[Tax ID: 12-3456789]
        T2[Billing email: billing@acme.com]
        T3[Billing address: 123 Main St...]
        T4[[Edit billing details]]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph CANCEL[Cancel subscription]
        C1[[⚠️ Cancel at end of period]]
        C2[Access remains until Nov 30 2026]
        C1 --- C2
    end

    HEADER ==> PLAN
    PLAN --> PAYMENT
    PAYMENT --> SEATS
    SEATS --> INVOICES
    INVOICES --> TAX
    TAX --> CANCEL

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class PM3,SM3,I3,T4 primary
    class C1 danger
```

**Stripe Billing:** Subscription via Stripe, proration khi thay đổi seats.
**PO accepted:** Enterprise customers có thể pay via invoice.
