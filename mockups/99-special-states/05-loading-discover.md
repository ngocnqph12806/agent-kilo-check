# Loading Skeleton — Discover

> **Mục đích:** Skeleton loading khi AI matching đang chạy.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed]
    end

    subgraph SEARCH[Search Bar Skeleton]
        S1[┌────────────────────────────┐]
        S2[┌────────────┐]
        S1 --- S2
    end

    subgraph SKELETONS[Skeleton Cards 5]
        C1[┌────────────────────────────────────┐<br/>│ ▓▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓                 │<br/>│ ▓▓▓▓▓▓▓▓▓▓▓▓▓                     │<br/>│ ▓▓▓▓▓▓▓▓▓▓▓                       │<br/>│ ▓▓▓▓ ▓▓▓▓▓▓                       │<br/>└────────────────────────────────────┘]
        C2[┌────────────────────────────────────┐<br/>│ ▓▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓                 │<br/>│ ▓▓▓▓▓▓▓▓▓▓▓▓▓                     │<br/>│ ▓▓▓▓▓▓▓▓▓▓▓                       │<br│└────────────────────────────────────┘]
        C3[┌────────────────────────────────────┐<br/>│ ▓▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓                 │<br│└────────────────────────────────────┘]
        C1 --- C2
        C2 --- C3
    end

    subgraph MSG[Loading message]
        M[✨ AI is finding your best matches...]
        SPIN[(spinner)]
        M --- SPIN
    end

    HEADER ==> SEARCH
    SEARCH --> SKELETONS
    SKELETONS --> MSG

    classDef skeleton fill:#F3F4F6,stroke:#D1D5DB,color:#374151
    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class C1,C2,C3,SPIN skeleton
```

**Shimmer animation:** Linear gradient moving across skeletons.
**Performance:** Skeleton hiển thị < 100ms ngay khi page load.
