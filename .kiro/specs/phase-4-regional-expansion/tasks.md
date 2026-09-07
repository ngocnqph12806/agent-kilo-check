# Phase 4 — Regional Expansion: Tasks

> **Task list triển khai Regional Expansion theo sprint (3 tuần/sprint).**
> **10 sprints × 3 tuần = 30 tuần (~ 7.5 tháng)**

---

## Sprint 22 (Tuần 50–52) — Multi-region Foundation

### Region setup

- [ ] [T-X01] **[P0]** Setup EKS cluster ap-southeast-3 (Jakarta)
- [ ] [T-X02] **[P0]** Setup EKS cluster ap-southeast-5 (Manila)
- [ ] [T-X03] **[P0]** Setup RDS Postgres per region với cross-region replication
- [ ] [T-X04] **[P0]** Setup ElastiCache Redis per region
- [ ] [T-X05] **[P0]** Deploy tất cả microservices sang 3 regions (Helm multi-region)
- [ ] [T-X06] **[P0]** Setup Route53 geolocation routing
- [ ] [T-X07] **[P0]** Setup Cloudflare Load Balancer + WAF

### Data residency

- [ ] [T-X10] **[P0]** Implement RegionResolver (JWT claim → IP geo → profile default)
- [ ] [T-X11] **[P0]** Implement cross-region data flow (Kafka event bridge)
- [ ] [T-X12] **[P0]** Implement per-region DB connection routing
- [ ] [T-X13] **[P0]** Test failover giữa regions (kill primary, verify replica)
- [ ] [T-X14] **[P0]** DR runbook + chaos engineering tests

### Global services

- [ ] [T-X20] **[P0]** Setup ClickHouse global cluster (analytics aggregation)
- [ ] [T-X21] **[P0]** Setup global Kafka topics (cross-region events)
- [ ] [T-X22] **[P0]** Setup multi-region S3 với replication

---

## Sprint 23 (Tuần 53–55) — i18n & Localization

### Translation infrastructure

- [ ] [T-X30] **[P0]** Setup Crowdin project (5 locales: VI, EN, ID, FIL, ZH)
- [ ] [T-X31] **[P0]** Extract tất cả strings ra message bundles (frontend + backend)
- [ ] [T-X32] **[P0]** Implement next-intl (Next.js) routing
- [ ] [T-X33] **[P0]** Implement i18next cho mobile app
- [ ] [T-X34] **[P0]** Implement LocaleResolver từ URL + browser + user preference
- [ ] [T-X35] **[P0]** Auto-translate workflow với LLM (GPT-4o-mini first-pass)
- [ ] [T-X36] **[P0]** Hire/contract translators cho 4 non-English locales
- [ ] [T-X37] **[P0]** Translation review workflow (native speaker QA)

### Localized content

- [ ] [T-X40] **[P0]** Translate tất cả marketing pages (landing, blog, FAQ)
- [ ] [T-X41] **[P0]** Localize skill taxonomy (add region-specific skills)
- [ ] [T-X42] **[P0]** Localize onboarding flow
- [ ] [T-X43] **[P0]** Localize email templates
- [ ] [T-X44] **[P0]** SEO: hreflang tags + locale-specific URLs (`/vn/`, `/id/`, `/ph/`, `/sg/`)
- [ ] [T-X45] **[P0]** Locale-aware date/time/currency formatting

### Testing

- [ ] [T-X50] **[P0]** Pseudo-localization test (e.g., `[ĐĂNG KÝ]`)
- [ ] [T-X51] **[P0]** RTL support (cho Phase 5+ AR — skip now)
- [ ] [T-X52] **[P0]** Translation coverage report (≥ 95%)

---

## Sprint 24 (Tuần 56–58) — Multi-currency & Local Payments

### Stripe Connect multi-region

- [ ] [T-X60] **[P0]** Register legal entities (SG, ID, PH)
- [ ] [T-X61] **[P0]** Setup Stripe Connect platform account per entity
- [ ] [T-X62] **[P0]** Implement Stripe multi-currency prices
- [ ] [T-X63] **[P0]** Update checkout để display local currency
- [ ] [T-X64] **[P0]** Implement currency conversion (cached exchange rates)
- [ ] [T-X65] **[P0]** Implement PPP pricing logic per region

### Local payment integration

- [ ] [T-X70] **[P0]** Vietnam: MoMo + ZaloPay + VNPay integration
- [ ] [T-X71] **[P0]** Singapore: PayNow + GrabPay (via Stripe/HitPay)
- [ ] [T-X72] **[P0]** Indonesia: GoPay + OVO + DANA (via Midtrans/Xendit)
- [ ] [T-X73] **[P0]** Philippines: GCash + GrabPay + PayMaya (via PayMongo)
- [ ] [T-X74] **[P0]** Test checkout flow với mỗi local payment method
- [ ] [T-X75] **[P0]** Payout cho Expert (local currency, local bank/e-wallet)

