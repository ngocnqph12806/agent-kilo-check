# Cancel Booking

> **Mục đích:** Xác nhận trước khi hủy + áp dụng refund policy.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Booking detail]
        H2[Cancel booking?]
        H1 --- H2
    end

    subgraph CTX[Context]
        C1[Session with Mai Tran]
        C2[📅 Thu Sep 11 19:00-20:00 in 5 hours]
        C1 --- C2
    end

    subgraph REASON[Reason for cancellation ⚠️]
        R1[( ) Schedule conflict]
        R2[( ) Not needed anymore]
        R3[( ) Found another mentor]
        R4[( ) Mentor unavailable]
        R5[( ) Other]
        R1 --- R2
        R2 --- R3
        R3 --- R4
        R4 --- R5
    end

    subgraph POLICY[Refund policy]
        P1[ⓘ Cancellation ≥ 24h before session: 100% refund 60 seeds]
        P2[ⓘ Cancellation < 24h before session: 50% refund 30 seeds]
        P3[ⓘ Your cancel is 5 hours before → 50% refund]
        P1 --- P2
        P2 --> P3
    end

    subgraph MSG[Optional message to mentor]
        M[┌──────────────────────────────┐<br/>│ Sorry I have to cancel...     │<br/>└──────────────────────────────┘]
    end

    subgraph ACTIONS[Confirm]
        A1[[Keep booking]]
        A2[[Cancel booking -50% refund →]]
        A1 --- A2
    end

    HEADER ==> CTX
    CTX --> REASON
    REASON --> POLICY
    POLICY --> MSG
    MSG --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class A1 primary
    class A2 danger
```

**Logic:** Tính toán refund dựa trên time-to-session:
- ≥ 24h: 100%
- < 24h: 50%
- No-show: 0%
