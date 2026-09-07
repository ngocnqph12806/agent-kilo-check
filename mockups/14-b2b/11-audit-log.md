# B2B Audit Log

> **Mục đích:** SOC 2 / GDPR compliance — track every action.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← ACME Corp]
        H2[📋 Audit Log]
        H1 --- H2
    end

    subgraph FILTERS[Filters]
        F1[Date range: Sep 1-30 ▼]
        F2[Actor: All ▼]
        F3[Action: All ▼]
        F4[Resource: All ▼]
        F5[[Export CSV]]
        F1 --- F2
        F2 --- F3
        F3 --- F4
        F4 --- F5
    end

    subgraph TABLE[Audit Entries]
        subgraph ROW1[Sep 12 14:23]
            R1A[mai@acme.com]
            R1B[invited an@acme.com to org]
            R1C[Role: Member]
            R1A --- R1B
            R1B --- R1C
        end

        subgraph ROW2[Sep 12 10:15]
            R2A[duc@acme.com]
            R2B[changed linh@acme.com role]
            R2C[From: Member → Admin]
            R2A --- R2B
            R2B --- R2C
        end

        subgraph ROW3[Sep 11 16:42]
            R3A[system]
            R3B[auto-deprovisioned spam@acme.com]
            R3C[Reason: account deleted in IdP]
            R3A --- R3B
            R3B --- R3C
        end

        subgraph ROW4[Sep 10 09:00]
            R4A[mai@acme.com]
            R4B[exported member list as CSV]
            R4C[248 records]
            R4A --- R4B
            R4B --- R4C
        end

        ROW1 --- ROW2
        ROW2 --- ROW3
        ROW3 --- ROW4
    end

    subgraph PAGINATION[Pagination]
        P1[← Prev  1 2 3 ... 28  Next →]
        P2[Total: 247 audit entries]
        P1 --- P2
    end

    HEADER ==> FILTERS
    FILTERS --> TABLE
    TABLE --> PAGINATION

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class F5 primary
```

**Retention:** 2 years (GDPR + SOC 2).
**Immutable:** Cannot edit/delete (append-only).
