# B2B Invite Member

> **Mục đích:** Invite members via email hoặc SSO auto-provision.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Members]
        H2[+ Invite Member]
        H1 --- H2
    end

    subgraph METHOD[Method]
        M1[(●) 📧 Email invitation]
        M2[( ) 🔗 SSO auto-provision]
        M3[( ) 📋 Bulk paste list]
        M1 --- M2
        M2 --- M3
    end

    subgraph FORM[Email Form]
        F1[Email addresses ⚠️]
        F2[┌──────────────────────────────────┐<br/>│ an@acme.com                        │<br││ linh@acme.com                      │<br││ hung@acme.com                      │<br│└──────────────────────────────────┘]
        F3[One per line, or comma separated]
        F1 --- F2
        F2 --- F3
    end

    subgraph ROLE[Assign role]
        R1[( ) 👑 Owner]
        R2[(●) 👑 Admin]
        R3[( ) 👤 Member]
        R1 --- R2
        R2 --- R3
    end

    subgraph DEPT[Department auto-assign]
        D1[(●) From email domain]
        D2[( ) Manual assignment]
        D3[Engineering from engineering@acme.com]
        D1 --- D2
        D2 --- D3
    end

    subgraph MSG[Personal message optional]
        MSG1[┌──────────────────────────────────┐<br/>│ Welcome to SkillSeed! Join...     │<br│└──────────────────────────────────┘]
    end

    subgraph PREVIEW[Preview]
        P1[📧 Will send 3 invitation emails]
        P2[👥 3 new members will be added]
        P3[📅 Expiry: 7 days to accept]
        P1 --- P2
        P2 --- P3
    end

    subgraph SEND[Send]
        S1[[Cancel]]
        S2[[Send 3 invitations →]]
        S1 --- S2
    end

    HEADER ==> METHOD
    METHOD --> FORM
    FORM --> ROLE
    ROLE --> DEPT
    DEPT --> MSG
    MSG --> PREVIEW
    PREVIEW --> SEND

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class S2 primary
```

**Bulk:** Paste lên tới 100 emails/lần.
**Expiry:** 7 ngày — sau đó gửi reminder hoặc cancel.
