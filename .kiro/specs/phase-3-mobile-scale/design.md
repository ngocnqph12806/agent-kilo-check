# Phase 3 — Mobile + Scale: Design

> **Tài liệu thiết kế kỹ thuật cho Mobile + Scale.**
> **Triết lý:** Tách monolith thành microservices có chọn lọc (theo bounded context), introduce Kubernetes cho production, giữ monolith cho MVP features.

---

## 1. High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│               PHASE 3 ARCHITECTURE (Microservices + Mobile)          │
└──────────────────────────────────────────────────────────────────────┘

   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
   │ iOS App      │   │ Android App  │   │ Web App      │
   │ (RN+Expo)    │   │ (RN+Expo)    │   │ (Next.js)    │
   │ App Store    │   │ Play Store   │   │ Vercel       │
   └──────┬───────┘   └──────┬───────┘   └──────┬───────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             │ HTTPS / WSS
                             ▼
        ┌────────────────────────────────────────────┐
        │   Cloudflare CDN + WAF + DDoS              │
        └─────────────────┬──────────────────────────┘
                          ▼
        ┌────────────────────────────────────────────┐
        │   API Gateway (Kong Enterprise / AWS APIGW) │
        │   - JWT auth + rate limit + CORS            │
        │   - Routing to microservices                │
        └─────┬──────┬──────┬──────┬──────┬──────┬───┘
              │      │      │      │      │      │
              ▼      ▼      ▼      ▼      ▼      ▼
   ┌──────────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐
   │ user-svc │ │match │ │sess- │ │book- │ │wallet│ │notif │
   │          │ │  -svc│ │svc   │ │svc   │ │-svc  │ │-svc  │
   └──────────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘
   ┌──────────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐
   │reputa-   │ │pod-  │ │bill- │ │report│ │admin │ │ ai   │
   │tion-svc  │ │svc   │ │svc   │ │-svc  │ │-svc  │ │co-pil│
   └──────────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘
              │      │      │      │      │      │
              └──────┴──────┴──────┴──────┴──────┘
                             │
                             ▼
        ┌────────────────────────────────────────────┐
        │   Event Bus (Apache Kafka on Kubernetes)   │
        └─────────────────┬──────────────────────────┘
                          │
                          ▼
        ┌────────────────────────────────────────────┐
        │   Data Layer                                  │
        │   - PostgreSQL (per service, 1 logical DB)  │
        │   - Redis (cache + session)                  │
        │   - Qdrant (vector)                          │
        │   - ClickHouse (analytics)                   │
        │   - S3 (recordings, passport PDFs)           │
        └─────────────────────────────────────────────┘

   External:
     Stripe | SendGrid | APNs/FCM | Onfido | OpenAI
     Twilio | AssemblyAI | Daily.co | Polygon (Phase 4)
