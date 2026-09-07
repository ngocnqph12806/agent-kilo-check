# VR Avatar Lobby

> **Mục đích:** Pre-session lobby trong VR — chọn avatar, test mic/cam.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Pod Session]
        H2[🎭 VR Lobby]
        H1 --- H2
    end

    subgraph AVATAR[Choose avatar]
        AV1[(🦊 Fox)]
        AV2[(🐼 Panda)]
        AV3[(🐯 Tiger)]
        AV4[(🦄 Unicorn)]
        AV5[(🧙 Wizard)]
        AV6[(👨‍🚀 Astronaut)]
        AV1 --- AV2
        AV2 --- AV3
        AV3 --- AV4
        AV4 --- AV5
        AV5 --- AV6
        SELECTED[[✓ Mai Avatar selected]]
    end

    subgraph PREVIEW[3D Preview]
        PR[┌──────────────────────────────────┐<br/>│                                  │<br/>│     3D avatar preview rendering   │<br/>│                                  │<br││     ╭───╮                       │<br││     │ M │ ← Your avatar         │<br││     ╰─┬─╯                       │<br││       │                          │<br││      ╱ ╲                         │<br││                                  │<br││   [Rotate] [Zoom] [Customize]   │<br│└──────────────────────────────────┘]
    end

    subgraph TEST[Test devices]
        T1[🎤 Microphone: ✓ working]
        T2[📷 Camera: ✓ detected]
        T3[🔊 Speakers: ✓ audio test]
        T4[🌐 Network: 56ms ✓ excellent]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph PARTICIPANTS[Participants 4/8]
        P1[(🦊 Mai Host ✓ ready]
        P2[(🐼 Duc ✓ ready]
        P3[(🐯 Linh ⚠️ mic issue]
        P4[(🦄 An ✓ ready]
        P1 --- P2
        P2 --- P3
        P3 --- P4
    end

    subgraph START[Enter VR space]
        ST1[[🎬 Enter Pod Session →]]
        ST2[Or wait for all to be ready]
        ST1 --- ST2
    end

    HEADER ==> AVATAR
    AVATAR --> PREVIEW
    PREVIEW --> TEST
    TEST --> PARTICIPANTS
    PARTICIPANTS --> START

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class SELECTED,ST1 primary
    class P3 warning
```

**Tech:** Meta Quest / Apple Vision Pro native SDK.
**Avatar:** Pre-built library hoặc custom via Ready Player Me.
