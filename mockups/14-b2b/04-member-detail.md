# B2B Member Detail

> **Mục đích:** Xem chi tiết 1 member: profile, sessions, role.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Members]
        H2[Mai Tran]
        H1 --- H2
    end

    subgraph HERO[Hero]
        AV[(👤)]
        N1[Mai Tran 🌳 Tree ✓ Verified]
        N2[mai@acme.com • 🇻🇳 Vietnam]
        N3[👑 Owner • Joined Aug 2025]
        N4[Last active: 2 hours ago]
        AV --- N1
        N1 --- N2
        N2 --- N3
        N3 --- N4
    end

    subgraph TABS[Tabs]
        TB1[[Activity] active]
        TB2[[Sessions 45]]
        TB3[[Reviews 38]]
        TB4[[Role & Permissions]]
        TB1 --- TB2
        TB2 --- TB3
        TB3 --- TB4
    end

    subgraph ACTIVITY[Activity tab]
        A1[📅 Last session: 2 hours ago<br/>Public Speaking with Duc Mai]
        A2[📊 Sessions this month: 8]
        A3[⭐ Avg rating given: 4.5]
        A4[🌱 Seeds earned this month: 240]
        A1 --- A2
        A2 --- A3
        A3 --- A4
    end

    subgraph PERMISSIONS[Permissions]
        P1[Owner has all permissions:]
        P2[✓ Invite members]
        P3[✓ Remove members]
        P4[✓ Change roles]
        P5[✓ View audit log]
        P6[✓ Manage billing]
        P1 --- P2
        P2 --- P3
        P3 --- P4
        P4 --- P5
        P5 --- P6
    end

    subgraph ROLE[Change role]
        R1[[Change to Admin]]
        R2[[Change to Member]]
        R3[[Demote]]
        R1 --- R2
        R2 --- R3
    end

    subgraph DANGER[Danger zone]
        D1[[Remove from org]]
        D2[User will lose all access and data]
        D1 --- D2
    end

    HEADER ==> HERO
    HERO --> TABS
    TABS --> ACTIVITY
    ACTIVITY --> PERMISSIONS
    PERMISSIONS --> ROLE
    ROLE --> DANGER

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class R1,R2 primary
    class R3,D1 danger
```

**Permissions:** RBAC matrix theo role (Owner, Admin, Member).
**Audit:** Mọi thay đổi role log vào audit_logs.
