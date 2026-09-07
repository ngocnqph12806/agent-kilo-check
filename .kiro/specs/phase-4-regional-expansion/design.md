# Phase 4 — Regional Expansion: Design

> **Tài liệu thiết kế kỹ thuật cho Regional Expansion.**
> **Triết lý:** Multi-region deployment với data residency, blockchain layer cho verifiable credentials, public API & SDK.

---

## 1. High-Level Architecture (Multi-Region)

```
┌──────────────────────────────────────────────────────────────────────┐
│                  PHASE 4 MULTI-REGION ARCHITECTURE                    │
└──────────────────────────────────────────────────────────────────────┘

                   ┌──────────────────────────┐
                   │   Cloudflare Global CDN   │
                   │   (smart routing + WAF)   │
                   └────────┬─────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
   ┌─────────┐         ┌─────────┐         ┌─────────┐
   │ Region: │         │ Region: │         │ Region: │
   │ VN + APAC SE-1   │ SG (SE-1)│         │ ID (SE-3)│
   │ (Singapore)      │          │         │ (Jakarta)│
   └────┬────┘         └────┬────┘         └────┬────┘
        │                   │                   │
        │ ┌─────────────────┴───────────────┐   │
        │ │   Microservices (per region)      │   │
        │ │   - K8s cluster (EKS multi-AZ)   │   │
        │ │   - All 13 services from Phase 3  │   │
        │ └─────────────────┬─────────────────┘   │
        │                   │                    │
        │ ┌─────────────────┴───────────────┐    │
        │ │   Data Layer (per region)         │    │
        │ │   - Postgres RDS Multi-AZ         │    │
        │ │   - Redis ElastiCache             │    │
        │ │   - Qdrant Cloud                  │    │
        │ │   - S3 (region-local)             │    │
        │ └─────────────────┬─────────────────┘    │
        │                   │                     │
        └───────────────────┼─────────────────────┘
                            │
                            ▼
   ┌──────────────────────────────────────────────────────┐
   │   Global Services (region-agnostic)                  │
   │   - Public API Gateway (us-east-1)                   │
   │   - Blockchain RPC (Polygon, global)                 │
   │   - Analytics aggregation (ClickHouse global)       │
   │   - Webhooks dispatcher (global)                    │
   │   - Developer Portal (Vercel)                       │
   └──────────────────────────────────────────────────────┘

   External:
     Stripe Connect (multi-region) | Polygon | OpenAI | Daily.co
     Crowdin (translation) | Local payment gateways
```

---

## 2. Multi-Region Strategy

### 2.1. Region topology

| Region | Primary DB | Users | Replication to |
|--------|-----------|-------|-----------------|
| **ap-southeast-1 (Singapore)** | Main cluster | VN, SG, MY, TH | Read replica to ap-southeast-3 |
| **ap-southeast-3 (Jakarta)** | Local cluster | ID | Read replica to ap-southeast-1 |
| **ap-southeast-5 (Manila)** | Local cluster | PH | Read replica to ap-southeast-1 |
| **us-east-1 (Virginia)** | Public API only | Global developers | — |
| **eu-west-1 (Ireland)** | Future EU | (Phase 5) | — |

### 2.2. Cross-region data flow

- **User data:** Stored in user's home region (per data residency).
- **Cross-region queries:** Async via Kafka event bus + global aggregator service.
- **Replication:** Async cross-region for DR (RPO 1h).
- **Global analytics:** Aggregated data → ClickHouse global cluster (no PII).

### 2.3. Region detection

```java
@Component
public class RegionResolver {
    public Region resolveRegion(HttpServletRequest request) {
        // 1. Check JWT claim (preferred)
        String regionFromToken = jwtService.extractClaim("region");
        if (regionFromToken != null) return Region.of(regionFromToken);
        
        // 2. IP geolocation
        String country = ipGeoService.lookup(request.getRemoteAddr());
        return Region.fromCountry(country);
        
        // 3. Default to user's profile.region
        // 4. Fallback: ap-southeast-1
    }
}
```

