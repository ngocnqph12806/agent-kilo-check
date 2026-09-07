# Events List

> **Mục đích:** Browse offline SkillSeed events (meetups, workshops).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Discover]
        H2[📍 Events]
        H1 --- H2
    end

    subgraph SEARCH[Search]
        S1[🔍 Search events]
        F1[[List view] [[Map view]]
        S1 --- F1
    end

    subgraph FILTER[Filter]
        F2[City ▼  Date ▼  Topic ▼  Free/Paid ▼]
    end

    subgraph EVENTS[Events]
        subgraph E1[Event 1]
            D1[📅 Sat Sep 14, 2pm-5pm]
            T1[🇻🇳 Hanoi Meetup: Skill Exchange]
            LOC1[📍 Toong Coworking, Hoan Kiem]
            ORG1[8/15 going]
            RSV1[[RSVP free →]]
        end

        subgraph E2[Event 2]
            D2[📅 Wed Sep 18, 7pm-9pm]
            T2[🇻🇳 HCMC: Public Speaking Workshop]
            LOC2[📍 Dreamplex, District 1]
            ORG2[12/20 going]
            FEE[$5 entry]
            RSV2[[RSVP $5 →]]
        end

        subgraph E3[Event 3]
            D3[📅 Sat Sep 21, 10am-12pm]
            T3[🇻🇳 Online: Python for Beginners]
            LOC3[💻 Zoom link provided after RSVP]
            ORG3[45/100 going]
            RSV3[[RSVP free →]]
        end

        E1 --- E2
        E2 --- E3
    end

    subgraph CREATE[Create event]
        CR1[[+ Create event Premium only]]
    end

    HEADER ==> SEARCH
    SEARCH --> FILTER
    FILTER --> EVENTS
    EVENTS --> CREATE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class RSV1,RSV3 primary
```

**GPS verification:** Check-in tại event geofence 200m radius.
**Seed reward:** 5 seeds/attendance tự động cộng vào wallet.
