# Reschedule Booking

> **Mục đích:** Đổi lịch session đã confirmed.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Booking detail]
        H2[Reschedule session]
        H1 --- H2
    end

    subgraph CURRENT[Current]
        C1[With Mai Tran]
        C2[📅 Thu Sep 11 19:00-20:00]
        C1 --- C2
    end

    subgraph NEWDATE[New date]
        D1[┌──────────────────────────┐<br/>│ 📅 Pick new date     ▼  │<br/>└──────────────────────────┘]
    end

    subgraph NEWSLOT[New time slot]
        S1[○ 18:00 - 18:30]
        S2[○ 18:30 - 19:00]
        S3[● 19:00 - 20:00]
        S4[○ 20:00 - 21:00]
        S1 --- S2
        S2 --- S3
        S3 --- S4
    end

    subgraph REASON[Reason for reschedule]
        R1[( ) Schedule conflict]
        R2[( ) Teacher requested]
        R3[( ) Other]
        R4[Notes (optional)]
        R1 --- R2
        R2 --- R3
        R3 --- R4
    end

    subgraph WARN[Note]
        W1[Reschedule counts as 1 free change.<br/>Subsequent changes may be subject to policy.]
    end

    subgraph ACTIONS[Actions]
        A1[[Cancel]]
        A2[[Confirm reschedule →]]
        A1 --- A2
    end

    HEADER ==> CURRENT
    CURRENT --> NEWDATE
    NEWDATE --> NEWSLOT
    NEWSLOT --> REASON
    REASON --> WARN
    WARN --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class A2 primary
```

**Validation:** New slot phải available với teacher.
**Notification:** Teacher nhận "Booking rescheduled" qua email + push.
