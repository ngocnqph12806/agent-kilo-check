# Forgot / Reset Password

> **Mục đích:** User request reset link → đặt password mới.
> **Phase:** 0 (Auth)

## Step 1 — Forgot Password (Request)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
        B[← Back to login]
        A --- B
    end

    subgraph FORM[Reset Password]
        T1[Reset your password<br/>━━━━━━━━━━━━━━━━━━━━]
        D1[Enter the email address associated with your account.]
        F1[Email ⚠️]
        F2[Send reset link →]
        T1 --> D1
        D1 --> F1
        F1 --> F2
    end

    HEADER ==> FORM

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class F2 primary
```

**Security:** Always show success message regardless of email exists.

## Step 2 — Email Sent (Generic message)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
    end

    subgraph SENT[Check Your Email]
        S1[📧 Check your email]
        S2[If an account exists for that email,<br/>we've sent a password reset link.<br/>(Check spam folder too)]
        S3[← Back to login]
        S1 --> S2
        S2 --> S3
    end

    HEADER ==> SENT
```

## Step 3 — Reset Password (Set New)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
    end

    subgraph FORM[Set New Password]
        T1[Set new password<br/>━━━━━━━━━━━━━]
        F1[New password ⚠️ ≥8 chars + chữ + số 👁]
        F2[Confirm password ⚠️]
        F3[Reset password →]
        T1 --> F1
        F1 --> F2
        F2 --> F3
    end

    HEADER ==> FORM

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class F3 primary
```

**TTL:** Reset token expires 1 hour.
