# Connected Calendars

> **Mục đích:** Kết nối Google / Outlook để auto-sync bookings.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[📅 Connected Calendars]
        H1 --- H2
    end

    subgraph CONNECTED[Connected]
        C1[[✓ Google Calendar connected]
        C2[Account: mai@acme.vn]
        C3[Auto-sync: ✓ enabled]
        C4[Last sync: 5 minutes ago]
        C5[[Disconnect]]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
    end

    subgraph NOTCONNECTED[Not connected]
        N1[[🪟 Microsoft Outlook]
        N2[Connect →]
        N1 --- N2

        P1[[🍎 Apple iCloud]
        P2[Connect →]
        P1 --- P2
    end

    subgraph SETTINGS[Sync settings]
        SS1[[✓] Add bookings as events]
        SS2[[✓] Show attendees email]
        SS3[[✓] Update on reschedule]
        SS4[[✓] Delete on cancel]
        SS5[Reminder: 30 min before]
        SS1 --- SS2
        SS2 --- SS3
        SS3 --- SS4
        SS4 --- SS5
    end

    subgraph PRIVACY[Privacy]
        PR1[SkillSeed only reads your availability<br/>It never modifies existing events.]
        PR2[Disconnect anytime — your data is deleted.]
        PR1 --- PR2
    end

    HEADER ==> CONNECTED
    CONNECTED --> NOTCONNECTED
    NOTCONNECTED --> SETTINGS
    SETTINGS --> PRIVACY

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class C5,N2,P2 primary
    class C5 danger
```

**OAuth scopes:** Minimal — chỉ events create/update/delete.
**Privacy:** Read-only access cho free/busy, write cho SkillSeed events only.
