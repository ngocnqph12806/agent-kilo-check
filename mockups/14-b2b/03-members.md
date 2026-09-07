# B2B Members List

> **Mục đích:** HR quản lý members: invite, remove, view stats.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← ACME Corp]
        H2[👥 Members]
        H1 --- H2
    end

    subgraph ACTIONS[Top Bar]
        A1[Search members]
        A2[Filter: Role ▼  Status ▼  Dept ▼]
        A3[[+ Invite members]]
        A1 --- A2
        A2 --- A3
    end

    subgraph TABLE[Members Table]
        subgraph HEADER[Header row]
            HR1[Avatar] --- HR2[Name] --- HR3[Email] --- HR4[Role] --- HR5[Sessions] --- HR6[Status] --- HR7[Actions]
        end

        subgraph ROW1[Member 1]
            M1[(👤)] --- M1N[Mai Tran] --- M1E[mai@acme.com] --- M1R[👑 Owner] --- M1S[45 sessions] --- M1ST[🟢 Active] --- M1A[[⋯]]
        end

        subgraph ROW2[Member 2]
            M2[(👤)] --- M2N[Duc Mai] --- M2E[duc@acme.com] --- M2R[👑 Admin] --- M2S[28 sessions] --- M2ST[🟢 Active] --- M2A[[⋯]]
        end

        subgraph ROW3[Member 3]
            M3[(👤)] --- M3N[Linh Pham] --- M3E[linh@acme.com] --- M3R[👤 Member] --- M3S[12 sessions] --- M3ST[🟡 Pending] --- M3A[[⋯]]
        end

        subgraph ROW4[Member 4]
            M4[(👤)] --- M4N[An Nguyen] --- M4E[an@acme.com] --- M4R[👤 Member] --- M4S[8 sessions] --- M4ST[⚪ Inactive 30d] --- M4A[[⋯]]
        end

        HEADER --- ROW1
        ROW1 --- ROW2
        ROW2 --- ROW3
        ROW3 --- ROW4
    end

    subgraph PAGINATION[Pagination]
        P1[← Prev  1 2 3 ... 12  Next →]
    end

    HEADER ==> ACTIONS
    ACTIONS ==> TABLE
    TABLE --> PAGINATION

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class A3 primary
```

**Bulk actions:** Select multiple → bulk invite, remove, change role.
**Export:** CSV download full member list.
