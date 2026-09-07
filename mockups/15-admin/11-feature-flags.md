# Admin Feature Flags

> **Mục đích:** Toggle features per region/user segment via LaunchDarkly-style flags.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Admin]
        H2[🚩 Feature Flags]
        H1 --- H2
    end

    subgraph SEARCH[Search]
        S1[🔍 Search flags]
        C1[Environment: production ▼]
        S1 --- C1
    end

    subgraph FLAGS[Active flags 12]
        F1[🎯 ai-matching-v2<br/>✓ ON for 50% Premium VN<br/>Status: rollout in progress]
        F2[🎙️ voice-assistant<br/>✓ ON for all iOS users<br/>Status: live]
        F3[📱 ar-overlay-yoga<br/>✗ OFF globally<br/>Status: staged]
        F4[💳 stripe-subscription<br/>✓ ON for all<br/>Status: live]
        F5[🔗 polygon-sbt<br/>✗ OFF globally<br/>Status: pending audit]
        F6[🌍 id-region-routing<br/>✓ ON for ID PH<br/>Status: live]
        F1 --- F2
        F2 --- F3
        F3 --- F4
        F4 --- F5
        F5 --- F6
    end

    subgraph RULE[Rules per flag]
        R1[Targeting: country IN VN SG]
        R2[User segment: Premium]
        R3[Percentage: 50% rollout]
        R4[Sticky: ✓ (consistent per user)]
        R1 --- R2
        R2 --- R3
        R3 --- R4
    end

    subgraph METRICS[Metrics per flag]
        M1[Users targeted: 2,341]
        M2[Active users: 1,847 79%]
        M3[Errors: 12 0.6%]
        M4[Performance p95: 1.4s]
        M5[Conversions ↑ 18%]
        M1 --- M2
        M2 --- M3
        M3 --- M4
        M4 --- M5
    end

    subgraph ACTIONS[Actions]
        A1[[▶ Toggle flag]]
        A2[[📊 View detailed analytics]]
        A3[[⏮ Rollback]]
        A1 --- A2
        A2 --- A3
    end

    HEADER ==> SEARCH
    SEARCH --> FLAGS
    FLAGS --> RULE
    RULE --> METRICS
    METRICS --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class A1,A2 primary
    class A3 danger
```

**Tech:** LaunchDarkly self-hosted hoặc Unleash.
**Audit log:** Mọi thay đổi flag log lại với timestamp + actor.
