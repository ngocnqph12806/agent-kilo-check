# Admin User Detail

> **Mục đích:** Xem full user profile + actions (ban, verify, impersonate).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Users]
        H2[Mai Tran]
        H1 --- H2
    end

    subgraph HERO[User Hero]
        AV[Avatar]
        N1[Mai Tran 🌳 Tree ✓ Verified]
        N2[⭐ 4.9 23 sessions]
        N3[mai@acme.vn 🇻🇳 Vietnam]
        N4[Joined: Aug 15 2025]
        N5[Last active: 2 hours ago]
        AV --- N1
        N1 --- N2
        N2 --- N3
        N3 --- N4
        N4 --- N5
    end

    subgraph TABS[Tabs]
        T1[[Profile] active]
        T2[[Bookings 45]]
        T3[[Ratings 38]]
        T4[[Wallet]]
        T5[[Sessions log]]
        T6[[Moderation]]
        T1 --- T2
        T2 --- T3
        T3 --- T4
        T4 --- T5
        T5 --- T6
    end

    subgraph PROFILE[Profile tab]
        P1[Bio: Product designer passionate about...]
        P2[Skills teach: Public Speaking Lvl 3, Python Lvl 4]
        P3[Skills learn: System Design, UX Research]
        P4[Availability: 18h/week]
        P5[Languages: Vietnamese, English]
        P6[Wallet balance: 75 seeds]
        P1 --- P2
        P2 --- P3
        P3 --- P4
        P4 --- P5
        P5 --- P6
    end

    subgraph ACTIONS[Admin Actions]
        A1[[Verify identity]]
        A2[[Reset password]]
        A3[[Send warning]]
        A4[[Ban user]]
        A5[[Impersonate]]
        A6[[Export user data GDPR]]
        A1 --- A2
        A2 --- A3
        A3 --- A4
        A4 --- A5
        A5 --- A6
    end

    subgraph AUDIT[Recent admin actions on this user]
        AT1[Sep 5 — Verified phone by admin john@skillseed]
        AT2[Aug 15 — Account created via Google OAuth]
        AT1 --- AT2
    end

    HEADER ==> HERO
    HERO --> TABS
    TABS --> PROFILE
    PROFILE --> ACTIONS
    ACTIONS --> AUDIT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class A1,A2,A3,A5,A6 primary
    class A4 danger
```

**Impersonate:** Chỉ SUPER_ADMIN, audit log required.
**Ban:** Confirm modal + reason required.