### Tax compliance

- [ ] [T-X80] **[P0]** Tax calculator per region (VN VAT 10%, SG GST 9%, ID VAT 11%, PH VAT 12%)
- [ ] [T-X81] **[P0]** Tax invoice generation
- [ ] [T-X82] **[P0]** Stripe Tax integration (auto-calculate)
- [ ] [T-X83] **[P0]** Cross-border VAT/GST logic (B2C digital services)

### Testing

- [ ] [T-X90] **[P0]** Test multi-currency purchase end-to-end
- [ ] [T-X91] **[P0]** Test payout to local bank account

---

## Sprint 25 (Tuần 59–61) — Blockchain SBT

### Smart contract

- [ ] [T-X100] **[P0]** Develop SkillSBT.sol (ERC-5114 soulbound)
- [ ] [T-X101] **[P0]** Security audit (CertiK hoặc OpenZeppelin)
- [ ] [T-X102] **[P0]** Deploy lên Polygon Mumbai (testnet)
- [ ] [T-X103] **[P0]** Deploy lên Polygon Mainnet
- [ ] [T-X104] **[P0]** Setup upgrade pattern (proxy contract cho future improvements)

### Backend integration

- [ ] [T-X110] **[P0]** Setup Web3j cho Java backend
- [ ] [T-X111] **[P0]** Setup private key management (AWS KMS hoặc HashiCorp Vault)
- [ ] [T-X112] **[P0]** Implement mint flow trong reputation-service
  - Event listener `passport.issued` → call smart contract
- [ ] [T-X113] **[P0]** Implement IPFS upload (Pinata hoặc web3.storage)
- [ ] [T-X114] **[P0]** Implement metadata JSON schema
- [ ] [T-X115] **[P0]** Implement revoke flow (user delete, fraud detection)

### Wallet connection

- [ ] [T-X120] **[P0]** Implement MetaMask connect (web)
- [ ] [T-X121] **[P0]** Implement WalletConnect (mobile)
- [ ] [T-X122] **[P0]** Sign-in with Ethereum (SIWE) optional
- [ ] [T-X123] **[P0]** Display wallet address trên profile
- [ ] [T-X124] **[P0]** Display SBT tokens owned (read from blockchain)

### Verification

- [ ] [T-X130] **[P0]** Public verify endpoint `/verify/{txHash}`
- [ ] [T-X131] **[P0]** SBT badge UI trên profile (link tới PolygonScan)
- [ ] [T-X132] **[P0]** Test full mint → verify flow

### Testing

- [ ] [T-X140] **[P0]** Smart contract test suite (Hardhat)
- [ ] [T-X141] **[P0]** Integration test với Mumbai testnet
- [ ] [T-X142] **[P0]** Gas optimization (target < $0.01/mint)

---

## Sprint 26 (Tuần 62–64) — Public API & Developer Platform

### API infrastructure

- [ ] [T-X150] **[P0]** Setup public API cluster (us-east-1)
- [ ] [T-X151] **[P0]** Implement OAuth2 client_credentials flow
- [ ] [T-X152] **[P0]** Implement API key management cho partners
- [ ] [T-X153] **[P0]** Implement rate limiting per tier (1K/10K/100K/day)

### OpenAPI & SDK

- [ ] [T-X160] **[P0]** Author OpenAPI 3.1 spec đầy đủ
- [ ] [T-X161] **[P0]** Generate TypeScript SDK
- [ ] [T-X162] **[P0]** Generate Python SDK
- [ ] [T-X163] **[P0]** Generate Java SDK
- [ ] [T-X164] **[P0]** Publish SDKs lên npm, PyPI, Maven Central

### Developer portal

- [ ] [T-X170] **[P0]** Setup docs.skillseed.com (Docusaurus hoặc custom)
- [ ] [T-X171] **[P0]** Getting Started guide
- [ ] [T-X172] **[P0]** API reference (auto-generated)
- [ ] [T-X173] **[P0]** Sandbox environment setup
- [ ] [T-X174] **[P0]** Webhook documentation
- [ ] [T-X175] **[P0]** Sample apps (Next.js, Python)

### Webhooks

- [ ] [T-X180] **[P0]** Implement webhook dispatcher service
- [ ] [T-X181] **[P0]** HMAC signing
- [ ] [T-X182] **[P0]** Retry logic với exponential backoff
- [ ] [T-X183] **[P0]** Webhook delivery status UI

### Testing

- [ ] [T-X190] **[P0]** Sandbox partners (5 công ty đầu tiên)
- [ ] [T-X191] **[P0]** Load test public API (10K req/min)

---

