# Video Call + Chat Panel

> **Mục đích:** Video call với chat panel mở rộng.

```mermaid
flowchart TB
    subgraph MAIN[Main Video]
        MV[┌─────────────────────────────────┐<br/>│                                 │<br/>│                                 │<br/>│      Mai Tran Teacher            │<br/>│                                 │<br/>│                                 │<br/>│  🟢 Connected • 18:42/60:00    │<br/>└─────────────────────────────────┘]
    end

    subgraph PIP[PiP]
        PIP1[┌─────┐<br/>│ You │<br/>└─────┘]
    end

    subgraph CHATPANEL[Chat Panel right side]
        T1[[Notes]]
        T2[[Chat] active]
        T3[[Resources]]
        T1 --- T2
        T2 --- T3
    end

    subgraph MESSAGES[Messages]
        M1[18:30 Mai: Hey Mai! Ready to start?]
        M2[18:31 You: Yes, super excited!]
        M3[18:35 Mai: Let's begin with an introduction round]
        M4[18:40 You: I'll share screen with my slides]
        M5[18:42 Mai: Perfect go ahead]
        M1 --- M2
        M2 --- M3
        M3 --- M4
        M4 --- M5
    end

    subgraph INPUT[Input]
        INP[Type a message...]
        SEND[[Send]]
        ATTACH[[📎]]
        INP --- ATTACH
        ATTACH --- SEND
    end

    subgraph CONTROLS[Controls]
        C1[[🎤]]
        C2[[📷]]
        C3[[🖥️]]
        C4[[🎨]]
        C5[[💬] active]
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
    PIP --> CHATPANEL
    CHATPANEL --> MESSAGES
    MESSAGES --> INPUT
    INPUT --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class T2 primary
    class C5 primary
    class C7 danger
```

**Persistence:** Chat messages lưu DB, accessible sau session.
**File share:** Upload PDF/images qua Resources tab.
