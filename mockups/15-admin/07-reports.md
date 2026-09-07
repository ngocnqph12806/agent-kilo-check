# Admin Reports Queue

> **Mục đích:** Handle user-reported issues: users, content, sessions.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Admin]
        H2[🚩 Reports Queue]
        H1 --- H2
    end

    subgraph STATS[Today]
        S1[Open: 12]
        S2[Resolved: 8]
        S3[Escalated: 2]
        S4[SLA: 4h avg]
        S1 --- S2
        S2 --- S3
        S3 --- S4
    end

    subgraph FILTERS[Filters]
        F1[Category ▼]
        F2[Priority ▼]
        F3[Assigned to ▼]
        F1 --- F2
        F2 --- F3
    end

    subgraph QUEUE[Queue items]
        Q1[🚩 User Report<br/>Reporter: An Nguyen<br/>Reported user: troll_user<br/>Reason: Harassment in chat<br/>Evidence: 3 screenshots<br/>Priority: 🔥 High<br/>Assigned: —<br/>Age: 1h<br/>[[View] [Assign to me] [Resolve]]
        Q2[🚩 Session Report<br/>Reporter: Mai Tran<br/>Reported session: #BK-2026-09-12-042<br/>Reason: No-show<br/>Evidence: empty chat log<br/>Priority: 🟡 Medium<br/>Assigned: john@skillseed<br/>Age: 4h<br/>[[View] [Resolve]]
        Q3[🚩 Content Report<br/>Reporter: anonymous<br/>Reported review: id 12345<br/>Reason: profanity<br/>Priority: 🟢 Low<br/>Assigned: —<br/>Age: 12h<br/>[[View] [Resolve]]
        Q1 --- Q2
        Q2 --- Q3
    end

    subgraph DETAIL[Detail view on click]
        D1[Full timeline of events]
        D2[All screenshots and evidence]
        D3[Related reports history]
        D4[User past behavior]
        D1 --- D2
        D2 --- D3
        D3 --- D4
    end

    subgraph ACTIONS[Resolve actions]
        A1[[Dismiss — no violation]]
        A2[[Warn reported user]]
        A3[[Suspend user 7 days]]
        A4[[Ban permanent]]
        A5[[Refund + escalate]]
        A1 --- A2
        A2 --- A3
        A3 --- A4
        A4 --- A5
    end

    HEADER ==> STATS
    STATS --> FILTERS
    FILTERS --> QUEUE
    QUEUE --> DETAIL
    DETAIL --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class A1,A2 primary
    class A3 warning
    class A4,A5 danger
```

**SLA tracking:** Admin được alert khi reports quá hạn.
**Audit:** Mọi resolution log + email thông báo reporter.
