# Onboarding — Step 3: Goals & Learning Style

> **Mục đích:** Thu thập main goal + learning style + bio.
> **Phase:** 1 (Onboarding Step 3/7)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
        B[Save & exit]
        A --- B
    end

    subgraph PROGRESS[Progress 3/7]
        P1[✓ ✓ ✓ ● ● ● ●]
    end

    subgraph CONTENT[Goals & Style]
        T1[Your goals & learning style<br/>━━━━━━━━━━━━━━━━━━━━━━━━]
        
        subgraph GOAL[Main goal]
            G1[( ) 🎯 Career advancement]
            G2[( ) 🌱 Personal hobby]
            G3[( ) 🔄 Career switch]
            G4[( ) 💼 Build a business]
            G5[( ) 🧠 Just curious]
            G1 --- G2
            G2 --- G3
            G3 --- G4
            G4 --- G5
        end

        subgraph STYLE[Learning style]
            ST1[( ) 👀 Visual diagrams videos]
            ST2[( ) 👂 Auditory discussions podcasts]
            ST3[( ) 📖 Reading text articles]
            ST4[( ) ✋ Kinesthetic practice hands-on]
            ST1 --- ST2
            ST2 --- ST3
            ST3 --- ST4
        end

        BIO[Bio optional<br/>┌──────────────────────────┐<br/>│ Tell others about...      │<br/>│              0/500 chars │<br/>└──────────────────────────┘]

        NAV[← Back    Next →]
        T1 --> GOAL
        GOAL --> STYLE
        STYLE --> BIO
        BIO --> NAV
    end

    HEADER ==> PROGRESS
    PROGRESS ==> CONTENT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef selected fill:#10B981,stroke:#047857,color:#fff
    class NAV primary
```

**Single select** cho cả goal và style.
