# Empty State — Bookings

> **Mục đích:** User mới chưa có booking nào.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed]
        H2[Bookings]
        H1 --- H2
    end

    subgraph TABS[Tabs]
        T1[[Upcoming] active]
        T2[[Past]]
        T3[[Cancelled]]
        T1 --- T2
        T2 --- T3
    end

    subgraph EMPTY[Empty state centered]
        ILL[(📅 illustration)]
        T4[No upcoming sessions]
        T5[Time to find your first mentor!]
        T6[Browse AI-matched mentors and book<br/>your first 30-minute session.]
        SUG1[[🌍 Browse Discover]]
        SUG2[[🔥 See trending skills]]
        SUG3[[📚 Browse by category]]
        ILL --- T4
        T4 --- T5
        T5 --- T6
        T6 --- SUG1
        SUG1 --- SUG2
        SUG2 --- SUG3
    end

    HEADER ==> TABS
    TABS ==> EMPTY

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class SUG1 primary
```

**CTAs:** Smart suggestions dựa trên user profile.
