# Onboarding — Step 2: Skills I Want to Learn

> **Mục đích:** Thu thập wanted skills (kỹ năng user muốn học) + priority.
> **Phase:** 1 (Onboarding Step 2/7)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
        B[Save & exit]
        A --- B
    end

    subgraph PROGRESS[Progress 2/7]
        P1[✓ ✓ ● ● ● ● ●]
    end

    subgraph CONTENT[What do you want to learn?]
        T1[What do you want to learn?<br/>━━━━━━━━━━━━━━━━━━━━━━━━━━━━]
        
        subgraph SEARCH[Search]
            S1[🔍 Search skills...]
        end

        subgraph SUGGEST[Suggested based on your profile]
            SG1[+ System Design]
            SG2[+ UX Research]
            SG3[+ Business English]
            SG4[+ Data Science]
            SG1 --- SG2
            SG2 --- SG3
            SG3 --- SG4
        end

        subgraph SELECTED[Your learning goals 4:]
            SK1[🎨 System Design ×<br/>Priority: ●●●●○ 4/5<br/>Target level: 3]
            SK2[🇬🇧 Business English ×<br/>Priority: ●●●○○ 3/5]
            SK1 --- SK2
        end

        NAV[← Back    Next →]
        T1 --> SEARCH
        SEARCH --> SUGGEST
        SUGGEST --> SELECTED
        SELECTED --> NAV
    end

    HEADER ==> PROGRESS
    PROGRESS ==> CONTENT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class NAV primary
```

**Validation:** Tối thiểu 1 skill required.
**Priority dots:** 1-5 scale.
