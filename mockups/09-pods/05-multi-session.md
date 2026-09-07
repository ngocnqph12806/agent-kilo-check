# Pod Multi-Participant Session

> **Mục đích:** Video call với 4-8 participants (Daily.co room max 200).

```mermaid
flowchart TB
    subgraph GRID[Video Grid 2x2 / 3x3 dynamic]
        P1[┌────────────┐<br/>│ Mai Tran   │<br/>│ HOST 🎤    │<br/>└────────────┘]
        P2[┌────────────┐<br/>│ Duc        │<br/>└────────────┘]
        P3[┌────────────┐<br/>│ Linh       │<br/>└────────────┘]
        P4[┌────────────┐<br/>│ An         │<br│└────────────┘]
        P5[┌────────────┐<br/>│ + 4 more   │<br/>└────────────┘]
        P1 --- P2
        P2 --- P3
        P3 --- P4
        P4 --- P5
    end

    subgraph SIDEBAR[Sidebar]
        T1[[Notes]]
        T2[[Chat 23]]
        T3[[Resources]]
        T4[[Participants 8]]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph PARTICIPANTS[Participants panel]
        PA1[👑 Mai Tran Host]
        PA2[👤 Duc Nguyen]
        PA3[👤 Linh Tran]
        PA4[👤 An Pham]
        PA5[👤 + 4 others]
        PA6[[🎤 Mute all non-host]]
        PA1 --- PA2
        PA2 --- PA3
        PA3 --- PA4
        PA4 --- PA5
        PA5 --- PA6
    end

    subgraph CONTROLS[Host controls]
        C1[[🎤 Mute me]]
        C2[[📷 Cam off]]
        C3[[🖥️ Share]]
        C4[[🎨 Whiteboard]]
        C5[[💬 Chat]]
        C6[[👥 Participants]]
        C7[[⏺️ Record]]
        C8[[✕ End Pod]]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
        C5 --- C6
        C6 --- C7
        C7 --- C8
    end

    GRID --> SIDEBAR
    SIDEBAR --> PARTICIPANTS
    PARTICIPANTS --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class C6 primary
    class C8 danger
```

**Capacity:** Tối đa 8 participants per pod session.
**Host controls:** Mute all, record, end session.
**Layout:** Auto grid 2x2 → 3x3 → 4x2 dynamic theo count.
