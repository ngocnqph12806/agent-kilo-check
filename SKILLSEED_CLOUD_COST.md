# SkillSeed — Cloud Cost Estimation & Financial Model

> **Mục đích:** Ước lượng chi phí hạ tầng cloud chính xác theo từng giai đoạn tăng trưởng. Phục vụ fundraising pitch, budgeting, và quyết định kỹ thuật (khi nào scale, khi nào optimize).

> **Last updated:** 2026-09-06  
> **Pricing basis:** AWS Singapore region (ap-southeast-1), giá USD 2026. Có thể ±15% tuỳ thời điểm.

---

## Mục lục

1. [Phương pháp ước lượng](#1-phương-pháp-ước-lượng)
2. [Giả định & Input chính](#2-giả-định--input-chính)
3. [AWS Pricing Reference (Singapore)](#3-aws-pricing-reference-singapore)
4. [Giai đoạn 0: MVP / Pre-launch (0–500 MAU)](#4-giai-đoạn-0-mvp--pre-launch-0500-mau)
5. [Giai đoạn 1: Early Traction (500–5.000 MAU)](#5-giai-đoạn-1-early-traction-5005000-mau)
6. [Giai đoạn 2: Product-Market Fit (5.000–50.000 MAU)](#6-giai-đoạn-2-product-market-fit-500050000-mau)
7. [Giai đoạn 3: Scale (50.000–500.000 MAU)](#7-giai-đoạn-3-scale-50000500000-mau)
8. [Chi phí AI/LLM API (tách riêng)](#8-chi-phí-aillm-api-tách-riêng)
9. [Chi phí SaaS bên thứ ba](#9-chi-phí-saas-bên-thứ-ba)
10. [Tổng chi phí vận hành (Burn Rate)](#10-tổng-chi-phí-vận-hành-burn-rate)
11. [Chiến lược tối ưu chi phí](#11-chiến-lược-tối-ưu-chi-phí)
12. [Reserved Instances & Savings Plans](#12-reserved-instances--savings-plans)
13. [Cost Monitoring & Alerts](#13-cost-monitoring--alerts)
14. [Hidden Costs Checklist](#14-hidden-costs-checklist)
15. [So sánh AWS vs GCP vs DigitalOcean](#15-so-sánh-aws-vs-gcp-vs-digitalocean)
16. [Dự phóng tài chính 3 năm](#16-dự-phóng-tài-chính-3-năm)
17. [Unit Economics & COGS](#17-unit-economics--cogs)
18. [Kịch bản tăng trưởng & Sensitivity](#18-kịch-bản-tăng-trưởng--sensitivity)

---

## 1. Phương pháp ước lượng

### 1.1. Cách tính

```
Monthly Cost = Σ (Resource × Usage × Unit Price)

Trong đó:
- Resource: loại tài nguyên (EC2, RDS, Redis...)
- Usage: lượng sử dụng (giờ compute, GB storage, request count...)
- Unit Price: giá từng đơn vị (từ AWS pricing)
```

### 1.2. Nguyên tắc

- **Tính theo usage thực tế**, không phải max capacity (để tránh over-provision)
- **Tách 3 loại chi phí:**
  - **Compute**: EC2/EKS, Lambda
  - **Storage**: RDS, S3, EBS
  - **Data transfer**: Egress (chi phí lớn bất ngờ!)
  - **API calls**: AI/LLM, third-party APIs
- **Dùng giá on-demand** làm baseline, tính thêm savings từ Reserved Instances
- **Cộng thêm buffer 20%** cho hidden costs & spike traffic

### 1.3. Công cụ ước lượng

- AWS Pricing Calculator: https://calculator.aws
- Vantage.sh (third-party cost analytics)
- CloudHealth / CloudZero (enterprise)

---

## 2. Giả định & Input chính

### 2.1. User metrics theo từng giai đoạn

| Metric | Stage 0 | Stage 1 | Stage 2 | Stage 3 |
|--------|:-------:|:-------:|:-------:|:-------:|
| **MAU (Monthly Active Users)** | 500 | 5.000 | 50.000 | 500.000 |
| **DAU (Daily Active Users)** | 100 | 1.000 | 10.000 | 100.000 |
| **Sessions/day** | 20 | 200 | 2.500 | 25.000 |
| **Bookings/month** | 100 | 2.000 | 25.000 | 250.000 |
| **API requests/day** | 5K | 100K | 1M | 10M |
| **Storage (GB)** | 5 | 50 | 500 | 5.000 |
| **Egress (TB/month)** | 0.1 | 1 | 10 | 100 |
| **Premium users** | 5 | 100 | 2.000 | 30.000 |
| **B2B clients** | 0 | 3 | 15 | 80 |
| **Concurrent video sessions** | 2 | 20 | 150 | 800 |

### 2.2. Tech stack assumptions

- **Microservices**: 5 services (user, matching, booking, session, wallet)
- **Database per service**: PostgreSQL RDS cho mỗi service
- **Cache**: ElastiCache Redis (1 cluster)
- **Message Queue**: MSK (Managed Kafka) — chỉ từ Stage 2
- **Container**: ECS Fargate (đơn giản hơn EKS cho < 50 services)
- **WebRTC**: Self-hosted (mediasoup + coturn) hoặc managed (LiveKit Cloud, Twilio Video)
- **Vector DB**: Qdrant self-hosted trên EC2 (Stage 2+)
- **CDN**: CloudFront
- **Storage**: S3 cho media

### 2.3. Traffic patterns

- **Peak/off-peak ratio**: 4x (peak 19h-22h theo giờ VN)
- **Geographic distribution**: 70% VN, 20% Indonesia/Philippines, 10% khác
- **Video quality**: 80% SD (1 Mbps), 20% HD (2.5 Mbps)
- **Avg session duration**: 45 phút

---

## 3. AWS Pricing Reference (Singapore)

### 3.1. Compute (Fargate)

| Resource | Spec | On-demand | 1-yr Reserved | 3-yr Reserved |
|----------|------|-----------|---------------|---------------|
| Fargate vCPU | 1 vCPU | $0.04656/hour | $0.03492 (-25%) | $0.02328 (-50%) |
| Fargate Memory | 1 GB | $0.00511/hour | $0.00383 (-25%) | $0.00256 (-50%) |
| EC2 t3.medium | 2 vCPU/4GB | $0.0528/hour | $0.0336 (-36%) | $0.0264 (-50%) |
| EC2 t3.large | 2 vCPU/8GB | $0.1056/hour | $0.0672 (-36%) | $0.0528 (-50%) |
| EC2 c5.xlarge | 4 vCPU/8GB | $0.192/hour | $0.122 (-36%) | $0.096 (-50%) |

### 3.2. Database (RDS PostgreSQL)

| Instance | vCPU | RAM | Storage | On-demand | 1-yr RI |
|----------|------|-----|---------|-----------|---------|
| db.t4g.medium | 2 | 4 GB | 100 GB | $0.082/hr + $0.115/GB | $0.054/hr |
| db.t4g.large | 2 | 8 GB | 200 GB | $0.164/hr + $0.115/GB | $0.108/hr |
| db.m6g.large | 2 | 8 GB | 200 GB | $0.171/hr + $0.115/GB | $0.110/hr |
| db.m6g.xlarge | 4 | 16 GB | 500 GB | $0.342/hr + $0.115/GB | $0.221/hr |
| db.r6g.large | 2 | 16 GB | 200 GB | $0.240/hr + $0.115/GB | $0.158/hr |

**Backup storage:** $0.095/GB-month (beyond free tier)

### 3.3. Other Services

| Service | Unit | Price |
|---------|------|-------|
| ElastiCache Redis cache.t4g.medium | hour | $0.068 |
| ElastiCache Redis cache.m6g.large | hour | $0.182 |
| S3 Standard storage | GB-month | $0.023 |
| S3 GET requests | 1,000 | $0.0004 |
| CloudFront egress | GB | $0.085 (first 10TB) |
| ALB (Application Load Balancer) | hour | $0.0225 |
| ALB LCU | hour | $0.008 |
| NAT Gateway | hour | $0.045 |
| NAT Gateway data processing | GB | $0.045 |
| CloudWatch logs ingested | GB | $0.50 |
| CloudWatch metrics | metric-month | $0.30 (first 10K free) |
| Secrets Manager | secret/month | $0.40 |
| KMS key | key/month | $1.00 |
| Egress to internet (EC2) | GB | $0.09 (first 10TB) |

---

## 4. Giai đoạn 0: MVP / Pre-launch (0–500 MAU)

**Mục tiêu:** Validate idea, có < 100 sessions/day, single region.

### 4.1. Kiến trúc

```
[Route53] → [CloudFront] → [ALB] → [ECS Fargate: 1 task user-service (0.5 vCPU, 1GB)]
                                  → [ECS Fargate: 1 task booking-service]
                                  → [ECS Fargate: 1 task wallet-service]
                                  → [ECS Fargate: 1 task matching-service (Python)]
                                  → [ECS Fargate: 1 task session-service (WebRTC SFU)]
                                  
[RDS PostgreSQL db.t4g.medium - shared cho 3 services]
[ElastiCache Redis cache.t4g.small]
[S3: avatars, portfolio files]
[coturn on EC2 t3.small (TURN server)]
```

### 4.2. Compute cost

| Resource | Spec | Qty | Hours/mo | Unit Price | Monthly |
|----------|------|-----|----------|------------|---------|
| Fargate user-service | 0.5 vCPU, 1GB | 1 | 730 | $0.0233 + $0.00511 | **$20.71** |
| Fargate booking-service | 0.5 vCPU, 1GB | 1 | 730 | $0.0233 + $0.00511 | **$20.71** |
| Fargate wallet-service | 0.25 vCPU, 0.5GB | 1 | 730 | $0.0116 + $0.00256 | **$10.36** |
| Fargate matching-service | 1 vCPU, 2GB | 1 | 730 | $0.0466 + $0.01022 | **$41.42** |
| Fargate session-service (WebRTC) | 2 vCPU, 4GB | 1 | 730 | $0.0932 + $0.02044 | **$82.85** |
| EC2 t3.small (TURN) | 2 vCPU, 2GB | 1 | 730 | $0.0264 | **$19.27** |
| **Subtotal Compute** | | | | | **$195.32** |

### 4.3. Storage & DB

| Resource | Qty | Unit | Monthly |
|----------|-----|------|---------|
| RDS db.t4g.medium (shared DB) | 730 hrs | $0.082 | $59.86 |
| RDS Storage 100GB gp3 | 100 | $0.115/GB | $11.50 |
| RDS Backup 50GB | 50 | $0.095/GB | $4.75 |
| ElastiCache cache.t4g.small | 730 hrs | $0.034 | $24.82 |
| S3 Standard 20GB | 20 | $0.023/GB | $0.46 |
| S3 Requests (10K GET) | 10 | $0.004/1K | $0.04 |
| EBS gp3 30GB (Fargate + EC2) | 30 | $0.08/GB | $2.40 |
| **Subtotal Storage** | | | **$103.83** |

### 4.4. Networking

| Resource | Qty | Unit | Monthly |
|----------|-----|------|---------|
| ALB | 730 hrs | $0.0225 | $16.43 |
| ALB LCU (low traffic) | 730 hrs | $0.008 | $5.84 |
| NAT Gateway | 730 hrs | $0.045 | $32.85 |
| NAT Gateway data (20GB) | 20 | $0.045/GB | $0.90 |
| CloudFront egress (50GB) | 50 | $0.085/GB | $4.25 |
| Route53 hosted zone | 1 | $0.50 | $0.50 |
| Data egress (50GB) | 50 | $0.09/GB | $4.50 |
| **Subtotal Networking** | | | **$65.27** |

### 4.5. Observability & Security

| Resource | Qty | Unit | Monthly |
|----------|-----|------|---------|
| CloudWatch logs (10GB) | 10 | $0.50/GB | $5.00 |
| CloudWatch metrics | 50 | $0.30/metric | $15.00 |
| Secrets Manager (5 secrets) | 5 | $0.40 | $2.00 |
| KMS | 1 | $1.00 | $1.00 |
| ECR (image storage 5GB) | 5 | $0.10/GB | $0.50 |
| **Subtotal Obs & Sec** | | | **$23.50** |

### 4.6. Stage 0 Total

| Category | Monthly | Annual |
|----------|---------|--------|
| Compute | $195.32 | $2.344 |
| Storage & DB | $103.83 | $1.246 |
| Networking | $65.27 | $783 |
| Observability & Security | $23.50 | $282 |
| **Subtotal AWS** | **$387.92** | **$4.655** |
| AI/LLM API (xem mục 8) | $80 | $960 |
| Third-party SaaS (xem mục 9) | $120 | $1.440 |
| **TOTAL** | **$588/month** | **$7.055/year** |

**Cost per MAU:** $588 / 500 = **$1.18/user/month**

---

## 5. Giai đoạn 1: Early Traction (500–5.000 MAU)

**Mục tiêu:** 5.000 users, 200 sessions/day, multiple regions bắt đầu.

### 5.1. Changes từ Stage 0

- Tách DB per service (5 RDS instances riêng)
- Thêm read replica cho 2 service hot (user, booking)
- Scale Fargate tasks (2-3 replicas mỗi service với auto-scaling)
- Thêm CDN cho static assets
- Setup Kafka (MSK)
- Thêm Qdrant cho vector search

### 5.2. Compute cost

| Resource | Spec | Qty | Hours/mo | Monthly |
|----------|------|-----|----------|---------|
| Fargate user-service | 1 vCPU, 2GB | 2 (HA) | 730 | $165.68 |
| Fargate booking-service | 1 vCPU, 2GB | 2 | 730 | $165.68 |
| Fargate wallet-service | 0.5 vCPU, 1GB | 2 | 730 | $82.85 |
| Fargate matching-service | 2 vCPU, 4GB | 2 | 730 | $331.36 |
| Fargate session-service | 2 vCPU, 4GB | 2 | 730 | $165.68 |
| Fargate notification-service | 0.5 vCPU, 1GB | 1 | 730 | $41.42 |
| EC2 c5.large (TURN/SFU) | 2 vCPU, 4GB | 2 | 730 | $154.16 |
| EC2 t3.medium (Qdrant) | 2 vCPU, 4GB | 1 | 730 | $38.54 |
| **Subtotal Compute** | | | | **$1,145.37** |

### 5.3. Database cost

| Resource | Qty | Monthly |
|----------|-----|---------|
| RDS user-service (db.t4g.medium + 100GB) | 1 | $71.36 |
| RDS booking-service (db.t4g.medium + 100GB) | 1 | $71.36 |
| RDS wallet-service (db.t4g.small + 50GB) | 1 | $36.78 |
| RDS matching-service (db.t4g.medium + 50GB) | 1 | $65.61 |
| RDS session-service (db.t4g.small + 30GB) | 1 | $34.48 |
| RDS Read Replica (user-service) | 1 | $71.36 |
| RDS Storage total 350GB | 350 | $40.25 |
| RDS Backups 200GB | 200 | $19.00 |
| ElastiCache cache.m6g.large (Redis) | 1 | $132.86 |
| **Subtotal DB** | | | **$543.06** |

### 5.4. Other costs

| Resource | Monthly |
|----------|---------|
| MSK Kafka (2 brokers, kafka.t3.small) | $117.00 |
| ALB (3 load balancers) | $49.28 + LCU |
| NAT Gateway (2 AZs for HA) | $65.70 |
| Data egress (300GB/mo) | $27.00 |
| CloudFront (500GB egress) | $42.50 |
| S3 (100GB + 100K requests) | $2.30 |
| EBS volumes (200GB total) | $16.00 |
| CloudWatch logs (50GB) + metrics | $40.00 |
| Secrets Manager (20 secrets) | $8.00 |
| **Subtotal** | **$367.78** |

### 5.5. Stage 1 Total

| Category | Monthly | Annual |
|----------|---------|--------|
| Compute | $1,145 | $13,744 |
| Database | $543 | $6,516 |
| Networking & Other | $368 | $4,415 |
| **Subtotal AWS** | **$2,056** | **$24,675** |
| AI/LLM API | $650 | $7,800 |
| Third-party SaaS | $350 | $4,200 |
| **TOTAL** | **$3,056/month** | **$36,675/year** |

**Cost per MAU:** $3,056 / 5,000 = **$0.61/user/month** (giảm 48% nhờ scale)

---

## 6. Giai đoạn 2: Product-Market Fit (5.000–50.000 MAU)

**Mục tiêu:** 50.000 users, 2.500 sessions/day, 3 regions (VN, SG, ID), 15 B2B clients.

### 6.1. Changes

- Multi-region deployment: ap-southeast-1 (Singapore, primary), ap-southeast-3 (Jakarta backup)
- Migrate từ Fargate → EKS (Kubernetes) cho cost optimization ở scale
- Larger DB instances với read replicas (3 replicas cho booking, 2 cho user)
- Redis cluster mode
- Kafka tăng lên 5 brokers
- CloudFront + edge caching
- Multi-AZ deployment tất cả services

### 6.2. Compute cost (EKS)

| Resource | Spec | Qty | Hours/mo | Monthly |
|----------|------|-----|----------|---------|
| EKS Control Plane | - | 1 | 730 | $73.00 |
| EC2 c5.2xlarge (worker nodes) | 8 vCPU, 16GB | 6 | 730 | $2,678.40 |
| EC2 c5.xlarge (WebRTC SFU) | 4 vCPU, 8GB | 4 | 730 | $560.64 |
| EC2 r5.large (Qdrant) | 2 vCPU, 16GB | 2 | 730 | $252.00 |
| EC2 m5.large (Kafka brokers) | 2 vCPU, 8GB | 5 | 730 | $385.20 |
| **Subtotal Compute** | | | | **$3,949.24** |

### 6.3. Database cost

| Resource | Qty | Monthly |
|----------|-----|---------|
| RDS user-service db.m6g.large (Multi-AZ) | 1 | $256.00 |
| RDS user-service read replica | 2 | $256.00 each → $512.00 |
| RDS booking-service db.m6g.xlarge (Multi-AZ) | 1 | $512.00 |
| RDS booking-service read replica | 2 | $512.00 each → $1,024.00 |
| RDS wallet-service db.m6g.large (Multi-AZ) | 1 | $256.00 |
| RDS matching-service db.m6g.large (Multi-AZ) | 1 | $256.00 |
| RDS session-service db.t4g.large | 1 | $164.00 |
| RDS Storage 1.5TB gp3 | 1500 | $172.50 |
| RDS Backups 800GB | 800 | $76.00 |
| ElastiCache Redis cluster.m6g.large (3 shards) | 1 | $798.00 |
| **Subtotal DB** | | | **$4,026.50** |

### 6.4. Other costs

| Resource | Monthly |
|----------|---------|
| MSK Kafka 5 brokers | $585.00 |
| ALB (5 LBs) + LCU | $200.00 |
| NAT Gateway (2×2 AZs) | $131.40 |
| Data egress (3TB) | $270.00 |
| CloudFront (3TB egress) | $255.00 |
| S3 (500GB + 1M requests) | $20.00 |
| EBS volumes (1TB) | $80.00 |
| CloudWatch + monitoring | $150.00 |
| Secrets Manager (50 secrets) | $20.00 |
| WAF (Web Application Firewall) | $30.00 |
| **Subtotal** | **$1,741.40** |

### 6.5. Stage 2 Total

| Category | Monthly | Annual |
|----------|---------|--------|
| Compute | $3,949 | $47,388 |
| Database | $4,027 | $48,324 |
| Other AWS | $1,741 | $20,891 |
| **Subtotal AWS** | **$9,717** | **$116,604** |
| AI/LLM API | $4,500 | $54,000 |
| Third-party SaaS | $1,200 | $14,400 |
| **TOTAL** | **$15,417/month** | **$185,004/year** |

**Cost per MAU:** $15,417 / 50,000 = **$0.31/user/month**

---

## 7. Giai đoạn 3: Scale (50.000–500.000 MAU)

**Mục tiêu:** 500.000 MAU, 25.000 sessions/day, multi-region (Asia + EU), B2B scale.

### 7.1. Changes

- Migrate WebRTC sang managed (LiveKit Cloud hoặc Twilio) — đỡ tự host
- Aurora Global Database cho cross-region
- ElastiCache Global Datastore
- CloudFront edge optimization
- Consider bare metal (i3en) cho Qdrant nếu vector DB lớn
- AWS Savings Plan 3-year (40-50% discount)

### 7.2. Compute cost (EKS + Savings Plan)

| Resource | Spec | Qty | Monthly |
|----------|------|-----|---------|
| EKS Control Plane (3 clusters) | - | 3 | $219.00 |
| EC2 c5.4xlarge workers (50 vCPU/100GB mỗi) | reserved 1-yr | 30 | $11,520.00 |
| EC2 c5.9xlarge SFU (36 vCPU/72GB) | on-demand | 8 | $4,228.80 |
| EC2 r5.2xlarge Qdrant (8 vCPU/64GB) | reserved | 4 | $1,344.00 |
| **Subtotal Compute** | | | **$17,311.80** |

### 7.3. Database cost

| Resource | Qty | Monthly |
|----------|-----|---------|
| Aurora PostgreSQL Global primary (db.r6g.4xlarge) | 1 | $3,720.00 |
| Aurora Global secondary (1 region) | 2 | $3,720.00 each |
| Aurora read replicas (per region) | 6 | $1,860 each |
| Aurora Storage 10TB | 10000 | $1,000.00 |
| Aurora Backups 5TB | 5000 | $475.00 |
| ElastiCache Redis Global (3 clusters, cluster.m6g.2xlarge) | 3 | $2,394 each |
| **Subtotal DB** | | | **$25,500.00** |

### 7.4. Other costs

| Resource | Monthly |
|----------|---------|
| MSK Kafka 10 brokers | $1,170.00 |
| ALB + WAF (10 LBs) | $500.00 |
| NAT Gateway (3×3 AZs) | $296.00 |
| Data egress (50TB) | $4,500.00 |
| CloudFront (20TB egress) | $1,700.00 |
| S3 (5TB + 50M requests) | $135.00 |
| EBS volumes (10TB gp3) | $800.00 |
| CloudWatch + Datadog APM | $800.00 |
| Secrets Manager + KMS | $100.00 |
| WAF + Shield Standard | $200.00 |
| **Subtotal** | **$10,201.00** |

### 7.5. Stage 3 Total

| Category | Monthly | Annual |
|----------|---------|--------|
| Compute | $17,312 | $207,744 |
| Database | $25,500 | $306,000 |
| Other AWS | $10,201 | $122,412 |
| **Subtotal AWS** | **$53,013** | **$636,156** |
| AI/LLM API | $25,000 | $300,000 |
| Third-party SaaS | $4,000 | $48,000 |
| **TOTAL** | **$82,013/month** | **$984,156/year** |

**Cost per MAU:** $82,013 / 500,000 = **$0.16/user/month**

### 7.6. Tổng kết cost efficiency qua các giai đoạn

| Stage | MAU | Monthly Total | Cost/MAU | Cost per session |
|-------|-----|---------------|----------|------------------|
| 0 — MVP | 500 | $588 | $1.18 | $29.40 |
| 1 — Early | 5.000 | $3.056 | $0.61 | $1.53 |
| 2 — PMF | 50.000 | $15.417 | $0.31 | $0.62 |
| 3 — Scale | 500.000 | $82.013 | $0.16 | $0.33 |

**Insight:** Cost per user giảm **87%** từ Stage 0 → Stage 3 nhờ economies of scale. Cost per session giảm **99%**.

---

## 8. Chi phí AI/LLM API (tách riêng)

### 8.1. Pricing reference (2026)

| Model | Input $/1M tokens | Output $/1M tokens | Context |
|-------|-------------------|---------------------|---------|
| **OpenAI GPT-4o mini** | $0.15 | $0.60 | 128K |
| **OpenAI GPT-4o** | $2.50 | $10.00 | 128K |
| **OpenAI o1-mini** | $3.00 | $12.00 | 128K |
| **Anthropic Claude 3.5 Haiku** | $0.80 | $4.00 | 200K |
| **Anthropic Claude 3.5 Sonnet** | $3.00 | $15.00 | 200K |
| **Google Gemini 1.5 Flash** | $0.075 | $0.30 | 1M |
| **Google Gemini 1.5 Pro** | $1.25 | $5.00 | 2M |
| **text-embedding-3-small** | $0.02 | - | 8K |
| **text-embedding-3-large** | $0.13 | - | 8K |

### 8.2. Usage estimates theo giai đoạn

| Use case | Stage 0 | Stage 1 | Stage 2 | Stage 3 |
|----------|---------|---------|---------|---------|
| **Matching explanations** (LLM) | 50K tokens/day | 500K | 5M | 50M |
| **AI Co-Pilot in session** (LLM streaming) | 10K tokens/day | 100K | 1.5M | 20M |
| **Embeddings for users** (indexing) | 5K tokens/day | 50K | 500K | 5M |
| **Embeddings for search** (queries) | 2K tokens/day | 30K | 300K | 3M |
| **RAG document processing** | 0 | 10K | 100K | 1M |

### 8.3. Model selection strategy

| Task | Model chính | Fallback |
|------|-------------|----------|
| Matching explanation | GPT-4o mini (95%) / Sonnet (5% complex) | Gemini Flash |
| AI Co-Pilot streaming | GPT-4o Realtime API | Claude Haiku |
| Embeddings | text-embedding-3-small | - |
| Skill categorization | GPT-4o mini | - |

### 8.4. AI cost per stage

| Stage | LLM cost/mo | Embedding cost/mo | Total AI/mo |
|-------|-------------|-------------------|-------------|
| **Stage 0** | $60 | $20 | **$80** |
| **Stage 1** | $500 | $150 | **$650** |
| **Stage 2** | $3,500 | $1,000 | **$4,500** |
| **Stage 3** | $18,000 | $7,000 | **$25,000** |

**Cost per session (Stage 3):** $25,000 / 25,000 sessions/day / 30 = **$0.033/session** AI cost

### 8.5. Cost optimization cho AI

- **Caching LLM responses** (Redis) — có thể cache 30-40% matching explanations
- **Routing thông minh** — gpt-4o-mini cho 95% cases, gpt-4o chỉ khi cần
- **Batch embedding generation** — index users off-peak
- **Prompt compression** — giảm input tokens 30-50%
- **Self-host khi scale** — vLLM/TGI host Llama 3.3 70B trên EC2, tiết kiệm 60%

---

## 9. Chi phí SaaS bên thứ ba

### 9.1. Essential services

| Service | Purpose | Stage 0 | Stage 1 | Stage 2 | Stage 3 |
|---------|---------|---------|---------|---------|---------|
| **SendGrid / Resend** | Email transactional | $0 (free tier) | $20 | $90 | $400 |
| **Twilio SMS** | OTP, notifications | $0 | $30 | $200 | $1,200 |
| **Stripe** | Payment processing | $0 (no revenue) | $50 (1.9% rev) | $500 | $5,000 |
| **Auth0 hoặc Clerk** | Auth (optional) | $0 (self-host) | $99 | $399 | $1,299 |
| **Sentry** | Error tracking | $0 (free) | $26 | $80 | $299 |
| **Datadog / Grafana Cloud** | APM + logs | $0 (free) | $50 | $300 | $1,200 |
| **LiveKit Cloud** (managed WebRTC) | Video - optional | $0 (self-host) | $0 | $0 | $2,000 |
| **Ideta/Persona** | KYC verification | $0 | $30 | $150 | $500 |
| **Mixpanel / PostHog** | Product analytics | $0 (free) | $0 (self-host) | $300 | $1,000 |
| **OpenAI Moderation API** | Content safety | $0 (free) | $20 | $100 | $500 |
| **Mapbox / Google Maps** | Location | $0 | $0 | $50 | $200 |
| **Various misc** | - | $120 | $25 | $30 | $100 |
| **TOTAL SaaS** | | **$120** | **$350** | **$1,200** | **$4,000** |

### 9.2. Cost-saving tips

- **Self-host auth** thay vì Auth0 (tiết kiệm $1.299/tháng ở Stage 3)
- **Use PostHog self-hosted** thay vì Mixpanel
- **AWS SES** thay vì SendGrid (90% rẻ hơn cho high volume)
- **Self-host Sentry** (Docker) nếu team có DevOps

---

## 10. Tổng chi phí vận hành (Burn Rate)

### 10.1. Monthly burn rate theo giai đoạn

| Category | Stage 0 | Stage 1 | Stage 2 | Stage 3 |
|----------|---------|---------|---------|---------|
| AWS Infrastructure | $388 | $2.056 | $9.717 | $53.013 |
| AI/LLM APIs | $80 | $650 | $4.500 | $25.000 |
| Third-party SaaS | $120 | $350 | $1.200 | $4.000 |
| **TOTAL Infra** | **$588** | **$3.056** | **$15.417** | **$82.013** |
| Team salaries (3-12 người) | $3.000 | $15.000 | $50.000 | $120.000 |
| Marketing & other | $500 | $2.000 | $10.000 | $40.000 |
| **TOTAL OPEX** | **$4.088** | **$20.056** | **$75.417** | **$242.013** |

### 10.2. Annual run rate

| Stage | Annual Infra | Annual Total (incl. team) |
|-------|--------------|---------------------------|
| Stage 0 | $7.056 | $49.056 |
| Stage 1 | $36.672 | $240.672 |
| Stage 2 | $185.004 | $905.004 |
| Stage 3 | $984.156 | $2.904.156 |

---

## 11. Chiến lược tối ưu chi phí

### 11.1. Theo resource

| Resource | Optimization | Saving |
|----------|--------------|--------|
| **Compute** | Reserved Instances / Savings Plans | 25-50% |
| **Compute** | Spot instances cho batch jobs | 60-70% |
| **Compute** | Right-sizing instances (dùng Compute Optimizer) | 20-30% |
| **Database** | Aurora Serverless cho dev/test | 50% |
| **Database** | Read replicas thay vì vertical scaling | 40% |
| **Storage** | S3 Intelligent-Tiering | 20-40% |
| **Storage** | GP3 thay về GP2 (default) | 20% |
| **Network** | VPC Endpoints thay vì NAT | 50% trên NAT cost |
| **Network** | CloudFront caching | 60-80% egress |
| **AI/LLM** | Model routing (mini vs full) | 60% |
| **AI/LLM** | Cache LLM responses | 30-40% |
| **AI/LLM** | Self-host khi >$20K/mo | 60% |
| **SaaS** | Self-host thay managed services | 50-70% |

### 11.2. Theo giai đoạn

**Stage 0 (MVP):** FOCUS trên tốc độ, không optimize cost
- Dùng managed services nhiều hơn (RDS thay self-host Postgres)
- Dùng Fargate thay EKS (đơn giản hơn)

**Stage 1 (Early):** Bắt đầu optimize moderate
- Right-sizing instances
- Tách DB per service
- Setup cost alerts

**Stage 2 (PMF):** Optimize serious
- Migrate EKS + Reserved Instances
- Aurora Serverless v2
- Spot instances cho batch AI jobs
- LLM caching system

**Stage 3 (Scale):** Optimize aggressive + FinOps team
- Savings Plans 3-year
- Self-host LLM nếu >$20K/mo
- Multi-region cost optimization
- Reserved Capacity cho CloudFront
- Negotiate Enterprise Discount Program (EDP) với AWS — tiết kiệm 10-20% trên toàn bill

### 11.3. Auto-scaling policies

```yaml
# Ví dụ Fargate auto-scaling
services:
  user-service:
    min_tasks: 2       # HA
    max_tasks: 10
    target_cpu: 60%    # Scale khi >60% CPU
    target_memory: 70%
    scale_in_cooldown: 300s
    scale_out_cooldown: 60s  # Scale out nhanh hơn scale in
```

---

## 12. Reserved Instances & Savings Plans

### 12.1. Compute Savings Plans

| Commitment | Discount | Phù hợp cho |
|------------|----------|-------------|
| 1-year, No Upfront | 27% | Stage 1-2 (predictable workload) |
| 1-year, All Upfront | 34% | Stage 2 (max saving) |
| 3-year, No Upfront | 41% | Stage 3+ (long-term commitment) |
| 3-year, All Upfront | 50% | Scale-out companies |

### 12.2. Áp dụng theo stage

| Stage | Chiến lược |
|-------|-----------|
| Stage 0 | 100% On-demand (linh hoạt) |
| Stage 1 | 50% On-demand + 50% 1-yr Savings Plan |
| Stage 2 | 30% On-demand + 70% 1-yr Savings Plan |
| Stage 3 | 60% 3-yr Savings Plan + 40% On-demand cho burst |

### 12.3. Cost saving example (Stage 3)

```
Without Savings Plan:  $53,013/month infra
With 3-yr 50% discount on compute portion:
  Compute saving: $17,312 × 0.50 = $8,656 saved
  New infra: $53,013 - $8,656 = $44,357/month
  
Annual saving: $103,872
```

### 12.4. Reserved Instances cho RDS

| Instance type | On-demand | 1-yr RI | 3-yr RI |
|---------------|-----------|---------|---------|
| db.r6g.large | $0.240/hr | $0.158 (-34%) | $0.122 (-49%) |

**Áp dụng:** Stage 2+ cho database instances chạy 24/7.

---

## 13. Cost Monitoring & Alerts

### 13.1. AWS Cost Explorer Setup

- Enable Cost Explorer
- Tag resources theo: `Service`, `Environment`, `Owner`
- Reserved cost reports weekly

### 13.2. Budget Alerts

```json
{
  "BudgetName": "skillseed-monthly-budget",
  "BudgetLimit": {
    "Amount": "15000",
    "Unit": "USD"
  },
  "TimeUnit": "MONTHLY",
  "Notifications": [
    {
      "Type": "ACTUAL",
      "Threshold": 50,
      "NotificationType": "EMAIL",
      "Recipients": ["ops@skillseed.app"]
    },
    {
      "Type": "ACTUAL", 
      "Threshold": 80,
      "NotificationType": "EMAIL",
      "Recipients": ["ops@skillseed.app", "ceo@skillseed.app"]
    },
    {
      "Type": "FORECASTED",
      "Threshold": 100,
      "NotificationType": "EMAIL",
      "Recipients": ["ops@skillseed.app", "ceo@skillseed.app"]
    }
  ]
}
```

### 13.3. Anomaly Detection

```yaml
# CloudWatch Anomaly Detection
anomaly_detection:
  service: AWS/Billing
  metric: EstimatedCharges
  algorithm: stddev
  threshold: 2  # Alert khi vượt 2 standard deviation
  action: SNS to ops team
```

### 13.4. Cost dashboards

Tools khuyến nghị:
- **Vantage.sh** ($150/mo) — cost analytics tốt nhất cho AWS
- **CloudZero** (enterprise) — cho >$50K/mo
- **AWS Cost Explorer** — miễn phí, đủ dùng cho <$30K/mo

### 13.5. Per-service cost attribution

```yaml
# Tag mọi resource
tags:
  Service: user-service | matching-service | booking-service | ...
  Environment: dev | staging | prod
  Owner: backend-team | data-team | ai-team
  CostCenter: engineering | data | marketing
```

→ Cost Explorer sẽ breakdown theo Service để biết service nào tốn nhiều nhất.

---

## 14. Hidden Costs Checklist

Danh sách chi phí hay bị quên khi estimate:

### 14.1. Data transfer (egress)

| Item | Cách tránh |
|------|-----------|
| Egress to internet (EC2) $0.09/GB | CloudFront caching, multi-region |
| Cross-AZ transfer $0.01/GB | Đặt AZ-aware services cùng AZ |
| Cross-region replication $0.02/GB | Chỉ replicate data thật cần |
| S3 egress $0.09/GB (after free tier) | CloudFront cho static assets |
| CloudFront to origin (back to S3) | Cache longer TTLs |

### 14.2. API request costs

| Service | Hidden cost |
|---------|------------|
| Secrets Manager | $0.40/secret/month + $0.05/10K API calls |
| KMS | $1/key/month + $0.03/10K requests |
| Lambda | $0.20/1M requests + GB-seconds |
| SNS/SQS | $0.50/1M requests |
| S3 | PUT requests $0.005/1K (often forgotten) |

### 14.3. Storage hidden costs

| Item | Cost |
|------|------|
| RDS snapshots beyond free tier | $0.095/GB |
| EBS snapshots | $0.05/GB |
| ECR image storage | $0.10/GB |
| CloudWatch logs beyond 5GB free | $0.50/GB |
| S3 Glacier retrieval | $0.01/GB + $0.03/GB expedited |

### 14.4. People costs (non-cloud but related)

- DevOps engineer: $3K-$8K/month (depending on region)
- 24/7 on-call rotation
- Security audit: $5K-$20K/year
- Penetration testing: $5K-$15K/year

### 14.5. Compliance costs

- SOC 2 Type II: $20K-$100K/year (Stage 3+ cần cho B2B)
- ISO 27001: $30K-$80K/year
- GDPR DPO (nếu EU users): $50K-$150K/year (part-time DPO)
- Penetration testing: $10K-$30K/year

---

## 15. So sánh AWS vs GCP vs DigitalOcean

### 15.1. Compute comparison (Singapore region, 2026)

| Spec | AWS EC2 | GCP Compute | DigitalOcean |
|------|---------|-------------|--------------|
| 2 vCPU, 4GB | $0.0528/hr (t3.medium) | $0.067/hr (n2-standard-2) | $0.036/hr (s-2vcpu-4gb) |
| 2 vCPU, 8GB | $0.1056/hr (t3.large) | $0.134/hr (n2-standard-2 highmem) | $0.060/hr (s-2vcpu-8gb) |
| 4 vCPU, 16GB | $0.2112/hr (t3.xlarge) | $0.268/hr | $0.119/hr (s-4vcpu-16gb) |
| **Winner** | Mid-tier | Đắt nhất | **Rẻ nhất ~40%** |

### 15.2. RDS comparison

| Service | AWS RDS | GCP Cloud SQL | DigitalOcean Managed DB |
|---------|---------|---------------|-------------------------|
| 2 vCPU, 8GB PostgreSQL | $0.171/hr | $0.198/hr | $0.087/hr |
| Backup | $0.095/GB | $0.080/GB | Free |
| **Winner** | Đắt | Đắt | **Rẻ nhất ~50%** |

### 15.3. Recommendation

| Use case | Recommended |
|----------|-------------|
| **MVP / Pre-seed (Stage 0)** | **DigitalOcean** hoặc **Hetzner** — rẻ nhất, đơn giản |
| **Early traction (Stage 1)** | **AWS** — ecosystem phong phú, dễ scale |
| **PMF (Stage 2)** | **AWS** hoặc **GCP** — managed services tốt |
| **Scale (Stage 3)** | **AWS** với EDP discount, hoặc **GCP** với committed use |

### 15.4. Hybrid approach

- **Application servers**: AWS/GCP (managed services)
- **AI/ML training**: AWS Spot / GCP Preemptible (rẻ)
- **Static assets**: Cloudflare R2 (egress free!)
- **Postgres**: DigitalOcean Managed hoặc Crunchy Bridge (rẻ hơn RDS)
- **Redis**: Upstash (pay-per-request)

---

## 16. Dự phóng tài chính 3 năm

### 16.1. Revenue projection

| Year | Premium users | B2B clients | Annual Revenue | Source |
|------|---------------|-------------|----------------|--------|
| Y1 (Stage 0→1) | 200 | 0 | $30.000 | Subscription only |
| Y2 (Stage 2) | 5.000 | 15 | $500.000 | Sub + B2B |
| Y3 (Stage 3) | 30.000 | 80 | $5.000.000 | Sub + B2B + marketplace |

### 16.2. Cost projection (3 năm)

| Category | Y1 | Y2 | Y3 |
|----------|----|----|-----|
| **Infrastructure (AWS)** | $24.675 | $116.604 | $636.156 |
| **AI/LLM** | $7.800 | $54.000 | $300.000 |
| **Third-party SaaS** | $4.200 | $14.400 | $48.000 |
| **Subtotal Infra** | **$36.675** | **$185.004** | **$984.156** |
| **Team (3→8→15 người)** | $180.000 | $480.000 | $900.000 |
| **Marketing** | $30.000 | $120.000 | $480.000 |
| **Compliance & Legal** | $5.000 | $30.000 | $100.000 |
| **Office & Misc** | $12.000 | $30.000 | $60.000 |
| **TOTAL OPEX** | **$263.675** | **$845.004** | **$2.524.156** |

### 16.3. P&L projection

| Item | Y1 | Y2 | Y3 |
|------|----|----|-----|
| Revenue | $30.000 | $500.000 | $5.000.000 |
| COGS (Infra + AI) | -$36.675 | -$185.004 | -$984.156 |
| **Gross Profit** | **-$6.675** | **$314.996** | **$4.015.844** |
| **Gross Margin** | **-22%** | **63%** | **80%** |
| Operating Expenses | -$227.000 | -$660.000 | -$1.540.000 |
| **Operating Income** | **-$233.675** | **-$345.004** | **$2.475.844** |
| Operating Margin | -779% | -69% | 50% |

### 16.4. Funding needs

| Round | Amount | Runway | Use case |
|-------|--------|--------|----------|
| Pre-seed | $100K | 6 tháng | MVP + validate |
| Seed | $1M | 18 tháng | 5K MAU + team 5 người |
| Series A | $5M | 18 tháng | 50K MAU + Series B ready |
| Series B | $25M | 30 tháng | 500K MAU + profitability |

---

## 17. Unit Economics & COGS

### 17.1. Cost per user cohort

| Metric | Y1 | Y2 | Y3 |
|--------|----|----|-----|
| COGS per MAU | $0.61 | $0.31 | $0.16 |
| COGS per session | $1.53 | $0.62 | $0.33 |
| AI cost per session | $0.33 | $0.18 | $0.10 |

### 17.2. LTV/CAC analysis

| Metric | Value |
|--------|-------|
| ARPU (Premium user) | $9.9/month |
| Avg lifetime | 12 tháng |
| **LTV** | **$118.8** |
| CAC (paid) | $15 |
| CAC (organic) | $3 |
| **Blended CAC** | **$8** |
| **LTV/CAC ratio** | **14.85x** (rất tốt) |

### 17.3. Payback period

```
Monthly gross profit per user = $9.9 × 0.63 - $0.31 = $5.93
CAC payback = $8 / $5.93 = 1.35 tháng ✅
```

### 17.4. COGS optimization roadmap

| Optimization | Effort | Saving | When |
|--------------|--------|--------|------|
| Caching LLM responses | Low | 30% AI cost | Stage 1 |
| Use Gemini Flash thay GPT-4o-mini | Low | 50% AI cost | Stage 1 |
| Migrate Fargate → EKS + Savings Plan | High | 30% compute | Stage 2 |
| Self-host LLM với Llama 3.3 70B | High | 60% AI cost | Stage 3+ |
| Multi-region consolidation | High | 25% data transfer | Stage 3 |
| Aurora Global thay RDS replicated | Medium | 20% DB cost | Stage 3 |
| Cloudflare R2 thay S3 egress | Low | 80% egress for static | Stage 2 |

---

## 18. Kịch bản tăng trưởng & Sensitivity

### 18.1. Three scenarios

| Scenario | Y1 MAU | Y2 MAU | Y3 MAU | Y3 Revenue |
|----------|--------|--------|--------|------------|
| 🐢 **Conservative** | 1.000 | 8.000 | 50.000 | $600K |
| 🎯 **Base case** | 5.000 | 50.000 | 500.000 | $5M |
| 🚀 **Aggressive** | 10.000 | 150.000 | 1.500.000 | $18M |

### 18.2. Infrastructure cost per scenario (Y3)

| Scenario | Y3 MAU | Y3 Infra/mo | Y3 Infra/yr |
|----------|--------|-------------|-------------|
| Conservative | 50K | $15.000 | $180.000 |
| Base case | 500K | $82.000 | $984.000 |
| Aggressive | 1.5M | $220.000 | $2.640.000 |

### 18.3. Sensitivity analysis

Biến nào ảnh hưởng cost nhiều nhất?

| Variable | Impact (+50%) | Sensitivity |
|----------|---------------|-------------|
| Video bandwidth (SD → HD) | +$15K/mo | ⭐⭐⭐⭐⭐ |
| Sessions per user (2x) | +$25K/mo | ⭐⭐⭐⭐⭐ |
| LLM cost per call (2x) | +$12K/mo | ⭐⭐⭐⭐ |
| AWS pricing (+20%) | +$10K/mo | ⭐⭐⭐ |
| DAU/MAU ratio (2x) | +$30K/mo | ⭐⭐⭐⭐⭐ |
| Storage growth (2x) | +$2K/mo | ⭐ |

### 18.4. Break-even analysis

```
Fixed costs (Y2): $845K
Contribution margin per user (Y2): $5.93/mo
Break-even users: 845,000 / (5.93 × 12) = ~12,000 MAU

→ SkillSeed break-even ở MAU ~12K (khoảng cuối Y2 trong base case)
```

---

## 📚 Tóm tắt & Khuyến nghị

### Cost optimization priority

1. **Stage 0 (MVP):** Tốc độ > Cost. Dùng managed services. ~$600/mo total.
2. **Stage 1 (Early):** Right-sizing, Reserved Instances. Cost/MAU ~$0.61.
3. **Stage 2 (PMF):** EKS migration, LLM caching, self-host auth. Cost/MAU ~$0.31.
4. **Stage 3 (Scale):** Savings Plans 3-yr, self-host LLM, FinOps team. Cost/MAU ~$0.16.

### "Cost per session" target cho sustainable business

| Stage | Target | Current |
|-------|--------|---------|
| Stage 0 | <$30 | $29.40 ✅ |
| Stage 1 | <$2 | $1.53 ✅ |
| Stage 2 | <$0.75 | $0.62 ✅ |
| Stage 3 | <$0.40 | $0.33 ✅ |

### Risk mitigations

- **Cost spike từ LLM**: Set hard budget limit, có fallback models
- **Egress surprise**: Setup CloudWatch alarm cho data transfer >$5K/mo
- **Database runaway**: Use Aurora Serverless v2 với auto-pause
- **AI cost explosion**: Rate-limit LLM calls per user

### Next steps cho founder

1. **Setup AWS account** + enable Cost Explorer + Budget alerts
2. **Tag everything** từ ngày đầu (không tag = không track được cost)
3. **Build cost dashboard** trên Grafana hoặc Vantage.sh
4. **Review monthly** với team: cost vs growth, optimize quick wins
5. **Negotiate EDP** khi bill > $30K/mo (savings 10-20%)

---

## Phụ lục: Quick reference chi phí

```
┌──────────────────────────────────────────────┐
│  SKILL SEED CLOUD COST CHEAT SHEET            │
├──────────────────────────────────────────────┤
│  Stage 0 (MVP):          $588 / month        │
│  Stage 1 (1K-5K MAU):    $3,056 / month      │
│  Stage 2 (50K MAU):      $15,417 / month     │
│  Stage 3 (500K MAU):     $82,013 / month     │
│                                               │
│  Cost per MAU:  $1.18 → $0.61 → $0.31 → $0.16│
│  Cost per session: $29 → $1.5 → $0.6 → $0.3│
│                                               │
│  Biggest cost driver: Compute + LLM API      │
│  Biggest saving: Reserved Instances + LLM    │
│    caching + EKS migration                   │
│                                               │
│  Break-even: ~12K MAU (cuối Y2 base case)    │
└──────────────────────────────────────────────┘
```

---

> **Liên hệ với tài liệu khác:**
> - **Business model**: `SKILLSEED.md` §13 (Business Model)
> - **Architecture**: `SKILLSEED.md` §6 (System Architecture)
> - **Roadmap**: `SKILLSEED.md` §16 (Implementation phases)
> - **Cost per service**: `SKILLSEED_API_AND_DB.md` (resource sizing)

> **Tác giả:** SkillSeed Team — Phiên bản 1.0 — 2026-09-06  
> **Disclaimer:** Số liệu ước lượng dựa trên giá AWS Singapore 2026, có thể ±15-20% tuỳ thời điểm và usage pattern thực tế.
