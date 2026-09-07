# Voice Conversation

> **Mục đích:** Real-time voice conversation với AI assistant (OpenAI Realtime API).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Voice]
        H2[SkillSeed Assistant]
        H1 --- H2
    end

    subgraph ORB[Animated orb center]
        O1[(🎙️ active)]
        WAVE[Waveform visualization]
        O1 --- WAVE
    end

    subgraph LIVE[Live transcript]
        L1[18:42 You: Tôi muốn học public speaking]
        L2[18:42 AI: Tuyệt vời! Bạn muốn học với mentor ở quốc gia nào?]
        L3[18:43 You: Singapore, giờ GMT+7]
        L4[18:43 AI: Tôi tìm thấy 5 mentor Public Speaking ở Singapore]
        L5[18:43 AI: Top 1 là Mai Tran với rating 4.9. Bạn muốn book?]
        L1 --- L2
        L2 --- L3
        L3 --- L4
        L4 --- L5
    end

    subgraph TOOLS[Tools used by AI]
        T1[[search_mentors tool]]
        T2[[book_session tool]]
        T3[[get_user_profile tool]]
        T1 --- T2
        T2 --- T3
    end

    subgraph CONTROLS[Controls bottom]
        C1[[🎤 Mute]]
        C2[[📷 Camera off]]
        C3[[💬 Text mode]]
        C4[[⏹ End conversation]]
        C1 --- C2
        C2 --- C3
        C3 --- C4
    end

    HEADER ==> ORB
    ORB --> LIVE
    LIVE --> TOOLS
    TOOLS --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class C4 danger
```

**Barge-in:** User có thể ngắt lời AI.
**Tools:** Function calling → real backend actions (search, book).
**Latency target:** < 1.5s end-to-end.
