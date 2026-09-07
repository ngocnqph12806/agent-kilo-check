# Pod Detail

> **Mục đích:** Xem chi tiết pod, join, xem forum, RSVP session.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Pods]
    end

    subgraph HERO[Pod Hero]
        COVER[Cover image]
        T1[Public Speaking Club VN]
        T2[🎓 Public Pod • 👥 6/8 members]
        T3[Created by Mai Tran ⭐ 4.9]
        T4[Topic: Weekly public speaking practice]
        COVER --- T1
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph TABS[Tabs]
        TB1[[About] active]
        TB2[[Members 6]]
        TB3[[Forum 12]]
        TB4[[Sessions 8 upcoming]]
        TB1 --- TB2
        TB2 --- TB3
        TB3 --- TB4
    end

    subgraph ABOUT[About tab]
        A1[Description:]
        A2["A friendly space for Vietnamese learners to practice<br/>public speaking weekly. All levels welcome!"]
        A3[Meeting schedule: Every Saturday 7pm GMT+7]
        A4[Language: Vietnamese, English]
        A5[Tags: public-speaking, vietnam, practice]
        A1 --- A2
        A2 --- A3
        A3 --- A4
        A4 --- A5
    end

    subgraph MEMBERS[Members preview]
        M1[(👤 Mai)] --- M2[(👤 Duc)] --- M3[(👤 Linh)] --- M4[(👤 An)] --- M5[+4 more]
    end

    subgraph ACTIONS[Pod Actions]
        AC1[[Join Pod free →]]
        AC2[[💬 Open chat]]
        AC3[[📅 RSVP next session]]
        AC4[[🚩 Report]]
        AC1 --- AC2
        AC2 --- AC3
        AC3 --- AC4
    end

    HEADER ==> HERO
    HERO --> TABS
    TABS --> ABOUT
    ABOUT --> MEMBERS
    MEMBERS ==> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class AC1,AC3 primary
    class AC4 danger
```

**Member-only content:** Forum + sessions chỉ visible sau khi join.
**Multi-participant video:** Daily.co room, max 8 participants.
