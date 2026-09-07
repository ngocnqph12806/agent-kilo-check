# Pod Forum

> **Mục đích:** Discussion forum cho pod members (text posts, replies, reactions).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Public Speaking Club VN]
    end

    subgraph TABS[Pod Tabs]
        TB1[[About]]
        TB2[[Members]]
        TB3[[Forum 12] active]
        TB4[[Sessions]]
        TB1 --- TB2
        TB2 --- TB3
        TB3 --- TB4
    end

    subgraph COMPOSE[Compose]
        CMP[Share something with the pod...]
        CMP1[[📷 Photo] [[📎 File] [[Post]]
        CMP --- CMP1
    end

    subgraph POSTS[Forum Posts]
        subgraph P1[Post 1 — Pinned]
            AV1[Avatar]
            N1[Mai Tran Creator • 2d ago]
            T1[📌 Welcome new members!]
            C1["Excited to have 4 new members this week. Let's<br/>introduce yourselves in the comments!"]
            R1[❤️ 12  💬 8  🔄 2]
            AV1 --- N1
            N1 --- T1
            T1 --- C1
            C1 --- R1
        end

        subgraph P2[Post 2]
            AV2[Avatar]
            N2[Duc Nguyen • 1d ago]
            T2[Any tips for dealing with nervousness?]
            C2["I have a presentation next week and feeling<br/>anxious. What works for you?"]
            R2[❤️ 5  💬 12]
            AV2 --- N2
            N2 --- T2
            T2 --- C2
            C2 --- R2
        end

        subgraph P3[Post 3]
            AV3[Avatar]
            N3[Linh Tran • 5h ago]
            T3[Recording from yesterday's session]
            C3[[🎬 video-link.mp4]]
            R3[❤️ 8  💬 3]
            AV3 --- N3
            N3 --- T3
            T3 --- C3
            C3 --- R3
        end

        P1 --- P2
        P2 --- P3
    end

    HEADER ==> TABS
    TABS ==> COMPOSE
    COMPOSE --> POSTS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class CMP1 primary
```

**Threading:** Replies indented, max 3 levels deep.
**Reactions:** 6 emoji types (❤️ 🔥 💡 👏 🤔 😂).
**Moderation:** Auto-flag toxic content qua OpenAI Mod API.
