# VR Whiteboard

> **Mục đích:** 3D whiteboard trong VR — collaborative drawing trong không gian.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Back]
        H2[🎨 VR Whiteboard — Pod: Public Speaking Club]
        H1 --- H2
    end

    subgraph SPACE[3D Space]
        SP[┌──────────────────────────────────┐<br/>│                                  │<br││  🦊 Mai ────► [WHITEBOARD]       │<br││                                  │<br││  🐼 Duc ────► collaborative    │<br││                                  │<br││  🐯 Linh ────► drawing          │<br││                                  │<br││  [3D whiteboard with sticky      │<br││   notes + diagrams in space]    │<br││                                  │<br│└──────────────────────────────────┘]
    end

    subgraph TOOLS[VR Tools]
        T1[[✏️ Pen]]
        T2[[📝 Sticky note]]
        T3[[🔷 Shape]]
        T4[[T Text]]
        T5[[🖼️ Image]]
        T6[[🗑️ Erase]]
        T7[[💾 Save]]
        T8[[↩ Undo]]
        T1 --- T2
        T2 --- T3
        T3 --- T4
        T4 --- T5
        T5 --- T6
        T6 --- T7
        T7 --- T8
    end

    subgraph CONTENT[Whiteboard content]
        C1[🟡 Sticky: "Opening hooks"]
        C2[🟢 Sticky: "Body language"]
        C3[🔵 Diagram: speech structure]
        C4[📝 Notes: 3-second rule + pause]
        C1 --- C2
        C2 --- C3
        C3 --- C4
    end

    subgraph COLLAB[Collaboration]
        CB1[👥 4 users drawing]
        CB2[🎨 Color assigned per user]
        CB3[💾 Auto-save every 30s]
        CB4[📥 Export PNG PDF]
        CB1 --- CB2
        CB2 --- CB3
        CB3 --- CB4
    end

    subgraph CONTROLS[Controls]
        CT1[Right hand: tools]
        CT2[Left hand: navigate]
        CT3[[✕ Exit VR]]
        CT1 --- CT2
        CT2 --- CT3
    end

    HEADER ==> SPACE
    SPACE --> TOOLS
    TOOLS --> CONTENT
    CONTENT --> COLLAB
    COLLAB --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class T1,T2,T3,T4,T5,T7,CT3 primary
```

**Multi-user sync:** Y.js CRDT cho collaborative state.
**Spatial 3D:** Whiteboard là 3D plane trong không gian — walk around.
