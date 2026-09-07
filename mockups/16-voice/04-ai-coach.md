# AI Coach Long-term

> **Mục đích:** Persistent AI mentor với long-term memory của user's learning journey.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Discover]
        H2[🤖 Your AI Coach]
        H1 --- H2
    end

    subgraph PROFILE[Your learning profile]
        P1[🎯 Main goal: Become Senior PM]
        P2[📚 Learning streak: 12 weeks]
        P3[🌱 Total sessions: 87]
        P4[⭐ Avg rating received: 4.7]
        P5[📈 Skills gained: 14 unique]
        P1 --- P2
        P2 --- P3
        P3 --- P4
        P4 --- P5
    end

    subgraph ROADMAP[6-month roadmap]
        R1[Month 1-2: Foundations ✓]
        R2[● Month 3-4: Strategy — Current]
        R3[○ Month 5-6: Leadership]
        R4[Progress: 60%]
        R1 --- R2
        R2 --- R3
        R3 --- R4
    end

    subgraph WEEKLY[This week's plan]
        W1[Monday: Book System Design session]
        W2[Wednesday: Practice pitch video]
        W3[Friday: Read article on OKRs]
        W4[Sunday: Review session notes]
        W1 --- W2
        W2 --- W3
        W3 --- W4
    end

    subgraph INSIGHTS[Personal insights]
        I1[💡 You learn best via 1-1 sessions<br/>88% completion rate vs 45% self-study]
        I2[💡 Peak learning time: Tue-Thu 7-9 PM]
        I3[💡 You're faster than 73% of users<br/>in your cohort Senior PM track]
        I4[⚠️ Gap: No practice sessions in 2 weeks<br/>Consider scheduling a mock interview]
        I1 --- I2
        I2 --- I3
        I3 --- I4
    end

    subgraph ACTIONS[Quick actions]
        A1[[💬 Chat with coach]]
        A2[[📅 Book suggested session]]
        A3[[📊 View detailed analytics]]
        A4[[🎯 Update goals]]
        A1 --- A2
        A2 --- A3
        A3 --- A4
    end

    HEADER ==> PROFILE
    PROFILE --> ROADMAP
    ROADMAP --> WEEKLY
    WEEKLY --> INSIGHTS
    INSIGHTS ==> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class A1,A2 primary
    class I4 warning
```

**Premium+ only:** AI Coach chỉ dành cho Premium+ subscribers.
**Memory:** Long-context LLM (1M tokens) nhớ lịch sử dài hạn.
**Cohort comparison:** Anonymous percentile so với peers cùng track.
