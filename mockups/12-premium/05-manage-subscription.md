# Subscription Management

> **Mục đích:** User xem + quản lý subscription (upgrade/downgrade/cancel).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[Subscription]
        H1 --- H2
    end

    subgraph CURRENT[Current Plan]
        CP1[🌱 Premium]
        CP2[$9.9/month billed monthly]
        CP3[Next billing: Oct 7, 2026]
        CP4[Payment: Visa **** 4242]
        CP1 --- CP2
        CP2 --- CP3
        CP3 --- CP4
    end

    subgraph USAGE[Usage this month]
        U1[Bookings: 12 / Unlimited]
        U2[AI Co-Pilot sessions: 8]
        U3[AI insights generated: 3]
        U1 --- U2
        U2 --- U3
    end

    subgraph ACTIONS[Manage]
        A1[[Upgrade to Family $14.9/mo]]
        A2[[Change payment method]]
        A3[[View invoices]]
        A4[[Cancel subscription]]
        A1 --- A2
        A2 --- A3
        A3 --- A4
    end

    subgraph CANCELWARN[Cancel warning modal]
        CW1[⚠️ Are you sure?]
        CW2[You'll lose access to:]
        CW3[• Unlimited booking → back to 10/mo]
        CW4[• AI Co-Pilot unlimited]
        CW5[• Priority matching]
        CW6[Benefits remain until Oct 7 2026]
        CW7[[Keep subscription]] --- CW8[[Cancel anyway]]
        CW1 --- CW2
        CW2 --- CW3
        CW3 --- CW4
        CW4 --- CW5
        CW5 --- CW6
        CW6 --- CW7
    end

    HEADER ==> CURRENT
    CURRENT --> USAGE
    USAGE --> ACTIONS
    ACTIONS --> CANCELWARN

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class A1,A2 primary
    class A4,CW8 danger
```

**Stripe Customer Portal:** Link tới portal.stripe.com cho advanced management.
**Refund:** Pro-rated refund nếu cancel trong 14 ngày đầu.
