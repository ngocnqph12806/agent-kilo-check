# Rating Modal (Multi-criteria)

> **Mục đích:** Thu thập feedback 5 tiêu chí sau session.
> **Phase:** 2 (Multi-criteria rating)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[× How was your session?]
    end

    subgraph CTX[Context]
        C1[Session with Mai Tran — Public Speaking]
        C2[📅 Sep 11, 19:00-20:00]
        C1 --- C2
    end

    subgraph RATINGS[Rate 5 dimensions each 1-5 stars]
        R1[Knowledge of subject<br/>☆☆☆☆☆]
        R2[Clarity of explanation<br/>☆☆☆☆☆]
        R3[Helpfulness<br/>☆☆☆☆☆]
        R4[Punctuality<br/>☆☆☆☆☆]
        R5[Friendliness<br/>☆☆☆☆☆]
        AVG[Overall: 0.0/5 auto-calculated]
        R1 --- R2
        R2 --- R3
        R3 --- R4
        R4 --- R5
        R5 --> AVG
    end

    subgraph REVIEW[Write a review optional]
        RV[┌──────────────────────────────────────┐<br/>│ Mai was amazing...                      │<br/>│                              0/500    │<br/>└──────────────────────────────────────┘]
    end

    subgraph REBOOK[Would you book again?]
        RB1[( ) Yes!]
        RB2[( ) Maybe]
        RB3[( ) Probably not]
        RB1 --- RB2
        RB2 --- RB3
    end

    subgraph SUBMIT[Submit]
        S1[Submit rating]
        S2[You can skip — we'll auto-rate 5⭐ after 7 days.]
        S1 --- S2
    end

    HEADER ==> CTX
    CTX --> RATINGS
    RATINGS --> REVIEW
    REVIEW --> REBOOK
    REBOOK --> SUBMIT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class S1 primary
```

**Trigger:** Auto-show trong 5 phút sau session end.
**Skip:** Close modal → background task remind sau 24h.
**Auto-rate:** Cron job sau 7 ngày tự rate 5⭐ nếu user chưa rate.
