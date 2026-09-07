# Admin Users List

> **Mục đích:** Search, filter, view, ban, verify users.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Admin]
        H2[👥 Users]
        H1 --- H2
    end

    subgraph SEARCH[Search Bar]
        S1[🔍 Search by name email or user ID]
        F1[Filter: Status ▼  Verified ▼  Country ▼  Joined ▼]
        EX[[Export CSV]]
        S1 --- F1
        F1 --- EX
    end

    subgraph TABLE[Users Table]
        subgraph HEADER[Header]
            H1[Avatar] --- H2[Name] --- H3[Email] --- H4[Country] --- H5[Sessions] --- H6[Spent Seeds] --- H7[Joined] --- H8[Status] --- H9[Actions]
        end

        subgraph R1[Row 1]
            A1[(👤)] --- N1[Mai Tran] --- E1[mai@acme.vn] --- C1[🇻🇳 VN] --- SE1[45 sessions] --- SP1[280 sd spent] --- J1[Aug 2025] --- ST1[✅ Active ✓ Verified] --- AC1[[⋯ View Ban]]
        end

        subgraph R2[Row 2]
            A2[(👤)] --- N2[Spam User] --- E2[spam@xyz.com] --- C2[🇨🇳 CN] --- SE2[0 sessions] --- SP2[0 sd] --- J2[Sep 2026] --- ST2[🚫 Banned] --- AC2[[⋯ Unban]]
        end

        subgraph R3[Row 3]
            A3[(👤)] --- N3[New User] --- E3[new@example.com] --- C3[🇮🇩 ID] --- SE3[0 sessions] --- SP3[0 sd] --- J3[Today] --- ST3[⏳ Pending verification] --- AC3[[⋯ Verify]]
        end

        HEADER --- R1
        R1 --- R2
        R2 --- R3
    end

    subgraph PAGINATION[Pagination]
        P1[← Prev  1 2 3 ... 847  Next →]
        P2[Total: 8,432 users]
        P1 --- P2
    end

    HEADER ==> SEARCH
    SEARCH ==> TABLE
    TABLE --> PAGINATION

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class EX primary
    class AC1 danger
    class ST3 warning
```

**Bulk actions:** Select multiple → bulk ban, verify, send email.
**Search:** Full-text search on email/name + filter by status.