---

## 3. i18n Architecture

### 3.1. Tech stack

| Layer | Tool |
|-------|------|
| Translation management | **Crowdin** (SaaS) |
| Frontend i18n | **next-intl** (Next.js) hoặc **i18next** (mobile) |
| Backend i18n | **Java MessageResource** + Crowdin sync |
| Translation memory | Crowdin built-in |
| Glossary | Crowdin + skill-specific terms |
| Auto-suggest | LLM (GPT-4o-mini) cho first-draft translations |
| LQA (Linguistic QA) | Crowdin + native reviewer |

### 3.2. Workflow

```
1. Developer mark strings với key trong code
   → e.g., t("booking.confirm.title")

2. CI pipeline auto-push new strings → Crowdin

3. Translators (in-house + freelancers) dịch → Crowdin

4. Auto-sync translations về codebase (PR hoặc direct commit)

5. LQA: native reviewer check translation quality trước release

6. Production deploy → load messages.{locale}.json
```

### 3.3. Pluralization & locale-specific

```typescript
// VN: không có số nhiều, chỉ 1 form
t("booking.count", { count: 1 }); // "1 buổi học"
t("booking.count", { count: 5 }); // "5 buổi học"

// EN: 2 forms (singular/plural)
t("booking.count", { count: 1 }); // "1 session"
t("booking.count", { count: 5 }); // "5 sessions"

// ID: có thể có nhiều forms (sedikit/banyak)
```

### 3.4. Backend messages

```java
@Component
public class MessageService {
    @Autowired private MessageSource messageSource;
    
    public String get(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }
}
```

DB-backed message overrides per region (e.g., region-specific FAQs).

---

## 4. Multi-Currency & Local Payment

### 4.1. Stripe Connect setup

```
SkillSeed Inc (US parent)
  ├── Connect Platform Account
  ├── VN entity: SkillSeed Vietnam LLC
  │     └── Stripe account (VND, MoMo, ZaloPay)
  ├── SG entity: SkillSeed Singapore Pte Ltd
  │     └── Stripe account (SGD, PayNow, cards)
  ├── ID entity: PT SkillSeed Indonesia
  │     └── Stripe account (IDR, GoPay, OVO, DANA)
  └── PH entity: SkillSeed Philippines Inc
        └── Stripe account (PHP, GCash, GrabPay)
```

### 4.2. Pricing per region (PPP)

| Region | Premium/mo | Family/mo | Seed Pack 100 | Expert rate ceiling |
|--------|-----------|-----------|---------------|---------------------|
| VN | 249.000 VND (~$10) | 374.000 VND (~$15) | 124.000 VND (~$5) | 5.000.000 VND (~$200) |
| SG | $9.9 SGD | $14.9 SGD | $4.9 SGD | $200 SGD |
| ID | 49.000 IDR (~$3) | 74.000 IDR (~$4.5) | 19.000 IDR (~$1.2) | 990.000 IDR (~$60) |
| PH | ₱499 (~$9) | ₱749 (~$13.5) | ₱249 (~$4.5) | ₱9.999 (~$180) |

### 4.3. Local payment integration

| Region | Local Methods | Provider |
|--------|---------------|----------|
| VN | MoMo, ZaloPay, VNPay, Bank transfer | MoMo API, ZaloPay API, VNPay gateway |
| SG | PayNow, GrabPay, cards | Stripe + HitPay |
| ID | GoPay, OVO, DANA, ShopeePay | Midtrans, Xendit |
| PH | GCash, GrabPay, PayMaya, 7-Eleven | PayMongo, DragonPay |

### 4.4. Tax handling

- **VN:** VAT 10% cho digital services.
- **SG:** GST 9% (2026).
- **ID:** VAT 11% + PE (Perceived Enterprise) considerations.
- **PH:** VAT 12% cho digital services.

Logic: tax calculator per region, included in Stripe checkout.

