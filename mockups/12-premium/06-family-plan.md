# Family Plan Setup

> **Mục đích:** Owner add up to 4 members, manage shared seed pool.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Premium Plans]
        H2[Family Plan Setup]
        H1 --- H2
    end

    subgraph PLAN[Plan Details]
        P1[🌱 SkillSeed Family]
        P2[$14.9/month billed monthly]
        P3[Up to 4 members sharing 200 monthly seeds]
        P4[1 owner + 3 members]
        P1 --- P2
        P2 --- P3
        P3 --- P4
    end

    subgraph OWNER[Owner already added]
        O1[(👤)] --- O2[Mai Tran Owner]
        O3[200 seeds allocated]
        O1 --- O2
        O2 --- O3
    end

    subgraph ADD[Add Member]
        A1[Email address ⚠️]
        A2[mai@acme.vn]
        A3[[Send invite →]]
        A4[Invited: duc@acme.vn — pending]
        A5[Invited: linh@acme.vn — pending]
        A1 --- A2
        A2 --- A3
        A3 --- A4
        A4 --- A5
    end

    subgraph POOL[Seed Pool]
        SP1[Total monthly: 200 seeds]
        SP2[Owner Mai: 80 seeds allocated]
        SP3[Member Duc: 60 seeds allocated]
        SP4[Member Linh: 60 seeds allocated]
        SP5[Available: 0]
        SP6[[Adjust allocation]]
        SP1 --- SP2
        SP2 --- SP3
        SP3 --- SP4
        SP4 --- SP5
        SP5 --- SP6
    end

    subgraph CHECKOUT[Confirm]
        CO1[[Subscribe $14.9/month →]]
        CO2[💳 Visa **** 4242]
        CO1 --- CO2
    end

    HEADER ==> PLAN
    PLAN --> OWNER
    OWNER --> ADD
    ADD --> POOL
    POOL --> CHECKOUT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class A3,SP6,CO1 primary
```

**Member prerequisites:** Phải có SkillSeed account (sign up trước hoặc tự động qua invite).
**Seed allocation:** Owner phân bổ seeds/tháng cho từng member.
