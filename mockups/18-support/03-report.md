# Report a Problem

> **Mục đích:** User report về user khác hoặc content vi phạm.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Back]
        H2[🚩 Report a Problem]
        H1 --- H2
    end

    subgraph WHO[Reporting]
        W1[What are you reporting?]
        W2[(●) 👤 A user]
        W3[( ) 💬 A message]
        W4[( ) ⭐ A review]
        W5[( ) 📅 A session]
        W6[( ) 🎓 A skill/profile]
        W7[( ) 🎙️ Other]
        W1 --- W2
        W2 --- W3
        W3 --- W4
        W4 --- W5
        W5 --- W6
        W6 --- W7
    end

    subgraph TARGET[Who or what]
        T1[User/Content: @troll_account]
        T1A[[Change target]]
        T1 --- T1A
    end

    subgraph REASON[Reason ⚠️]
        R1[( ) 🚫 Harassment or bullying]
        R2[( ) 💬 Hate speech]
        R3[( ) 🎭 Fake profile]
        R4[( ) 📢 Spam or scam]
        R5[( ) 🔞 Inappropriate content]
        R6[( ) ⚖️ Threats or violence]
        R7[( ) 🚨 Illegal activity]
        R8[( ) ❓ Other]
        R1 --- R2
        R2 --- R3
        R3 --- R4
        R4 --- R5
        R5 --- R6
        R6 --- R7
        R7 --- R8
    end

    subgraph DETAIL[Details]
        D1[What happened? ⚠️]
        D2[┌──────────────────────────────────┐<br/>│ This user sent me threatening... │<br│└──────────────────────────────────┘]
        D3[Attach screenshots 📸]
        D4[3 files max each ≤ 5MB]
        D1 --- D2
        D2 --- D3
        D3 --- D4
    end

    subgraph CONTACT[Contact preference]
        CT1[(●) Email me the outcome]
        CT2[( ) Stay anonymous from reported user]
        CT3[Email: mai@acme.vn]
        CT1 --- CT2
        CT2 --- CT3
    end

    subgraph SUBMIT[Submit]
        SU1[[Submit report]]
        SU2[We'll review within 24 hours]
        SU1 --- SU2
    end

    HEADER ==> WHO
    WHO --> TARGET
    TARGET --> REASON
    REASON --> DETAIL
    DETAIL --> CONTACT
    CONTACT --> SUBMIT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class SU1 primary
    class R2,R6,R7 danger
```

**Priority routing:** Life-threatening → immediate alert to safety team.
**Anonymous option:** Protect reporter identity.
