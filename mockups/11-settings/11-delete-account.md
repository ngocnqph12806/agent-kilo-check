# Delete Account (GDPR)

> **Mục đích:** Right to be forgotten — confirm + 30-day grace period.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[⚠️ Delete Account]
        H1 --- H2
    end

    subgraph WARNING[Warning]
        W1[⚠️ This action is permanent and cannot be undone.]
        W2[After 30 days, we will permanently delete:]
        W3[• Your profile and Skill DNA]
        W4[• All bookings and ratings history]
        W5[• All wallet transactions]
        W6[• All uploaded photos and files]
        W7[• All chat and forum messages]
        W8[Your Skill Passports will be revoked on-chain.]
        W9[Skill Seeds balance: 75 — will expire unredeemable.]
        W1 --- W2
        W2 --- W3
        W3 --- W4
        W4 --- W5
        W5 --- W6
        W6 --- W7
        W7 --- W8
        W8 --- W9
    end

    subgraph ALTERNATIVE[Consider alternatives]
        A1[[💤 Deactivate account temporarily]]
        A2[[📦 Export my data first]]
        A3[[📧 Contact support]]
        A1 --- A2
        A2 --- A3
    end

    subgraph CONFIRM[Confirm deletion]
        C1[Type your email to confirm: mai@acme.vn]
        C2[[I understand this is permanent]]
        C3[Reason for leaving (optional):]
        R1[( ) Found alternative]
        R2[( ) Privacy concerns]
        R3[( ) Not enough matches]
        R4[( ) Other ___]
        R1 --- R2
        R2 --- R3
        R3 --- R4
        DEL[[Delete my account in 30 days →]]
        C1 --- C2
        C2 --- C3
        C3 --- R4
        R4 --> DEL
    end

    HEADER ==> WARNING
    WARNING --> ALTERNATIVE
    ALTERNATIVE --> CONFIRM

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class A2 primary
    class DEL danger
```

**Grace period:** 30 days để cancel nếu đổi ý.
**Soft delete:** User account disabled ngay, data hard delete sau 30 ngày qua cron job.
