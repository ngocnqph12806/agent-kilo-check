# Onboarding — Done

> **Mục đích:** Celebrate hoàn thành Skill DNA, show summary, trigger 30 starter seeds.
> **Phase:** 1 (Onboarding Step 7/7)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
    end

    subgraph PROGRESS[Done 7/7]
        P1[✓ ✓ ✓ ✓ ✓ ✓ ✓ 🎉]
    end

    subgraph CONTENT[Complete]
        CE[┌──────────────────────┐<br/>│  ✨    ✨    ✨      │<br/>│                      │<br/>│  Your Skill DNA is   │<br/>│       complete!       │<br/>└──────────────────────┘]
        
        T1[Here's your profile summary:]
        
        subgraph SUMMARY[Summary]
            SM1[📊 Public Speaking Lvl 3 — 5 years]
            SM2[💻 Python Lvl 4 — 3 years]
            SM3[🎨 Want to learn: System Design, UX]
            SM4[🕐 18 hours/week available]
            SM5[🌐 VN, EN]
            SM1 --- SM2
            SM2 --- SM3
            SM3 --- SM4
            SM4 --- SM5
        end

        SEEDS[🌱 30 starter seeds added to your wallet!]
        HINT[→ AI is finding your top 10 matches...]
        CTA[Show me my matches →]
        
        CE --> T1
        T1 --> SUMMARY
        SUMMARY --> SEEDS
        SEEDS --> HINT
        HINT --> CTA
    end

    HEADER ==> PROGRESS
    PROGRESS ==> CONTENT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class CTA primary
```

**Trigger events:**
- `user.onboarding_completed`
- `wallet.credited` (30 seeds, expires in 6 months)
- Redirect → `/discover` sau click CTA
