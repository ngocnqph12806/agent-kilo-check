# Phase 5 — Beyond: Design (Vision-level)

> **Tài liệu thiết kế vision-level — Phase 5 là moonshot, không có deadline cứng cho mọi feature.**
> **Triết lý:** Voice + AR/VR + Global = future of human skill exchange. Xây dựng platform-agnostic primitives (Voice AI layer, AR/VR SDK) để mở rộng dài hạn.

---

## 1. Vision Architecture (5 năm)

```
┌──────────────────────────────────────────────────────────────────────┐
│              SKILLSEED 2030: GitHub of Real Skills                    │
└──────────────────────────────────────────────────────────────────────┘

   Interfaces (Multi-modal):
   ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
   │  Web    │ │ Mobile  │ │ Voice   │ │ AR/VR   │ │ CLI/API │
   └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘
        │           │           │           │           │
        └───────────┴───────────┴───────────┴───────────┘
                            │
                            ▼
        ┌────────────────────────────────────────────────────┐
        │           SkillSeed Core Platform                   │
        │                                                     │
        │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
        │  │  Skill DNA  │  │  Matching   │  │ Reputation  │ │
        │  │  + AI       │  │  Engine v3  │  │  + zk-proof │ │
        │  └─────────────┘  └─────────────┘  └─────────────┘ │
        │                                                     │
        │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
        │  │ Wallet      │  │  Sessions   │  │  Community  │ │
        │  │ + Credits   │  │  Multi-modal│  │ + Offline   │ │
        │  └─────────────┘  └─────────────┘  └─────────────┘ │
        │                                                     │
        │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
        │  │ Marketplace │  │ Public API  │  │ Analytics   │ │
        │  │ + Subs      │  │ + Webhooks  │  │ + Insights  │ │
        │  └─────────────┘  └─────────────┘  └─────────────┘ │
        └─────────────────────────┬───────────────────────────┘
                                  │
                                  ▼
        ┌────────────────────────────────────────────────────┐
        │   Global Data & Trust Layer                          │
        │   - Postgres (8+ regions)                            │
        │   - Qdrant (vector)                                  │
        │   - Kafka global                                     │
        │   - Polygon / Multi-chain SBT + zk-SNARK             │
        │   - IPFS / Arweave (decentralized storage)           │
        │   - DID (Decentralized Identifier)                   │
        └─────────────────────────────────────────────────────┘

   External:
     OpenAI Realtime | Anthropic | Apple Vision Pro | Meta Quest
     Polygon | zkSync | Stripe Connect | Local payment | EU/US banks
     Apple Pay | Google Pay | Venmo | Cash App | SEPA | iDEAL
```

---

## 2. Voice-first AI Architecture

### 2.1. Stack

| Layer | Tech |
|-------|------|
| STT (Speech-to-Text) | OpenAI Whisper API hoặc Deepgram streaming |
| LLM | OpenAI GPT-4o Realtime API (audio-in, audio-out native) |
| TTS | OpenAI TTS-1 hoặc ElevenLabs (multilingual) |
| Wake word | Porcupine (Picovoice) hoặc Snowboy |
| VAD (Voice Activity Detection) | Silero VAD |
| Streaming protocol | WebRTC + custom audio frames |

### 2.2. Realtime API integration

```typescript
import { RealtimeAgent, RealtimeSession } from '@openai/agents-realtime';

const agent = new RealtimeAgent({
    name: 'SkillSeed Assistant',
    instructions: `Bạn là trợ lý AI của SkillSeed — nền tảng trao đổi kỹ năng.
    Hỗ trợ người dùng:
    - Tìm mentor phù hợp
    - Đặt lịch session
    - Trả lời câu hỏi về kỹ năng
    - Hướng dẫn sử dụng app
    
    Ngôn ngữ: phát hiện từ user, trả lời cùng ngôn ngữ.
    Giọng: thân thiện, rõ ràng, ngắn gọn.`,
    tools: [
        searchMentorsTool,
        bookSessionTool,
        getUserProfileTool,
    ],
});

const session = new RealtimeSession(agent, {
    model: 'gpt-realtime',
    turnDetection: { type: 'server_vad' },
});

await session.connect({ apiKey: OPENAI_API_KEY });
```

### 2.3. Voice UX patterns

