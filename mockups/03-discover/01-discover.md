# Discover (Home)

> **Mục đích:** Daily AI-matched list. Entry point sau onboarding.
> **Phase:** 1 (Main App — Default landing sau login)

```mermaid
flowchart TB
    subgraph HEADER[Top Bar]
        H1[🌱 SkillSeed]
    end

    subgraph TABS[Bottom Tabs]
        T1[🏠 Discover ●]
        T2[📅 Bookings]
        T3[💰 Wallet]
        T4[👤 Profile]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph SEARCH[Search Row]
        S1[🔍 Search skills or people...]
        S2[⚙ Filter]
        S1 --- S2
    end

    subgraph TOPMATCHES[✨ Top matches for you today]
        TH[Why these? AI picked based on your Skill DNA + schedule.]
        
        subgraph CARD1[Match Card 1]
            M1[Avatar] --> M2[Mai Tran ⭐ 4.9 23 sessions]
            M2 --> M3[🌳 Tree tier ✓ Verified 🇻🇳 VN]
            M3 --> M4[Teaches Public Speaking, Storytelling]
            M4 --> M5[🕐 Free today 6-9 PM]
            M5 --> M6[ℹ️ Why we matched: 92% match...]
            M6 --> M7[Book session →  View profile]
        end

        subgraph CARD2[Match Card 2]
            N1[Avatar] --> N2[John Smith ⭐ 4.8 47 sessions]
            N2 --> N3[🌲 Forest tier ✓ Verified 🇸🇬 SG]
            N3 --> N4[Teaches Python, System Design]
            N4 --> N5[🕐 Free tomorrow 7-10 AM]
            N5 --> N6[Book session →  View profile]
        end

        SHOW[Show 8 more matches]
    end

    subgraph TOPMENTORS[🎓 Top mentors this week]
        TM1[chip: Public Speaking]
        TM2[chip: Python]
        TM3[chip: UX Design]
        TM4[chip: Yoga]
        TM5[chip: Photography]
        TM1 --- TM2
        TM2 --- TM3
        TM3 --- TM4
        TM4 --- TM5
    end

    HEADER ==> TABS
    TABS ==> SEARCH
    SEARCH ==> TOPMATCHES
    TOPMATCHES ==> TOPMENTORS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class T1 primary
    class M7,N6 primary
```

**States:**
- **Loading:** Skeleton cards (5 placeholders)
- **Empty:** "No matches yet — try widening filters"
- **Cold start (new user):** Show "Founding Mentor" badge
- **Premium:** AI insights badge per match
