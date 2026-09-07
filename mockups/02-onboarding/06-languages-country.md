# Onboarding — Step 5: Languages & Country

> **Mục đích:** Multi-select languages + chọn country.
> **Phase:** 1 (Onboarding Step 5/7)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
        B[Save & exit]
        A --- B
    end

    subgraph PROGRESS[Progress 5/7]
        P1[✓ ✓ ✓ ✓ ✓ ● ●]
    end

    subgraph CONTENT[Languages & location]
        T1[Languages & location<br/>━━━━━━━━━━━━━━━━━━━━]
        
        subgraph LANG[Languages you speak multi-select]
            L1[[✓] 🇻🇳 Vietnamese]
            L2[[✓] 🇺🇸 English]
            L3[[ ] 🇯🇵 Japanese]
            L4[[ ] 🇰🇷 Korean]
            L5[[ ] 🇨🇳 Chinese]
            L6[[ ] 🇫🇷 French]
            ADD[+ Add language]
            L1 --- L2
            L2 --- L3
            L3 --- L4
            L4 --- L5
            L5 --- L6
            L6 --- ADD
        end

        COUNTRY[Country<br/>┌──────────────────────────┐<br/>│ 🇻🇳 Vietnam          ▼  │<br/>└──────────────────────────┘]

        NAV[← Back    Next →]
        T1 --> LANG
        LANG --> COUNTRY
        COUNTRY --> NAV
    end

    HEADER ==> PROGRESS
    PROGRESS ==> CONTENT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef selected fill:#10B981,stroke:#047857,color:#fff
    class NAV,L1,L2 primary
```

**Min:** 1 language required.