- **Push-to-talk:** Nhấn nút để nói (đơn giản, không cần wake word).
- **Always-on:** "Hey SkillSeed" wake word (advanced, cần on-device).
- **Barge-in:** User có thể ngắt lời AI.

### 2.4. Voice flow: "Tìm mentor public speaking Singapore"

```
[User giọng]: "Hey SkillSeed, tôi muốn học public speaking với mentor Singapore"
                │
                ▼
         Wake word detected
                │
                ▼
         Audio stream → Realtime API
                │
                ▼
         LLM: extract intent {
            action: "search_mentor",
            skill: "public_speaking",
            region: "SG"
         }
                │
                ▼
         Tool call: searchMentors(skill, region)
                │
                ▼
         Backend: GET /matches?skill=...&region=SG
                │
                ▼
         Realtime API nhận data → synthesize response
                │
                ▼
[AI giọng]: "Tôi tìm thấy 5 mentor Public Speaking ở Singapore. 
            Mentor hàng đầu là Mai Tran với rating 4.9. 
            Bạn muốn book session với Mai không?"
                │
                ▼
         User: "OK, book 30 phút tối nay"
                │
                ▼
         Tool call: bookSession(mentorId, "30min", "today_evening")
                │
                ▼
         Backend: POST /bookings
                │
                ▼
[AI giọng]: "Đã book Mai Tran, 30 phút, lúc 8h tối nay. Tôi đã gửi link video call cho bạn."
```

---

## 3. AR/VR Architecture

### 3.1. AR Stack (WebXR + Native)

| Layer | Tech |
|-------|------|
| Web AR | WebXR API + Three.js |
| iOS Native | ARKit + RealityKit |
| Android Native | ARCore + Sceneform (legacy) hoặc Filament |
| Vision Pro | visionOS + RealityKit Pro |
| Sharing | Photon Engine hoặc custom WebRTC + signaling |
| Anchors | Cloud Anchors (Google) hoặc Azure Spatial Anchors |

### 3.2. AR Session flow

```
1. Teacher bật AR mode trong session
   - Chọn template (e.g., "Yoga pose", "Cooking recipe", "Bike repair")
   
2. Cả 2 bên mở AR view trên phone
   - Camera back hiển thị environment
   - 3D anchors (yoga instructor avatar, recipe steps overlay, repair diagram)
   
3. Realtime sync:
   - Pose/anchor positions sync qua WebRTC
   - Teacher có thể vẽ annotation (AR drawing)
   
4. Recording:
   - AR + audio + annotations được record
   - Save to S3, MP cho later review
```

### 3.3. Vision Pro native app

```swift
import RealityKit
import SkillSeedARSDK

struct SessionARView: View {
    @State var session: ARSession
    
    var body: some View {
        RealityView { content in
            // Load skill template
            let template = SkillTemplate.yogaSession
            content.add(template.environment)
            
            // Spawn mentor avatar (if applicable)
            let avatar = await Entity(named: "mentor-avatar")
            content.add(avatar)
        }
        .gesture(TapGesture().targetedToAnyEntity().onEnded { _ in
            // Anchor or annotate
        })
    }
}
```

### 3.4. Meta Horizon (VR) integration

- Build cho Quest standalone hoặc PC VR.
- Sử dụng Meta XR SDK + Unity hoặc Unreal.

---

## 4. zk-SNARK for Skill Verification

### 4.1. Use case

HR tech company muốn verify candidate có "Public Speaking ≥ 4.5 stars" nhưng không cần biết identity, sessions count, hay chi tiết khác.

### 4.2. zk circuit (Circom 2)

```circom
pragma circom 2.1.6;

include "circomlib/poseidon.circom";

template SkillThreshold(levels, threshold) {
    signal input rating;        // private (4.7)
    signal input skillId;       // private (hashed)
    signal input passportHash;  // private (commitment)
    signal input threshold;     // public (4.5)
    
    signal output valid;
    
    // Check rating >= threshold
    component gt = GreaterEqThan(8);
    gt.in[0] <== rating * 100;  // scale to int
    gt.in[1] <== threshold * 100;
    
    // Verify passport hash matches
    component hash = Poseidon(2);
    hash.inputs[0] <== skillId;
    hash.inputs[1] <== rating;
    hash.out === passportHash;
    
    valid <== gt.out;
}
```

### 4.3. Workflow

