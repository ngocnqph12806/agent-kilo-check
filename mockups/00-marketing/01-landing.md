# Landing Page

> **Mục đích:** Convert visitor → email signup. Hero CTA là primary conversion.
> **Phase:** 0 (Marketing)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed Logo]
        H2[Login]
        H3[Get Started]
        H1 --> H2
        H2 --> H3
    end

    subgraph HERO[Hero Section]
        H4[Teach what you know.<br/>Learn what you love.<br/>Pay with your time, not your wallet.]
        H5[📧 Email Input]
        H6[Get Early Access →]
        H7[🌱 30 free starter seeds when you join]
        H4 --> H5
        H5 --> H6
        H6 --> H7
    end

    subgraph PROBLEM[The Problem]
        P1[❌ Online courses are expensive $200-$2,000]
        P2[❌ Hard to find a mentor who actually teaches]
        P3[❌ Lonely online learning — no real connection]
        P1 --- P2
        P2 --- P3
    end

    subgraph SOLUTION[The Solution: 3 Pillars]
        S1[💚 Money-Less<br/>Trade time for time]
        S2[🤖 AI-Matched<br/>Find perfect matches]
        S3[✓ Verified<br/>Skill Passport on-chain]
        S1 --- S2
        S2 --- S3
    end

    subgraph HOW[How It Works — 3 Steps]
        W1[① Create Skill DNA<br/>3-min form]
        W2[② AI Matching<br/>Top 10 mentors]
        W3[③ Book Session<br/>Video call 1-1]
        W1 --> W2
        W2 --> W3
    end

    subgraph BENEFITS[Benefits]
        B1[For Learners:<br/>• Free 1-1 learning<br/>• Personalized<br/>• Global mentors]
        B2[For Teachers:<br/>• Teach when free<br/>• Earn Seeds<br/>• Build network]
        B1 --- B2
    end

    subgraph FAQ[FAQ — 6 Questions]
        F1[▸ Is SkillSeed really free?]
        F2[▸ How do Skill Seeds work?]
        F3[▸ What if I don't have skills to teach?]
        F4[▸ Is it safe?]
        F5[▸ Which countries?]
        F6[▸ Can I become Premium mentor?]
        F1 --- F2
        F2 --- F3
        F3 --- F4
        F4 --- F5
        F5 --- F6
    end

    subgraph FINALCTA[Final CTA]
        FC1[Ready to teach what you know?]
        FC2[📧 Email Input]
        FC3[Join Waitlist →]
        FC1 --> FC2
        FC2 --> FC3
    end

    subgraph FOOTER[Footer]
        FT1[Privacy | Terms | Contact | Twitter | LinkedIn]
        FT2[© 2026 SkillSeed]
        FT1 --> FT2
    end

    HEADER ==> HERO
    HERO ==> PROBLEM
    PROBLEM ==> SOLUTION
    SOLUTION ==> HOW
    HOW ==> BENEFITS
    BENEFITS ==> FAQ
    FAQ ==> FINALCTA
    FINALCTA ==> FOOTER

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef muted fill:#F3F4F6,stroke:#D1D5DB,color:#374151
    class H6,FC3,H3 primary
    class PROBLEM,FAQ muted
```

**Annotations:**
- CTA copy variations để A/B test: "Get Early Access" / "Join Waitlist" / "Start Free"
- Tracking: GA4 events `hero_cta_click`, `scroll_depth`, `email_signup`
- Mobile: stack sections vertically, hero image under CTA
- Cookie banner hiển thị 1 lần đầu tiên với Accept/Decline
