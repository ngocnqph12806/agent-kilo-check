# Premium Success

> **Mục đích:** Confirm subscription active + next steps.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed]
    end

    subgraph SUCCESS[Success]
        S1[(✅)]
        S2[Welcome to Premium!]
        S3[Bạn là Premium member từ hôm nay.]
        S4[Benefits unlocked:]
        B1[✓ Unlimited booking]
        B2[✓ AI Co-Pilot unlimited]
        B3[✓ Priority matching]
        B4[✓ AI insights]
        B5[✓ No ads]
        B1 --- B2
        B2 --- B3
        B3 --- B4
        B4 --- B5
        S1 --- S2
        S2 --- S3
        S3 --- S4
        S4 --- B1
    end

    subgraph NEXT[Next]
        N1[📅 Next billing: Oct 7, 2026 $10.89]
        N2[[Go to Discover → see unlimited matches]]
        N3[[Manage subscription]]
        N1 --- N2
        N2 --- N3
    end

    subgraph SHARE[Share]
        SH1[Tell your friends! Share SkillSeed →]
        SH2[[Share on LinkedIn]] --- SH3[[Copy invite link]]
    end

    HEADER ==> SUCCESS
    SUCCESS --> NEXT
    NEXT --> SHARE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class N2 primary
```

**Trigger:** `checkout.session.completed` webhook → activate + send email + show this page.
**Welcome bonus:** 30 extra seeds + Premium badge in profile.
