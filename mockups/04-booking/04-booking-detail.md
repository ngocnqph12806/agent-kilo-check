# Booking Detail

> **Mục đích:** Xem full info, join session, cancel, rate.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Bookings]
    end

    subgraph HERO[Booking Hero]
        AV[Avatar]
        I1[Session with Mai Tran]
        I2[📅 Thursday Sep 11 19:00-20:00 GMT+7 60 min]
        I3[Status: ✅ Confirmed accepted 2 days ago]
        AV --> I1
        I1 --> I2
        I2 --> I3
    end

    subgraph COUNTDOWN[Countdown]
        CD[┌──────────────────────────────┐<br/>│    ⏰ 01:42:35 until session    │<br/>└──────────────────────────────┘]
    end

    subgraph DETAILS[Details]
        D1[Skill: Public Speaking]
        D2[Cost: 60 seeds escrow held]
        D1 --- D2
    end

    subgraph MEET[Meeting]
        M1[Link will be available 10 min before start.]
        M2[📋 Copy meeting link]
        M3[Add to Google Calendar]
        M1 --- M2
        M2 --- M3
    end

    subgraph MSG[Your message]
        MG["Hi Mai, I'd love to learn about..."]
    end

    subgraph ACTIONS[Actions]
        A1[Join session off until 18:50]
        A2[Cancel booking]
        A1 --- A2
    end

    subgraph RATING[After session]
        R1[Rate your experience form appears when status = completed]
    end

    HEADER ==> HERO
    HERO ==> COUNTDOWN
    COUNTDOWN --> DETAILS
    DETAILS --> MEET
    MEET --> MSG
    MSG --> ACTIONS
    ACTIONS --> RATING

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class A1 primary
    class A2 danger
```

**State variants:**
- **Pending:** Show countdown to auto-decline
- **In Progress:** Green border, "Live" badge
- **Completed:** Replace actions với rating CTA
- **Cancelled:** Muted styling
