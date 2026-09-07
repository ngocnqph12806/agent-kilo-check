# Sign Up

> **Mục đích:** Tạo account mới qua email hoặc OAuth.
> **Phase:** 0 (Auth)

```mermaid
flowchart TB
    subgraph HEADER[Header]
        A[🌱 SkillSeed]
        B[Already member? → Login]
        A --- B
    end

    subgraph FORM[Create Account Form]
        T1[Create your account<br/>━━━━━━━]
        F1[Full name ⚠️]
        F2[Email ⚠️]
        F3[Password ⚠️ ≥8 chars + chữ + số 👁]
        F4[Confirm password ⚠️]
        F5[( ) I agree to Terms & Privacy ⚠️]
        F6[Create Account →]
        T1 --> F1
        F1 --> F2
        F2 --> F3
        F3 --> F4
        F4 --> F5
        F5 --> F6
    end

    subgraph DIVIDER[─── or sign up with ───]
        D1[G  Continue with Google]
        D2[🍎 Continue with Apple]
        D1 --- D2
    end

    subgraph FOOTER[Footer]
        FT[Already have an account? Login]
    end

    HEADER ==> FORM
    FORM ==> DIVIDER
    DIVIDER ==> FOOTER

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class F6,D1,D2 primary
```

**States:**
- **Loading:** Spinner trên button "Creating..."
- **Error (email exists):** Inline error + link "Try login"
- **Validation realtime:** onBlur validate
- **Success →** redirect `/onboarding`

**Tracking:** GA4 `signup_started`, `signup_completed`, `signup_failed_reason`
