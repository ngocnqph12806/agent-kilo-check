# Admin Skills Review

> **Mục đích:** Review custom skills user-submitted.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Admin]
        H2[🎓 Custom Skills Review]
        H1 --- H2
    end

    subgraph STATS[Queue stats]
        S1[Pending: 23]
        S2[Approved today: 12]
        S3[Rejected today: 3]
        S4[Avg review time: 4 hours]
        S1 --- S2
        S2 --- S3
        S3 --- S4
    end

    subgraph QUEUE[Queue]
        Q1[Skill: "Bavarian Cooking"<br/>Submitted by: @chef_hans<br/>Category: Cuisine<br/>Description: Traditional Bavarian recipes<br/>Duplicates: none found<br/>Tags: cooking, bavaria, german<br/>Confidence legit: 89%<br/>[[Approve] [Reject] [Edit]]
        Q2[Skill: "Magic Tricks Beginner"<br/>Submitted by: @magician_joe<br/>Category: Hobbies<br/>Description: Basic card tricks and illusions<br/>Duplicates: similar to 'Card Magic'<br/>Confidence legit: 75%<br/>[[Approve] [Merge with existing] [Reject]]
        Q3[Skill: "Buy Crypto Safely Scam-free"<br/>Submitted by: @crypto_guru<br/>Category: Finance<br/>Description: Buy Bitcoin without losing money<br/>⚠️ Potential scam promotion<br/>Confidence legit: 12%<br/>[[Reject] [Ban user] [Investigate]]
        Q1 --- Q2
        Q2 --- Q3
    end

    subgraph BULK[Bulk approve]
        B1[[Approve all duplicates-free]]
        B2[[Reject all low confidence]]
        B1 --- B2
    end

    HEADER ==> STATS
    STATS --> QUEUE
    QUEUE --> BULK

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef danger fill:#EF4444,stroke:#DC2626,color:#fff
    class B1 primary
    class B2 danger
```

**ML scoring:** Auto-confidence score dựa trên description + user history.
**Merge:** Duplicate detection → gộp với skill hiện có.
