# B2B Org Settings

> **Mục đích:** Cấu hình org: name, branding, domain, region.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← ACME Corp]
        H2[⚙ Org Settings]
        H1 --- H2
    end

    subgraph GENERAL[General]
        G1[Org name ⚠️]
        G2[ACME Corp]
        G3[Display name for members]
        G4[Internal domain]
        G5[acme.com]
        G6[Timezone]
        G7[Asia/Ho_Chi_Minh ▼]
        G1 --- G2
        G2 --- G3
        G3 --- G4
        G4 --- G5
        G5 --- G6
        G6 --- G7
    end

    subgraph BRAND[Branding Enterprise only]
        B1[Logo upload]
        B2[📁 Drop PNG or click]
        B3[Primary color]
        B4[#10B981]
        B5[Custom subdomain acme.skillseed.app]
        B6[Available: ✓ enabled]
        B1 --- B2
        B2 --- B3
        B3 --- B4
        B4 --- B5
        B5 --- B6
    end

    subgraph POLICIES[Policies]
        P1[Default role for new members]
        P2[(●) Member]
        P3[( ) Admin]
        P4[( ) Manual approval]
        P5[Require identity verification]
        P6[[✓] Yes]
        P7[Session recording policy]
        P8[(●) Opt-in per session]
        P9[( ) Always record]
        P10[( ) Disabled]
        P1 --- P2
        P2 --- P3
        P3 --- P4
        P4 --- P5
        P5 --- P6
        P6 --- P7
        P7 --- P8
        P8 --- P9
        P9 --- P10
    end

    subgraph DANGER[Danger zone]
        D1[[Transfer ownership to another member]]
        D2[[⚠️ Delete organization]]
        D3[This is permanent and irreversible.]
        D1 --- D2
        D2 --- D3
    end

    HEADER ==> GENERAL
    GENERAL --> BRAND
    BRAND --> POLICIES
    POLICIES --> DANGER

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class B5 primary
    class D1,D2 danger
```

**Subdomain:** Enterprise tier mới có custom domain.
**Branding:** Logo + primary color hiển thị trong toàn bộ B2B UI.