```
1. User generate passport hash on-chain (commitment)
   - Public: passportHash (on-chain)
   - Private: skillId, rating (off-chain)
3. When HR wants to verify "rating ≥ 4.5":
   - User generate zk-proof off-chain (private input = rating)
   - Public input: passportHash, threshold=4.5
   - Proof: valid=true/false
4. HR verifies proof:
   - Verify proof (no access to rating)
   - If valid → credential verified
   - Optional zk-passport showing the hash matches on-chain
```

### 4.4. Tools

- **Circom 2** cho circuit.
- **snarkjs** cho proving/verifying.
- **Polygon zkEVM** hoặc **zkSync** for cheap verification.

---

## 5. Offline Events Platform

### 5.1. Architecture

```
┌─────────────────────────────────────────────────────┐
│             OFFLINE EVENTS SUBSYSTEM                  │
└─────────────────────────────────────────────────────┘

  Event Service (Java/Spring)
       │
       ├─ Event CRUD
       ├─ RSVP + waitlist
       ├─ Geo-verification (geofence 200m radius)
       ├─ QR check-in (signed token)
       └─ Seed rewards (5 seeds/attendance)
       
  Event Discovery (web + mobile)
       │
       ├─ Map view (Mapbox GL)
       ├─ List view với filter (city, topic, date)
       └─ Featured events (admin-curated)
       
  Integrations:
       ├─ Google Places API (location autocomplete)
       ├─ Coworking partner API (WeWork, Toong, etc.)
       └─ Meetup.com import (legacy migration)
```

### 5.2. Geofence verification

```java
@Service
public class CheckInService {
    
    public boolean verifyLocation(UUID eventId, LatLng userLocation, LatLng eventLocation) {
        double distance = haversine(userLocation, eventLocation);
        double radius = event.getGeofenceRadiusMeters(); // default 200m
        return distance <= radius;
    }
    
    public boolean verifyQrCode(UUID eventId, String qrToken) {
        // QR token = HMAC(eventId + userId + timestamp, secret)
        // Verify chữ ký + timestamp freshness (< 5 phút)
        return hmacService.verify(qrToken, eventId);
    }
}
```

---

## 6. EU/US Expansion Architecture

### 6.1. Multi-region expansion (8+ regions)

```
Production Regions (Phase 5):
  - ap-southeast-1 (Singapore)  ← existing
  - ap-southeast-3 (Jakarta)    ← existing  
  - ap-southeast-5 (Manila)     ← existing
  - eu-central-1 (Frankfurt)    ← new
  - eu-west-1 (Ireland)         ← new
  - eu-west-3 (Paris)           ← new
  - us-east-1 (Virginia)        ← new (existing for Public API)
  - us-west-2 (Oregon)          ← new
  - ap-northeast-1 (Tokyo)      ← Phase 5.5 (Japan, Korea)
```

### 6.2. EU data residency

- User ở EU → data ở EU cluster.
- Cross-region chỉ aggregate analytics (no PII).
- Local DPO cho mỗi EU cluster.

### 6.3. EU/US payment methods

| Region | Methods |
|--------|---------|
| DE | SEPA, Klarna, Giropay, Sofort |
| FR | Carte Bancaire, SEPA |
| NL | iDEAL, Bancontact |
| IT | SEPA, Bancomat |
| ES | SEPA, Bizum |
| US | Apple Pay, Google Pay, Venmo, Cash App, Zelle, ACH |

---

## 7. Creator Subscription Model

### 7.1. Subscription tiers

```
Expert creator tạo tiers:
  - Bronze ($5/mo): Q&A monthly, 1 group session
  - Silver ($15/mo): + materials, 2 group sessions
  - Gold ($50/mo): + 1-1 monthly, exclusive content

Platform take: 15% commission (giảm dần theo tier).

Subscriber benefits:
  - Exclusive content feed
  - Group session access
  - Direct message priority
  - Discount trên 1-1 session
```

### 7.2. Stripe Billing Connect

- Expert onboard qua Stripe Connect Express.
- Recurring subscription via `subscriptions.create`.
- Payout 80-85% cho expert sau Stripe fees.

---

## 8. Endorsement Graph

### 8.1. Concept

LinkedIn-style endorsement nhưng on-chain, mang tính verifiable.

```solidity
struct Endorsement {
    address endorser;
    address endorsee;
    bytes32 skillHash;
    uint256 timestamp;
    string message;       // optional
}

mapping(bytes32 => Endorsement[]) public endorsements;
```

