# B2B Export Report

> **Mục đích:** Configure + generate reports (PDF/CSV).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Reports]
        H2[📤 Export Report]
        H1 --- H2
    end

    subgraph TYPE[Report type]
        T1[( ) 📊 Engagement Report]
        T2[(●) 🎓 Skills Report]
        T3[( ) 💰 ROI Report]
        T4[( ) 📋 Member Activity]
        T5[( ) ⭐ Quality Report]
        T1 --- T2
        T2 --- T3
        T3 --- T4
        T4 --- T5
    end

    subgraph SCOPE[Scope]
        SC1[Date range]
        SC2[Sep 1 - Sep 30 2026 ▼]
        SC3[Compare with]
        SC4[Aug 2026 ▼]
        SC5[Departments]
        SC6[All Engineering Sales Marketing]
        SC7[Members]
        SC8[Active only ☑]
        SC1 --- SC2
        SC2 --- SC3
        SC3 --- SC4
        SC4 --- SC5
        SC5 --- SC6
        SC6 --- SC7
        SC7 --- SC8
    end

    subgraph FORMAT[Format]
        F1[(●) PDF formatted]
        F2[( ) CSV raw data]
        F3[( ) Excel with charts]
        F4[( ) JSON API]
        F1 --- F2
        F2 --- F3
        F3 --- F4
    end

    subgraph OPTIONS[Options]
        O1[[✓] Include member names]
        O2[[✓] Include charts and graphs]
        O3[[✓] Add executive summary]
        O4[[✓] Anonymize sensitive data]
        O1 --- O2
        O2 --- O3
        O3 --- O4
    end

    subgraph SCHED[Schedule optional]
        SCH1[Send monthly to: leadership@acme.com]
        SCH2[Day: 1st of month ▼]
        SCH3[Format: PDF ▼]
        SCH1 --- SCH2
        SCH2 --- SCH3
    end

    subgraph GEN[Generate]
        G1[[Generate now]]
        G2[Estimated time: 30 seconds]
        G1 --- G2
    end

    HEADER ==> TYPE
    TYPE --> SCOPE
    SCOPE --> FORMAT
    FORMAT --> OPTIONS
    OPTIONS --> SCHED
    SCHED --> GEN

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class G1 primary
```

**Delivery:** Auto email to leadership khi scheduled.
**Branding:** PDF có logo + colors của org (Enterprise).
