# Toast — Error

> **Mục đích:** Brief error notification bottom-right.

```mermaid
flowchart TB
    subgraph APP[App background]
        A[Any page]
    end

    subgraph TOAST[Error toast bottom-right]
        T[┌──────────────────────────────────────────┐<br/>│ ❌ Couldn't book session. Try again.  ×   │<br/>│ Network error. Check your connection.    │<br/>└──────────────────────────────────────────┘]
    end

    subgraph AUTO[Auto-dismiss + retry]
        AD1[Visible: 8 seconds longer than success]
        AD2[Click × to dismiss]
        AD3[[🔄 Retry] action if applicable]
        AD4[Multiple errors stack vertically]
        AD1 --- AD2
        AD2 --- AD3
        AD3 --- AD4
    end

    A --> TOAST
    TOAST --> AUTO

    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class T danger
```

**A11y:** `role="alert"`, `aria-live="assertive"`.
**Logging:** All errors → Sentry với context.
