# Public User Profile

> **Mục đích:** Xem chi tiết user khác → quyết định book session.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Back]
    end

    subgraph HERO[Profile Hero]
        AV[┌────────┐<br/>│ Avatar │<br/>└────────┘]
        N1[Mai Tran]
        N2[🌳 Tree ✓ Verified 🇻🇳 Vietnam]
        N3[⭐ 4.9 23 sessions]
        N4[💚 145 sessions taught]
        AV --> N1
        N1 --> N2
        N2 --> N3
        N3 --> N4
    end

    subgraph BIO[Bio]
        B1["Product designer passionate about helping others<br/>find their voice. 8 years experience in public speaking."]
    end

    subgraph SKILLS[Skills I teach]
        S1[• Public Speaking Lvl 5, 8y, 60 seeds/h]
        S2[• Storytelling Lvl 4, 6y, 60 seeds/h]
        S3[• Presentation Design Lvl 4, 5y, 80 seeds/h]
        S1 --- S2
        S2 --- S3
    end

    subgraph LANGS[Languages]
        L1[Vietnamese native, English fluent]
    end

    subgraph AVAIL[Availability next 7 days]
        AV1[Mon ●●●○○ 18:00-21:00<br/>Tue ●●●○○ 18:00-21:00<br/>Wed ●●○○○ 19:00-20:30<br/>Thu ●●●○○ 18:00-21:00<br/>Fri ✗ -<br/>Sat ✗ -<br/>Sun ●●●○○ 14:00-18:00<br/><br/>Timezone: GMT+7 your time]
    end

    subgraph REVIEWS[Recent reviews]
        R1[⭐⭐⭐⭐⭐ "Amazing mentor! Helped me land a<br/>presentation at Google." — Linh N., 2w ago]
        R2[⭐⭐⭐⭐⭐ "Patient and clear. Highly recommend."<br/>— Duc M., 1mo ago]
        R1 --- R2
    end

    subgraph PASSPORT[Skill Passport]
        P1[✓ Public Speaking  View passport →]
    end

    subgraph ACTIONS[CTA]
        BK[Book a session →  60 seeds = 1 hour]
        SEC[💬 Message] --- SEC2[🚩 Report]
    end

    HEADER ==> HERO
    HERO --> BIO
    BIO --> SKILLS
    SKILLS --> LANGS
    LANGS --> AVAIL
    AVAIL --> REVIEWS
    REVIEWS --> PASSPORT
    PASSPORT ==> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class BK primary
```

**SEO:** Public page có Open Graph tags.
**Verified badge:** Click → expand to verification details.