```

---

## 2. Microservices — Service Catalog

### 2.1. Service list (Phase 3)

| Service | Responsibility | Tech | DB |
|---------|---------------|------|-----|
| **api-gateway** | Routing, JWT, rate-limit, CORS | Kong | — |
| **user-service** | Profile, auth, Skill DNA | Java/Spring | Postgres |
| **matching-service** | AI matching pipeline | Python/FastAPI | Qdrant + cache |
| **session-service** | Booking, video orchestration | Java/Spring | Postgres |
| **wallet-service** | Seed ledger | Java/Spring | Postgres |
| **notification-service** | Push, email, in-app | Java/Spring | Postgres |
| **reputation-service** | Ratings, Skill Passport | Java/Spring | Postgres |
| **pod-service** | Learning Pods | Java/Spring | Postgres |
| **billing-service** | Stripe, subscriptions | Java/Spring | Postgres |
| **ai-copilot-service** | Notes, summary | Java/Spring + Python LLM | Postgres |
| **report-service** | B2B analytics | Java/Spring | ClickHouse |
| **admin-service** | Admin actions | Java/Spring | Postgres |
| **moderation-service** | Content scan | Java/Spring | Postgres |

### 2.2. Communication

- **Synchronous:** REST (OpenFeign) hoặc gRPC (internal)
- **Asynchronous:** Kafka (event-driven cho cross-service workflows)
- **Saga pattern:** Cho multi-service transactions (e.g., booking → wallet → notification)

### 2.3. Service boundaries

| Service | Owns | Publishes events | Consumes events |
|---------|------|------------------|-----------------|
| user-service | users, skill_dna, verification | user.updated, user.deleted | — |
| matching-service | embeddings cache | match.scored | user.updated |
| session-service | bookings, sessions | booking.created, booking.confirmed, session.started, session.ended | user.updated, billing.activated |
| wallet-service | wallets, transactions | wallet.credited, wallet.debited | booking.confirmed, session.ended |
| notification-service | notifications, push_subs | — | ALL events |
| reputation-service | ratings, passports | passport.issued, rating.created | session.ended |
| pod-service | pods, pod_members, pod_posts | pod.member_joined, pod.session_started | — |
| billing-service | subscriptions, invoices | billing.activated, billing.canceled | user.updated |
| ai-copilot-service | session_notes, summaries | notes.generated | session.started, session.ended |
| report-service | reports | — | ALL events (analytics) |

---

## 3. Mobile App Architecture (React Native + Expo)

### 3.1. Project structure

```
apps/
  mobile/                  # React Native + Expo
    src/
      app/                 # Expo Router (file-based)
        (auth)/
          login.tsx
          register.tsx
        (tabs)/
          discover.tsx
          bookings.tsx
          wallet.tsx
          profile.tsx
        booking/[id].tsx
        passport/[id].tsx
      components/
      hooks/
      lib/
        api.ts            # React Query setup
        storage.ts        # MMKV + SecureStore
        push.ts           # Push notification setup
        biometric.ts      # Face ID / Touch ID
      stores/             # Zustand stores
```

### 3.2. Tech stack

| Layer | Tech |
|-------|------|
| Framework | React Native 0.74 + Expo SDK 51 |
| Navigation | Expo Router (file-based, type-safe) |
| State | Zustand + React Query |
| Forms | React Hook Form + Zod |
| Storage | MMKV (fast KV) + Expo SecureStore (secure) |
| Push | Expo Notifications + APNs/FCM |
| Biometric | Expo LocalAuthentication |
| Camera | Expo Camera (avatar, KYC) |
| Video | Daily.co React Native SDK |
| Charts | Victory Native |
| Build | EAS Build + EAS Submit |

### 3.3. Code sharing với web

- `packages/shared-types` — TypeScript types (API contracts)
- `packages/business-logic` — Pure functions (matching score, tier calc, etc.)
- `packages/ui-primitives` — Cross-platform UI components (Tamagui hoặc Restyle)

### 3.4. Deep linking

```
skillseed://booking/abc-123
skillseed://passport/xyz-789
skillseed://user/me
https://skillseed.app/b/{id}  ← universal links
```

iOS: Associated Domains file.
Android: intent-filter trong AndroidManifest.xml.

---

## 4. Microservices Migration Strategy

### 4.1. Strangler Fig pattern

Không big-bang rewrite. Migrate từng bounded context:

```
Phase 3.1 (tháng 9):
  - Tách notification-service (ít risk nhất)
  - Tách billing-service (Stripe cần dedicated service)

Phase 3.2 (tháng 10):
  - Tách matching-service (đã có Python, chỉ cần containerize)
  - Tách ai-copilot-service

Phase 3.3 (tháng 11):
  - Tách pod-service (bounded context mới)
  - Tách report-service (B2B)

Phase 3.4 (tháng 12):
  - Tách reputation-service (ratings + passports)
  - Giữ user-service + session-service + wallet-service trong monolith

Phase 3.5 (tháng 13):
  - Decompose session-service thành booking-service + session-orchestrator
  - Wallet-service giữ nguyên
