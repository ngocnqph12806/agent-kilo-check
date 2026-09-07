# Checkout / Stripe

> **Mục đích:** Stripe Checkout session cho Premium subscription.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Back]
        H2[🔒 Secure Checkout]
        H1 --- H2
    end

    subgraph SUMMARY[Order Summary]
        OS1[🌱 SkillSeed Premium]
        OS2[$9.9/month]
        OS3[Billed monthly. Cancel anytime.]
        OS4[Subtotal: $9.90]
        OS5[Tax 10%: $0.99]
        OS6[Total today: $10.89]
        OS7[Next charge: Oct 7, 2026]
        OS1 --- OS2
        OS2 --- OS3
        OS3 --- OS4
        OS4 --- OS5
        OS5 --- OS6
        OS6 --- OS7
    end

    subgraph PAYMENT[Payment Method]
        PM1[○ Card]
        PM2[○ Apple Pay]
        PM3[○ Google Pay]
        PM4[○ MoMo]
        PM5[○ VNPay]
        PM6[○ PayNow]
        PM7[○ GCash]
        PM1 --- PM2
        PM2 --- PM3
        PM3 --- PM4
        PM4 --- PM5
        PM5 --- PM6
        PM6 --- PM7
    end

    subgraph CARD[Card Details]
        CD1[Card number]
        CD2[MM/YY]
        CD3[CVC]
        CD4[ZIP/Postal code]
        CD5[Country ▼]
        CD1 --- CD2
        CD2 --- CD3
        CD3 --- CD4
        CD4 --- CD5
    end

    subgraph BILLING[Billing Address]
        BA1[Full name]
        BA2[Address line 1]
        BA3[City]
        BA4[Country ▼]
        BA1 --- BA2
        BA2 --- BA3
        BA3 --- BA4
    end

    subgraph CONSENT[Consent]
        CN1[[✓] I agree to Terms of Service]
        CN2[[✓] I agree to Privacy Policy]
        CN1 --- CN2
    end

    subgraph SUBMIT[Submit]
        SU1[[Subscribe now $10.89 →]]
        SU2[🔒 Powered by Stripe]
        SU1 --- SU2
    end

    HEADER ==> SUMMARY
    SUMMARY --> PAYMENT
    PAYMENT --> CARD
    CARD --> BILLING
    BILLING --> CONSENT
    CONSENT --> SUBMIT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class SU1 primary
```

**Stripe Elements:** Embedded form với PCI compliance.
**Webhook:** `checkout.session.completed` → activate subscription.
**Error:** Inline validation cho card, declined, 3DS challenge.
