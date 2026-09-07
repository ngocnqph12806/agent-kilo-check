# Bug Report Form

> **Mục đích:** Form báo bug có cấu trúc để dev reproduce được.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Help]
        H2[🐛 Report a Bug]
        H1 --- H2
    end

    subgraph CONTEXT[Context]
        C1[Browser: Chrome 119 ▼ auto-detected]
        C2[OS: macOS 14 ▼]
        C3[Device: MacBook Pro 16"]
        C4[App version: 2.3.4]
        C5[Network: WiFi 4G]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
    end

    subgraph STEPS[Steps to reproduce ⚠️]
        ST1[1. Go to Discover]
        ST2[2. Filter by Python]
        ST3[3. Click on a mentor profile]
        ST4[4. Try to book session]
        ST5[What happened?]
        ST6[┌──────────────────────────────────┐<br/>│ Page froze after clicking Book...   │<br│└──────────────────────────────────┘]
        ST1 --- ST2
        ST2 --- ST3
        ST3 --- ST4
        ST4 --- ST5
        ST5 --- ST6
    end

    subgraph EXPECTED[Expected vs actual]
        EX1[Expected: Booking modal opens]
        EX2[Actual: Page hangs spinner forever]
        EX1 --- EX2
    end

    subgraph SEVERITY[Severity]
        SV1[( ) 🟢 Minor — minor UI glitch]
        SV2[( ) 🟡 Moderate — feature broken]
        SV3[(●) 🔴 Major — feature unusable]
        SV4[( ) 🚨 Critical — data loss / security]
        SV1 --- SV2
        SV2 --- SV3
        SV3 --- SV4
    end

    subgraph SCREEN[Screenshots]
        SC1[[📁 Drop files or click]]
        SC2[Auto-attach console logs]
        SC1 --- SC2
    end

    subgraph SUBMIT[Submit]
        SU1[[Submit bug report]]
        SU2[Linear ticket auto-created: ENG-4521]
        SU3[We'll email you when fixed]
        SU1 --- SU2
        SU2 --- SU3
    end

    HEADER ==> CONTEXT
    CONTEXT --> STEPS
    STEPS --> EXPECTED
    EXPECTED --> SEVERITY
    SEVERITY --> SCREEN
    SCREEN --> SUBMIT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class SU1 primary
    class SV3,SV4 danger
```

**Auto-context:** Browser/OS/version captured automatically.
**Console logs:** Optional attach để dev debug nhanh hơn.
