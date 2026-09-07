# Admin Anomaly Detection

> **Mục đích:** Detect fraud patterns + spam activities.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Admin]
        H2[🚩 Anomaly Detection]
        H1 --- H2
    end

    subgraph ALERTS[Active Alerts 7]
        A1[🔴 Rating Ring Detected<br/>Users abc + def mutually rated 5⭐<br/>8 sessions in 30 days<br/>Confidence: 92%<br/>[[Review]] [[Dismiss]]
        ]
        A2[🔴 Velocity Anomaly<br/>User xyz123 booked 12 sessions in 1h<br/>Likely bot or mistake<br/>Confidence: 88%<br/>[[Suspend]] [[Investigate]]
        ]
        A3[🟡 Suspicious New Account<br/>No avatar + bio + 0 skills<br/>Booked within 5min of signup<br/>Confidence: 65%<br/>[[Flag for review]]
        ]
        A4[🟡 Skill Drift<br/>User mnp added 12 unrelated skills<br/>Possible spam or fake<br/>[[Verify with user]]
        ]
        A1 --- A2
        A2 --- A3
        A3 --- A4
    end

    subgraph FILTERS[Filters]
        F1[Severity: All ▼]
        F2[Type: All ▼]
        F3[Date range ▼]
        F4[Status: Active ▼]
        F1 --- F2
        F2 --- F3
        F3 --- F4
    end

    subgraph ALGORITHMS[Algorithms]
        AL1[• Mutual rating detection]
        AL2[• Velocity check]
        AL3[• Profile completeness]
        AL4[• Skill category drift]
        AL5[• IP geolocation anomalies]
        AL6[• Device fingerprinting]
        AL1 --- AL2
        AL2 --- AL3
        AL3 --- AL4
        AL4 --- AL5
        AL5 --- AL6
    end

    subgraph ACTIONS[Quick Actions]
        Q1[[🔄 Refresh]]
        Q2[[📊 Generate weekly report]]
        Q3[[⚙ Adjust thresholds]]
        Q1 --- Q2
        Q2 --- Q3
    end

    HEADER ==> ALERTS
    ALERTS --> FILTERS
    FILTERS --> ALGORITHMS
    ALGORITHMS --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class Q1,Q2,Q3 primary
    class A1,A2 danger
    class A3,A4 warning
```

**ML model:** Logistic Regression trained trên historical fraud labels.
**Threshold:** Configurable per algorithm (default 80% confidence).