```

### 4.2. Shared libraries (Java)

```
skillseed-libs/
├── skillseed-common        # Common utilities, exceptions, base entities
├── skillseed-auth          # JWT parsing, OAuth2 client
├── skillseed-events        # Kafka producer/consumer base
├── skillseed-observability # Metrics, tracing, logging
├── skillseed-api-spec      # OpenAPI specs cho mỗi service
└── skillseed-db-migrations # Flyway base migrations (per service)
```

Build bằng Maven multi-module, publish lên GitHub Packages.

---

## 5. Kubernetes Architecture

### 5.1. Cluster topology

```
Production EKS / GKE cluster (region: ap-southeast-1)
├── Namespaces:
│   ├── skillseed-prod
│   │   ├── api-gateway (3 pods)
│   │   ├── user-service (2 pods)
│   │   ├── matching-service (2 pods)
│   │   ├── session-service (3 pods)
│   │   ├── wallet-service (2 pods)
│   │   ├── notification-service (2 pods)
│   │   ├── reputation-service (2 pods)
│   │   ├── pod-service (2 pods)
│   │   ├── billing-service (2 pods)
│   │   ├── ai-copilot-service (2 pods)
│   │   ├── report-service (2 pods)
│   │   └── admin-service (1 pod)
│   ├── skillseed-staging (mirror prod, 1 pod each)
│   └── monitoring
│       ├── prometheus
│       ├── grafana
│       └── loki
└── Ingress: ALB / Nginx
```

### 5.2. Deployment strategy

- **Helm charts** cho mỗi service.
- **ArgoCD** cho GitOps deployment.
- **HPA** (Horizontal Pod Autoscaler) cho services có traffic biến động (session, matching).
- **PDB** (Pod Disruption Budget) để đảm bảo availability.

### 5.3. CI/CD

```yaml
# .github/workflows/backend-deploy.yml
on:
  push:
    branches: [main]
    paths: [services/**]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '21' }
      - run: mvn verify
      
  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: docker/build-push-action@v5
        with:
          context: services/${{ matrix.service }}
          push: true
          tags: ghcr.io/skillseed/${{ matrix.service }}:${{ github.sha }}
  
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - uses: argocd-action@v1
        with:
          version: latest
          server: ${{ secrets.ARGOCD_SERVER }}
          token: ${{ secrets.ARGOCD_TOKEN }}
          apps: skillseed-${{ matrix.service }}
          revision: ${{ github.sha }}
```

---

## 6. Database — Per-service databases

### 6.1. Logical DB split

```
Database per service pattern:

skillseed_users        → user-service
skillseed_matching     → matching-service (cache layer, fallback Qdrant)
skillseed_sessions     → session-service
skillseed_wallet       → wallet-service
skillseed_notifications → notification-service
skillseed_reputation   → reputation-service (ratings, passports)
skillseed_pods         → pod-service
skillseed_billing      → billing-service (subscriptions, invoices)
skillseed_copilot      → ai-copilot-service (notes, summaries)
skillseed_reports      → report-service (analytics snapshots)
skillseed_admin        → admin-service (audit logs, moderation queue)
```

Cross-service data access qua events (Kafka) hoặc dedicated API calls (read-only).

### 6.2. Shared schema (Flyway per service)

Mỗi service quản lý schema riêng. Ví dụ `pod-service`:
```
V1__init.sql    # learning_pods, pod_members, pod_posts
V2__add_visibility.sql
V3__add_premium.sql
```

### 6.3. Distributed transactions

Ví dụ flow "Complete session" (Phase 3 monolith-style → microservices):

```
Saga: Complete Session + Release Escrow + Issue Passport

Steps:
  [Session service]    Mark booking completed → emit session.completed
  [Wallet service]     Receive → credit teacher → emit wallet.credited
  [Reputation service] Receive → update rating avg → emit rating.updated
  [Notification svc]   Receive → send "Rate session" email → emit notif.sent

Compensation (if fail):
  Wallet revert transaction → emit wallet.reverted
  Session reset status → emit session.failed
```

Saga framework: **Spring Statemachine** hoặc **Apache Camel Saga EIP**.

---

## 7. B2B Architecture

### 7.1. Multi-tenancy

- **Schema per tenant** (Enterprise) hoặc **shared schema with row-level** (SME).
- Tenant context qua JWT claims (organization_id).
- Spring Data JPA interceptor tự động filter.

```java
@Component
public class TenantInterceptor implements HibernateFilterConfigurer {
    @Override
    public void configure(SessionFactory sessionFactory) {
        sessionFactory.addFilter("tenantFilter", 
            "organization_id = :currentOrg");
    }
}
```

### 7.2. SSO

- **OIDC** cho Google Workspace, Microsoft Entra.
- **SAML 2.0** cho Enterprise (Okta, Auth0, Azure AD).
- Library: `spring-security-saml2-service-provider`.

Flow:
```
User click "Login with Google" 
→ redirect to Google OIDC 
→ Google callback to /sso/callback 
→ exchange code for token 
→ extract email + groups 
→ check org membership 
→ create JWT with org_id claim 
→ redirect to dashboard
```

### 7.3. Audit log

```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    actor_id UUID NOT NULL,
    action VARCHAR(100) NOT NULL,  -- 'user.invited', 'role.changed'
    resource_type VARCHAR(50),
    resource_id UUID,
    ip_address INET,
    user_agent TEXT,
    payload JSONB,
    created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_audit_org_time ON audit_logs(organization_id, created_at DESC);
```

Append-only table, retention 2 năm (GDPR), searchable qua admin UI.

---

## 8. Learning Pods — Architecture

### 8.1. Data model

```sql
CREATE TABLE learning_pods (
    id UUID PRIMARY KEY,
    organization_id UUID, -- nullable, B2B internal pods
    name VARCHAR(255),
    topic VARCHAR(255),
    description TEXT,
    creator_id UUID NOT NULL,
    max_members SMALLINT DEFAULT 8,
    is_public BOOLEAN DEFAULT true,
    is_premium BOOLEAN DEFAULT false,
    monthly_price_cents INT,
    stripe_product_id VARCHAR(100),
    cover_image_url TEXT,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE pod_members (
    pod_id UUID,
    user_id UUID,
    role VARCHAR(20), -- member, moderator, creator
    joined_at TIMESTAMPTZ,
    last_active_at TIMESTAMPTZ,
    PRIMARY KEY (pod_id, user_id)
);

CREATE TABLE pod_posts (
    id UUID PRIMARY KEY,
    pod_id UUID,
    author_id UUID,
    parent_id UUID, -- for threaded replies
    content TEXT,
    reactions JSONB, -- {emoji: count}
    created_at TIMESTAMPTZ
);

CREATE TABLE pod_sessions (
    id UUID PRIMARY KEY,
    pod_id UUID,
    scheduled_at TIMESTAMPTZ,
    duration_minutes SMALLINT,
    meeting_url TEXT,
    status VARCHAR(20),
    recording_url TEXT
);
```

### 8.2. Multi-participant video

- Daily.co hỗ trợ max 200 participants. SkillSeed giới hạn 8 → vừa đủ.
- Sử dụng Daily room có expiry = session duration + 30min.

---

## 9. Skill Passport — Architecture

### 9.1. Issuance rule

Khi user nhận rating mới → check:

```java
@EventListener
public void onRatingCreated(RatingCreatedEvent event) {
    Long sessionsCount = ratingRepo.countByRateeIdAndSkillIdAndOverallGreaterThanEqual(
        event.getRateeId(), event.getSkillId(), 4);
    BigDecimal avgRating = ratingRepo.avgOverallByRateeAndSkill(
        event.getRateeId(), event.getSkillId());
    
    if (sessionsCount >= 5 && avgRating.compareTo(new BigDecimal("4.5")) >= 0) {
        // Issue passport if not exists
        Optional<SkillPassport> existing = passportRepo.findByUserAndSkill(
            event.getRateeId(), event.getSkillId());
        if (existing.isEmpty()) {
            SkillPassport passport = new SkillPassport();
            passport.setUserId(event.getRateeId());
            passport.setSkillId(event.getSkillId());
            passport.setSessionsCount(sessionsCount);
            passport.setAvgRating(avgRating);
            passportRepo.save(passport);
            
            // Generate PDF + upload to S3
            String pdfUrl = passportPdfService.generate(passport);
            passport.setPdfUrl(pdfUrl);
            
            // OG image
            String ogImageUrl = passportOgService.generate(passport);
            passport.setOgImageUrl(ogImageUrl);
            
            // Notify user
            notificationService.send(event.getRateeId(), "🎉 Bạn đã nhận Skill Passport!");
        }
    }
}
```

### 9.2. PDF generation

- Backend: dùng **OpenHTMLToPDF** hoặc **Flying Saucer** (Java).
- Template: HTML với placeholder {{user_name}}, {{skill_name}}, {{rating}}, ...
- Render → upload lên S3.

### 9.3. OG image generation

- Dùng **Puppeteer** (Node) hoặc **Open Graph Image generation service** (`@vercel/og`).
- Render React component → screenshot → upload S3.

---

## 10. Stripe Billing Architecture

### 10.1. Stripe products

```
Product: SkillSeed Premium
  - Price ID: price_premium_monthly_usd = $9.9/month
  - Price ID: price_premium_monthly_vnd = 249.000đ/month
  - Price ID: price_premium_monthly_sgd = $13.9 SGD/month
  
Product: SkillSeed Family
  - Price ID: price_family_monthly_usd = $14.9/month

Product: Seed Pack Starter  - $4.9 (one-time)
Product: Seed Pack Growth   - $19.9 (one-time)
Product: Seed Pack Pro      - $49.9 (one-time)

Product: Expert Premium Session (marketplace)
  - Variable price set by expert
```

### 10.2. Webhook events

```java
@PostMapping("/webhooks/stripe")
public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, 
                                                    @RequestHeader("Stripe-Signature") String sig) {
    Event event = Webhook.constructEvent(payload, sig, webhookSecret);
    
    switch (event.getType()) {
        case "checkout.session.completed":
            handleCheckoutCompleted(event);
            break;
        case "invoice.paid":
            handleInvoicePaid(event);
            break;
        case "invoice.payment_failed":
            handlePaymentFailed(event);
            break;
        case "customer.subscription.deleted":
            handleSubscriptionCanceled(event);
            break;
        case "account.updated": // for Stripe Connect
            handleConnectAccountUpdated(event);
            break;
    }
    
    return ResponseEntity.ok("ok");
}
```

### 10.3. Stripe Connect (Marketplace)

- Express accounts cho Verified Expert.
- Platform fee = 15% qua `application_fee_amount`.
- Payout tự động theo schedule.

---

## 11. AI Co-Pilot — Multi-modal extension

### 11.1. Code editor session

- Sử dụng **Monaco Editor** (VS Code's editor) embedded.
- **Y.js** cho CRDT-based collaborative editing.
- WebSocket relay service (separate from chat) để sync edits.

```typescript
import * as Y from 'yjs';
import { MonacoBinding } from 'y-monaco';

const ydoc = new Y.Doc();
const ytext = ydoc.getText('monaco');
const provider = new WebsocketProvider('wss://collab.skillseed.app', roomId, ydoc);
const binding = new MonacoBinding(ytext, editor.getModel(), new Set([editor]), provider.awareness);
```

### 11.2. Interactive quiz

- Teacher create quiz trong session (multiple choice, free text).
- Student submit → instant feedback.
- Results saved trong session_notes.

---

## 12. AI Personality Insights (Premium)

### 12.1. Data sources

- Lịch sử session (topics, duration, rating received).
- Onboarding data (skills, goals, style).
- Activity patterns (active hours, frequency).

### 12.2. Pipeline

```
Monthly job (cron 1st of month):
  for each Premium user:
    - Aggregate 90-day data
    - Compute features:
      * Topics engaged most
      * Session duration distribution
      * Peak learning hours
      * Skill progression rate
    - LLM call:
        prompt = f"Phân tích learning behavior: {features}. 
                 Tạo personality profile + 6-month roadmap."
    - Save insights to DB
    - Send email: "Your monthly insights are ready"
```

### 12.3. Cohort comparison

- Ẩn danh: aggregate stats by persona cohort, so sánh user với percentile.
- "Bạn học nhanh hơn 70% users trong nhóm 'Sinh viên tech'".

---

## 13. Deployment — Phase 3

```
┌──────────────────────────────────────────────────────────────┐
│                    PHASE 3 INFRASTRUCTURE                     │
└──────────────────────────────────────────────────────────────┘

Cloud: AWS (ap-southeast-1) — Singapore cho VN + SEA

Compute:
  - EKS cluster (3 node groups: prod, staging, monitoring)
  - Karpenter autoscaling
  - Fargate fallback cho burst

Networking:
  - VPC với 3 subnets (public, private, database)
  - ALB ingress controller
  - Cloudflare CDN + WAF

Data:
  - RDS Postgres (Multi-AZ, 2 read replicas)
  - ElastiCache Redis (cluster mode)
  - Qdrant Cloud (managed)
  - ClickHouse Cloud (analytics)
  - S3 (recordings, PDFs, OG images)
  - EFS (shared configs)

Kafka:
  - MSK (Managed Streaming for Apache Kafka)
  - 3 brokers, replication factor 3

Observability:
  - Prometheus + Grafana
  - Loki + Tempo (logs + traces)
  - Datadog APM (alternative: New Relic)
  - Sentry (errors)
  - PagerDuty (on-call)

CI/CD:
  - GitHub Actions (build + test)
  - ArgoCD (GitOps deployment)
  - ECR (container registry)

Secrets:
  - AWS Secrets Manager
  - External Secrets Operator

Cost (estimate 20K MAU):
  EKS: $150/mo
  EC2/Fargate: $500/mo
  RDS: $400/mo
  ElastiCache: $150/mo
  S3: $50/mo
  CloudFront: $50/mo
  OpenAI: $1000/mo
  AssemblyAI: $800/mo
  Daily.co: $500/mo
  Stripe fees: variable
  Monitoring: $200/mo
  Misc: $300/mo
  ---
  Total: ~$4,100/mo (infrastructure only)
  + Salaries: $15K-30K/mo
  = $20K-35K/mo total
```

---

## 14. Out of Scope (Design — Phase 4+)

- ❌ Blockchain / Polygon SBT (Phase 4)
- ❌ Public API & Webhooks (Phase 4)
- ❌ Regional expansion (3 quốc gia mới) (Phase 4)
- ❌ Multi-language i18n 5 ngôn ngữ (Phase 4)
- ❌ AI matching v2 deep personalization (Phase 4)
- ❌ AR/VR (Phase 5)

---

## 15. Open Questions

| # | Câu hỏi | Owner | Deadline |
|---|----------|-------|----------|
| 1 | React Native hay Flutter cho cross-platform? | Mobile lead | Sprint 10 |
| 2 | Kong Gateway OSS hay Enterprise (có $$$)? | DevOps | Sprint 11 |
| 3 | Multi-tenant: schema-per-tenant hay shared với row-level filter? | Backend | Sprint 12 |
| 4 | Self-host Kafka (MSK) hay dùng Confluent Cloud? | DevOps | Sprint 13 |
| 5 | B2B reporting: real-time (ClickHouse) hay daily snapshots? | Backend | Sprint 14 |