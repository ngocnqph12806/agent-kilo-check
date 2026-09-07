# Phase 5 — Beyond: Tasks (Roadmap-level)

> **Phase 5 là giai đoạn "vision" — tasks này là roadmap định hướng, không phải sprint backlog cứng.**
> **Ưu tiên theo năm-quarter, không theo sprint.**

---

## Year 2 — Q1–Q2: Voice AI + AR Foundation

### Voice-first AI

- [ ] [T-V01] **[P0]** Pilot OpenAI Realtime API cho voice assistant
  - Test với 100 user nội bộ
  - Đo latency, accuracy, satisfaction
- [ ] [T-V02] **[P0]** Implement voice-first matching flow (4 use case chính)
  - "Tìm mentor X"
  - "Book session với Y"
  - "Hủy session Z"
  - "Lộ trình học W"
- [ ] [T-V03] **[P0]** Build voice UX components
  - Push-to-talk button (mobile + web)
  - Voice activity indicator
  - Barge-in support
- [ ] [T-V04] **[P0]** Wake word detection (optional)
  - Porcupine hoặc Picovoice
  - "Hey SkillSeed"
- [ ] [T-V05] **[P1]** Voice biometric authentication (opt-in)
- [ ] [T-V06] **[P1]** Voice cloning cho AI assistant (multilingual)
- [ ] [T-V07] **[P0]** Multilingual voice (5+ ngôn ngữ)
- [ ] [T-V08] **[P0]** Voice analytics dashboard
  - Usage, intent distribution, satisfaction

### AR Foundation

- [ ] [T-A01] **[P0]** Hire AR/VR lead engineer
- [ ] [T-A02] **[P0]** Setup AR development environment
  - WebXR + Three.js stack
  - iOS ARKit + RealityKit
  - Android ARCore
- [ ] [T-A03] **[P0]** Build SkillSeed AR SDK (shared library)
- [ ] [T-A04] **[P0]** Pilot AR session cho 2 verticals
  - Yoga (form correction)
  - Cooking (recipe overlay)
- [ ] [T-A05] **[P0]** Multi-user AR sync (anchor sharing)
- [ ] [T-A06] **[P0]** AR recording + replay

### Apple Vision Pro

- [ ] [T-A10] **[P0]** Apply cho Apple Vision Pro developer kit
- [ ] [T-A11] **[P0]** Develop visionOS native app
  - Skill sessions in immersive space
- [ ] [T-A12] **[P0]** Submit to visionOS App Store

---

## Year 2 — Q3: VR + Offline Events

### VR (Meta Quest)

- [ ] [T-VR01] **[SHOULD]** Meta Quest standalone app
- [ ] [T-VR02] **[SHOULD]** VR avatar system
- [ ] [T-VR03] **[SHOULD]** Immersive whiteboard
- [ ] [T-VR04] **[SHOULD]** Multi-user VR session

### Offline Events Platform

- [ ] [T-E01] **[P0]** Event service (Java/Spring) — full CRUD
- [ ] [T-E02] **[P0]** Event entity + migrations
- [ ] [T-E03] **[P0]** RSVP + waitlist logic
- [ ] [T-E04] **[P0]** GPS check-in (geofence verification)
- [ ] [T-E05] **[P0]** QR check-in (HMAC signed tokens)
- [ ] [T-E06] **[P0]** Seed rewards (5 seeds/attendance)
- [ ] [T-E07] **[P0]** Event discovery page (map + list)
- [ ] [T-E08] **[P0]** Event chat (WebSocket)
- [ ] [T-E09] **[P0]** Photo/video upload từ event
- [ ] [T-E10] **[P1]** Coworking space partnership API (WeWork, Toong)
- [ ] [T-E11] **[P0]** Offline events seeded trong 4 regions (SG, ID, PH, VN)

---

## Year 2 — Q4: EU Launch + Creator Subscriptions

### EU Compliance

- [ ] [T-EU01] **[P0]** Hire EU legal counsel
- [ ] [T-EU02] **[P0]** GDPR DPIA (Data Protection Impact Assessment)
- [ ] [T-EU03] **[P0]** Setup EU cluster (eu-central-1 Frankfurt)
- [ ] [T-EU04] **[P0]** Data residency enforcement cho EU users
- [ ] [T-EU05] **[P0]** DPO hire + training
- [ ] [T-EU06] **[P0]** Cookie consent per GDPR (granular)
- [ ] [T-EU07] **[P0]** Right to be forgotten (cross-region)

