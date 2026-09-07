# Phase 3 — Mobile + Scale: Tasks

> **Task list triển khai Mobile + Scale theo sprint (2 tuần/sprint).**
> **12 sprints × 2 tuần = 24 tuần (~ 6 tháng)**

---

## Sprint 10 (Tuần 26–27) — Microservices Foundation

### Infrastructure bootstrap

- [ ] [T-S01] **[P0]** Setup Kubernetes cluster (EKS hoặc GKE)
  - Region: ap-southeast-1 (Singapore)
  - 3 node groups: prod, staging, monitoring
  - Setup kubectl, helm, argocd
- [ ] [T-S02] **[P0]** Setup ArgoCD cho GitOps deployment
- [ ] [T-S03] **[P0]** Setup GitHub Container Registry (ECR hoặc GHCR)
- [ ] [T-S04] **[P0]** Setup Kong API Gateway (OSS hoặc Cloud)
  - Plugins: JWT, rate-limit, CORS, prometheus
- [ ] [T-S05] **[P0]** Setup Kafka cluster (MSK hoặc Redpanda)
- [ ] [T-S06] **[P0]** Setup shared Java libraries (`skillseed-libs`)
  - skillseed-common, skillseed-auth, skillseed-events, skillseed-observability
- [ ] [T-S07] **[P0]** Migrate monolith deployment từ Railway sang K8s
  - Helm chart cho monolith
  - Zero-downtime migration với traffic shifting

### First extraction: notification-service

- [ ] [T-S10] **[P0]** Extract notification-service repo
- [ ] [T-S11] **[P0]** Implement notifications table + Kafka consumer
- [ ] [T-S12] **[P0]** Implement email send (Resend), push (FCM/APNs), in-app
- [ ] [T-S13] **[P0]** Helm chart + ArgoCD app
- [ ] [T-S14] **[P0]** Migrate notification logic từ monolith → service
- [ ] [T-S15] **[P0]** Test: gửi booking confirmation qua cả 3 channels

---

## Sprint 11 (Tuần 28–29) — Billing Service + Stripe

### Stripe integration

- [ ] [T-S20] **[P0]** Setup Stripe account + verify business
- [ ] [T-S21] **[P0]** Create products + prices trong Stripe dashboard
  - Premium USD/VND/SGD, Family, Seed Packs
- [ ] [T-S22] **[P0]** Extract billing-service repo (Spring Boot)
- [ ] [T-S23] **[P0]** Implement Stripe Customer creation khi first checkout
- [ ] [T-S24] **[P0]** Implement checkout session API
- [ ] [T-S25] **[P0]** Implement webhook handler `/webhooks/stripe`
  - Events: checkout.session.completed, invoice.paid, customer.subscription.deleted, invoice.payment_failed
- [ ] [T-S26] **[P0]** Implement subscription state trong DB
- [ ] [T-S27] **[P0]** Frontend: `/billing` page
  - Show current plan + upgrade/downgrade
  - Stripe Customer Portal link

### Premium gating

- [ ] [T-S30] **[P0]** Backend: JWT claim `subscription_tier`
- [ ] [T-S31] **[P0]** Backend: `@PreAuthorize("hasTier('PREMIUM')")` cho endpoints Premium
- [ ] [T-S32] **[P0]** Frontend: Premium badge UI
- [ ] [T-S33] **[P0]** Free tier limit: 10 bookings/month với non-expert mentor
- [ ] [T-S34] **[P0]** Stripe Connect Express cho Verified Expert
  - Payout setup, KYC via Stripe

### Testing

- [ ] [T-S40] **[P0]** Test checkout flow (sandbox Stripe)
- [ ] [T-S41] **[P0]** Test webhook handling
- [ ] [T-S42] **[P0]** Test subscription cancellation

---

## Sprint 12 (Tuần 30–31) — Mobile App Foundation

### Mobile setup

- [ ] [T-S50] **[P0]** Setup React Native + Expo project
- [ ] [T-S51] **[P0]** Setup EAS Build + EAS Submit
- [ ] [T-S52] **[P0]** Setup code sharing với web
  - packages/shared-types
  - packages/business-logic
