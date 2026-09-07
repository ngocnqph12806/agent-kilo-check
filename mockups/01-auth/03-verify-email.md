# Verify Email

> **Mục đích:** Active account từ link email. Hiển thị 3 states: loading, success, expired.

## Loading State

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
    end

    subgraph LOADING[Verifying]
        L1[(🔄 Spinner)]
        L2[Verifying your email...]
        L1 --> L2
    end

    HEADER ==> LOADING
```

## Success State

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
    end

    subgraph SUCCESS[Verified]
        S1[✅ Email verified!]
        S2[Welcome to SkillSeed, Mai!]
        S3[🌱 You received 30 free starter seeds]
        S4[Complete your profile →]
        S1 --> S2
        S2 --> S3
        S3 --> S4
    end

    HEADER ==> SUCCESS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class S4 primary
```

## Expired State

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
    end

    subgraph EXPIRED[Expired]
        E1[⚠️ Link expired]
        E2[This verification link has expired.]
        E3[Resend verification email]
        E4[← Back to login]
        E1 --> E2
        E2 --> E3
        E3 --> E4
    end

    HEADER ==> EXPIRED

    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class E1 warning
```

**TTL:** Token hết hạn sau 24h.