### EU Localization

- [ ] [T-EU10] **[P0]** Translation cho DE, FR, ES, IT, NL, PL
- [ ] [T-EU11] **[P0]** Local payment integration
  - SEPA, iDEAL, Klarna, Bancontact, Giropay
- [ ] [T-EU12] **[P0]** EU marketing pages
- [ ] [T-EU13] **[P0]** EU launch (5+ countries)

### Creator Subscriptions

- [ ] [T-CR01] **[P0]** Stripe Billing Connect cho Creator
- [ ] [T-CR02] **[P0]** Subscription tier setup (Bronze/Silver/Gold)
- [ ] [T-CR03] **[P0]** Expert dashboard (subscribers, MRR, churn)
- [ ] [T-CR04] **[P0]** Subscriber benefits UI (exclusive content feed)
- [ ] [T-CR05] **[P0]** Pilot với 50 top experts
- [ ] [T-CR06] **[P0]** Discovery surface cho premium experts

---

## Year 3 — Q1: US Launch + US Compliance

### US Localization

- [ ] [T-US01] **[P0]** Hire US legal counsel
- [ ] [T-US02] **[P0]** US entity setup (Delaware C-Corp)
- [ ] [T-US03] **[P0]** US cluster (us-east-1)
- [ ] [T-US04] **[P0]** US payment: Venmo, Cash App, Zelle, ACH
- [ ] [T-US05] **[P0]** US tax (Stripe Tax + state-by-state)
- [ ] [T-US06] **[P0]** ADA accessibility (WCAG 2.1 AA)
- [ ] [T-US07] **[P0]** CCPA/CPRA compliance

### US Market Entry

- [ ] [T-US10] **[P0]** US landing page + localized content
- [ ] [T-US11] **[P0]** US partnership (universities, B2B)
- [ ] [T-US12] **[P0]** US community building (LinkedIn, Twitter, Discord)
- [ ] [T-US13] **[P0]** US press + media outreach
- [ ] [T-US14] **[P0]** US launch

---

## Year 3 — Q2: Advanced Reputation + zk-SNARK

### zk-SNARK

- [ ] [T-Z01] **[P0]** Hire zk-SNARK engineer
- [ ] [T-Z02] **[P0]** Setup Circom + snarkjs pipeline
- [ ] [T-Z03] **[P0]** Design circuit cho Skill Passport (threshold proof)
- [ ] [T-Z04] **[P0]** Implement proving service (backend)
- [ ] [T-Z05] **[P0]** Implement verification SDK (TypeScript, Python)
- [ ] [T-Z06] **[P0]** Deploy smart contract (Polygon zkEVM)
- [ ] [T-Z07] **[P0]** Pilot với 3 HR tech partners (LinkedIn, Workday, Indeed)
- [ ] [T-Z08] **[P0]** Selective disclosure UI (user chọn reveal gì)
- [ ] [T-Z09] **[P1]** Multi-skill circuit (batch verify)

### Endorsement Graph

- [ ] [T-EN01] **[P0]** Smart contract EndorsementRegistry.sol
- [ ] [T-EN02] **[P0]** UI: endorse skill của user khác
- [ ] [T-EN03] **[P0]** Cross-region endorsement support
- [ ] [T-EN04] **[P0]** Reputation score update với endorsement weight
- [ ] [T-EN05] **[P0]** Anti-spam (rate limit, mutual endorsement detection)

---

## Year 3 — Q3: AI Coach Long-term + Series B Prep

### AI Coach

- [ ] [T-C01] **[P0]** Persistent AI Coach cho Premium+
- [ ] [T-C02] **[P0]** Long-context LLM integration (1M tokens)
- [ ] [T-C03] **[P0]** Memory vector store per user
- [ ] [T-C04] **[P0]** Weekly check-in email
- [ ] [T-C05] **[P0]** Goal tracking + accountability
- [ ] [T-C06] **[P1]** Multi-modal coach (voice + text + AR)

### Series B Fundraising

- [ ] [T-B01] **[P0]** Pitch deck update với Year 2 metrics
- [ ] [T-B02] **[P0]** Financial model 3-year projection
- [ ] [T-B03] **[P0]** Reference calls (10 customers)
- [ ] [T-B04] **[P0]** Technical audit prep
- [ ] [T-B05] **[P0]** Series B target outreach (30 VCs)
- [ ] [T-B06] **[P0]** Close Series B ($15-25M)
- [ ] [T-B07] **[P0]** Board setup với new investors

### M&A exploration (parallel)

