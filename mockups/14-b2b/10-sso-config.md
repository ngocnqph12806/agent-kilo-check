# B2B SSO Config

> **Mục đích:** Admin org cấu hình SSO cho employees.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← ACME Corp Settings]
        H2[🔐 SSO Configuration]
        H1 --- H2
    end

    subgraph TYPE[SSO Type]
        T1[(●) Google Workspace OIDC]
        T2[( ) Microsoft Entra OIDC]
        T3[( ) SAML 2.0 generic]
        T4[( ) Custom OAuth]
        T1 --- T2
        T2 --- T3
        T3 --- T4
    end

    subgraph CONFIG[Google Workspace Config]
        C1[Client ID<br/>xxx.apps.googleusercontent.com]
        C2[Client Secret<br/>********************************]
        C3[Authorized redirect URI<br/>https://acme.skillseed.app/auth/callback]
        C4[HD Parameter: acme.com ⚠️ required]
        C1 --- C2
        C2 --- C3
        C3 --- C4
    end

    subgraph MAPPING[User attribute mapping]
        M1[email → user.email]
        M2[firstName → user.full_name]
        M3[department → org.department]
        M4[employeeId → user.external_id]
        M1 --- M2
        M2 --- M3
        M3 --- M4
    end

    subgraph PROV[Provisioning]
        P1[☑ Auto-provision new users on first SSO login]
        P2[☑ Sync department + role every 24h via SCIM]
        P3[☐ Disable manual registration]
        P1 --- P2
        P2 --- P3
    end

    subgraph TEST[Test]
        TE1[Test SSO connection →]
        TE2[Last test: Sep 5 — Success]
        TE1 --- TE2
    end

    subgraph SAVE[Save]
        SV[[Save changes]]
    end

    HEADER ==> TYPE
    TYPE --> CONFIG
    CONFIG --> MAPPING
    MAPPING --> PROV
    PROV --> TEST
    TEST --> SAVE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class TE1,SV primary
```

**Required:** IT admin of customer company phải config.
**Test mode:** Sandbox trước khi enable production.
