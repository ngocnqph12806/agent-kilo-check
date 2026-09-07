# B2B Skill Pool

> **Mục đích:** HR quản lý danh sách skills ưu tiên cho org.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← ACME Corp]
        H2[🎓 Skill Pool]
        H1 --- H2
    end

    subgraph STATS[Stats]
        S1[Total skills: 24]
        S2[Internal mentors: 18]
        S3[External experts invited: 6]
        S4[Avg coverage: 75%]
        S1 --- S2
        S2 --- S3
        S3 --- S4
    end

    subgraph PRIORITY[Priority skills]
        P1[🔥 High priority 8]
        P2[📌 Medium 10]
        P3[📗 Nice to have 6]
        P1 --- P2
        P2 --- P3
    end

    subgraph TABLE[Skill Pool Table]
        subgraph ROW1[Skill 1]
            SK1[Python Programming]
            SC1[Priority: 🔥 High]
            SM1[Internal mentors: 12]
            SG1[Gap: 🟢 Strong]
            AC1[[Edit]] [[Invite expert]]
        end

        subgraph ROW2[Skill 2]
            SK2[Public Speaking]
            SC2[Priority: 🔥 High]
            SM2[Internal mentors: 3]
            SG2[Gap: 🟡 Moderate]
            AC2[[Edit]] [[Invite expert]]
        end

        subgraph ROW3[Skill 3]
            SK3[Product Strategy]
            SC3[Priority: 🔥 High]
            SM3[Internal mentors: 0]
            SG3[Gap: 🔴 Critical]
            AC3[[Edit]] [[Invite expert]]
        end

        ROW1 --- ROW2
        ROW2 --- ROW3
    end

    subgraph ADD[Add skill]
        AD1[[+ Add new skill to pool]]
    end

    subgraph INVITE[Invite expert modal]
        IEM1[Choose an Expert from Marketplace]
        IEM2[Lisa Wong — System Design — $80/hr]
        IEM3[Send invite with internal budget $500/mo]
        IEM4[Auto-renew monthly]
        IEM1 --- IEM2
        IEM2 --- IEM3
        IEM3 --- IEM4
    end

    HEADER ==> STATS
    STATS --> PRIORITY
    PRIORITY --> TABLE
    TABLE --> ADD
    ADD --> INVITE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class AD1 primary
    class SG3 warning
```

**Internal budget:** Org set budget cho external experts.
**Integration:** Sync với marketplace để invite verified experts.
