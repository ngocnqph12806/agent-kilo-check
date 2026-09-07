# Voice Wake Screen

> **Mục đích:** Voice assistant ready state, show wake word + mic indicator.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Profile]
        H2[Voice Assistant]
        H1 --- H2
    end

    subgraph CENTER[Center Orb]
        ORB[(🎙️ pulsing orb)]
        PULSE[Animation: subtle pulse]
        ORB --- PULSE
    end

    subgraph LISTEN[Listening state]
        LI1["Hey SkillSeed" — speak naturally]
        LI2[Tap to interrupt]
        LI3[Examples:]
        LI4["Tìm mentor public speaking Singapore"]
        LI5["Book session với Mai tối nay"]
        LI6["Lộ trình học design 6 tháng"]
        LI1 --- LI2
        LI2 --- LI3
        LI3 --- LI4
        LI4 --- LI5
        LI5 --- LI6
    end

    subgraph SETTINGS[Quick settings]
        S1[Language: Auto-detect ▼]
        S2[Voice: Female ▼]
        S3[Wake word: Hey SkillSeed]
        S4[[🎚️ Voice settings →]]
        S1 --- S2
        S2 --- S3
        S3 --- S4
    end

    subgraph RECENT[Recent conversations]
        R1[Sep 11: Booked session with Mai]
        R2[Sep 10: Asked about System Design roadmap]
        R3[Sep 09: Found UX mentor in Singapore]
        R1 --- R2
        R2 --- R3
        RA[[View all history →]]
    end

    HEADER ==> CENTER
    CENTER --> LISTEN
    LISTEN --> SETTINGS
    SETTINGS --> RECENT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class S4,RA primary
```

**Wake word:** "Hey SkillSeed" (Porcupine on-device).
**VAD:** Silero VAD detect speech start/end.
**OpenAI Realtime API:** Audio-in, audio-out native.
