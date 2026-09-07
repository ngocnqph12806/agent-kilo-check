# AR Session — Cooking Overlay

> **Mục đích:** AR overlay cho cooking sessions với step-by-step recipe.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Back]
        H2[AR Session with Chef Mai]
        H1 --- H2
    end

    subgraph CAMERA[Camera View AR overlay]
        C[┌──────────────────────────────────┐<br/>│                                  │<br/>│   📷 Live camera view            │<br/>│                                  │<br/>│   ┌─────────────────────────┐   │<br/>│   │ Step 2 of 5             │   │<br/>│   │ ────────●──────        │   │<br/>│   │ "Add 2 tbsp oil"        │   │<br/>│   │                          │   │<br││   │ ⏱ 00:30 in step          │   │<br││   └─────────────────────────┘   │<br││                                  │<br││   ┌─────────────────────────┐   │<br││   │ 👨‍🍳 Chef Mai (PiP)        │   │<br││   └─────────────────────────┘   │<br││                                  │<br│└──────────────────────────────────┘]
    end

    subgraph ANCHOR[AR Anchor markers]
        A1[🟢 Pan location highlighted]
        A2[🟢 Stove knob indicator]
        A3[🟢 Ingredient bowl position]
        A1 --- A2
        A2 --- A3
    end

    subgraph SIDEBAR[Sidebar]
        T1[[📋 Recipe 5 steps] active]
        T2[[📷 Camera controls]]
        T3[[💬 Chat with chef]]
        T4[[🔧 Tools: timer ↩ undo]]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph STEPS[Recipe Steps]
        S1[✓ Step 1: Heat pan 2 min]
        S2[● Step 2: Add oil 30 sec]
        S3[○ Step 3: Add garlic]
        S4[○ Step 4: Stir-fry veg]
        S5[○ Step 5: Plate]
        S1 --- S2
        S2 --- S3
        S3 --- S4
        S4 --- S5
    end

    subgraph CONTROLS[AR Controls]
        CO1[[📷 Capture photo]]
        CO2[[📹 Record video]]
        CO3[[👆 Place anchor]]
        CO4[[✏️ Draw annotation]]
        CO5[[✕ End session]]
        CO1 --- CO2
        CO2 --- CO3
        CO3 --- CO4
        CO4 --- CO5
    end

    HEADER ==> CAMERA
    CAMERA --> ANCHOR
    ANCHOR --> SIDEBAR
    SIDEBAR --> STEPS
    STEPS --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class T1,CO3 primary
    class CO5 danger
```

**Tech:** WebXR + ARKit/ARCore native SDKs.
**Multi-user:** Anchor positions sync giữa 2 devices via WebRTC.
**Templates:** Yoga, cooking, repair, makeup, languages.