---

## 5. Blockchain SBT Architecture

### 5.1. Smart contract design

```solidity
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";

contract SkillSBT is ERC721 {
    address public minter; // SkillSeed backend
    mapping(uint256 => SkillMetadata) public metadata;
    mapping(address => bool) public revoked;
    
    struct SkillMetadata {
        string userId;          // SkillSeed UUID
        string skillSlug;
        uint256 sessionsCount;
        uint256 avgRating;      // × 10 (e.g., 47 = 4.7)
        uint256 issuedAt;
        string signature;       // Backend signature
        string ipfsHash;        // Metadata JSON trên IPFS
    }
    
    constructor() ERC721("SkillSeed Passport", "SKSP") {
        minter = msg.sender;
    }
    
    function mint(
        address to,
        uint256 tokenId,
        SkillMetadata calldata meta,
        string calldata uri
    ) external {
        require(msg.sender == minter, "Only minter");
        require(!revoked[to], "User revoked");
        _safeMint(to, tokenId);
        metadata[tokenId] = meta;
        _setTokenURI(tokenId, uri);
    }
    
    function revoke(uint256 tokenId) external {
        require(msg.sender == minter, "Only minter");
        address owner = ownerOf(tokenId);
        revoked[owner] = true;
        _burn(tokenId);
    }
    
    // Soulbound: không cho transfer
    function _beforeTokenTransfer(
        address from, address to, uint256 tokenId, uint256 batchSize
    ) internal pure override {
        require(from == address(0) || to == address(0), "Soulbound: cannot transfer");
    }
    
    // Verification
    function verify(uint256 tokenId) external view returns (bool) {
        return _ownerOf(tokenId) != address(0) && !revoked[ownerOf(tokenId)];
    }
}
```

### 5.2. Backend integration

```java
@Service
public class SbtService {
    @Autowired private Web3j web3j;
    @Autowired private ContractService contractService;
    
    public TransactionReceipt mintSkillPassport(User user, Skill skill, SkillPassport passport) {
        // 1. Generate metadata JSON
        Metadata metadata = new Metadata(user, skill, passport);
        String json = objectMapper.writeValueAsString(metadata);
        
        // 2. Upload to IPFS
        String ipfsHash = ipfsService.upload(json);
        
        // 3. Sign với backend private key
        String signature = signService.sign(metadata);
        
        // 4. Mint on Polygon
        Function mintFn = new Function(
            "mint",
            Arrays.asList(
                user.getWalletAddress(),  // hoặc platform-owned nếu user chưa có wallet
                BigInteger.valueOf(passport.getTokenId()),
                buildMetadataStruct(metadata, signature, ipfsHash),
                "ipfs://" + ipfsHash
            ),
            Collections.emptyList()
        );
        
        return web3j.ethSendTransaction(Transaction.createEthCallTransaction(
            minterAddress, contractAddress, FunctionEncoder.encode(mintFn)
        )).send();
    }
}
```

### 5.3. IPFS storage

- Pinata hoặc web3.storage (free tier).
- Metadata JSON chứa skill info + signature.

### 5.4. Wallet connection

```typescript
// MetaMask integration
import { ethers } from 'ethers';

async function connectWallet() {
    const provider = new ethers.BrowserProvider(window.ethereum);
    await provider.send("eth_requestAccounts", []);
    const signer = await provider.getSigner();
    const address = await signer.getAddress();
    
    // Send to backend
    await api.post('/users/me/wallet', { address, signature: await signMessage(signer) });
}
```

### 5.5. Verification endpoint

```
GET /api/v1/verify/{txHash}
→ {
    valid: true,
    user: { name: "Nguyen Van A", country: "VN" },
    skill: { name: "Public Speaking", category: "communication" },
    stats: { sessions: 23, rating: 4.7 },
    contract: "0x1234...",
    network: "polygon-mainnet",
    blockNumber: 51234567,
    issuedAt: "2026-09-15"
}
```

---

