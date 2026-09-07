# Create Pod

> **Mục đích:** Form tạo Pod mới.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Cancel]
        H2[Create new Pod]
        H1 --- H2
    end

    subgraph FORM[Form]
        F1[Name ⚠️]
        F2[Topic ⚠️]
        F3[Description]
        F4[Cover image upload]
        F5[Max members ▼]
        F6[4 members]
        F7[5 members]
        F8[6 members]
        F9[7 members]
        F10[8 members]
        F6 --- F7
        F7 --- F8
        F8 --- F9
        F9 --- F10
        F11[Visibility:]
        F12[(●) Public discoverable]
        F13[( ) Private invite-only]
        F14[Premium Pod: monthly fee]
        F15[( ) Enable — set price below]
        F16[$ /month]
        F1 --- F2
        F2 --- F3
        F3 --- F4
        F4 --- F5
        F5 --> F6
        F11 --> F12
        F12 --- F13
        F13 --- F14
        F14 --> F15
        F15 --- F16
    end

    subgraph TAGS[Tags]
        T1[+ Add tag e.g. public-speaking vietnam]
        T2[public-speaking ✕]
        T3[vietnam ✕]
        T1 --- T2
        T2 --- T3
    end

    subgraph SCHEDULE[Schedule]
        S1[Recurring meeting day + time]
        S2[Timezone: Asia/Ho_Chi_Minh ▼]
        S1 --- S2
    end

    subgraph SUBMIT[Submit]
        SU1[[Create Pod →]]
        SU2[Or save as draft]
        SU1 --- SU2
    end

    HEADER ==> FORM
    FORM --> TAGS
    TAGS --> SCHEDULE
    SCHEDULE --> SUBMIT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class SU1 primary
```

**Validation:** Name + topic required. Min 2 tags suggested.
**Premium Pod:** Requires Stripe Connect onboarding for creator.
