# Discover Filters

> **Mục đích:** Narrow down AI matches với multi-criteria filter.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Filter matches]
        H2[×]
        H1 --- H2
    end

    subgraph SKILL[Skill]
        SK[┌──────────────────────────────┐<br/>│ Any skill              ▼   │<br/>└──────────────────────────────┘]
    end

    subgraph LANG[Language]
        L1[[✓] Vietnamese]
        L2[[✓] English]
        L3[[ ] Japanese]
        L4[+ Add]
        L1 --- L2
        L2 --- L3
        L3 --- L4
    end

    subgraph COUNTRY[Country]
        C1[[✓] Vietnam]
        C2[[ ] Singapore]
        C3[[ ] Global]
        C4[+ Add]
        C1 --- C2
        C2 --- C3
        C3 --- C4
    end

    subgraph RATING[Minimum rating]
        R1[────●────── 3.5+]
    end

    subgraph TIER[Tier]
        T1[[✓] Any]
        T2[[ ] Sprout]
        T3[[✓] Sapling]
        T4[[✓] Tree+]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph AVAIL[Availability]
        A1[[✓] Available this week]
        A2[[ ] Available today]
        A3[[ ] Available weekends]
        A1 --- A2
        A2 --- A3
    end

    subgraph VER[Verified]
        V1[[✓] Yes verified identity + phone]
    end

    subgraph ACTIONS[Actions]
        AC1[Reset]
        AC2[Apply filters →]
        AC1 --- AC2
    end

    HEADER ==> SKILL
    SKILL --> LANG
    LANG --> COUNTRY
    COUNTRY --> RATING
    RATING --> TIER
    TIER --> AVAIL
    AVAIL --> VER
    VER --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class AC2 primary
```

**Persistence:** Filter state lưu vào URL query string.