## Sprint 27 (Tuần 65–67) — AI Matching v2

### Multilingual embeddings

- [ ] [T-X200] **[P0]** Setup multilingual embedding pipeline
- [ ] [T-X201] **[P0]** Compare OpenAI text-embedding-3-large vs BGE-M3
- [ ] [T-X202] **[P0]** A/B test multilingual vs single-language
- [ ] [T-X203] **[P0]** Production rollout của winning model
- [ ] [T-X204] **[P0]** Re-rank model trained trên multi-region data

### Cross-language matching

- [ ] [T-X210] **[P0]** Implement cross-language embeddings (cùng vector space)
- [ ] [T-X211] **[P0]** Test VN user matched với ID user (different languages)
- [ ] [T-X212] **[P0]** Localized match explanations (per locale LLM)

### Bias & fairness

- [ ] [T-X220] **[P0]** Bias detection pipeline (gender, region, language)
- [ ] [T-X221] **[P0]** Fairness metrics dashboard
- [ ] [T-X222] **[P0]** Alert system nếu bias detected
- [ ] [T-X223] **[P0]** Manual review cho flagged cases

### A/B test framework

- [ ] [T-X230] **[P0]** Implement experiment framework (variant assignment, exposure logging)
- [ ] [T-X231] **[P0]** Statistical significance calculator
- [ ] [T-X232] **[P0]** UI cho A/B test results (cho product team)

### Testing

- [ ] [T-X240] **[P0]** Validate cross-region matching quality
- [ ] [T-X241] **[P0]** Load test 50K concurrent matching requests

---

## Sprint 28 (Tuần 68–70) — Cross-border Features + Compliance

### Cross-border matching

- [ ] [T-X250] **[P0]** User preference: "Allow cross-border matches"
- [ ] [T-X251] **[P0]** Timezone overlap scoring (≥ 2h/day)
- [ ] [T-X252] **[P0]** Currency conversion display
- [ ] [T-X253] **[P0]** Filter by region
- [ ] [T-X254] **[P0]** Language requirement enforcement

### Auto-translate reviews

- [ ] [T-X260] **[P0]** LLM translate rating reviews
- [ ] [T-X261] **[P0]** Display original + translated toggle
- [ ] [T-X262] **[P0]** Cache translations

### Compliance

- [ ] [T-X270] **[P0]** Indonesia UU PDP compliance audit
- [ ] [T-X271] **[P0]** Philippines DPA compliance audit
- [ ] [T-X272] **[P0]** Vietnam Nghị định 13/2023 compliance review
- [ ] [T-X273] **[P0]** SOC 2 Type II audit (start observation period)
- [ ] [T-X274] **[P0]** Cookie consent per region

### Data subject rights

- [ ] [T-X280] **[P0]** Right to access (data export)
- [ ] [T-X281] **[P0]** Right to delete (cross-region propagation)
- [ ] [T-X282] **[P0]** Right to rectify
- [ ] [T-X283] **[P0]** Consent withdrawal flow

---

## Sprint 29 (Tuần 71–73) — Country Launch Preparation

### Singapore launch

- [ ] [T-X290] **[P0]** SG landing page + localized content
- [ ] [T-X291] **[P0]** SG partnership outreach (NUS, NTU, Grab, Shopee)
- [ ] [T-X292] **[P0]** SG community building (LinkedIn, meetups)
- [ ] [T-X293] **[P0]** SG legal entity setup (Pte Ltd)
- [ ] [T-X294] **[P0]** SG press + media outreach (Tech in Asia, e27)

### Indonesia launch

- [ ] [T-X300] **[P0]** ID landing page + content
- [ ] [T-X301] **[P0]** ID partnership (UI, ITB, Telkom)
- [ ] [T-X302] **[P0]** ID community (Instagram, TikTok)
- [ ] [T-X303] **[P0]** ID legal entity (PT)
- [ ] [T-X304] **[P0]** ID creator partnerships (Bahasa micro-influencers)

### Philippines launch

- [ ] [T-X310] **[P0]** PH landing page
- [ ] [T-X311] **[P0]** PH partnership (UP, Ateneo, BPO companies)
- [ ] [T-X312] **[P0]** PH community (Facebook groups)
- [ ] [T-X313] **[P0]** PH legal entity

### Operations

- [ ] [T-X320] **[P0]** Customer support team (3 timezones)
- [ ] [T-X321] **[P0]** Country-specific FAQ + help center
- [ ] [T-X322] **[P0]** Multi-region on-call rotation
- [ ] [T-X323] **[P0]** Local content moderation team

---

## Sprint 30 (Tuần 74–76) — Public Launch (3 quốc gia)

### Marketing campaign

