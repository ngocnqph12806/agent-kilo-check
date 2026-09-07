# Login

> **Mục đích:** Authenticate user hiện tại.
> **Phase:** 0 (Auth)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
    end

    subgraph FORM[Login Form]
        T1[Welcome back<br/>━━━━━]
        F1[Email ⚠️]
        F2[Password ⚠️ 👁]
        F3[Forgot password?]
        F4[Login →]
        T1 --> F1
        F1 --> F2
        F2 --> F3
        F3 --> F4
    end

    subgraph DIVIDER[─── or ───]
        D1[G  Continue with Google]
        D2[🍎 Continue with Apple]
        D1 --- D2
    end

    subgraph FOOTER[Footer]
        FT[Don't have an account? Sign up]
    end

    HEADER ==> FORM
    FORM ==> DIVIDER
    DIVIDER ==> FOOTER

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class F4,D1,D2 primary
```

**Error state (5 failed attempts):**
```mermaid
flowchart TB
    subgraph ERROR[⚠️ Too many attempts]
        E1[Try again in 15 minutes]
        E2[Forgot password → reset via email]
        E1 --> E2
    end
```

**Rate limit:** 5 attempts/IP/15 min → block.
