# Notifications List

> **Mục đích:** Tổng hợp thông báo (booking, reminder, matches, reviews).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Notifications]
        H2[[Mark all read]]
        H1 --- H2
    end

    subgraph TODAY[Today]
        N1[🟢 Avatar] --> N2[Mai Tran accepted your booking]
        N2 --> N3["Public Speaking, Sep 11 19:00"]
        N3 --> N4[10 min ago [View]]
        N1 --- N4
    end

    subgraph REMINDER[Reminder]
        R1[⏰ Reminder: Session with Mai Tran starts in 1 hour]
        R2[Sep 11, 18:00 [Join now →]]
        R1 --- R2
    end

    subgraph YESTERDAY[Yesterday]
        Y1[🎉 You've been matched with 3 new mentors this week!]
        Y2[[See matches →]]
        Y1 --- Y2

        Y3[⭐ Linh N. left you a 5-star review]
        Y4["Amazing mentor!" [View profile]]
        Y3 --- Y4
    end

    subgraph WEEK[This week]
        W1[🌱 30 starter seeds added to your wallet]
        W2[Welcome to SkillSeed!]
        W1 --- W2
    end

    HEADER ==> TODAY
    TODAY --> REMINDER
    REMINDER --> YESTERDAY
    YESTERDAY --> WEEK

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef unread fill:#F0FDF4,stroke:#10B981,color:#000
    class R2,Y2 primary
    class N1,R1,Y1,W1 unread
```

**Channels:** Email + push (web/APNs/FCM) + in-app.
**Polling:** Frontend poll mỗi 60s, hoặc dùng SSE/WebSocket (Phase 2+).
**Read state:** Tap → mark read → update UI optimistic.
