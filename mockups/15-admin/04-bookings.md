# Admin Bookings List

> **Mục đích:** Search, filter, force-cancel bookings globally.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Admin]
        H2[📅 Bookings]
        H1 --- H2
    end

    subgraph SEARCH[Search]
        S1[🔍 Search by booking ID or user]
        F1[Status: All ▼]
        F2[Date range ▼]
        F3[Skill ▼]
        F4[Country ▼]
        F1 --- F2
        F2 --- F3
        F3 --- F4
    end

    subgraph METRICS[Today]
        M1[Total: 412]
        M2[Pending: 28]
        M3[Confirmed: 234]
        M4[Completed: 142]
        M5[Cancelled: 8]
        M1 --- M2
        M2 --- M3
        M3 --- M4
        M4 --- M5
    end

    subgraph TABLE[Bookings table]
        subgraph R1[Booking 1]
            ID1[#BK-2026-09-12-042]
            ID1A[Teacher: Mai Tran]
            ID1B[Learner: An Nguyen]
            ID1C[Sep 12 19:00 60 min]
            ID1D[✅ Confirmed]
            ID1E[[View] [Force cancel]]
            ID1 --- ID1A
            ID1A --- ID1B
            ID1B --- ID1C
            ID1C --- ID1D
            ID1D --- ID1E
        end

        subgraph R2[Booking 2]
            ID2[#BK-2026-09-12-041]
            ID2A[Teacher: John Smith]
            ID2B[Learner: Linh Pham]
            ID2C[Sep 12 14:00 30 min]
            ID2D[⏳ Pending awaiting acceptance]
            ID2E[[View] [Force cancel]]
            ID2 --- ID2A
            ID2A --- ID2B
            ID2B --- ID2C
            ID2C --- ID2D
            ID2D --- ID2E
        end

        subgraph R3[Booking 3]
            ID3[#BK-2026-09-11-098]
            ID3A[Teacher: Lisa Wong]
            ID3B[Learner: Duc Mai]
            ID3C[Sep 11 10:00 60 min]
            ID3D[🚫 Cancelled technical issue]
            ID3E[[View] [Reopen]]
            ID3 --- ID3A
            ID3A --- ID3B
            ID3B --- ID3C
            ID3C --- ID3D
            ID3D --- ID3E
        end

        R1 --- R2
        R2 --- R3
    end

    subgraph ACTIONS[Bulk actions]
        AC1[[Cancel selected]]
        AC2[[Refund selected]]
        AC3[[Export CSV]]
        AC1 --- AC2
        AC2 --- AC3
    end

    HEADER ==> SEARCH
    SEARCH --> METRICS
    METRICS --> TABLE
    TABLE --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class AC3 primary
    class ID1D warning
    class ID1E,ID2E,AC1,AC2 danger
```

**Force cancel:** Refund 100% seeds cho cả 2 phía.
**Audit:** Mọi force-cancel log vào admin_audit.
