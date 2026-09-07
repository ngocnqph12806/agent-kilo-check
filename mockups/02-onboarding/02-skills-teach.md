# Onboarding — Step 1: Skills I Can Teach

> **Mục đích:** Thu thập offered skills (kỹ năng user có thể dạy).
> **Phase:** 1 (Onboarding Step 1/7)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
        B[Save & exit]
        A --- B
    end

    subgraph PROGRESS[Progress 1/7]
        P1[✓ ● ● ● ● ● ●]
    end

    subgraph CONTENT[What can you teach?]
        T1[What can you teach?<br/>━━━━━━━━━━━━━━━━━]
        
        subgraph SEARCH[Search]
            S1[🔍 Type a skill...]
        end

        subgraph SUGGEST[Suggestions]
            SG1[+ Public Speaking]
            SG2[+ Python]
            SG3[+ Yoga]
            SG4[+ Cooking]
            SG1 --- SG2
            SG2 --- SG3
            SG3 --- SG4
        end

        subgraph SELECTED[Your teaching skills 3:]
            SK1[📊 Public Speaking ×<br/>Level: ●●●○○ 3/5<br/>Years: 5  Rate: 60 seeds/h<br/>Edit description...]
            SK2[💻 Python ×<br/>Level: ●●●●○ 4/5<br/>Years: 3  Rate: 60 seeds/h]
            SK1 --- SK2
        end

        ADD[+ Add custom skill]
        
        NAV[← Back    Next →]
        T1 --> SEARCH
        SEARCH --> SUGGEST
        SUGGEST --> SELECTED
        SELECTED --> ADD
        ADD --> NAV
    end

    HEADER ==> PROGRESS
    PROGRESS ==> CONTENT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class NAV primary
```

**Validation:** Tối thiểu 1 skill required để next.
**Tap chip:** Mở modal edit level/years/rate/description.
**Custom skill:** Auto-suggest taxonomy → confirm "Add as new".
