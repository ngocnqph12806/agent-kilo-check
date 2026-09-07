# Bookings List

> **Mục đích:** Quản lý sessions sắp tới + lịch sử qua 3 tabs.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed]
    end

    subgraph TABS[Bottom Tabs]
        T1[🏠 Discover]
        T2[📅 Bookings ●]
        T3[💰 Wallet]
        T4[👤 Profile]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph TABFILTER[Top Tabs]
        F1[[Upcoming] active]
        F2[[Past]]
        F3[[Cancelled]]
        F1 --- F2
        F2 --- F3
    end

    subgraph TODAY[─── Today ───]
        C1[⏰ Starts in 2 hours<br/>Avatar] --> C2[Mai Tran — Public Speaking]
        C2 --> C3[📅 Today 19:00-20:00 60 min]
        C3 --> C4[Status: ✅ Confirmed]
        C4 --> C5[Join session →  Reschedule  Cancel]
    end

    subgraph TOMORROW[─── Tomorrow ───]
        D1[Avatar] --> D2[John Smith — Python]
        D2 --> D3[📅 Tomorrow 10:00-10:30 30 min]
        D3 --> D4[Status: ⏳ Pending awaiting acceptance]
        D4 --> D5[Cancel request]
    end

    subgraph WEEK[─── This week ───]
        E1[Avatar] --> E2[An Nguyen — UX Research]
        E2 --> E3[📅 Saturday 14:00-15:00 60 min]
        E3 --> E4[Status: ✅ Confirmed]
        E4 --> E5[Join session →  Cancel]
    end

    HEADER ==> TABS
    TABS ==> TABFILTER
    TABFILTER ==> TODAY
    TODAY --> TOMORROW
    TOMORROW --> WEEK

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class T2 primary
    class C5,E5 primary
```

**States per booking:**
- `pending` → Awaiting acceptance, auto-decline in 24h
- `confirmed` → Countdown + Join button enabled 10min before
- `in_progress` → Green border, "Session live"
- `completed` → Show rating form
- `cancelled` → Muted, "Refunded X seeds"
- `no_show` → Red border
