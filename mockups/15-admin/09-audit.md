# Admin Audit Log

> **Mục đích:** Track mọi admin action cho compliance.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Admin]
        H2[📋 Audit Log]
        H1 --- H2
    end

    subgraph FILTERS[Filters]
        F1[Date range: Last 7 days ▼]
        F2[Actor: All admins ▼]
        F3[Action type ▼]
        F4[Resource type ▼]
        F5[[Export CSV]]
        F1 --- F2
        F2 --- F3
        F3 --- F4
        F4 --- F5
    end

    subgraph TABLE[Audit entries]
        subgraph R1[Sep 12 14:23:12]
            A1[john@skillseed.com]
            A2[Banned user troll_user]
            A3[Reason: harassment 3rd strike]
            A4[IP: 1.2.3.4]
            A1 --- A2
            A2 --- A3
            A3 --- A4
        end

        subgraph R2[Sep 12 11:45:08]
            B1[jane@skillseed.com]
            B2[Approved custom skill Bavarian Cooking]
            B3[Category: Cuisine]
            B1 --- B2
            B2 --- B3
        end

        subgraph R3[Sep 12 09:12:34]
            C1[system auto]
            C2[Force-cancelled booking #BK-2026-09-12-001]
            C3[Reason: teacher no-show]
            C4[Refund: 100% both parties]
            C1 --- C2
            C2 --- C3
            C3 --- C4
        end

        subgraph R4[Sep 11 16:30:00]
            D1[john@skillseed.com]
            D2[Exported user data for GDPR request]
            D3[User: user@acme.com]
            D4[Records: 247]
            D1 --- D2
            D2 --- D3
            D3 --- D4
        end

        R1 --- R2
        R2 --- R3
        R3 --- R4
    end

    subgraph PAGINATION[Pagination]
        P1[← Prev  1 2 3 ... 89  Next →]
        P2[Total: 8,847 entries]
        P1 --- P2
    end

    subgraph RETENTION[Retention]
        RT1[2 years per SOC 2 + GDPR]
        RT2[Auto-archive to S3 Glacier after 90 days]
        RT1 --- RT2
    end

    HEADER ==> FILTERS
    FILTERS --> TABLE
    TABLE --> PAGINATION
    PAGINATION --> RETENTION

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class F5 primary
```

**Immutable:** Không cho edit/delete — append-only.
**Compliance:** SOC 2 + GDPR — retention 2 years.