### 8.2. Reputation impact

```
Reputation score formula:
  score = (rating_avg * log(sessions + 1)) 
        + (endorsement_count * 0.5)
        + (verified_endorsement_count * 2)
        + (cross_region_endorsements * 0.3)

Top 1% users: "Legend+"
Top 5%: "Forest+"
Top 20%: "Tree+"
```

---

## 9. AI Coach Long-term (Premium+)

### 9.1. Persistent AI mentor

```
Concept: Mỗi Premium+ user có 1 "AI Coach" persistent:
  - Nhớ lịch sử học
  - Hiểu mục tiêu dài hạn
  - Suggest sessions mới dựa trên gaps
  - Động viên + accountability
  - Voice + text interface

Tech: 
  - LLM với long context (1M tokens)
  - Memory vector store
  - Periodic check-in (weekly summary email)
```

### 9.2. Tech stack

```typescript
class AICoach {
    private userId: string;
    private memoryStore: VectorStore;
    private conversationHistory: Message[];
    
    async onSessionComplete(session: Session): Promise<void> {
        // Update memory với session insights
        await this.memoryStore.add({
            userId: this.userId,
            content: `Learned ${session.skill} from ${session.mentor.name}. 
                      Key insights: ${session.summary}`,
            timestamp: new Date(),
        });
        
        // Generate next-week suggestions
        const suggestions = await this.llm.suggestNextSteps(this.memoryStore);
        await this.notifyUser(suggestions);
    }
    
    async chat(message: string): Promise<string> {
        const context = await this.memoryStore.search(message, limit: 5);
        return this.llm.chat({
            system: "You are the user's long-term learning coach.",
            messages: [...this.conversationHistory, { user: message }],
            context,
        });
    }
}
```

---

## 10. Series B & Strategic Exit Prep

### 10.1. Series B metrics to hit

| Metric | Target |
|--------|--------|
| ARR | ≥ $5M (raise trigger) |
| Growth rate | ≥ 100% YoY |
| MAU | ≥ 500K |
| Gross margin | ≥ 75% |
| LTV/CAC | ≥ 5x |
| Net Revenue Retention | ≥ 110% |
| Magic Number | ≥ 1.0 |

### 10.2. Strategic acquirer landscape

| Acquirer Type | Examples | Rationale |
|---------------|----------|-----------|
| EdTech giants | Coursera, Udemy, Byju's | Skill passport + community moat |
| HR Tech | LinkedIn, Indeed, Workday | Verified credentials API |
| Big Tech | Meta, Google, Microsoft | Future of Work positioning |
| Consulting | Accenture, McKinsey | L&D platform for enterprise |

### 10.3. M&A scenarios

- **Acquire:** Mua EdTech nhỏ (1-5 người team) có user base, technology, hoặc geography expansion.
- **Acquired:** Bán cho strategic acquirer (pre-IPO).
- **IPO:** Series C/D để IPO trên NASDAQ/SGX.
- **Stay private:** Series B/C với growth equity (no exit).

---

## 11. Long-term Bets (Beyond Phase 5)

### 11.1. Brain-Computer Interface (BCI)

- Pilot với Neuralink hoặc Kernel.
- Skill learning qua direct neural stimulation.
- Timeline: 2030+ (R&D only).

### 11.2. AGI Tutor

- Tích hợp AGI agent làm default AI tutor.
- SkillSeed = "AGI's school of real skills".
- Vision: Skill DNA + AGI = personalized lifelong learning.

### 11.3. Decentralized Governance (DAO)

- SkillSeed DAO quản lý platform.
- Token holders vote on policies.
- Reputation on-chain = governance weight.
- Defer đến khi đủ mature (Phase 6+).

---

## 12. Open Questions

| # | Câu hỏi | Owner |
|---|----------|-------|
| 1 | Voice: Realtime API hay self-hosted Whisper + LLM? | Voice AI lead |
| 2 | AR/VR: WebXR-first hay native-first? | AR/VR lead |
| 3 | Vision Pro vs Meta Quest priority? | Founder |
| 4 | zk-SNARK: Polygon zkEVM hay zkSync? | Blockchain lead |
| 5 | EU cluster: 1 big (Frankfurt) hay multiple small? | DevOps |
| 6 | Series B vs M&A: take decision criteria? | Founder + Board |