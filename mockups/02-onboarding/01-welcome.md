# Onboarding — Welcome

> **Mục đích:** Chào mừng user mới, giới thiệu Skill DNA flow.
> **Phase:** 1 (Onboarding Step 0/7)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
        B[Save & exit]
        A --- B
    end

    subgraph PROGRESS[Progress 0/7]
        P1[● ● ● ● ● ● ●]
    end

    subgraph CONTENT[Welcome]
        T1[Welcome, Mai! 👋]
        T2[Let's build your Skill DNA —<br/>a 3-minute profile that helps AI find<br/>perfect matches for you.]
        T3[🎁 You'll get 30 free starter seeds after.]
        B1[Let's go →]
        T1 --> T2
        T2 --> T3
        T3 --> B1
    end

    HEADER ==> PROGRESS
    PROGRESS ==> CONTENT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class B1 primary
```

**Trigger:** Sau khi verify email thành công.
**Next:** Click → Step 1 (Skills I Can Teach).