- [ ] [T-M01] **[P0]** Strategic acquirer landscape analysis
- [ ] [T-M02] **[P0]** Inbound interest evaluation
- [ ] [T-M03] **[P0]** IP portfolio review
- [ ] [T-M04] **[P0]** Acquire candidates (2-3 small EdTech startups)

---

## Year 3 — Q4: Scale + Future Bets

### Scale operations

- [ ] [T-S01] **[P0]** Hire executive team
  - COO, VP Engineering, VP Sales, VP Marketing
- [ ] [T-S02] **[P0]** International hubs (London, NY, Tokyo)
- [ ] [T-S03] **[P0]** Enterprise sales team (US + EU)
- [ ] [T-S04] **[P0]** Customer Success team scale
- [ ] [T-S05] **[P0]** Marketing team scale (40+ people)

### Future bets

- [ ] [T-F01] **[P2]** BCI R&D pilot (Neuralink / Kernel partnership)
- [ ] [T-F02] **[P2]** AGI integration partnership (OpenAI / Anthropic)
- [ ] [T-F03] **[P2]** DAO governance exploration
- [ ] [T-F04] **[P2]** Marketplace for AI-generated content (Phase 6 prep)

### Strategic exit prep (if applicable)

- [ ] [T-X01] **[P0]** M&A materials ready
- [ ] [T-X02] **[P0]** Acquisition conversations với 3-5 strategics
- [ ] [T-X03] **[P0]** Term sheet negotiation
- [ ] [T-X04] **[P0]** Close deal OR continue to Series C

---

## Cross-cutting (continuous)

- [ ] [T-Y01] **[P0]** Quarterly OKR setting + review
- [ ] [T-Y02] **[P0]** Annual board strategy meeting
- [ ] [T-Y03] **[P0]** Continuous product-market fit monitoring
- [ ] [T-Y04] **[P0]** Culture + hiring quality
- [ ] [T-Y05] **[P0]** ESG / impact reporting (annual)
- [ ] [T-Y06] **[P1]** Industry thought leadership (conferences, papers)

---

## Definition of Done — Phase 5

Phase 5 là giai đoạn dài (2 năm). "Done" có nhiều definitions:

### 18-month milestone

- [ ] Voice-first matching serving 20%+ MAU
- [ ] AR/VR capability deployed cho 3+ verticals
- [ ] EU live (5+ countries)
- [ ] US live
- [ ] MAU ≥ 1M

### 24-month milestone

- [ ] Offline events platform hosting 1K+/year
- [ ] zk-SNARK verify adopted by 3+ HR tech
- [ ] Creator subscriptions 5K+ subscribers
- [ ] ARR ≥ $10M
- [ ] Series B closed ($15-25M) OR strategic exit signed

### 36-month vision

- [ ] "GitHub of real skills" brand recognition
- [ ] 5M+ MAU global
- [ ] $30M+ ARR
- [ ] Net positive impact measurement (skills taught, hours exchanged)

---

## Effort Estimation

Phase 5 rất parallel và explore. Estimate rất khó chính xác:

| Initiative | Effort (Y2) | Effort (Y3) |
|-----------|-------------|-------------|
| Voice AI | 600h | 300h |
| AR/VR | 800h | 400h |
| Offline Events | 200h | 100h |
| EU Launch | 400h | 100h |
| US Launch | 100h | 600h |
| zk-SNARK | 100h | 400h |
| Creator Subs | 200h | 200h |
| AI Coach | 100h | 400h |
| Series B / M&A | 100h | 200h |
| Operations Scale | 800h | 1500h |
| Future Bets | 50h | 200h |
| **Total** | **~3,450h** | **~4,400h** |

Tương đương team 25-50 người × 2 năm.

---

## Risk Register (Phase 5)

| Risk | Trigger | Mitigation |
|------|---------|------------|
| Voice AI chi phí cao | OpenAI Realtime > $50K/mo | Self-host Whisper + small LLM cho simple intents |
| AR/VR adoption thấp | < 5% sessions dùng AR | Optional feature, không block core |
| EU/US compliance phức tạp | Local lawyer delay | Buffer 6 tháng, big 4 audit |
| Series B down round | Market downturn | Bridge funding từ existing investors |
| Strategic exit không xảy ra | No buyer interest | Continue growth, prepare for Series C |
| Founder burnout | > 70h/tuần liên tục | Hire COO + executive team ASAP |
| Competitor copy | Big Tech launches similar feature | Network effect + data moat + community |