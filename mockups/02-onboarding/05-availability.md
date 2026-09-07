# Onboarding — Step 4: Availability

> **Mục đích:** Set weekly recurring free slots qua time grid.
> **Phase:** 1 (Onboarding Step 4/7)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
        B[Save & exit]
        A --- B
    end

    subgraph PROGRESS[Progress 4/7]
        P1[✓ ✓ ✓ ✓ ● ● ●]
    end

    subgraph CONTENT[When are you usually free?]
        T1[When are you usually free?<br/>━━━━━━━━━━━━━━━━━━━━━━━━━━]
        TZ[Timezone: Asia/Ho_Chi_Minh ▼]
        T2[Tap slots when you're available each week.]
        
        subgraph GRID[Weekly Grid]
            G1[    Mon Tue Wed Thu Fri Sat Sun<br/>06: [ ] [ ] [ ] [ ] [ ] [ ] [ ]<br/>07: [ ] [ ] [ ] [ ] [ ] [ ] [ ]<br/>08: [✓] [✓] [✓] [✓] [✓] [ ] [ ]<br/>09: [✓] [✓] [✓] [✓] [✓] [ ] [ ]<br/>...<br/>18: [✓] [✓] [ ] [✓] [ ] [ ] [ ]<br/>19: [✓] [✓] [ ] [✓] [ ] [ ] [ ]<br/>20: [✓] [✓] [ ] [✓] [ ] [ ] [ ]<br/>21: [✓] [✓] [ ] [✓] [ ] [ ] [ ]<br/>]
        end

        TOTAL[Total: 18 hours/week]
        TIP[💡 Tip: Set at least 2-3 hours/week to get matches.]

        NAV[← Back    Next →]
        T1 --> TZ
        TZ --> T2
        T2 --> GRID
        GRID --> TOTAL
        TOTAL --> TIP
        TIP --> NAV
    end

    HEADER ==> PROGRESS
    PROGRESS ==> CONTENT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef slot fill:#10B981,stroke:#047857,color:#fff
    class NAV primary
```

**Interactions:**
- Tap cell → toggle
- Drag → multi-select
- Mobile: scroll ngang + pinch zoom
- Hint: show timezone cho user khác
