# AR Repair Overlay

> **Mục đích:** AR overlay cho repair sessions (bike, appliance, electronics).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Back]
        H2[AR Repair with Tom]
        H1 --- H2
    end

    subgraph CAMERA[Camera + AR overlay]
        C[┌──────────────────────────────────┐<br/>│                                  │<br/>│  📷 Live camera (back camera)    │<br/>│                                  │<br││      🔧 Tom (PiP top-right)      │<br││                                  │<br││   ┌─────────────────────────┐   │<br││   │ ⚠️ Step 3: Disassemble  │   │<br││   │                          │   │<br││   │ Remove 4 screws marked  │   │<br││   │ with 🟢 green circles   │   │<br││   │                          │   │<br││   │ Use screwdriver #2      │   │<br││   └─────────────────────────┘   │<br││                                  │<br││   🟢 screw 1                     │<br││   🟢 screw 2                     │<br││   🟢 screw 3                     │<br││   🟢 screw 4                     │<br│└──────────────────────────────────┘]
    end

    subgraph STEPS[Repair steps]
        S1[✓ Step 1: Position bike]
        S2[✓ Step 2: Remove wheel]
        S3[● Step 3: Disassemble hub Current]
        S4[○ Step 4: Replace bearing]
        S5[○ Step 5: Reassemble]
        S6[○ Step 6: Test spin]
        S1 --- S2
        S2 --- S3
        S3 --- S4
        S4 --- S5
        S5 --- S6
    end

    subgraph TOOLS[Tools tracker]
        T1[🔧 Screwdriver #2 — using]
        T2[🔧 Wrench 10mm — needed next]
        T3[🧴 Grease — apply step 5]
        T4[🦺 Safety glasses — ✓ on]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph SIDEBAR[Sidebar]
        TB1[[🔩 Parts 3] active]
        TB2[[💬 Chat with Tom]]
        TB3[[⏱ Timer 8:32 / 60min]]
        TB4[[📸 Photo evidence]]
        TB1 --- TB2
        TB2 --- TB3
        TB3 --- TB4
    end

    subgraph CONTROLS[Controls]
        CT1[[📷 Photo]]
        CT2[[📹 Record]]
        CT3[[🔍 Zoom +]]
        CT4[[🔍 Zoom -]]
        CT5[[✕ End]]
        CT1 --- CT2
        CT2 --- CT3
        CT3 --- CT4
        CT4 --- CT5
    end

    HEADER ==> CAMERA
    CAMERA --> STEPS
    STEPS --> TOOLS
    TOOLS --> SIDEBAR
    SIDEBAR --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class TB1,CT1 primary
    class T4 warning
```

**Use case:** Bike repair, electronics, home appliances.
**AR anchor:** Pre-placed tại vị trí screws/components cần tương tác.
