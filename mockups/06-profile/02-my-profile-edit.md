# My Profile (Edit)

> **Mục đích:** Xem + cập nhật Skill DNA bất kỳ lúc nào.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Profile]
        H2[[Edit] [⚙]]
        H1 --- H2
    end

    subgraph HERO[Profile Hero]
        AV[Avatar]
        N1[Mai Tran 🌳 Tree ✓ Verified]
        N2[⭐ 4.9 23 sessions]
        N3[🌱 75 seeds]
        AV --> N1
        N1 --> N2
        N2 --> N3
    end

    subgraph BIO[Bio]
        B1["Product designer passionate about..."]
    end

    subgraph TEACH[Skills I teach 2]
        T1[• Public Speaking Lvl 3, 5y [Edit] [×]]
        T2[• Python Lvl 4, 3y [Edit] [×]]
        T3[[+ Add]]
        T1 --- T2
        T2 --- T3
    end

    subgraph LEARN[Skills I want to learn 3]
        L1[• System Design Prio 4 [Edit] [×]]
        L2[• UX Research Prio 3 [Edit] [×]]
        L3[• Business English Prio 3 [Edit] [×]]
        L4[[+ Add]]
        L1 --- L2
        L2 --- L3
        L3 --- L4
    end

    subgraph AVAIL[Availability]
        A1[18 hours/week [Edit] ⚙]
    end

    subgraph LANGS[Languages]
        LG[Vietnamese, English [Edit] ⚙]
    end

    subgraph COUNTRY[Country]
        C1[🇻🇳 Vietnam [Edit] ⚙]
    end

    subgraph PUBLIC[Public profile]
        P1[[View public page →]]
    end

    HEADER ==> HERO
    HERO --> BIO
    BIO --> TEACH
    TEACH --> LEARN
    LEARN --> AVAIL
    AVAIL --> LANGS
    LANGS --> COUNTRY
    COUNTRY --> PUBLIC

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class P1 primary
```

**Edit mode:** Tap Edit → chuyển sang form inline (không modal, UX mượt hơn).
**Auto-save:** Mỗi field tự save sau 1s không gõ tiếp.