## 6. Public API & Developer Platform

### 6.1. API Gateway

```
Route: https://api.skillseed.com/v1/* (public)
Auth: OAuth2 client_credentials hoặc API key

Endpoints:
  GET  /v1/passports/{id}
  GET  /v1/users/{id}/profile (public fields only)
  GET  /v1/skills/search?q=...
  GET  /v1/skills/{slug}
  POST /v1/webhooks (register webhook URL)
  
  Webhook events:
    passport.issued
    passport.revoked
    passport.verified (khi bên thứ 3 verify)
```

### 6.2. OpenAPI spec

- Source of truth: `packages/api-spec/openapi.yaml`
- Generated SDKs: TypeScript, Python, Java, Go.
- Hosted docs: **docs.skillseed.com** (ReadTheDocs hoặc custom Docusaurus).

### 6.3. Developer portal

```
docs.skillseed.com
├── Getting Started
├── Authentication
├── API Reference (auto-generated)
├── SDKs (TS, Python, Java)
├── Webhooks
├── Sandbox
├── Partners showcase
└── Support / Forum
```

### 6.4. Sandbox environment

- Separate cluster `sandbox-api.skillseed.com`
- Sample data, no rate limits
- Free access cho mọi developer

---

## 7. AI Matching v2

### 7.1. Multilingual embeddings

**Model:** `text-embedding-3-large` (OpenAI, supports 100+ languages) hoặc **BGE-M3** (open-source, multilingual).

### 7.2. Region-specific fine-tuning

```
For each region:
  1. Collect (user, click, book, complete) tuples
  2. Fine-tune base embeddings với region data
  3. Re-train re-ranker với region-specific features
  4. A/B test vs base model
  5. Gradual rollout (10% → 50% → 100%)
```

### 7.3. Cross-language matching

- Embedding space unified → match VN user với ID user dễ dàng.
- Translation layer: query translated to user's preferred language, but vector stays same.

### 7.4. Bias detection

```python
def detect_bias(matches):
    metrics = {
        'gender_match_rate': matches.groupby('gender').mean('match_score'),
        'region_match_rate': matches.groupby('region').mean('match_score'),
        'language_match_rate': matches.groupby('language').mean('match_score'),
    }
    
    # Alert if disparity > 10%
    for metric, values in metrics.items():
        disparity = values.max() - values.min()
        if disparity > 0.1:
            alert(f"Bias detected: {metric} disparity = {disparity:.2%}")
```

### 7.5. AI explanations localized

LLM prompt per locale — truyền locale vào prompt:
```
"Bạn là trợ lý AI matching. Hãy giải thích ngắn gọn (2-3 câu, 
{output_language}) tại sao user A và user B là match tốt."
```

---

## 8. Data Residency & Compliance

### 8.1. Per-region Postgres

```yaml
# Terraform: RDS per region
module "rds_apac_main" {
  source     = "terraform-aws-modules/rds/aws"
  identifier = "skillseed-postgres-apac-main"
  engine     = "postgres"
  engine_version = "16.3"
  instance_class = "db.r6g.2xlarge"
  allocated_storage = 100
  multi_az = true
  
  # Encryption
  storage_encrypted = true
  kms_key_id = aws_kms_key.rds.arn
  
  # Backup
  backup_retention_period = 30
  
  region = "ap-southeast-1"
}
```

### 8.2. Cross-region replication

```
ap-southeast-1 (primary)
   ↓ async replication
ap-southeast-3 (read replica for ID)
ap-southeast-5 (read replica for PH)

RPO: 1 hour (acceptable for non-critical data)
RTO: 4 hours (DNS failover + promotion)
```

### 8.3. GDPR / UU PDP / DPA compliance

- **Right to delete:** Cross-region delete qua Kafka event.
- **Right to export:** Aggregate data từ region + global services.
- **Consent management:** Per-region consent UI.
- **DPO hire:** Phase 4.5 (full-time).

### 8.4. SOC 2 Type II

