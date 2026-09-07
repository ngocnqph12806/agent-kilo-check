# Settings — Notifications

> **Mục đích:** Granular control cho từng channel × event type.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[Notifications]
        H1 --- H2
    end

    subgraph TABLE[Notification Matrix]
        subgraph HEADER[Header row]
            HR1[Event] --- HR2[📧 Email] --- HR3[📱 Push] --- HR4[💬 In-app]
        end

        subgraph BOOKING[Booking events]
            B1[New booking request]
            B1E[[✓]] --- B1P[[✓]] --- B1I[[✓]]
            B2[Booking accepted]
            B2E[[✓]] --- B2P[[✓]] --- B2I[[✓]]
            B3[Booking cancelled]
            B3E[[✓]] --- B3P[[✓]] --- B3I[[✓]]
            B4[Session reminder 24h]
            B4E[[✓]] --- B4P[[✓]] --- B4I[[✓]]
            B5[Session reminder 1h]
            B5E[[✓]] --- B5P[[✓]] --- B5I[[✓]]
            B1 --- B2
            B2 --- B3
            B3 --- B4
            B4 --- B5
        end

        subgraph SOCIAL[Social]
            S1[New match suggestion]
            S1E[[✓]] --- S1P[[✓]] --- S1I[[✓]]
            S2[5-star review received]
            S2E[[✓]] --- S2P[[ ]] --- S2I[[✓]]
            S3[New follower]
            S3E[[✓]] --- S3P[[ ]] --- S3I[[✓]]
            S1 --- S2
            S2 --- S3
        end

        subgraph MKT[Marketing]
            M1[Newsletter monthly]
            M1E[[✓]] --- M1P[[ ]] --- M1I[[ ]]
            M2[Product updates]
            M2E[[✓]] --- M2P[[ ]] --- M2I[[ ]]
            M3[Promotional offers]
            M3E[[ ]] --- M3P[[ ]] --- M3I[[ ]]
            M1 --- M2
            M2 --- M3
        end

        subgraph DIGEST[Digest mode]
            D1[Off individual]
            D2[Daily digest 9am]
            D3[Weekly digest Monday]
            D4[Real-time]
            D1 --- D2
            D2 --- D3
            D3 --- D4
        end

        HEADER --- BOOKING
        BOOKING --> SOCIAL
        SOCIAL --> MKT
        MKT --> DIGEST
    end

    subgraph QUITE[Quiet hours]
        Q1[Do not disturb 10pm - 7am]
        Q2[Timezone: Asia/Ho_Chi_Minh]
        Q1 --- Q2
    end

    HEADER ==> TABLE
    TABLE --> QUITE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
```

**Smart digest:** Bundle low-priority events → digest email daily/weekly.
**Quiet hours:** Pause push notifications theo timezone.
