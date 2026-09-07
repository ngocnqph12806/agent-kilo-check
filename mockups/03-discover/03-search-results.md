# Search Results

> **Mục đích:** Search results cho skills hoặc mentors qua global search bar.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed]
        H2[🔍 python]
        H3[×]
        H1 --- H2
        H2 --- H3
    end

    subgraph TABS[Result Tabs]
        T1[[Mentors 156 active]]
        T2[[Skills 24]]
        T3[[Pods 12]]
        T4[[Blog posts 8]]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph TOPMATCHES[Top Matches]
        M1[Avatar] --> M2[Mai Tran ⭐ 4.9]
        M3[Avatar] --> M4[John Smith ⭐ 4.8]
        M5[Avatar] --> M6[Lisa Wong ⭐ 4.9 Verified Expert]
        M1 --- M2
        M3 --- M4
        M5 --- M6
    end

    subgraph SUGGESTIONS[Did you mean]
        S1[python for beginners]
        S2[advanced python]
        S3[python data science]
        S4[python for kids]
        S1 --- S2
        S2 --- S3
        S3 --- S4
    end

    subgraph FILTERS[Refine]
        F1[Country ▼]
        F2[Rating ▼]
        F3[Price ▼]
        F4[Available now]
        F1 --- F2
        F2 --- F3
        F3 --- F4
    end

    subgraph RESULTS[Result List]
        R1[Row 1 — Mai Tran — Python Lvl 4]
        R2[Row 2 — An Nguyen — Python Lvl 3]
        R3[Row 3 — Duc Mai — Python Lvl 5]
        R1 --- R2
        R2 --- R3
        LD[[Load more]]
    end

    HEADER ==> TABS
    TABS --> TOPMATCHES
    TOPMATCHES --> SUGGESTIONS
    SUGGESTIONS --> FILTERS
    FILTERS --> RESULTS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class T1 primary
```

**Search engine:** PostgreSQL full-text search + Elasticsearch (Phase 2+).
**Autocomplete:** Top suggestions sau 200ms typing.