- Audit bởi Big 4 hoặc specialized firm (e.g., Vanta, Drata).
- Scope: Security, Availability, Confidentiality.
- Time: 6 tháng observation period → certificate.
- Cost: $50K–$100K.

---

## 9. Deployment — Phase 4

```
Multi-region production:

Region: ap-southeast-1 (primary)
  - EKS cluster prod-apac-1 (3 AZs)
  - RDS Postgres Multi-AZ + 2 read replicas
  - ElastiCache Redis cluster mode
  - S3 + CloudFront
  - MSK Kafka 3 brokers
  - OpenSearch (logs)

Region: ap-southeast-3 (Jakarta)
  - EKS cluster prod-id-1
  - RDS Postgres + cross-region replica
  - CloudFront

Region: ap-southeast-5 (Manila)
  - EKS cluster prod-ph-1
  - RDS Postgres + cross-region replica

Global services:
  - us-east-1: Public API Gateway, Blockchain RPC relay
  - Vercel: Marketing + Developer Portal (Edge network)
  - Polygon: Smart contract (already deployed)

Observability:
  - Datadog (multi-region)
  - Sentry (multi-region)
  - PagerDuty (global on-call)

Cost estimate (200K MAU):
  EKS (3 regions): $1,500/mo
  RDS (3 regions): $2,000/mo
  Redis (3 regions): $600/mo
  S3 + CloudFront: $500/mo
  OpenAI: $5,000/mo (matching + AI explanations)
  AssemblyAI: $3,000/mo
  Daily.co: $2,500/mo
  Polygon RPC: $500/mo
  Crowdin: $500/mo
  Stripe fees: variable (~3% of revenue)
  Infrastructure total: ~$16K/mo
  + Team: $50-80K/mo
  = $66-96K/mo total
```

---

## 10. Country GTM Strategy

### 10.1. Singapore

- **Positioning:** Premium, English-first, professional development.
- **Channels:** LinkedIn, Tech in Asia, e27, fintech meetups.
- **Partnerships:** NUS, NTU, Singapore Management University, Grab, Shopee.
- **Pricing:** USD/SGD parity (premium market).

### 10.2. Indonesia

- **Positioning:** Affordable, mobile-first, Bahasa-friendly.
- **Channels:** Instagram, TikTok, YouTube, Gojek ecosystem.
- **Partnerships:** Universitas Indonesia, ITB, Telkom, Bukalapak alumni.
- **Pricing:** PPP (low, like India).
- **Compliance:** UU PDP, PSE registration (if required).

### 10.3. Philippines

- **Positioning:** Career upskilling, BPO/tech, English-first.
- **Channels:** Facebook (dominant), TikTok, LinkedIn.
- **Partnerships:** UP, Ateneo, DLSU, Globe, BPO companies.
- **Pricing:** Mid-tier.
- **Compliance:** DPA Philippines.

---

## 11. Out of Scope (Phase 4 Design)

- ❌ Voice-first AI (Phase 5)
- ❌ AR/VR (Phase 5)
- ❌ Offline community events platform (Phase 5)
- ❌ EU/US expansion (Phase 5+)
- ❌ Crypto-native features khác ngoài SBT (NFT, tokens, etc.)
- ❌ Decentralized identity (DID) full implementation

---

## 12. Open Questions

| # | Câu hỏi | Owner | Deadline |
|---|----------|-------|----------|
| 1 | Multi-region: 3 EKS clusters hay 1 cluster multi-region? | DevOps | Sprint 22 |
| 2 | Blockchain: Polygon, Base, hay multi-chain? | Blockchain lead | Sprint 23 |
| 3 | Translation: in-house team hay agency (Crowdin pro)? | Localization | Sprint 22 |
| 4 | Local entity setup: SG/ID/PH nào trước? | Founder | Sprint 22 |
| 5 | AI v2: self-hosted (BGE-M3) hay OpenAI multilingual? | AI researcher | Sprint 25 |