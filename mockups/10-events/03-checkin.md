# Event Check-in

> **Mục đích:** Verify user đang ở event qua GPS hoặc QR code.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Event]
        H2[Check in]
        H1 --- H2
    end

    subgraph CTX[Context]
        C1[🇻🇳 Hanoi Meetup: Skill Exchange]
        C2[📍 Toong Coworking Hoan Kiem]
        C3[Started 5 min ago — ends in 2h 55m]
        C1 --- C2
        C2 --- C3
    end

    subgraph METHOD[Check-in method]
        T1[[📍 GPS Auto-detect]]
        T2[[📷 Scan QR code]] active
        T3[[🔢 Enter 6-digit code]]
        T1 --- T2
        T2 --- T3
    end

    subgraph QR[QR scanner]
        Q1[┌──────────────────────────┐<br/>│                          │<br/>│   📷 Camera preview       │<br/>│   ┌──────────────┐       │<br/>│   │              │       │<br/>│   │   Scanning   │       │<br/>│   │   QR code    │       │<br/>│   │              │       │<br││   └──────────────┘       │<br││                          │<br│└──────────────────────────┘]
        Q2[Align QR code from event organizer within the frame]
        Q1 --- Q2
    end

    subgraph ORG[Or get code from organizer]
        OC[Code: 8X3K9P]
        OC1[Valid for 10 minutes]
        OC --- OC1
    end

    subgraph GPS[GPS method variant]
        G1[📍 Your location detected]
        G2[Toong Coworking 250m from you ✓]
        G3[Move within 200m to auto check-in]
        G4[[Manually override]]
        G1 --- G2
        G2 --- G3
        G3 --- G4
    end

    HEADER ==> CTX
    CTX --> METHOD
    METHOD --> QR
    QR --> ORG
    QR --> GPS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class T1,T2 primary
```

**Geofence:** 200m radius từ event location.
**QR token:** HMAC signed, valid 10 min.
**Reward:** 5 seeds auto-credited sau check-in success.
