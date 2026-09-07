# Pods Discover

> **Mục đích:** Browse public Learning Pods (4-8 người cùng chủ đề).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Discover]
        H2[🎓 Learning Pods]
        H1 --- H2
    end

    subgraph SEARCH[Search]
        S1[🔍 Search pods by topic or name]
        F1[Filter: Topic ▼  Size ▼  Public ▼]
        S1 --- F1
    end

    subgraph FEATURED[Featured Pods]
        subgraph P1[Public Speaking Club VN]
            AV1[👥 6/8 members]
            T1[Topic: Weekly public speaking practice]
            MT1[Meets: Saturday 7pm GMT+7]
            LP1[Leader: Mai Tran ⭐ 4.9]
            AV1 --- T1
            T1 --- MT1
            MT1 --- LP1
            J1[[Join free →]]
        end

        subgraph P2[Python for Beginners ID]
            AV2[👥 4/6 members]
            T2[Topic: Python basics in Bahasa]
            MT2[Meets: Wednesday 8pm GMT+7]
            LP2[Leader: Budi ⭐ 4.8]
            AV2 --- T2
            T2 --- MT2
            MT2 --- LP2
            J2[[Join free →]]
        end

        subgraph P3[Premium: UX Mastermind SG]
            AV3[👥 8/8 FULL]
            T3[Topic: Senior UX design monthly]
            MT3[Fee: $30/month]
            LP3[Leader: Sarah ⭐ 5.0]
            AV3 --- T2
            T2 --- MT3
            MT3 --- LP3
            J3[[Waitlist →]]
        end

        P1 --- P2
        P2 --- P3
    end

    subgraph SUGGEST[AI Suggestions for you]
        AS1[🎯 Pods trending in your area VN]
        AS2[🎯 Based on your interests: Public Speaking, Design]
        AS1 --- AS2
    end

    subgraph CREATE[Create your own Pod]
        CR1[[+ Create new Pod]]
    end

    HEADER ==> SEARCH
    SEARCH ==> FEATURED
    FEATURED --> SUGGEST
    SUGGEST --> CREATE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef premium fill:#FEF3C7,stroke:#F59E0B,color:#92400E
    class J1,J2 primary
    class P3,J3 premium
```

**Pod types:** Public (free), Private (invite-only), Premium (monthly fee).
**Capacity:** 4-8 members per pod.