- [ ] [T-X330] **[P0]** Simultaneous launch event (online + offline)
- [ ] [T-X331] **[P0]** PR in 3 countries
- [ ] [T-X332] **[P0]** Influencer campaign (15 micro-influencers across regions)
- [ ] [T-X333] **[P0]** Paid ads (Google + Meta) $20K total
- [ ] [T-X334] **[P0]** SEO content (50+ articles per region)

### Monitoring

- [ ] [T-X340] **[P0]** Daily metrics review per region
- [ ] [T-X341] **[P0]** Customer support SLA tracking
- [ ] [T-X342] **[P0]** Bug triage (3 timezones)
- [ ] [T-X343] **[P0]** Conversion funnel analysis

### Iteration

- [ ] [T-X350] **[P0]** Weekly feedback from country teams
- [ ] [T-X351] **[P0]** Quick fixes cho top user complaints
- [ ] [T-X352] **[P0]** A/B test localized variants

---

## Sprint 31 (Tuần 77–79) — Series A Fundraising

### Preparation

- [ ] [T-X360] **[P0]** Pitch deck updated với Phase 3 + 4 metrics
- [ ] [T-X361] **[P0]** Financial model (3-year projection)
- [ ] [T-X362] **[P0]** Data room (contracts, metrics, code samples)
- [ ] [T-X363] **[P0]** Reference calls (3 B2B customers)
- [ ] [T-X364] **[P0]** Diligence prep (tech audit, legal audit)

### Investor outreach

- [ ] [T-X370] **[P0]** List 30 target VCs (EdTech, Future of Work, SEA focus)
- [ ] [T-X371] **[P0]** Warm intros qua advisors
- [ ] [T-X372] **[P0]** First meetings → 10 second meetings
- [ ] [T-X373] **[P0]** Partner meetings → 3 finalists
- [ ] [T-X374] **[P0]** Term sheet negotiation
- [ ] [T-X375] **[P0]** Close Series A ($3-5M)

---

## Cross-cutting tasks (30 tuần)

- [ ] [T-X400] **[P0]** Daily standup (global team, async-first)
- [ ] [T-X401] **[P0]** Weekly sprint review + monthly OKR
- [ ] [T-X402] **[P0]** Cross-region team sync (monthly all-hands)
- [ ] [T-X403] **[P0]** Code review với timezone overlap windows
- [ ] [T-X404] **[P0]** Quarterly board update (khi có investor)
- [ ] [T-X405] **[P0]** Track North Star + region-specific KPIs
- [ ] [T-X406] **[P1]** Customer interview per region (5/tháng)

---

## Definition of Done — Phase 4

### Functional

- [ ] 100% FR-X MUST đã implement
- [ ] Live ở 4 quốc gia (VN, SG, ID, PH)
- [ ] i18n 5 ngôn ngữ ≥ 95% coverage
- [ ] Local payment methods per region
- [ ] SBT minting hoạt động end-to-end
- [ ] Public API + 20 partners

### Non-Functional

- [ ] Multi-region p95 latency < 200ms (in-region)
- [ ] Public API uptime ≥ 99.95%
- [ ] SOC 2 Type II audit passed

### Business

- [ ] MAU ≥ 200.000 (across all regions)
- [ ] Premium subs ≥ 5.000
- [ ] B2B clients ≥ 50
- [ ] ARR ≥ $1M
- [ ] NPS ≥ 55

### Operational

- [ ] Multi-region production stable
- [ ] Country teams operational
- [ ] Series A closed

---

## Effort Estimation

| Sprint | Focus | Effort |
|--------|-------|--------|
| 22 | Multi-region foundation | 200h |
| 23 | i18n + L10n | 180h |
| 24 | Multi-currency + Payments | 180h |
| 25 | Blockchain SBT | 160h |
| 26 | Public API + DevRel | 160h |
| 27 | AI Matching v2 | 140h |
| 28 | Cross-border + Compliance | 140h |
| 29 | Country launch prep | 100h |
| 30 | Public launch | 80h |
| 31 | Series A | 80h (founder) |
| **Tổng** | | **~1,420h** + nhiều parallel team |

---

## Risk Register

| Risk | Trigger | Mitigation |
|------|---------|------------|
| Multi-region bug ảnh hưởng nhiều user | Outage toàn region | Canary deploy, auto-rollback |
| Local payment integration delay | Provider verification issue | 2 providers per region, dự phòng |
| Translation quality kém | Native review feedback | Pre-launch LQA với native speakers |
| SBT smart contract bug | Security audit fail | Audit trước khi deploy, bug bounty |
| Data residency violation | Audit fail | Legal review trước launch |
| Country GM hire chậm | Không tìm được người phù hợp | Founder tạm quản lý 1 region, hire dần |
| Series A không close | Investor pass | Bridge funding từ existing investors |
| Legal entity setup delay | Country regulation | Thuê local lawyer |