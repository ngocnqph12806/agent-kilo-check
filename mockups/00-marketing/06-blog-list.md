# Blog List

> **Mục đích:** Content marketing — blog posts về skill development, founder stories.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed]
        H2[[Login]]
    end

    subgraph TITLE[Title]
        T1[SkillSeed Blog]
        T2[Stories on learning, teaching, and the future of work]
        T1 --- T2
    end

    subgraph FEATURED[Featured post]
        F1[📌 How I learned Public Speaking from 5 strangers in 3 countries]
        F2[By Mai Tran · 8 min read · Sep 8 2026]
        F3[[Read article →]]
        F1 --- F2
        F2 --- F3
    end

    subgraph CATS[Categories]
        C1[[🌱 Skill Stories]]
        C2[[💡 Learning Tips]]
        C3[[🚀 Founder Notes]]
        C4[[🌍 Global Voices]]
        C5[[📊 Research]]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
    end

    subgraph POSTS[Recent posts]
        P1[Sep 12 — Why I switched from $200 courses to peer learning<br/>3 min read · Learning Tips]
        P2[Sep 10 — How we built AI matching without bias<br/>8 min read · Founder Notes]
        P3[Sep 08 — Featured story: How I learned...<br/>5 min read · Skill Stories]
        P4[Sep 05 — What I learned from 100 user interviews<br/>7 min read · Founder Notes]
        P5[Sep 01 — The Seed economy explained<br/>4 min read · Research]
        P1 --- P2
        P2 --- P3
        P3 --- P4
        P4 --- P5
        LD[[Load more]]
    end

    subgraph NEWSLETTER[Newsletter]
        N1[📧 Get weekly insights in your inbox]
        N2[Email: mai@acme.vn]
        N3[[Subscribe]]
        N1 --- N2
        N2 --- N3
    end

    HEADER ==> TITLE
    TITLE --> FEATURED
    FEATURED --> CATS
    CATS --> POSTS
    POSTS --> NEWSLETTER

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class F3,N3 primary
```

**SEO:** hreflang tags, structured data (Article schema).
**Multi-lang:** VI, EN, ID, PH, ZH versions.
