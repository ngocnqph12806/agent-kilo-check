# Voice History

> **Mục đích:** Lịch sử các voice conversations để replay hoặc reference.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Voice Assistant]
        H2[📜 Conversation History]
        H1 --- H2
    end

    subgraph SEARCH[Search]
        S1[🔍 Search conversations]
        F1[Filter: Today This week Month All]
        S1 --- F1
    end

    subgraph LIST[Conversations]
        subgraph C1[Today Sep 12]
            AV1[🎙️]
            T1[Booked session with Mai Tran<br/>for Public Speaking tonight 7pm]
            TM1[2 min ago]
            AV1 --- T1
            T1 --- TM1
            AC1[[Replay transcript] [Re-run voice]]
        end

        subgraph C2[Today Sep 12]
            AV2[🎙️]
            T2[Asked about learning System Design<br/>from Singapore mentors]
            TM2[1 hour ago]
            AV2 --- T2
            T2 --- TM2
            AC2[[Replay transcript] [Re-run voice]]
        end

        subgraph C3[Yesterday Sep 11]
            AV3[🎙️]
            T3[Found UX Design mentor<br/>Lisa Wong rating 4.9]
            TM3[Yesterday 18:42]
            AV3 --- T3
            T3 --- TM3
            AC3[[Replay transcript] [Re-run voice]]
        end

        subgraph C4[Sep 10]
            AV4[🎙️]
            T4[Got 6-month roadmap for Senior PM<br/>career track]
            TM4[Sep 10 14:20]
            AV4 --- T4
            T4 --- TM4
            AC4[[Replay transcript] [Re-run voice]]
        end

        C1 --- C2
        C2 --- C3
        C3 --- C4
    end

    subgraph INSIGHTS[Insights from history]
        I1[📊 47 conversations total]
        I2[Top intent: search_mentors 60%]
        I3[book_session 25%]
        I4[learning_advice 15%]
        I5[Avg session: 1.5 minutes]
        I1 --- I2
        I2 --- I3
        I3 --- I4
        I4 --- I5
    end

    HEADER ==> SEARCH
    SEARCH --> LIST
    LIST --> INSIGHTS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class AC1,AC2,AC3,AC4 primary
```

**Transcript:** Save audio + text để replay.
**Privacy:** User xóa lịch sử bất kỳ lúc nào.
