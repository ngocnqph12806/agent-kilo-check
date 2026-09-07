# In-App Feedback Widget

> **Mục đích:** Persistent floating button → modal feedback.

```mermaid
flowchart TB
    subgraph APP[App screen with widget]
        A[Any page background]
        W[┌────────┐<br/>│ 💬     │<br/>│ Feedback│<br/>└────────┘]
        A --> W
    end

    subgraph MODAL[Feedback Modal opens on click]
        H[× Help us improve!]
        T[What's on your mind?]
        OPT1[( ) 💡 Suggestion]
        OPT2[( ) 🐛 Bug report]
        OPT3[( ) ❓ Question]
        OPT4[( ) 💬 Other]
        OPT1 --- OPT2
        OPT2 --- OPT3
        OPT3 --- OPT4
        TXT[Tell us more:<br/>┌──────────────────────────┐<br/>│                          │<br/>│                          │<br/>└──────────────────────────┘]
        SCR[📸 Screenshot optional]
        EMA[📧 Email optional for follow-up]
        SB[[Submit]]
        H --> T
        T --> OPT4
        OPT4 --> TXT
        TXT --> SCR
        SCR --> EMA
        EMA --> SB
    end

    subgraph THANK[Thank you]
        TH1[✅ Thanks for your feedback!]
        TH2[We'll review and follow up if needed.]
        TH3[Continue using SkillSeed →]
        TH1 --- TH2
        TH2 --- TH3
    end

    W ==> MODAL
    MODAL --> THANK

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class W,SB,TH3 primary
```

**Routing:** Auto-categorize → Linear (bugs) / Notion (suggestions) / help center (questions).
**Persistence:** Floating button on every page except video session.
