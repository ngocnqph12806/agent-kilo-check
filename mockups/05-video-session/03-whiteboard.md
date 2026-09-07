# Video Call + Whiteboard

> **Mục đích:** Video call với whiteboard overlay cho collaborative drawing.

```mermaid
flowchart TB
    subgraph MAIN[Main Video + Whiteboard overlay]
        V[┌─────────────────────────────────┐<br/>│  Mai Tran (PiP top-right)        │<br/>│                                 │<br/>│  ~~~~ drawing arrows ~~~~       │<br/>│  ~ ✏️ sketch of concept ~        │<br/>│                                 │<br/>│  Toolbar: [Pen] [Eraser] [Color]│<br/>└─────────────────────────────────┘]
    end

    subgraph SIDEBAR[Sidebar]
        T1[[Notes]]
        T2[[Chat]]
        T3[[Whiteboard] active]
        T4[[Resources]]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph WHITEBOARD[Whiteboard panel]
        WB1[┌─────────────────────────────────┐<br/>│                                  │<br/>│   ☐  ☐  ☐  ☐  ☐  ☐  ☐         │<br/>│   ┌──────────────────────────┐  │<br/>│   │   Concept diagram         │  │<br/>│   │   ┌──┐    ┌──┐          │  │<br/>│   │   │A │───►│B │          │  │<br/>│   │   └──┘    └──┘          │  │<br/>│   └──────────────────────────┘  │<br/>│                                  │<br/>│   Tools:                         │<br/>│   [✏️ Pen] [🖌️ Brush] [📏 Line]│<br/>│   [⬜ Rect] [⭕ Circle] [T Text]│<br/>│   [🗑️ Clear] [💾 Save]          │<br/>│   [🎨 Red] [🔵 Blue] [⚫ Black]  │<br/>└─────────────────────────────────┘]
    end

    subgraph CONTROLS[Controls]
        C1[[🎤]]
        C2[[📷]]
        C3[[🖥️]]
        C4[[🎨] active]
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

    MAIN --> SIDEBAR
    SIDEBAR --> WHITEBOARD
    WHITEBOARD --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class T3,C4 primary
```

**Tech:** Excalidraw embed hoặc tự build canvas với Y.js sync.
**Save:** Snapshot whiteboard mỗi 30s, lưu S3 + post-session notes.
