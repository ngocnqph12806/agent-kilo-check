# Onboarding — Step 6: Avatar

> **Mục đích:** Upload ảnh đại diện hoặc chọn avatar mặc định.
> **Phase:** 1 (Onboarding Step 6/7)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
        B[Save & exit]
        A --- B
    end

    subgraph PROGRESS[Progress 6/7]
        P1[✓ ✓ ✓ ✓ ✓ ✓ ●]
    end

    subgraph CONTENT[Add a profile photo]
        T1[Add a profile photo<br/>━━━━━━━━━━━━━━━━━━]
        
        subgraph UPLOAD[Upload]
            AV[┌──────────┐<br/>│          │<br/>│   👤    │<br/>│          │<br/>└──────────┘<br/>Upload photo]
        end

        WHY[Why add a photo?<br/>• Profiles with photos get 3× more matches<br/>• Helps people recognize you in sessions]

        subgraph DEFAULT[Or choose an avatar]
            D1[🦊]
            D2[🐼]
            D3[🐱]
            D4[🐯]
            D5[🦁]
            D6[🐰]
            D1 --- D2
            D2 --- D3
            D3 --- D4
            D4 --- D5
            D5 --- D6
        end

        NAV[← Back    Next →]
        T1 --> UPLOAD
        UPLOAD --> WHY
        WHY --> DEFAULT
        DEFAULT --> NAV
    end

    HEADER ==> PROGRESS
    PROGRESS ==> CONTENT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class AV primary
    class NAV primary
```

**Validation:** ≤ 5MB, image/* MIME, auto-crop 1:1.
**Optional:** Có thể skip step này.
