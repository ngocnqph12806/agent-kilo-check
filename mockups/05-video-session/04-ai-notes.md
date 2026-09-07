# Video Call + AI Notes Panel

> **Mục đích:** Video call với AI Co-Pilot auto-generate notes real-time.

```mermaid
flowchart TB
    subgraph MAIN[Main Video]
        V[┌─────────────────────────────────┐<br/>│      Mai Tran Teacher            │<br/>│                                 │<br/>│  🟢 Connected • 18:42/60:00    │<br/>└─────────────────────────────────┘]
    end

    subgraph PIP[PiP]
        PIP1[┌─────┐<br/>│ You │<br/>└─────┘]
    end

    subgraph SIDEBAR[Sidebar]
        T1[[Notes AI] active]
        T2[[Chat]]
        T3[[Resources]]
        T1 --- T2
        T2 --- T3
    end

    subgraph AI[AI Notes panel]
        AI1[┌──────────────────────────────────┐<br/>│ ✨ AI Co-Pilot is taking notes    │<br/>│                                  │<br/>│ 🕐 18:30 — Session started       │<br││  Topic: Public Speaking intro     │<br││                                  │<br││ 🕐 18:35 — Discussed:             │<br││  • Opening hook techniques        │<br││  • 3-second rule                  │<br││  • Eye contact                    │<br││                                  │<br││ 🕐 18:42 — Action item:           │<br││  • Practice 3-min pitch by Fri    │<br││                                  │<br││ 🟡 Aha moment detected:           │<br││  "feel the pause, don't fill it"  │<br││                                  │<br││ [⚙ Settings] [📋 Copy] [💾 Save] │<br│└──────────────────────────────────┘]
    end

    subgraph CONTROLS[Controls]
        C1[[🎤]]
        C2[[📷]]
        C3[[🖥️]]
        C4[[🎨]]
        C5[[💬]]
        C6[[⚙]]
        C7[[✕ Leave]]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
        C5 --- C6
        C6 --- C7
    end

    MAIN --> PIP
    PIP --> SIDEBAR
    SIDEBAR --> AI
    AI --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class T1 primary
```

**Tech:** AssemblyAI streaming transcription → GPT-4o-mini summarize mỗi 5 phút.
**Opt-in:** User phải bật AI Co-Pilot mỗi session (privacy).
**Post-session:** Save notes vào DB, accessible ở booking detail page.
