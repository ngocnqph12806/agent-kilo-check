# Video Session (Main)

> **Mục đích:** Trải nghiệm học 1-1 qua video call (Daily.co embed).

```mermaid
flowchart TB
    subgraph MAIN[Main Video Area]
        MV[┌─────────────────────────────────┐<br/>│                                 │<br/>│                                 │<br/>│                                 │<br/>│      Mai Tran Teacher            │<br/>│                                 │<br/>│                                 │<br/>│  🟢 Connected • 18:42/60:00    │<br/>│                                 │<br/>└─────────────────────────────────┘]
    end

    subgraph PIP[Picture-in-Picture]
        PIP1[┌────────────┐<br/>│            │<br/>│  You PiP   │<br/>│            │<br/>└────────────┘]
    end

    subgraph SIDEBAR[Side Panel Tabs]
        T1[[Notes] active]
        T2[[Chat]]
        T3[[Resources]]
        T1 --- T2
        T2 --- T3
    end

    subgraph NOTES[Notes panel]
        N1[09:42 — Discussed opening hook techniques]
        N2[09:51 — Action item: practice 3-min pitch by Friday]
        N3[10:05 — Aha moment: 'feel the pause, don't fill it']
        N4[✨ AI Co-Pilot is taking notes]
        N1 --- N2
        N2 --- N3
        N3 --- N4
    end

    subgraph CONTROLS[Control Bar Bottom]
        C1[[🎤 Mic]]
        C2[[📷 Camera]]
        C3[[🖥️ Share]]
        C4[[🎨 Whiteboard]]
        C5[[💬 Chat]]
        C6[[⚙ Settings]]
        C7[[✕ Leave session]]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
        C5 --- C6
        C6 --- C7
    end

    MAIN --> PIP
    PIP --> SIDEBAR
    SIDEBAR --> NOTES
    NOTES --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class T1 primary
    class C7 danger
```

**Tech:** Daily.co React Native SDK (mobile) / React SDK (web).
**Network indicator:** 4 mức: excellent/good/poor/bad.
**Recording opt-in:** Teacher toggle → user consent → start.
