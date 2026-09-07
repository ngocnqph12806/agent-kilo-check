# AR Yoga Overlay

> **Mục đích:** AR form correction cho yoga sessions.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Back]
        H2[AR Yoga with Sarah]
        H1 --- H2
    end

    subgraph CAMERA[Camera + AR Avatar]
        C[┌──────────────────────────────────┐<br/>│                                  │<br/>│  📷 Live camera (back camera)    │<br/>│                                  │<br││      🧘 Sarah avatar (3D)        │<br││      ↓ mirrors your position ↓   │<br││                                  │<br││  ┌─────────────────────────┐    │<br││  │ ✏️ Pose feedback:        │    │<br││  │ "Lower your hips 5cm"   │    │<br││  │ "Align knees over toes" │    │<br││  │ Score: 78/100           │    │<br││  └─────────────────────────┘    │<br││                                  │<br│└──────────────────────────────────┘]
    end

    subgraph POSES[Current Sequence]
        P1[1. Mountain Pose]
        P2[● 2. Warrior II — Current]
        P3[○ 3. Triangle]
        P4[○ 4. Downward Dog]
        P5[○ 5. Child's Pose]
        P1 --- P2
        P2 --- P3
        P3 --- P4
        P4 --- P5
    end

    subgraph FEEDBACK[Pose Feedback]
        F1[✓ Heel alignment]
        F2[⚠️ Hip tilt left side]
        F3[⚠️ Arms not parallel to floor]
        F4[✗ Breath — exhale on transition]
        F1 --- F2
        F2 --- F3
        F3 --- F4
    end

    subgraph SIDEBAR[Sidebar]
        T1[[🧘 Poses 5] active]
        T2[[📷 Camera switch front/back]]
        T3[[💬 Chat with Sarah]]
        T4[[📊 My form score]]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph CONTROLS[Controls]
        CO1[[⏸ Pause]]
        CO2[[⏭ Next pose]]
        CO3[[🔄 Reset position]]
        CO4[[✕ End session]]
        CO1 --- CO2
        CO2 --- CO3
        CO3 --- CO4
    end

    HEADER ==> CAMERA
    CAMERA --> POSES
    POSES --> FEEDBACK
    FEEDBACK --> SIDEBAR
    SIDEBAR --> CONTROLS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class T1 primary
    class F2,F3 warning
    class F4 danger
```

**Pose detection:** MediaPipe BlazePose + custom scoring model.
**3D avatar:** Teacher's recorded avatar synced to student's pose.
