# B2B Skill Gap Heatmap

> **Mục đích:** Visualize skill coverage theo level × category trong org.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← ACME Corp]
        H2[🎓 Skills Heatmap]
        H1 --- H2
    end

    subgraph FILTERS[Filters]
        F1[Department ▼]
        F2[Level ▼ Junior Mid Senior]
        F3[Category ▼ Tech Business Soft Skills]
        F1 --- F2
        F2 --- F3
    end

    subgraph HEATMAP[Heatmap Matrix]
        subgraph ROWS[Rows = Career Level]
            R1[Junior]
            R2[Mid]
            R3[Senior]
            R4[Lead]
            R5[Director]
            R1 --- R2
            R2 --- R3
            R3 --- R4
            R4 --- R5
        end

        subgraph COLS[Columns = Skill Category]
            C1[Engineering]
            C2[Design]
            C3[Product]
            C4[Marketing]
            C5[Sales]
            C6[Ops]
            C1 --- C2
            C2 --- C3
            C3 --- C4
            C4 --- C5
            C5 --- C6
        end

        subgraph CELLS[Cell values]
            H1[🟢 Strong ≥5 mentors]
            H2[🟡 Moderate 1-4]
            H3[🔴 Gap 0]
            H4[⚪ N/A]
            H1 --- H2
            H2 --- H3
            H3 --- H4
        end

        ROWS --- COLS
        COLS --> CELLS
    end

    subgraph DETAIL[Cell detail Product-Mid]
        D1[Product Management — Mid Level]
        D2[Internal mentors: 0 🔴 GAP]
        D3[Possible actions:]
        D4[• Hire externally]
        D5[• Up-skill Mid→Senior internally]
        D6[• Invite external Verified Expert]
        D1 --- D2
        D2 --- D3
        D3 --- D4
        D4 --- D5
        D5 --- D6
    end

    subgraph ACTIONS[Suggested actions]
        A1[[📧 Recruit externally]]
        A2[[📚 Create upskilling program]]
        A3[[👥 Invite external expert]]
        A1 --- A2
        A2 --- A3
    end

    HEADER ==> FILTERS
    FILTERS --> HEATMAP
    HEATMAP --> DETAIL
    DETAIL ==> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class A1,A2,A3 primary
```

**Real-time calculation:** ClickHouse query mỗi lần mở page.
**Drill-down:** Click cell → list of mentors + suggested actions.
