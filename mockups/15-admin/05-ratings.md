# Admin Ratings Moderation

> **Mục đích:** Review reported ratings + hide inappropriate content.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Admin]
        H2[⭐ Ratings Moderation]
        H1 --- H2
    end

    subgraph QUEUE[Reported ratings 7]
        R1[⭐⭐⭐⭐⭐ "Mai was amazing but the video kept freezing..."<br/>Reported by: anonymous<br/>Reason: tech issue complaint not relevant<br/>Confidence: spam 78%<br/>[[Hide] [Keep] [Ban rater]]
        R2[⭐ "Terrible teacher. Don't waste your time."<br/>Reported by: spam-bot-detector<br/>Reason: profanity + no detail<br/>Confidence: toxic 92%<br/>[[Hide] [Delete] [Warn rater]]
        R3[⭐⭐⭐⭐⭐ "Best mentor ever!!! 🙌🙌🙌🎉🎉"<br/>Reported by: rating-ring-detector<br/>Reason: User A rated User B 5⭐ 8 times in 30 days<br/>Confidence: ring 88%<br/>[[Investigate] [Suspend B]]
        R1 --- R2
        R2 --- R3
    end

    subgraph FLAGS[Auto-flagged]
        F1[⚠️ Profanity detected 2]
        F2[⚠️ Phone number in text 1]
        F3[⚠️ External link 0]
        F4[⚠️ Personal attack 1]
        F1 --- F2
        F2 --- F3
        F3 --- F4
    end

    subgraph STATS[This week]
        S1[Total ratings: 1,247]
        S2[Auto-flagged: 7 0.6%]
        S3[Hidden: 3]
        S4[Deleted: 1]
        S5[Avg handling time: 2h]
        S1 --- S2
        S2 --- S3
        S3 --- S4
        S4 --- S5
    end

    subgraph ACTIONS[Moderation actions]
        A1[[Hide rating from public]]
        A2[[Delete permanently]]
        A3[[Warn rater via email]]
        A4[[Ban rater repeat offender]]
        A5[[Notify both parties]]
        A1 --- A2
        A2 --- A3
        A3 --- A4
        A4 --- A5
    end

    HEADER ==> QUEUE
    QUEUE --> FLAGS
    FLAGS --> STATS
    STATS --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class A1,A5 primary
    class A2,A4 danger
    class A3 warning
```

**Auto-moderation:** OpenAI Moderation API flags profanity, PII, external links.
**Audit:** Mọi moderation action log + notify both parties.