- [ ] [T-S53] **[P0]** Implement API client (React Query + axios)
- [ ] [T-S54] **[P0]** Implement auth flow (login, register, refresh)
- [ ] [T-S55] **[P0]** Implement biometric login (Face ID / Fingerprint)
- [ ] [T-S56] **[P0]** Implement secure storage (SecureStore + MMKV)
- [ ] [T-S57] **[P0]** Setup deep linking (skillseed://)

### Mobile UI

- [ ] [T-S60] **[P0]** Setup navigation (Expo Router)
- [ ] [T-S61] **[P0]** Implement Discover screen (mobile-optimized)
- [ ] [T-S62] **[P0]** Implement Bookings screen (list + detail)
- [ ] [T-S63] **[P0]** Implement Wallet screen
- [ ] [T-S64] **[P0]** Implement Profile screen
- [ ] [T-S65] **[P0]** Implement Onboarding (7 steps)

### Push notification mobile

- [ ] [T-S70] **[P0]** Setup Expo Notifications
- [ ] [T-S71] **[P0]** APNs configuration (Apple Developer)
- [ ] [T-S72] **[P0]** FCM configuration (Firebase project)
- [ ] [T-S73] **[P0]** Test push end-to-end (iOS + Android)

### Testing & store prep

- [ ] [T-S80] **[P0]** TestFlight setup + internal testing
- [ ] [T-S81] **[P0]** Google Play internal testing track
- [ ] [T-S82] **[P0]** App Store metadata (description, screenshots, keywords)
- [ ] [T-S83] **[P0]** Privacy nutrition labels (iOS) + Data safety form (Android)

---

## Sprint 13 (Tuần 32–33) — Mobile App Complete + TestFlight

### Mobile video + camera

- [ ] [T-S90] **[P0]** Integrate Daily.co React Native SDK
- [ ] [T-S91] **[P0]** Implement in-app camera (avatar, KYC)
- [ ] [T-S92] **[P0]** Implement video call screen
- [ ] [T-S93] **[P0]** Implement screen share (iOS/Android)
- [ ] [T-S94] **[P0]** Test video call on real devices

### Mobile polish

- [ ] [T-S100] **[P0]** Loading states + skeletons
- [ ] [T-S101] **[P0]** Error states + retry
- [ ] [T-S102] **[P0]** Offline mode (cached bookings, profile)
- [ ] [T-S103] **[P0]** App icon + splash screen
- [ ] [T-S104] **[P0]** Dark mode support
- [ ] [T-S105] **[P1]** Home screen widget (Android) / Live Activity (iOS)

### Beta launch

- [ ] [T-S110] **[P0]** Invite 100 beta testers (TestFlight + Play Console)
- [ ] [T-S111] **[P0]** In-app feedback widget
- [ ] [T-S112] **[P0]** Crashlytics setup
- [ ] [T-S113] **[P0]** Daily bug triage trong 2 tuần beta
- [ ] [T-S114] **[P0]** Submit to App Store + Play Store (review ~ 1 week)

---

## Sprint 14 (Tuần 34–35) — B2B Workspace Foundation

### Organization data model

- [ ] [T-S120] **[P0]** Add organization tables (DB migration)
- [ ] [T-S121] **[P0]** Add `organization_id` to users (nullable, cho B2B)
- [ ] [T-S122] **[P0]** Add role enum (USER, ORG_ADMIN, ORG_OWNER, SUPER_ADMIN)

### SSO

- [ ] [T-S130] **[P0]** Implement Google Workspace OIDC
- [ ] [T-S131] **[P0]** Implement Microsoft Entra OIDC
- [ ] [T-S132] **[P1]** Implement SAML 2.0 (spring-security-saml2)
- [ ] [T-S133] **[P0]** Test SSO flow end-to-end

### Org admin dashboard

- [ ] [T-S140] **[P0]** Org admin pages: members, skill pool, reports
- [ ] [T-S141] **[P0]** Member management (invite, remove, role change)
- [ ] [T-S142] **[P0]** Internal skill matching (only within org)
- [ ] [T-S143] **[P0]** Skill gap heatmap visualization
- [ ] [T-S144] **[P1]** CSV export for reports

### Audit log

- [ ] [T-S150] **[P0]** Implement audit_logs table + insert logic
- [ ] [T-S151] **[P0]** Admin UI: search + filter audit logs
- [ ] [T-S152] **[P0]** Retention policy (2 years)

### Testing

- [ ] [T-S160] **[P0]** E2E SSO test với Google Workspace mock
- [ ] [T-S161] **[P0]** B2B permission tests (cross-org access denied)

---

## Sprint 15 (Tuần 36–37) — Learning Pods + Skill Passport

### Pods service

- [ ] [T-S170] **[P0]** Extract pod-service repo (hoặc thêm vào monolith tạm)
- [ ] [T-S171] **[P0]** Implement pod CRUD APIs
- [ ] [T-S172] **[P0]** Implement pod posts (forum)
- [ ] [T-S173] **[P0]** Implement pod members + roles
- [ ] [T-S174] **[P0]** Multi-participant video (Daily.co room, max 8)
- [ ] [T-S175] **[P0]** Pod discover page `/pods`
- [ ] [T-S176] **[P0]** AI suggestions endpoint (gợi ý pod trending)

### Skill Passport

- [ ] [T-S180] **[P0]** DB: skill_passports table (đã có schema từ Phase 1, implement)
- [ ] [T-S181] **[P0]** Auto-issue logic (event listener trên rating.created)
- [ ] [T-S182] **[P0]** Public page `/passport/{id}` (SSR Next.js, đẹp)
- [ ] [T-S183] **[P0]** Open Graph image generation (Puppeteer service)
- [ ] [T-S184] **[P0]** PDF generation (OpenHTMLToPDF)
- [ ] [T-S185] **[P0]** S3 upload + CDN URL
- [ ] [T-S186] **[P0]** Passport badge trên profile + Discover card
- [ ] [T-S187] **[P0]** LinkedIn share button (manual copy)

### Testing

- [ ] [T-S190] **[P0]** Pod multi-participant video test (3–5 người thật)
- [ ] [T-S191] **[P0]** Passport auto-issue flow

---

## Sprint 16 (Tuần 38–39) — Multi-modal Sessions

### Code editor live

- [ ] [T-S200] **[P0]** Setup Monaco Editor integration
- [ ] [T-S201] **[P0]** Y.js CRDT setup cho collaborative editing
- [ ] [T-S202] **[P0]** WebSocket relay service (collab.skillseed.app)
- [ ] [T-S203] **[P0]** Shared cursor + awareness
- [ ] [T-S204] **[P0]** Test với 3 người cùng edit

### Interactive quiz

- [ ] [T-S210] **[P0]** Quiz builder UI (teacher tạo trong session)
- [ ] [T-S211] **[P0]** Quiz runtime (student trả lời real-time)
- [ ] [T-S212] **[P0]** Save quiz results to session_notes
- [ ] [T-S213] **[P1]** Quiz templates library (e.g., "Java OOP basics")

### AR overlay (basic)

- [ ] [T-S220] **[SHOULD]** WebXR setup
- [ ] [T-S221] **[SHOULD]** AR overlay cho mobile (camera + 3D models)
- [ ] [T-S222] **[SHOULD]** Test với yoga/nấu ăn sessions

### Testing

- [ ] [T-S230] **[P0]** Load test code editor (5 concurrent editors)
- [ ] [T-S231] **[P0]** Quiz sync accuracy

---

## Sprint 17 (Tuần 40–41) — AI Personality Insights (Premium)

### Data pipeline

- [ ] [T-S240] **[P0]** Scheduled job: aggregate 90-day data cho Premium users
- [ ] [T-S241] **[P0]** Feature extraction (topics, duration, peak hours, progression)
- [ ] [T-S242] **[P0]** Cohort definition + aggregation (ClickHouse)

### LLM analysis

- [ ] [T-S250] **[P0]** LLM prompt engineering cho personality analysis
- [ ] [T-S251] **[P0]** Generate 6-month roadmap (LLM)
- [ ] [T-S252] **[P0]** Save insights to DB
- [ ] [T-S253] **[P0]** Send monthly email: "Your insights are ready"

### UI

- [ ] [T-S260] **[P0]** Premium-only page `/insights`
- [ ] [T-S261] **[P0]** Personality profile visualization
- [ ] [T-S262] **[P0]** 6-month roadmap interactive timeline
- [ ] [T-S263] **[P1]** Cohort comparison chart

### Testing

- [ ] [T-S270] **[P0]** Test scheduled job với 100 fake Premium users
- [ ] [T-S271] **[P0]** LLM output quality review

---

## Sprint 18 (Tuần 42–43) — Marketplace

### Verified Expert

- [ ] [T-S280] **[P0]** Verification level 4 (expert review)
- [ ] [T-S281] **[P0]** Expert profile (set hourly rate USD)
- [ ] [T-S282] **[P0]** Stripe Connect Express onboarding flow
- [ ] [T-S283] **[P0]** Discovery filter: "Verified Expert only"

### Marketplace booking

- [ ] [T-S290] **[P0]** Booking flow với USD payment
- [ ] [T-S291] **[P0]** Platform fee logic (15%)
- [ ] [T-S292] **[P0]** Payout schedule + reporting cho expert
- [ ] [T-S293] **[P0]** Tax form (1099 cho US experts, qua Stripe)

### Seed Packs

- [ ] [T-S300] **[P0]** Stripe one-time purchase cho Seed Packs
- [ ] [T-S301] **[P0]** Credit seeds to wallet sau purchase
- [ ] [T-S302] **[P0]** Display trên `/wallet` page

### NGO / Corporate gifting

- [ ] [T-S310] **[SHOULD]** Admin tool: bulk credit grant
- [ ] [T-S311] **[SHOULD]** Corporate bulk purchase (invoiced)

### Multi-currency

- [ ] [T-S320] **[P0]** Display prices theo locale (IP-based + user preference)
- [ ] [T-S321] **[P0]** Stripe multi-currency prices (USD, VND, SGD, IDR, INR)
- [ ] [T-S322] **[P0]** Backend lưu canonical USD
- [ ] [T-S323] **[P0]** Exchange rate cache (daily refresh)

---

## Sprint 19 (Tuần 44–45) — B2B Polish + First Clients

### B2B reporting

- [ ] [T-S330] **[P0]** ClickHouse schema cho reports
- [ ] [T-S331] **[P0]** Report-service API: MAU, learning hours, skills gap
- [ ] [T-S332] **[P0]** Visualization: dashboard charts
- [ ] [T-S333] **[P0]** Export CSV/PDF

### Custom branding

- [ ] [T-S340] **[SHOULD]** Org logo upload + display
- [ ] [T-S341] **[SHOULD]** Primary color customization (CSS variables)

### B2B go-to-market

- [ ] [T-S350] **[P0]** Outreach tới 50 công ty SME VN (LinkedIn)
- [ ] [T-S351] **[P0]** Pilot program: 3 tháng free cho 5 công ty đầu
- [ ] [T-S352] **[P0]** Case study viết cho mỗi pilot
- [ ] [T-S353] **[P0]** ROI calculator landing page

---

## Sprint 20 (Tuần 46–47) — Scale Preparation

### Performance optimization

- [ ] [T-S360] **[P0]** DB query optimization (slow query review)
- [ ] [T-S361] **[P0]** Redis cache tuning (hit rate > 80%)
- [ ] [T-S362] **[P0]** Frontend bundle optimization (initial < 250KB)
- [ ] [T-S363] **[P0]** Image optimization (next/image, lazy loading)
- [ ] [T-S364] **[P0]** CDN configuration (Cloudflare)

### Observability

- [ ] [T-S370] **[P0]** Prometheus metrics cho mỗi service
- [ ] [T-S371] **[P0]** Grafana dashboards (business + technical)
- [ ] [T-S372] **[P0]** Distributed tracing (Tempo)
- [ ] [T-S373] **[P0]** Sentry full setup (errors + performance)
- [ ] [T-S374] **[P0]** On-call rotation (PagerDuty)

### Reliability

- [ ] [T-S380] **[P0]** Chaos engineering tests (kill a pod, verify failover)
- [ ] [T-S381] **[P0]** Backup + DR plan (RPO 1h, RTO 4h)
- [ ] [T-S382] **[P0]** Load test 10K concurrent users (k6)
- [ ] [T-S383] **[P0]** Auto-scaling verified (HPA, Karpenter)

---

## Sprint 21 (Tuần 48–49) — Phase 3 Launch

### Public launch

- [ ] [T-S390] **[P0]** Mobile app live trên App Store + Play Store
- [ ] [T-S391] **[P0]** Public Premium launch announcement
- [ ] [T-S392] **[P0]** B2B outreach + first 5 deals signed
- [ ] [T-S393] **[P0]** Press release + media kit

### Marketing

- [ ] [T-S400] **[P0]** Paid ads (Google + Facebook) $5K
- [ ] [T-S401] **[P0]** Influencer campaign (10 micro-influencers)
- [ ] [T-S402] **[P0]** Partnership với 5 trường ĐH
- [ ] [T-S403] **[P0]** Community events (workshop offline)

### Monitoring

- [ ] [T-S410] **[P0]** Daily metrics review
- [ ] [T-S411] **[P0]** Customer Success team onboarding
- [ ] [T-S412] **[P0]** Bug triage daily trong 2 tuần đầu

---

## Cross-cutting tasks (song song 24 tuần)

- [ ] [T-S500] **[P0]** Daily standup + weekly sprint review
- [ ] [T-S501] **[P0]** Code review + ADRs cho architectural changes
- [ ] [T-S502] **[P0]** Track North Star: Weekly Active Sessions Completed
- [ ] [T-S503] **[P0]** Monthly all-hands với metrics review
- [ ] [T-S504] **[P1]** Quarterly OKR setting
- [ ] [T-S505] **[P1]** Customer interview (5 B2B + 10 B2C/tháng)
- [ ] [T-S506] **[P1]** Investor update (monthly)

---

## Definition of Done — Phase 3

### Functional

- [ ] 100% FR-S MUST đã implement
- [ ] iOS + Android apps live
- [ ] Stripe subscriptions active (Premium + Family)
- [ ] ≥ 5 B2B clients signed
- [ ] Pods feature working
- [ ] Skill Passport issuance working

### Non-Functional

- [ ] Mobile app cold start < 2s
- [ ] Backend p95 < 300ms
- [ ] Uptime ≥ 99.9%
- [ ] LCP < 2.5s mobile
- [ ] SOC 2 Type II readiness audit passed

### Business

- [ ] MAU ≥ 20.000
- [ ] Premium subs ≥ 1.000
- [ ] B2B clients ≥ 5
- [ ] Stripe MRR ≥ $10K
- [ ] Sessions/month ≥ 10.000
- [ ] D30 retention ≥ 25%
- [ ] NPS ≥ 50

### Operational

- [ ] Microservices migrated & stable
- [ ] K8s + ArgoCD production
- [ ] Full observability (metrics, logs, traces)
- [ ] On-call rotation established
- [ ] B2B Sales & CS teams hired

---

## Effort Estimation

| Sprint | Focus | Effort |
|--------|-------|--------|
| 10 | Microservices foundation | 200h |
| 11 | Billing + Stripe | 160h |
| 12 | Mobile foundation | 180h |
| 13 | Mobile polish + beta | 140h |
| 14 | B2B foundation | 160h |
| 15 | Pods + Passport | 140h |
| 16 | Multi-modal sessions | 140h |
| 17 | AI Personality | 100h |
| 18 | Marketplace | 140h |
| 19 | B2B GTM | 100h |
| 20 | Scale prep | 120h |
| 21 | Launch | 80h |
| **Tổng** | | **~1,660h** (~ 24 tuần × 70h/tuần × 1 founder, hoặc 8–10 người × 20h/tuần) |

---

## Risk Register

| Risk | Trigger | Mitigation |
|------|---------|------------|
| Mobile app bị App Store reject | Policy violation | Pre-review với Apple, fix trước submit |
| Microservices migration gây downtime | Bug trong extraction | Canary deploy, feature flag rollback |
| Stripe integration delay | Account verification | Apply sớm tháng 8 |
| B2B sales chậm | Không reach được HR Director | Outsource sales agency |
| Kubernetes complexity | Frequent outages | Hire DevOps senior, dùng managed services |
| Cost overrun | > $35K/mo | Downgrade infra, defer features |
| Founder burnout | > 60h/tuần liên tục | Delegate rõ ràng, founder focus strategy |