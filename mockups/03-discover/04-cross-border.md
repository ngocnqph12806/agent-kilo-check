# Cross-border Matching

> **Mục đích:** Toggle matching với mentors ở quốc gia khác + currency conversion.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Discover]
        H2[🌍 Cross-border Matching]
        H1 --- H2
    end

    subgraph TOGGLE[Enable]
        T1[🌍 Cross-border matching]
        T2[[● Enabled]]
        T1 --- T2
        D1[📍 Currently matching: Vietnam only]
        D2[🌐 Will also match: SG ID PH JP]
        D1 --- D2
    end

    subgraph PREF[Preferences]
        P1[Region scope]
        P2[(●) Local + Global]
        P3[( ) Global only]
        P4[( ) Asia only]
        P5[( ) Custom list]
        P1 --- P2
        P2 --- P3
        P3 --- P4
        P4 --- P5
    end

    subgraph TZ[Timezone requirement]
        TZ1[Minimum overlap with my timezone]
        TZ2[─────●─────── 2 hours/day]
        TZ3[ⓘ Mentors with less overlap won't show]
        TZ1 --- TZ2
        TZ2 --- TZ3
    end

    subgraph LANG[Language]
        L1[Required language overlap]
        L2[[✓] Vietnamese]
        L3[[✓] English]
        L4[[✓] Any other]
        L1 --- L2
        L2 --- L3
        L3 --- L4
    end

    subgraph CURRENCY[Display]
        CU1[Show prices in:]
        CU2[(●) Auto from region]
        CU3[( ) My local currency USD]
        CU1 --- CU2
        CU2 --- CU3
        EX1[Example: 60 seeds ≈ $2.40 USD ≈ ¥360 JPY]
    end

    subgraph TRANSLATE[Translation]
        TR1[[✓] Auto-translate reviews to my language]
        TR2[Powered by LLM · cached 7 days]
        TR1 --- TR2
    end

    subgraph SAVE[Save]
        SV[[Save preferences]]
    end

    HEADER ==> TOGGLE
    TOGGLE --> PREF
    PREF --> TZ
    TZ --> LANG
    LANG --> CURRENCY
    CURRENCY --> TRANSLATE
    TRANSLATE --> SAVE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class SV primary
```

**Use case:** VN user học từ SG mentor (overlap Asia timezone).
**Currency:** Backend lưu canonical USD, frontend convert theo locale.
