# Settings — Privacy

> **Mục đích:** Profile visibility, blocked users, data sharing controls.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[Privacy]
        H1 --- H2
    end

    subgraph VIS[Profile visibility]
        V1[(●) Public anyone can view]
        V2[( ) Logged-in users only]
        V3[( ) Only matched mentors]
        V4[( ) Private hidden]
        V1 --- V2
        V2 --- V3
        V3 --- V4
    end

    subgraph INDEX[Search discoverability]
        I1[[✓] Show in Discover]
        I2[[✓] Show in search results]
        I3[[ ] Show online status]
        I1 --- I2
        I2 --- I3
    end

    subgraph REVIEWS[Review privacy]
        R1[[✓] Show my reviews on profile]
        R2[[✓] Allow reviews from anyone]
        R3[( ) Only verified mentors]
        R1 --- R2
        R2 --- R3
    end

    subgraph BLOCK[Blocked users 3]
        B1[(👤 spam-user)]
        B2[(👤 another-spam)]
        B3[(+ Add to blocklist)]
        B1 --- B2
        B2 --- B3
    end

    subgraph DATA[Data sharing]
        D1[[ ] Share anonymized data for AI training]
        D2[[ ] Allow analytics tracking]
        D3[[✓] Share data with verified B2B partners]
        D1 --- D2
        D2 --- D3
    end

    subgraph EXPORT[Data & Privacy actions]
        E1[[📦 Export my data GDPR]]
        E2[[🗑️ Delete account]]
        E1 --- E2
    end

    HEADER ==> VIS
    VIS --> INDEX
    INDEX --> REVIEWS
    REVIEWS --> BLOCK
    BLOCK --> DATA
    DATA --> EXPORT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class E1 primary
    class E2 danger
```

**GDPR compliance:** Right to export + delete.
**Blocklist:** Blocked users can't see profile, can't send messages.
