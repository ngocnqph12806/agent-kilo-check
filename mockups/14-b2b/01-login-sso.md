# B2B Login (SSO)

> **Mục đích:** Enterprise login qua Google Workspace / Microsoft Entra / SAML.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[🌱 SkillSeed for Business]
        H2[[Personal account? Login here]]
        H1 --- H2
    end

    subgraph TITLE[Title]
        T1[Sign in to your company workspace]
        T2[Use your work email to continue]
        T1 --- T2
    end

    subgraph SSO[SSO Options]
        O1[[🔵 Continue with Google Workspace]]
        O2[[🪟 Continue with Microsoft Entra]]
        O3[[🔐 Continue with SAML SSO]]
        O4[[📧 Continue with email magic link]]
        O1 --- O2
        O2 --- O3
        O3 --- O4
    end

    subgraph COMPANY[Supported companies]
        C1[✓ ACME Corp]
        C2[✓ Globex Inc]
        C3[✓ Initech]
        C4[✓ See all 247 companies →]
        C1 --- C2
        C2 --- C3
        C3 --- C4
    end

    subgraph SEC[Security notice]
        S1[🔒 Your company may monitor activity on this device]
        S2[By signing in you agree to your company's policy]
        S1 --- S2
    end

    HEADER ==> TITLE
    TITLE --> SSO
    SSO --> COMPANY
    COMPANY --> SEC

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class O1,O2,O3 primary
```

**Domain detection:** Auto-detect company từ email domain.
**Just-in-time provisioning:** Auto-create user nếu SSO mới.
