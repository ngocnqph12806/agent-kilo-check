# Contact Support

> **Mục đích:** Multi-channel support (chat, email, video call).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Help]
        H2[📞 Contact Support]
        H1 --- H2
    end

    subgraph TOPIC[Topic]
        T1[(●) 🐛 Bug report]
        T2[( ) 💳 Payment issue]
        T3[( ) 🚫 Account banned]
        T4[( ) 🤝 Session dispute]
        T5[( ) 💡 Feature request]
        T6[( ) ❓ Other]
        T1 --- T2
        T2 --- T3
        T3 --- T4
        T4 --- T5
        T5 --- T6
    end

    subgraph PRIORITY[Priority]
        P1[( ) Low response in 48h]
        P2[(●) Medium 24h]
        P3[( ) High 4h]
        P4[( ) Urgent 1h]
        P1 --- P2
        P2 --- P3
        P3 --- P4
    end

    subgraph DESC[Description]
        D1[Subject ⚠️]
        D2[Booking confirmation email not received]
        D3[Details ⚠️]
        D4[┌──────────────────────────────────┐<br/>│ I booked a session with Mai at...   │<br││                                   │<br│└──────────────────────────────────┘]
        D5[Attach screenshot]
        D6[[📁 Drop files or click]]
        D1 --- D2
        D2 --- D3
        D3 --- D4
        D4 --- D5
        D5 --- D6
    end

    subgraph CONTACT[Contact preference]
        C1[( ) 📧 Email reply only]
        C2[(●) 💬 In-app chat best]
        C3[( ) 📞 Schedule video call]
        C1 --- C2
        C2 --- C3
    end

    subgraph SUBMIT[Submit]
        SU1[[Submit ticket]]
        SU2[Ticket ID: #45821 created]
        SU3[Track at help.skillseed.app/tickets/45821]
        SU1 --- SU2
        SU2 --- SU3
    end

    HEADER ==> TOPIC
    TOPIC --> PRIORITY
    PRIORITY --> DESC
    DESC --> CONTACT
    CONTACT --> SUBMIT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class SU1 primary
```

**Channels:** Intercom (chat) + Zendesk (ticket) + Calendly (video).
**SLA:** Premium = 4h, Free = 24h, B2B Enterprise = 1h.
