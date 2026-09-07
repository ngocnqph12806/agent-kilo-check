# Sessions & Devices (Security)

> **Mục đích:** User quản lý active sessions + revoke suspicious devices.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[📱 Sessions & Devices]
        H1 --- H2
    end

    subgraph CURRENT[This device]
        CD1[(💻)] --- CD2[MacBook Pro · Chrome]
        CD3[📍 Hanoi Vietnam]
        CD4[Last active: now]
        CD5[✓ This device]
        CD2 --- CD3
        CD3 --- CD4
        CD4 --- CD5
    end

    subgraph OTHER[Other active sessions 3]
        O1[(📱)] --- O2[iPhone 15 · Safari]
        O3[📍 Ho Chi Minh City Vietnam]
        O4[Last active: 2 hours ago]
        O5[[Revoke]]
        O2 --- O3
        O3 --- O4
        O4 --- O5

        P1[(💻)] --- P2[Windows · Firefox]
        P3[📍 Singapore]
        P4[Last active: yesterday]
        P5[[Revoke]]
        P2 --- P3
        P3 --- P4
        P4 --- P5

        Q1[(🤖)] --- Q2[Unknown device · Chrome]
        Q3[📍 Tokyo Japan]
        Q4[Last active: 3 days ago ⚠️ suspicious]
        Q5[[Revoke]] [[Report suspicious]]
        Q2 --- Q3
        Q3 --- Q4
        Q4 --- Q5

        O1 --- P1
        P1 --- Q1
    end

    subgraph SECURITY[Security actions]
        S1[[🔒 Sign out all other devices]]
        S2[[🔑 Change password]]
        S3[[🛡️ Enable 2FA]]
        S1 --- S2
        S2 --- S3
    end

    HEADER ==> CURRENT
    CURRENT --> OTHER
    OTHER --> SECURITY

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class S1,S2,S3 primary
    class Q4 warning
    class O5,P5,Q5 danger
```

**Auto-detection:** IP geolocation + device fingerprint.
**Security alerts:** Email khi có login mới từ device lạ.
