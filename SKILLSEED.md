# SkillSeed — Nền tảng Trao đổi Kỹ năng Siêu nhỏ bằng AI & Kinh tế Danh tiếng

> **Tagline:** *Teach what you know. Learn what you love. Pay with your time, not your wallet.*

> Tài liệu đặc tả sản phẩm & kỹ thuật — phiên bản 1.0 (Last updated: 2026-09-06)

---

## Mục lục

1. [Tổng quan & Tầm nhìn](#1-tổng-quan--tầm-nhìn)
2. [Vấn đề thực tế & Cơ hội thị trường](#2-vấn-đề-thực-tế--cơ-hội-thị-trường)
3. [Đối tượng người dùng & Phân khúc](#3-đối-tượng-người-dùng--phân-khúc)
4. [Đề xuất Giá trị Độc đáo (UVP)](#4-đề-xuất-giá-trị-độc-đáo-uvp)
5. [Tính năng sản phẩm (Product Features)](#5-tính-năng-sản-phẩm-product-features)
6. [Kiến trúc hệ thống (System Architecture)](#6-kiến-trúc-hệ-thống-system-architecture)
7. [Tech Stack tối ưu cho Java Backend Developer](#7-tech-stack-tối-ưu-cho-java-backend-developer)
8. [Thiết kế Cơ sở dữ liệu](#8-thiết-kế-cơ-sở-dữ-liệu)
9. [AI Matching Engine — Thiết kế chi tiết](#9-ai-matching-engine--thiết-kế-chi-tiết)
10. [Hệ thống Skill Seeds & Reputation](#10-hệ-thống-skill-seeds--reputation)
11. [Real-time Session Subsystem](#11-real-time-session-subsystem)
12. [Bảo mật, Privacy & Trust](#12-bảo-mật-privacy--trust)
13. [Mô hình Kinh doanh & Đơn vị tiền tệ](#13-mô-hình-kinh-doanh--đơn-vị-tiền-tệ)
14. [Phân tích Cạnh tranh](#14-phân-tích-cạnh-tranh)
15. [Xu hướng thị trường hỗ trợ (2026–2030)](#15-xu-hướng-thị-trường-hỗ-trợ-20262030)
16. [Lộ trình Phát triển (18–24 tháng)](#16-lộ-trình-phát-triển-1824-tháng)
17. [Rủi ro & Chiến lược giảm thiểu](#17-rủi-ro--chiến-lược-giảm-thiểu)
18. [Kế hoạch Go-to-Market](#18-kế-hoạch-go-to-market)
19. [Đội ngũ & Kỹ năng cần tuyển](#19-đội-ngũ--kỹ-năng-cần-tuyển)
20. [KPI & Chỉ số Thành công](#20-kpi--chỉ-số-thành-công)
21. [Tài liệu tham khảo & Nghiên cứu tiếp](#21-tài-liệu-tham-khảo--nghiên-cứu-tiếp)

---

## 1. Tổng quan & Tầm nhìn

### 1.1. Khái niệm

**SkillSeed** là một ứng dụng web & mobile cho phép người dùng **trao đổi trực tiếp các buổi hướng dẫn/học kỹ năng ngắn (15–60 phút)** với nhau mà **không dùng tiền tệ fiat**, dựa trên hệ thống **"hạt giống kỹ năng" (Skill Seeds)** — một đơn vị giá trị nội bộ được tạo ra khi người dùng dạy người khác. Điểm đột phá nằm ở:

- **AI Matching Engine** đóng vai trò "người mai mối thông minh", ghép cặp dựa trên *Skill DNA* (bản đồ năng lực đa chiều), mục tiêu học, lịch rảnh và cá tính học tập.
- **Verifiable Reputation System** — mỗi buổi học tạo ra một *Skill Passport* có thể xác minh, có thể mang theo như một "huy hiệu năng lực" trên LinkedIn hoặc CV.
- **Cơ chế kinh tế hybrid** — vừa có phần "money-less" (dùng Seeds) vừa có phần "money" (Premium cho verified expert), tạo ra hai vòng tuần hoàn giá trị tách biệt.

### 1.2. Tầm nhìn dài hạn (5–10 năm)

> Trở thành **"GitHub của kỹ năng thực"** — nơi mỗi người đều có thể chứng minh được họ biết gì, dạy được gì, và ai đã từng học từ họ — bất kể bằng cấp, quốc tịch hay thu nhập.

### 1.3. Sứ mệnh

- **Dân chủ hoá giáo dục** — biến mọi người thành cả học viên lẫn người dạy.
- **Tái phân phối cơ hội** — người có thời gian nhưng thiếu tiền vẫn được học từ chuyên gia.
- **Chống cô đơn** — biến giao tiếp trực tuyến thành kết nối thật, có chiều sâu.

---

## 2. Vấn đề thực tế & Cơ hội thị trường

### 2.1. Các "vết thương" của thị trường hiện tại

| Vấn đề | Dữ liệu thực tế (2025–2026) | Tác động |
|--------|------------------------------|---------|
| Khoảng cách kỹ năng ngày càng rộng | 60% người lao động cần đào tạo lại trong 5 năm tới (WEF Future of Jobs 2025) | Mất việc, lương thấp |
| Giáo dục chính quy đắt đỏ | Khóa học online $200–$2.000; bootcamp $5.000–$15.000 | Bất bình đẳng cơ hội |
| Đại dịch cô đơn | 1/3 người trưởng thành cảm thấy cô đơn (WHO 2025); giới trẻ châu Á đặc biệt cao | Sức khoẻ tâm thần, năng suất |
| Mentor truyền thống khan hiếm | Tỉ lệ mentor/Mentee 1:500 ở các nước đang phát triển | Sinh viên/khởi nghiệp khó tìm người dẫn đường |
| Lãng phí tiềm năng nghề nghiệp | Người nghỉ hưu, bà mẹ, người khuyết tật có kinh nghiệm quý nhưng không có kênh chia sẻ | Lãng phí tri thức xã hội |
| Freelancer/creator khó kiếm tiền từ kiến thức | Nền tảng kiếm tiền tri thức (Udemy, Skillshare) cắt phí 30–65% | Ít người dám dạy |

### 2.2. Cơ hội thị trường

- **TAM (Total Addressable Market):** ~$400B — toàn bộ ngành EdTech toàn cầu (2025)
- **SAM (Serviceable Addressable Market):** ~$50B — phân khúc peer-to-peer learning + micro-learning
- **SOM (Serviceable Obtainable Market) — 5 năm đầu:** 100.000–500.000 user trả phí ở Đông Nam Á & Ấn Độ

---

## 3. Đối tượng người dùng & Phân khúc

### 3.1. B2C — Người dùng cá nhân

| Persona | Tuổi | Mục tiêu chính | Pain point |
|---------|------|----------------|------------|
| **Sinh viên "Hạt giống"** | 18–24 | Học kỹ năng mới, xây CV | Không đủ tiền mua khóa học |
| **Chuyên gia trẻ "Đa năng"** | 25–35 | Mở rộng kỹ năng ngang/hobby | Ít thời gian, muốn học nhanh |
| **Người chuyển nghề "Lội ngược dòng"** | 30–45 | Học nhanh kỹ năng mới để đổi việc | Cần mentor thực chiến, không phải giáo trình |
| **Người lớn tuổi "Truyền lửa"** | 55+ | Chia sẻ kinh nghiệm, chống cô đơn | Không có kênh chia sẻ thuận tiện |
| **Bà mẹ sau sinh "Tái khởi"** | 28–40 | Học kỹ năng linh hoạt, kiếm thêm thu nhập | Bị giới hạn thời gian |

### 3.2. B2B — Doanh nghiệp

| Persona | Quy mô | Mục tiêu | Cơ hội |
|---------|--------|----------|--------|
| **Startup công nghệ** | 10–50 người | Cross-training, knowledge sharing | Phí $99/user/năm |
| **Công ty SME** | 50–500 người | Upskilling chi phí thấp | Phí $5.000–$50.000/năm |
| **Tập đoàn lớn** | 1.000+ | Đào tạo nội bộ + cộng đồng alumni | Custom Enterprise |
| **Quỹ đào tạo cộng đồng** | Phi lợi nhuận | Tài trợ Seeds cho người thu nhập thấp | Partnership + branding |

### 3.3. B2Edu — Tổ chức giáo dục

- Trường ĐH, cao đẳng: cung cấp "Skill Exchange" như hoạt động ngoại khóa
- Thư viện cộng đồng: tổ chức buổi exchange miễn phí
- Trung tâm hướng nghiệp: dùng làm công cụ coaching

---

## 4. Đề xuất Giá trị Độc đáo (UVP)

### 4.1. Ba trụ cột giá trị

```
┌─────────────────────────────────────────────────────┐
│                  SKILL SEED                          │
├─────────────────────────────────────────────────────┤
│  1. MONEY-LESS         2. AI-MATCHED         3.       │
│     BẢN CHẤT             THÔNG MINH           VERIFIED │
│     Trao đổi bằng        Ghép cặp dựa          REPUTATION │
│     thời gian, không     trên Skill DNA        Skill Passport │
│     tiền                  & mục tiêu            trên blockchain │
└─────────────────────────────────────────────────────┘
```

### 4.2. So với đối thủ

| Đặc điểm | Skillshare/Udemy | TimeRepublik/Simbi | MentorCruise | **SkillSeed** |
|----------|:-----------------:|:------------------:|:------------:|:-------------:|
| Không cần tiền | ❌ | ✅ | ❌ | ✅ |
| AI matching | ❌ | ❌ | ⚠️ thủ công | ✅ |
| Video call tích hợp | N/A | ❌ | ✅ | ✅ |
| Verifiable reputation | ❌ | ⚠️ rating đơn giản | ✅ | ✅ (blockchain) |
| B2B license | ❌ | ❌ | ⚠️ | ✅ |
| AI co-pilot trong buổi học | ❌ | ❌ | ❌ | ✅ |
| Hệ thống token nội bộ | ❌ | ✅ | ❌ | ✅ |
| Mobile-first UX hiện đại | ⚠️ | ❌ | ⚠️ | ✅ |

---

## 5. Tính năng sản phẩm (Product Features)

### 5.1. MVP (Tháng 1–3)

#### 🔹 5.1.1. Onboarding & Profile
- Đăng ký bằng email/Google/Apple ID
- Tạo **Skill DNA** bằng onboarding 7 bước (3 phút):
  - Kỹ năng tôi có thể dạy (chọn từ 2.000+ tags + tự nhập)
  - Kỹ năng tôi muốn học
  - Trình độ mỗi kỹ năng (1–5 sao + portfolio upload)
  - Sở thích, mục tiêu nghề nghiệp
  - Phong cách học (Visual / Auditory / Reading / Kinesthetic)
  - Lịch rảnh hàng tuần (timezone-aware)
  - Ngôn ngữ giao tiếp
- Verification: Email + Phone + selfie AI liveness check

#### 🔹 5.1.2. Matching Engine v1
- Tab "Discover" → AI đề xuất top 10 matches mỗi ngày
- Filter: theo kỹ năng, khu vực, ngôn ngữ, rating
- "Why we matched" — giải thích ngắn gọn lý do ghép cặp

#### 🔹 5.1.3. Booking & Session
- Xem profile đối phương, lịch rảnh
- Book slot 15/30/60 phút → nhận xác nhận qua email + in-app
- Tích hợp Google Calendar / Outlook

#### 🔹 5.1.4. Video Session
- WebRTC video call tích hợp sẵn (không cần cài tool khác)
- Share screen, whiteboard cơ bản
- Ghi âm (opt-in) → tạo transcript sau buổi học

#### 🔹 5.1.5. Đánh giá & Reputation v1
- Sau mỗi buổi: form đánh giá 5 tiêu chí (kiến thức, truyền đạt, hữu ích, đúng giờ, thân thiện)
- Skill DNA của người dạy được cập nhật theo đánh giá

#### 🔹 5.1.6. Seed Wallet
- Sau buổi dạy: nhận Seeds (1 seed/phút dạy)
- Mỗi user được cấp 30 "Free Starter Seeds" để dùng thử
- Seeds hết hạn sau 6 tháng (chống tích trữ)

### 5.2. V2 (Tháng 4–9) — Sau MVP

#### 🔹 5.2.1. AI Co-Pilot trong buổi học
- Auto-generated notes theo real-time
- Action items sau buổi
- Gợi ý follow-up topic
- Phát hiện "Aha moment" và bookmark

#### 🔹 5.2.2. Verifiable Skill Passport
- Sau 5+ buổi học được đánh giá 5 sao → cấp Skill Passport
- Public link có thể chia sẻ trên LinkedIn
- Tạo hình ảnh Open Graph đẹp khi share

#### 🔹 5.2.3. Group Pods & Learning Circles
- Tạo nhóm 4–8 người cùng chủ đề
- AI gợi ý chủ đề trending theo khu vực
- Có thể mở rộng thành mastermind thu phí

#### 🔹 5.2.4. Multi-modal Sessions nâng cao
- **Code editor live** (Monaco/CodeMirror) cho dạy lập trình
- **AR overlay** cho kỹ năng thực hành (nấu ăn, sửa đồ, yoga)
- **Interactive quiz** mid-session để kiểm tra hiểu bài

#### 🔹 5.2.5. Mobile App (iOS + Android)
- React Native hoặc Flutter (cross-platform)
- Push notification cho booking, reminder, match mới
- Offline mode cho xem tài liệu đã lưu

#### 🔹 5.2.6. Advanced Filters & Search
- Tìm theo giọng nói, quốc gia, ngành nghề
- "Top mentors tuần này" ở mỗi kỹ năng

### 5.3. V3 (Tháng 10–18) — Scale & B2B

#### 🔹 5.3.1. B2B Skill Exchange Workspace
- Dashboard cho HR/Manager
- Tạo "skill pool" nội bộ
- Reporting & analytics (skills gap, learning velocity)
- SSO + Audit log + GDPR compliance

#### 🔹 5.3.2. Blockchain Reputation (Soulbound Token)
- Skill Passport được mint thành SBT trên Polygon/Base
- Không thể chuyển nhượng → chống gian lận
- Có thể verify bởi bên thứ 3 (HR tech, EdTech khác)

#### 🔹 5.3.3. Marketplace mở rộng
- Verified Expert có thể bán buổi premium ($)
- NGO có thể tặng Seeds cho người thu nhập thấp
- Corporate bulk credit purchase

#### 🔹 5.3.4. AI Personality Insights (Premium)
- Phân tích chi tiết cá tính học tập qua lịch sử
- Gợi ý lộ trình 6 tháng cá nhân hoá
- So sánh ẩn danh với cohort cùng nhóm

#### 🔹 5.3.5. Public API & Webhooks
- Cho phép EdTech khác tích hợp Skill Passport
- HR platforms có thể pull verified credentials
- Webhook khi user nhận badge mới

---

## 6. Kiến trúc hệ thống (System Architecture)

### 6.1. Sơ đồ tổng quan (High-Level)

```
┌──────────────────────────────────────────────────────────────┐
│                     CLIENT LAYER                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │ Web (Next.js)│  │ iOS App      │  │ Android App  │        │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘        │
└─────────┼──────────────────┼──────────────────┼───────────────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             │ HTTPS/WSS
                             ▼
┌──────────────────────────────────────────────────────────────┐
│                      API GATEWAY                              │
│           Spring Cloud Gateway + JWT Auth                     │
└─────────┬────────────────────────────────────────────────────┘
          │
          ├─────────────────┬─────────────────┬────────────────┐
          ▼                 ▼                 ▼                ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐  ┌──────────┐
│ User Service │   │Matching Svc  │   │Session Svc   │  │Wallet Svc│
│ (Java/Spring)│   │(Java+Python) │   │(Java+WebRTC) │  │(Java)    │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘  └────┬─────┘
       │                  │                  │               │
       ├──────────────────┼──────────────────┼───────────────┤
       ▼                  ▼                  ▼               ▼
┌────────────────────────────────────────────────────────────────┐
│                     DATA & MESSAGING LAYER                      │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌─────────────┐  │
│  │ PostgreSQL │ │ Redis      │ │ Kafka      │ │ Qdrant      │  │
│  │ (Primary)  │ │ (Cache)    │ │ (Events)   │ │ (Vector DB) │  │
│  └────────────┘ └────────────┘ └────────────┘ └─────────────┘  │
└────────────────────────────────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────┐
│                  EXTERNAL / THIRD-PARTY                        │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ LLM API  │ │ Stripe   │ │ SendGrid │ │ Polygon  │          │
│  │ (AI svc) │ │ (Payment)│ │ (Email)  │ │ (SBT)    │          │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘          │
└──────────────────────────────────────────────────────────────┘
```

### 6.2. Microservices — Danh sách chi tiết

| Service | Công nghệ chính | Trách nhiệm |
|---------|----------------|-------------|
| **api-gateway** | Spring Cloud Gateway | Routing, JWT, rate-limit |
| **user-service** | Spring Boot + JPA | Profile, Auth, Skill DNA |
| **matching-service** | Spring Boot + Python sidecar | AI matching algorithm |
| **session-service** | Spring Boot + WebRTC | Booking, video call orchestration |
| **wallet-service** | Spring Boot | Seeds ledger, transaction |
| **reputation-service** | Spring Boot + Polygon SDK | Ratings, Skill Passport, SBT |
| **notification-service** | Spring Boot + Kafka | Email, push, in-app |
| **search-service** | Spring Boot + ElasticSearch | Search profiles, skills |
| **analytics-service** | Spring Boot + ClickHouse | Metrics, BI, dashboards |
| **ai-co-pilot-service** | Spring Boot + Spring AI | Real-time session AI assistant |
| **moderation-service** | Spring Boot + OpenAI Mod API | Content safety |
| **billing-service** | Spring Boot + Stripe | Premium subs, B2B invoices |

### 6.3. Kiến trúc Real-time Session

```
[Client A]              [Session Service]              [Client B]
    │                          │                            │
    │──── WebRTC Offer ───────>│                            │
    │<─── SDP Answer ──────────│                            │
    │                                                    │
    │═══════ DTLS/SRTP Encrypted P2P Media ══════════════>│
    │                                                    │
    │──── WebSocket (signaling, chat) ────────────────────>│
    │<───── WebSocket (chat, reactions) ──────────────────│
    │                          │                            │
    │─── AI Co-Pilot request ──>│                            │
    │<── AI insights / notes ──│                            │
```

- **Media** đi P2P qua WebRTC (DTLS-SRTP), server chỉ là TURN fallback
- **Signaling & metadata** qua WebSocket tới Session Service
- **AI Co-Pilot** có thể chạy real-time streaming (OpenAI Realtime API / Gemini Live)

---

## 7. Tech Stack tối ưu cho Java Backend Developer

> **Nguyên tắc chọn stack:** Tận dụng tối đa thế mạnh Java/Spring, bổ sung các công nghệ "nên học thêm" theo thứ tự ưu tiên.

### 7.1. Backend (Core — Java Dev là chủ)

| Layer | Công nghệ | Lý do chọn |
|-------|-----------|------------|
| **Language** | Java 21 (LTS) | Records, pattern matching, virtual threads (Project Loom) |
| **Framework** | Spring Boot 3.3.x | Ecosystem trưởng thành, productivity cao |
| **Reactive** | Spring WebFlux (chọn lọc) | Cho real-time & high-concurrency endpoints |
| **Persistence** | Spring Data JPA + Hibernate | Quen thuộc với Java dev |
| **Migration** | Flyway | Quản lý DB schema version |
| **Validation** | Jakarta Bean Validation | Validate request DTO |
| **Security** | Spring Security 6 + OAuth2 Resource Server | JWT, role-based access |
| **API Docs** | springdoc-openapi (Swagger) | Auto-generate API docs |
| **Testing** | JUnit 5 + Mockito + Testcontainers | Unit + integration tests |
| **Build** | Maven hoặc Gradle | Maven quen thuộc hơn với hầu hết Java dev |

### 7.2. AI / ML Layer (Cần học thêm — Nhưng nhẹ nhàng)

| Layer | Công nghệ | Lý do |
|-------|-----------|-------|
| **LLM API** | OpenAI GPT-4o / Anthropic Claude 3.5 / Google Gemini 2.0 | Multi-provider, dễ tích hợp qua Spring AI |
| **Framework** | Spring AI (1.0+) | Java-native, quen thuộc với Spring stack |
| **Vector DB** | Qdrant hoặc pgvector (Postgres) | Embedding lưu trữ skill profiles |
| **Embedding** | text-embedding-3-small (OpenAI) hoặc sentence-transformers | Tính similarity giữa các skill profiles |
| **ML Sidecar** | Python FastAPI (chỉ 2–3 service) | Khi cần custom model (recommendation, ranking) |
| **Orchestration** | LangChain4j | Chain LLM calls, dễ cho Java dev |

> **Gợi ý học thêm cho Java dev:** Spring AI + LangChain4j + cơ bản về Prompt Engineering. Có thể start trong 2 tuần nếu đã biết Spring.

### 7.3. Data Layer

| Loại | Công nghệ | Use case |
|------|-----------|----------|
| **Primary DB** | PostgreSQL 16 | User, booking, ratings, transactional data |
| **Cache** | Redis 7 | Session cache, rate-limit, hot data |
| **Search** | ElasticSearch 8 | Full-text search profiles/skills |
| **Vector DB** | Qdrant | Skill DNA embedding search |
| **OLAP** | ClickHouse | Analytics events, BI dashboard |
| **Message Queue** | Apache Kafka 3.x | Event-driven giữa microservices |
| **Object Storage** | MinIO (self-host) hoặc AWS S3 | Avatar, portfolio, recording |

### 7.4. Real-time & Communication

| Layer | Công nghệ |
|-------|-----------|
| **Video** | WebRTC (native browser + Java SDK: Kurento / mediasoup) |
| **TURN Server** | coturn (open-source) hoặc Twilio Network Traversal |
| **Signaling** | Spring Boot + WebSocket (STOMP) |
| **Chat** | WebSocket + Redis Pub/Sub |
| **Realtime AI** | OpenAI Realtime API hoặc custom WebSocket |

### 7.5. Frontend (Cần học thêm)

| Loại | Công nghệ | Gợi ý cho Java dev |
|------|-----------|---------------------|
| **Web** | Next.js 15 (React + Server Components) | TypeScript — syntax gần Java, dễ học |
| **Mobile** | React Native (Expo) hoặc Flutter | React Native giúp share code với web |
| **State** | Zustand hoặc Redux Toolkit | Nhẹ, dễ hiểu |
| **UI Lib** | shadcn/ui + TailwindCSS | Đẹp, nhanh |
| **Real-time Client** | simple-peer (WebRTC wrapper) + socket.io-client |

> **Gợi ý học thêm:** TypeScript + React cơ bản (1 tháng), sau đó Next.js (1 tháng). Nếu chỉ làm MVP, có thể thuê freelancer frontend 2–3 tháng.

### 7.6. DevOps & Infrastructure

| Layer | Công nghệ |
|-------|-----------|
| **Container** | Docker + Docker Compose (dev) |
| **Orchestration** | Kubernetes (production) — hoặc đơn giản hơn: ECS/Fargate |
| **CI/CD** | GitHub Actions (Java dev quen thuộc) |
| **Monitoring** | Prometheus + Grafana + Loki (EKK stack alternative) |
| **APM** | Spring Boot Actuator + Micrometer + Datadog/New Relic |
| **Cloud** | AWS (Tokyo region cho VN/Đông Nam Á) hoặc GCP |
| **IaC** | Terraform |
| **Secrets** | HashiCorp Vault hoặc AWS Secrets Manager |

### 7.7. Tổng kết: Tech Stack "lai" tối ưu cho Java Dev

```
✅ Java Dev thế mạnh (sử dụng ngay):
   Java 21, Spring Boot 3, Spring Cloud, Spring Security, JPA, Kafka,
   PostgreSQL, Redis, Maven, JUnit, Docker, GitHub Actions

📚 Cần học thêm (ưu tiên cao, học trong 1–2 tháng):
   Spring AI, TypeScript/React cơ bản, Next.js, LangChain4j

📚 Cần học thêm (ưu tiên trung bình, 2–4 tháng):
   WebRTC, Kubernetes, ElasticSearch, ClickHouse

🔧 Có thể thuê/freelancer:
   UI/UX design, Mobile native polish, Data engineering nặng
```

---

## 8. Thiết kế Cơ sở dữ liệu

### 8.1. Sơ đồ ERD (rút gọn)

```
┌────────────┐         ┌──────────────┐         ┌──────────────┐
│   users    │1────────*│skill_offered│         │skill_wanted  │
│            │         │ (user_id)   │         │ (user_id)    │
└─────┬──────┘         └──────────────┘         └──────────────┘
      │1                                               │
      │                                                │
      │*                                               │*
┌─────▼──────────┐         ┌─────────────────┐         │
│  bookings      │*───────1│    sessions     │         │
│ (teacher_id,   │         │ (recording_url) │         │
│  learner_id)   │         └────────┬────────┘         │
└────────────────┘                  │1                  │
                                   │                   │
                                   │*                  │
                            ┌──────▼──────┐            │
                            │  ratings    │            │
                            │ (5 criteria)│            │
                            └─────────────┘            │
                                                      │
                            ┌──────────────────────────▼─┐
                            │   seed_transactions        │
                            │   (wallet ledger)          │
                            └─────────────────────────────┘

┌────────────────┐         ┌─────────────────────┐
│skill_passports │         │  learning_pods      │
│ (user_id,      │         │  (4-8 members,      │
│  skill_id, SBT)│         │   topic)            │
└────────────────┘         └─────────────────────┘
```

### 8.2. Schema chính (PostgreSQL — rút gọn)

```sql
-- USERS
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255),
    full_name VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    bio TEXT,
    country_code CHAR(2),
    timezone VARCHAR(50),
    languages TEXT[], -- ['vi','en','ja']
    learning_style VARCHAR(20), -- visual/auditory/reading/kinesthetic
    verified BOOLEAN DEFAULT false,
    verification_level SMALLINT DEFAULT 0, -- 0=email, 1=phone, 2=id, 3=liveness
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_users_country ON users(country_code);
CREATE INDEX idx_users_verified ON users(verified) WHERE verified = true;

-- SKILLS (taxonomy + custom)
CREATE TABLE skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50), -- tech/business/art/language/life/...
    is_custom BOOLEAN DEFAULT false,
    parent_id UUID REFERENCES skills(id)
);

-- USER_SKILLS (many-to-many với metadata)
CREATE TABLE user_skills_offered (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    skill_id UUID REFERENCES skills(id),
    level SMALLINT CHECK (level BETWEEN 1 AND 5),
    years_experience INT,
    description TEXT,
    hourly_seed_rate INT, -- số seed/giờ yêu cầu
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE user_skills_wanted (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    skill_id UUID REFERENCES skills(id),
    priority SMALLINT, -- 1-5
    target_level SMALLINT,
    notes TEXT
);

-- AVAILABILITY
CREATE TABLE user_availability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    day_of_week SMALLINT, -- 0-6
    start_time TIME,
    end_time TIME,
    timezone VARCHAR(50)
);

-- SESSIONS / BOOKINGS
CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id UUID REFERENCES users(id),
    learner_id UUID REFERENCES users(id),
    skill_id UUID REFERENCES skills(id),
    scheduled_at TIMESTAMPTZ NOT NULL,
    duration_minutes SMALLINT NOT NULL CHECK (duration_minutes IN (15,30,45,60)),
    status VARCHAR(20) DEFAULT 'pending', -- pending/confirmed/in_progress/completed/cancelled
    seed_amount INT NOT NULL, -- số seed sẽ chuyển
    meeting_url TEXT,
    recording_url TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT now(),
    cancelled_at TIMESTAMPTZ,
    cancellation_reason TEXT
);

CREATE INDEX idx_bookings_teacher ON bookings(teacher_id, scheduled_at);
CREATE INDEX idx_bookings_learner ON bookings(learner_id, scheduled_at);
CREATE INDEX idx_bookings_status ON bookings(status, scheduled_at);

-- RATINGS (multi-criteria)
CREATE TABLE ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID REFERENCES bookings(id) UNIQUE,
    rater_id UUID REFERENCES users(id),
    ratee_id UUID REFERENCES users(id),
    knowledge_score SMALLINT CHECK (knowledge_score BETWEEN 1 AND 5),
    clarity_score SMALLINT,
    helpfulness_score SMALLINT,
    punctuality_score SMALLINT,
    friendliness_score SMALLINT,
    overall_score SMALLINT GENERATED ALWAYS AS (
        (knowledge_score + clarity_score + helpfulness_score + punctuality_score + friendliness_score) / 5.0
    ) STORED,
    review_text TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- SEED WALLET (ledger-based, không update trực tiếp balance)
CREATE TABLE seed_wallets (
    user_id UUID PRIMARY KEY REFERENCES users(id),
    balance INT NOT NULL DEFAULT 0,
    total_earned INT NOT NULL DEFAULT 0,
    total_spent INT NOT NULL DEFAULT 0,
    last_expiring_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE seed_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID REFERENCES seed_wallets(user_id),
    type VARCHAR(20) NOT NULL, -- earn/spend/expire/grant/refund
    amount INT NOT NULL, -- positive=earn, negative=spend
    booking_id UUID REFERENCES bookings(id),
    description TEXT,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_seed_tx_wallet ON seed_transactions(wallet_id, created_at DESC);
CREATE INDEX idx_seed_tx_expiring ON seed_transactions(expires_at) WHERE expires_at IS NOT NULL;

-- SKILL PASSPORTS (verification badges)
CREATE TABLE skill_passports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    skill_id UUID REFERENCES skills(id),
    sessions_completed INT,
    avg_rating DECIMAL(2,1),
    sbt_tx_hash VARCHAR(66), -- Polygon tx hash, null nếu chưa mint
    issued_at TIMESTAMPTZ DEFAULT now(),
    revoked BOOLEAN DEFAULT false,
    UNIQUE(user_id, skill_id)
);

-- LEARNING PODS
CREATE TABLE learning_pods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    topic VARCHAR(255),
    description TEXT,
    creator_id UUID REFERENCES users(id),
    max_members SMALLINT DEFAULT 8,
    is_public BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE pod_members (
    pod_id UUID REFERENCES learning_pods(id),
    user_id UUID REFERENCES users(id),
    role VARCHAR(20) DEFAULT 'member', -- member/moderator/creator
    joined_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (pod_id, user_id)
);
```

### 8.3. Dữ liệu phi quan hệ

- **Vector embeddings** của skill DNA → Qdrant (collection: `user_skill_embeddings`)
- **Session recordings & transcripts** → S3/MinIO + Elasticsearch index để search
- **Event log** (user actions) → Kafka topic + ClickHouse cho analytics

---

## 9. AI Matching Engine — Thiết kế chi tiết

### 9.1. Pipeline tổng quan

```
┌──────────────────────────────────────────────────────────────────┐
│                    MATCHING PIPELINE                              │
└──────────────────────────────────────────────────────────────────┘

[Input: User wants to learn "Public Speaking"]
         │
         ▼
┌─────────────────────────┐
│ 1. Skill Embedding      │ ← Embedding model (text-embedding-3)
│    Query → Vector       │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│ 2. Candidate Retrieval  │ ← Qdrant ANN search (top 200 candidates)
│    Vector search in     │
│    user_skill_embeddings│
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│ 3. Hard Filters         │ ← Timezone overlap, language, level diff
│    - Lịch rảnh trùng    │
│    - Cùng ngôn ngữ      │
│    - Verified           │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│ 4. Re-ranking Model     │ ← Custom Python model (Gradient Boost)
│    Features:            │
│    - Skill similarity   │
│    - Rating avg         │
│    - Response rate      │
│    - Time overlap score │
│    - Personality match  │
│    - Recent activity    │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│ 5. LLM Explanation      │ ← Spring AI → GPT-4o
│    "Bạn 92% hợp với    │
│     Mai vì: ..."         │
└──────────┬──────────────┘
           │
           ▼
[Output: Top 10 matches với explanations]
```

### 9.2. Multi-factor Scoring

**Score tổng hợp** = weighted sum của:

| Factor | Trọng số | Cách tính |
|--------|----------|----------|
| **Skill Match** | 0.30 | Cosine similarity giữa embeddings |
| **Schedule Overlap** | 0.20 | Số giờ overlap/tổng giờ rảnh trong tuần |
| **Rating History** | 0.15 | Avg rating × log(số buổi đã dạy) |
| **Response Rate** | 0.10 | Tỉ lệ phản hồi booking trong 24h |
| **Personality Match** | 0.10 | RIASEC/MBTI compatibility |
| **Language Match** | 0.10 | Có overlap ngôn ngữ không |
| **Diversity** | 0.05 | Boost nếu match chưa từng xuất hiện |

### 9.3. Cold Start Strategy

Khi user mới (chưa có rating):
- Match dựa trên **Skill similarity + Schedule overlap** (cold-start-friendly)
- Ưu tiên người dạy "early adopter" (có badge "Founding Mentor")
- Hiển thị "Newcomer-friendly mentors" filter

### 9.4. Continuous Learning

- Log lại mọi booking outcome (completed/cancelled/rebook)
- Re-train re-ranking model mỗi tuần
- A/B test scoring weights

---

## 10. Hệ thống Skill Seeds & Reputation

### 10.1. Quy tắc Seed Economy

```
┌──────────────────────────────────────────────────┐
│             SKILL SEED ECONOMY RULES              │
├──────────────────────────────────────────────────┤
│  +60 seeds/month     = Dạy 60 phút               │
│  Free starter pack   = 30 seeds (1 lần)          │
│  Seed expiry         = 6 tháng sau khi nhận       │
│  Peer session rate   = 60 seeds/giờ (mặc định)   │
│  Expert premium rate = 200-1000 seeds/giờ         │
│  Anti-hoarding       = Expiry + max balance cap   │
└──────────────────────────────────────────────────┘
```

### 10.2. Seed Wallet — Ledger Pattern

- **KHÔNG** update trực tiếp `balance` trong DB
- Mọi thay đổi tạo 1 record trong `seed_transactions`
- `balance` được tính từ sum of transactions (có thể cache trong Redis)
- **Lý do:** audit trail, dispute resolution, accounting

### 10.3. Cron job xử lý expiry

```java
@Scheduled(cron = "0 0 1 * * *") // 1h sáng mỗi ngày
public void processSeedExpiry() {
    // Tìm tất cả transaction có expires_at < now() và chưa expire
    // Tạo transaction "expire" với amount = -remaining
    // Cập nhật last_expiring_at cho wallet
}
```

### 10.4. Reputation System chi tiết

**Tier hóa user:**

| Tier | Tiêu chí | Quyền lợi |
|------|----------|-----------|
| 🌱 **Sprout** | Mới đăng ký | Starter seeds, profile cơ bản |
| 🌿 **Sapling** | 5+ buổi hoàn thành, rating ≥ 4.0 | Được phép dạy |
| 🌳 **Tree** | 20+ buổi, rating ≥ 4.5, verified | Skill Passport, boost trong search |
| 🌲 **Forest** | 100+ buổi, rating ≥ 4.7 | Premium expert status, mint SBT |
| 🏆 **Legend** | 500+ buổi, rating ≥ 4.9, peer-nominated | Đề xuất tính năng, mentorship program |

### 10.5. Anti-gaming Mechanism

- **Velocity check**: Không cho phép dạy >8 giờ/ngày (ngăn spam)
- **Reciprocal rating**: Phát hiện "rating rings" (hai user luôn rate nhau 5⭐)
- **Skill DNA drift**: Nếu user đột ngột thêm kỹ năng không liên quan → flag for verification
- **First-session low rating**: Sau 3 buổi đầu có rating < 3⭐ → yêu cầu verification bổ sung

---

## 11. Real-time Session Subsystem

### 11.1. Session Lifecycle

```
   Created          Confirmed         Started          Completed        Rated
      │                 │                │                  │              │
  [Pending] ──────> [Confirmed] ────> [InProgress] ────> [Completed] ──> [Rated]
      │                 │                                  │
      └─timeout 24h──> [Expired]                          └─no-show──> [NoShow]
      
  Cancellation reasons: teacher_unavailable, learner_unavailable,
                        technical_issue, other
```

### 11.2. WebRTC Architecture

```
┌──────────────┐                        ┌──────────────┐
│   Client A   │◄══ DTLS/SRTP P2P ════►│   Client B   │
└──────┬───────┘                        └──────┬───────┘
       │                                       │
       └─────────────WebSocket─────────────────┘
                       │
                       ▼
              ┌──────────────────┐
              │ Session Service  │ ← signaling, chat, AI co-pilot
              │ (Spring Boot)    │
              └────────┬─────────┘
                       │
              ┌────────▼─────────┐
              │   TURN Server    │ ← fallback khi P2P fail
              │   (coturn)       │
              └──────────────────┘
```

### 11.3. AI Co-Pilot (trong session)

Real-time assistant lắng nghe (qua audio transcription streaming) và:

- **Auto-generate notes** theo từng phút
- **Detect "aha moments"** (giọng nói hào hứng, "à!", "hiểu rồi!")
- **Suggest follow-up topics**: "Có vẻ bạn đang quan tâm X, muốn tìm hiểu thêm?"
- **Auto-quiz mid-session**: "Hãy thử giải thích lại bằng ví dụ khác"

Tech: OpenAI Realtime API hoặc AssemblyAI streaming + LLM summary.

### 11.4. Session Recording & Privacy

- **Default**: KHÔNG ghi hình (chỉ ghi transcript nếu cả 2 đồng ý)
- **Opt-in recording**: Lưu trên S3, mã hóa AES-256, xóa sau 30 ngày
- **Transcript-only mode**: Chỉ lưu text, dùng cho AI notes & search

---

## 12. Bảo mật, Privacy & Trust

### 12.1. Security Stack

| Layer | Biện pháp |
|-------|-----------|
| **Transport** | HTTPS only (TLS 1.3), HSTS |
| **Auth** | JWT (short-lived) + Refresh token (httpOnly cookie) |
| **API Security** | OAuth2, rate-limit, request signing |
| **Data at rest** | PostgreSQL TDE, S3 SSE-KMS |
| **Sensitive fields** | Field-level encryption (Bouncy Castle/JCE) |
| **Secrets** | HashiCorp Vault |
| **Video** | DTLS-SRTP (E2E encrypted) |
| **Anti-fraud** | Device fingerprinting, IP geolocation, velocity checks |

### 12.2. Privacy Compliance

- **GDPR** (EU): Right to be forgotten, data export, consent management
- **PDPA** (VN/Thái Lan): Tương tự GDPR
- **CCPA** (California): Opt-out of data sale
- **COPPA** (US): Không cho phép user <13 tuổi

### 12.3. Verification Levels

| Level | Phương thức | Quyền |
|-------|------------|-------|
| 0 | Email | Xem, tạo profile |
| 1 | + Phone (SMS OTP) | Book session |
| 2 | + Government ID (qua Onfido/Persona) | Dạy & nhận seeds |
| 3 | + Liveness check (selfie AI) | Skill Passport |
| 4 | + Video interview (peer-nominated) | Forest/Legend tier |

### 12.4. Moderation

- **Pre-session**: AI scan profile ảnh, bio text
- **During session**: AI moderation audio real-time (phát hiện toxic, harassment)
- **Post-session**: Review text scan + report button
- **Response**: Auto-flag → human review → warning/suspension/ban

### 12.5. Safety Tools cho Session Offline (nếu có)

- Share location với emergency contact
- Quick-exit button (đóng app + thông báo)
- Report nhanh sau buổi học

---

## 13. Mô hình Kinh doanh & Đơn vị tiền tệ

### 13.1. Bốn nguồn thu chính

```
REVENUE STREAMS
──────────────────────────────────────────────────
1. Premium B2C Subscription ──── 55-60%
2. Skill Seed Marketplace ───── 15-20%
3. B2B Enterprise License ───── 15-20%
4. API & Verification Service ─ 5-10%
──────────────────────────────────────────────────
```

### 13.2. Chi tiết Pricing

#### B2C Premium
| Tier | Giá | Quyền |
|------|-----|-------|
| **Free** | $0 | 10 buổi/tháng với non-expert, 60 seeds starter |
| **Premium** | $9.9/tháng | Unlimited booking, AI insights, no ads, priority matching |
| **Premium Family** | $14.9/tháng | 4 thành viên, share seeds pool |

#### Skill Seed Pack (cho user muốn học expert)
| Pack | Seeds | Giá USD |
|------|-------|---------|
| Starter | 100 | $4.9 |
| Growth | 500 | $19.9 |
| Pro | 1500 | $49.9 |

#### B2B
| Quy mô | Pricing |
|--------|---------|
| Startup (<50) | $99/user/năm |
| SME (50–500) | $70/user/năm (volume discount) |
| Enterprise (500+) | Custom ($50k–$500k/năm) |

#### Marketplace Commission
- 10–20% commission khi expert bán buổi premium
- 5% commission khi user đổi seeds lấy voucher

### 13.3. Unit Economics (dự kiến năm 2)

| Metric | Value |
|--------|-------|
| Avg Revenue Per User (ARPU) | $4.5/tháng |
| Customer Acquisition Cost (CAC) | $8 (organic + paid) |
| Lifetime Value (LTV) | $54 (12 tháng avg) |
| LTV/CAC ratio | 6.75 (rất tốt) |
| Gross margin | 78% (mostly infra cost) |
| Payback period | 1.8 tháng |

### 13.4. Funding Milestones

| Giai đoạn | Số tiền | Mục tiêu |
|-----------|---------|----------|
| **Pre-seed** | $50–100k | MVP + 1.000 user đầu tiên |
| **Seed** | $500k–1M | 50.000 user, 1 quốc gia |
| **Series A** | $3–5M | 500.000 user, 3 quốc gia, B2B pilot |
| **Series B** | $15–25M | 5M user, scale Đông Nam Á + Ấn Độ |

---

## 14. Phân tích Cạnh tranh

### 14.1. Direct Competitors

| Đối thủ | Mô hình | Điểm mạnh | Điểm yếu | SkillSeed hơn ở đâu |
|---------|---------|-----------|----------|---------------------|
| **Skillshare** | Subscription video course | Thư viện khổng lồ | Không tương tác 1-1, $200+/năm | AI matching, free |
| **Udemy** | Marketplace | Giá rẻ, đa dạng | Chất lượng không đều, không personal | Verifiable skill passport |
| **TimeRepublik** | Time banking | Miễn phí, peer-to-peer | UI cũ, matching thủ công, không video | Modern UX, AI, video tích hợp |
| **Simbi** | Time banking | Cộng đồng tốt | Niche (creative), ít user | Tech-forward, B2B |
| **ADPList** | Mentor 1-1 | Mentor chất lượng cao | Trả phí cao ($200-500/giờ) | Miễn phí cơ bản, đa dạng kỹ năng |
| **MentorCruise** | Mentor 1-1 | Domain-specific | Trả phí, focus tech only | Đa ngành, free tier |
| **Preply/italki** | Language learning | Nổi tiếng | Chỉ ngôn ngữ | Đa kỹ năng |
| **Facebook Groups** | Free community | Scale lớn | Không có hệ thống reputation | Quality control, AI |

### 14.2. Indirect Competitors (gần kề)

- **Discord/Slack communities**: free, nhưng thiếu matching & reputation
- **LinkedIn Learning**: B2B focus, content-only
- **Course platforms (Coursera, edX)**: chính quy, không peer-to-peer

### 14.3. Competitive Moat của SkillSeed

1. **Network effect** — mỗi user mới làm hệ thống tốt hơn
2. **Data flywheel** — càng nhiều session → matching càng chính xác
3. **Reputation moat** — Skill Passport rất khó fake
4. **Community moat** — Pod & mentor relationship khó replicate
5. **Tech moat** — AI matching + WebRTC + Blockchain tích hợp

---

## 15. Xu hướng thị trường hỗ trợ (2026–2030)

### 15.1. Megatrends thuận lợi

| Xu hướng | Tác động đến SkillSeed | Nguồn |
|----------|------------------------|-------|
| **AI democratization** (LLM mạnh, giá rẻ) | AI matching + co-pilot khả thi | McKinsey AI Report 2025 |
| **Loneliness epidemic** | Nhu cầu kết nối thật tăng | WHO 2025 |
| **Lifelong learning** | Mọi người cần học liên tục | WEF Future of Jobs |
| **Web3 verifiable credentials** | Soulbound Token được chấp nhận | Ethereum Foundation |
| **Time banking revival** | Mutual aid movement, degrowth | The Guardian 2024 |
| **Asia emerging market** | Smartphone cao, ngân sách thấp | GSMA Mobile Economy |
| **B2B upskilling crisis** | Doanh nghiệp tìm giải pháp rẻ | Deloitte 2025 |
| **Hybrid work culture** | Chấp nhận video call như bình thường | Gartner |
| **Green/sustainability movement** | Người dùng prefer company có tác động xã hội | Edelman Trust |

### 15.2. Risks from trends

- **AI commoditization** → matching có thể bị copy → cần data moat
- **Web3 backlash** → SBT có thể không được mainstream chấp nhận → có plan B (zk-proof off-chain)
- **Privacy regulation tightening** → cần invest vào compliance từ đầu

---

## 16. Lộ trình Phát triển (18–24 tháng)

### Phase 0: Validation (Tuần 1–4)

**Mục tiêu:** Xác nhận có đủ người muốn dùng

- [ ] Phỏng vấn 30 khách hàng tiềm năng (5 nhóm persona)
- [ ] Tạo Landing page đơn giản (Carrd hoặc Next.js) + waitlist
- [ ] Chạy quảng cáo Facebook/Google $200 → đo conversion rate
- [ ] Khảo sát: "Bạn sẵn sàng dạy miễn phí kỹ năng X?" / "Bạn sẵn sàng học miễn phí?"
- [ ] **Go/No-Go decision** dựa trên >40% interested

**Deliverable:** Validation report + 200+ waitlist email

### Phase 1: MVP (Tháng 2–4) — Solo Dev OK

**Tech:** Spring Boot + Next.js + Postgres + Redis + basic WebRTC

- [ ] User registration + email verification
- [ ] Profile + Skill DNA (form-based, không AI)
- [ ] Simple matching (filter-based, không AI)
- [ ] Booking flow (manual time selection)
- [ ] Video call (Daily.co embedded hoặc Jitsi)
- [ ] Rating form cơ bản (1 overall score)
- [ ] Seed wallet (in-memory + simple ledger)
- [ ] Deploy lên AWS Free Tier hoặc Railway/Render

**Deliverable:** MVP live với 50–100 beta users

**Team:** 1–2 người (Java dev + 1 frontend freelancer)

**Chi phí ước tính:** $500–1.500/tháng (infra + freelancer)

### Phase 2: AI & Polish (Tháng 5–8) — Nhỏ team

**Tech additions:** Spring AI, Qdrant, ElasticSearch

- [ ] AI matching engine v1 (embedding-based)
- [ ] Smart recommendations trên Discover page
- [ ] "Why we matched" explanations (LLM)
- [ ] In-session AI co-pilot (notes + action items)
- [ ] Mobile-responsive PWA (React Native deferred)
- [ ] Push notification (email + web push)
- [ ] Analytics dashboard (PostHog hoặc self-hosted)
- [ ] Nâng cấp verification: phone + selfie
- [ ] Marketing: SEO blog, 3 user stories/tháng

**Deliverable:** 1.000–5.000 MAU, 500+ sessions completed

**Team:** 3–4 người (Java dev + AI engineer + frontend + marketing)

**Chi phí:** $3.000–5.000/tháng

### Phase 3: Mobile + Scale (Tháng 9–14)

**Tech additions:** React Native, Kubernetes, Kafka

- [ ] Mobile app iOS + Android (React Native / Expo)
- [ ] Multi-modal session (code editor, whiteboard, AR)
- [ ] Learning Pods
- [ ] Skill Passport v1 (PDF + public link)
- [ ] B2B workspace MVP (basic dashboard)
- [ ] Partnership với 5 trường ĐH + 3 quỹ đào tạo
- [ ] Stripe integration cho Premium
- [ ] Multi-currency support (USD, VND, SGD, IDR, INR)
- [ ] AI Personality Insights (Premium feature)

**Deliverable:** 20.000–50.000 MAU, 1.000+ Premium subs, 5+ B2B clients

**Team:** 6–10 người (thêm DevOps, Designer, Sales B2B)

**Chi phí:** $15.000–30.000/tháng

**Raise:** Seed round $500k–1M

### Phase 4: Regional Expansion (Tháng 15–24)

- [ ] Blockchain SBT (Polygon) cho Skill Passport
- [ ] 3 quốc gia ngoài VN (Singapore, Indonesia, Philippines)
- [ ] B2B scale (50+ công ty)
- [ ] API public + partner integrations
- [ ] i18n đầy đủ (5 ngôn ngữ)
- [ ] AI matching v2 (deep personalization)

**Deliverable:** 200.000+ MAU, 5.000+ Premium, $1M+ ARR

**Raise:** Series A $3–5M

### Phase 5: Beyond (Năm 2–3)

- [ ] Voice-first AI matching
- [ ] AR/VR sessions (Meta Horizon, Apple Vision Pro)
- [ ] Offline community events
- [ ] M&A opportunities với EdTech khác
- [ ] IPO hoặc strategic exit

---

## 17. Rủi ro & Chiến lược giảm thiểu

### 17.1. Risk Matrix

| Rủi ro | Xác suất | Tác động | Mitigation |
|--------|----------|----------|------------|
| **Cold start problem** | Cao | Cao | Launch trong cộng đồng dày đặc (sinh viên, dev), partnership trường ĐH, founder-led seeding |
| **Safety/trust incidents** | Trung bình-Cao | Rất cao | Multi-tier verification, AI moderation, insurance, quick reporting, ban policy nghiêm |
| **Free riders** | Trung bình | Trung bình | Seed expiry, velocity check, AI anomaly detection, mandatory first session as learner |
| **Chất lượng buổi học không đều** | Cao | Cao | Multi-criteria rating, peer verification, AI session analysis, quality leaderboard |
| **Cạnh tranh từ EdTech lớn** | Trung bình | Trung bình | Network effect & community moat sớm, focus niche trước |
| **AI matching chưa đủ tốt** | Trung bình | Trung bình | A/B test liên tục, human-in-the-loop, fallback manual search |
| **Pháp lý (giáo dục tư nhân)** | Thấp-Trung bình | Trung bình | Consult lawyer từ tháng 3, không gọi "khóa học chính quy", tuân thủ quy định |
| **Privacy regulation** | Trung bình | Cao | Privacy-by-design từ đầu, DPO thuê từ Phase 3 |
| **Burn rate quá nhanh** | Trung bình | Cao | Lean team, bootstrap Phase 1–2, raise sau khi có traction |
| **Founder burnout** | Trung bình | Cao | Co-founder sớm, clear work-life boundaries |

### 17.2. Kill criteria

Dừng dự án nếu sau Phase 2 (tháng 8):
- <500 MAU
- <50 sessions completed
- <20% retention D7
- Không tìm được PMF signals trong user interviews

---

## 18. Kế hoạch Go-to-Market

### 18.1. Phase 1 — Founder-led (0–1.000 users)

**Channels:**
- Cá nhân founder chia sẻ trên LinkedIn, Facebook, Twitter
- Cold email tới 50 câu lạc bộ sinh viên, 20 coworking space
- Product Hunt launch
- Reddit: r/learnprogramming, r/languagelearning, r/careerguidance
- Seed community trong Slack/Discord của founder

**Content:**
- "Tại sao tôi bỏ [job hiện tại] để xây SkillSeed" (founder story)
- "5 kỹ năng tôi học được miễn phí trong 1 tháng qua SkillSeed"
- Video demo + tutorial

### 18.2. Phase 2 — Community-led (1.000–10.000 users)

**Channels:**
- Referral program: "Mời bạn → nhận 30 seeds"
- Partnership: trường ĐH, thư viện, NGO giáo dục
- Micro-influencers (5k–50k followers) trong niche: ngôn ngữ, lập trình, design
- TikTok/Reels: short clips "Tôi vừa học X miễn phí từ chuyên gia Ấn Độ"

**Content:**
- Case studies: "Từ 0 đến first job nhờ kỹ năng học trên SkillSeed"
- User-generated content: chia sẻ Skill Passport
- Podcast: phỏng vấn mentor & learner

### 18.3. Phase 3 — Paid + B2B (10.000+ users)

**B2C:**
- Google Ads (long-tail keywords: "học X miễn phí")
- Facebook/Instagram retargeting
- YouTube ads (educational content)

**B2B:**
- Outbound sales tới HR Director (LinkedIn Sales Navigator)
- Partnership với HR Tech platforms (BambooHR, Workday)
- Sponsor HR/People conferences
- Case study ROI cho mỗi vertical

### 18.4. Budget allocation (12 tháng đầu)

| Category | % Budget |
|----------|---------|
| Infrastructure & tools | 25% |
| Team salaries | 40% |
| Marketing & content | 20% |
| Legal & compliance | 5% |
| Reserve | 10% |

---

## 19. Đội ngũ & Kỹ năng cần tuyển

### 19.1. Phase 1 (Solo)

| Vai trò | Kỹ năng cần | Có thể tự làm? |
|---------|--------------|----------------|
| Full-stack Dev (bạn) | Java/Spring + basic React | ✅ |
| Designer (freelance) | Figma, UX research | Thuê $500–1.500/tháng |
| Marketing (part-time) | Content + community | Tự làm 50% + thuê 50% |

### 19.2. Phase 2 (3–4 người)

- Java Backend Dev (bạn) — focus AI integration
- AI/ML Engineer (Python) — recommendation system
- Frontend Dev (React/Next.js)
- Marketing/Growth (content + community)

### 19.3. Phase 3 (8–12 người) — thêm:

- Mobile Dev (React Native hoặc Flutter)
- DevOps/SRE (Kubernetes)
- UI/UX Designer full-time
- Product Manager
- B2B Sales (1–2 người)
- Customer Success

### 19.4. Advisors cần tìm

- Cựu founder EdTech (đặc biệt châu Á)
- HR/People leader ở công ty lớn
- Investor focus EdTech/future of work
- AI researcher (advisor cho matching algorithm)

---

## 20. KPI & Chỉ số Thành công

### 20.1. North Star Metric

> **Weekly Active Sessions Completed** — số buổi học hoàn thành trong tuần

### 20.2. Growth KPIs

| Metric | Target Phase 1 | Target Phase 2 | Target Phase 3 |
|--------|:---------------:|:---------------:|:---------------:|
| MAU | 1.000 | 20.000 | 200.000 |
| Weekly Sessions | 200 | 5.000 | 50.000 |
| D7 Retention | 25% | 35% | 45% |
| D30 Retention | 10% | 20% | 30% |
| Session/MAU/week | 0.2 | 0.25 | 0.25 |
| NPS | 30 | 50 | 60 |

### 20.3. Quality KPIs

| Metric | Target |
|--------|--------|
| Avg session rating | ≥ 4.3/5 |
| % sessions ≥ 4⭐ | > 75% |
| Show-up rate | > 90% |
| Cancellation rate | < 10% |
| Re-book rate | > 40% |
| Verified user % | > 60% |

### 20.4. Business KPIs

| Metric | Year 1 | Year 2 | Year 3 |
|--------|:------:|:------:|:------:|
| ARR | $30k | $500k | $5M |
| Premium subs | 200 | 5.000 | 30.000 |
| B2B clients | 0 | 10 | 50 |
| Gross margin | 60% | 75% | 80% |
| CAC | $15 | $10 | $8 |
| LTV | $30 | $80 | $150 |

---

## 21. Tài liệu tham khảo & Nghiên cứu tiếp

### 21.1. Bài học từ startup tương tự

- **Skillshare (US)** — marketplace model, scaling issues
- **Superprof (FR)** — 1-1 tutoring, đạt 10M+ users
- **Preply (US/UA)** — language focus, $120M+ raise
- **ADPList (SG)** — mentor platform, community-first
- **TimeRepublik (BR)** — time banking, UI limitations

### 21.2. Đọc thêm để nghiên cứu sâu

- *"The Future of Employment" — WEF 2025*
- *"Loneliness epidemic" — WHO 2025*
- *"Soulbound Tokens" — Buterin, Weyl, Ohlhaver (2022)*
- *"Time banks: a systematic review" — Journal of Social Economics*
- *"AI in Education" — UNESCO 2025 report*

### 21.3. Tools cần dùng ngay

- **Research:** Typeform (survey), UserTesting (interviews)
- **Design:** Figma, Whimsical (flow)
- **No-code prototype:** FlutterFlow, Glide
- **Project mgmt:** Notion, Linear
- **Analytics:** PostHog (self-host ok), Mixpanel

### 21.4. Templates & Boilerplates

- Spring Boot microservice starter: https://github.com/oktadev/okta-spring-boot
- Next.js + Supabase starter
- WebRTC sample: https://github.com/webrtc/samples

---

## Phụ lục A: MVP Tech Stack (Phase 1) — Recommended Setup

```yaml
# docker-compose.yml (rút gọn)
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: skillseed
      POSTGRES_USER: skillseed
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
  
  redis:
    image: redis:7-alpine
  
  backend:
    build: ./backend
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/skillseed
      SPRING_REDIS_HOST: redis
    ports:
      - "8080:8080"
  
  frontend:
    build: ./frontend
    ports:
      - "3000:3000"
    environment:
      NEXT_PUBLIC_API_URL: http://localhost:8080

volumes:
  pgdata:
```

## Phụ lục B: Glossary

| Thuật ngữ | Định nghĩa |
|-----------|------------|
| **Skill DNA** | Bản đồ năng lực đa chiều của user (skills + level + style + goals) |
| **Skill Seed** | Đơn vị giá trị nội bộ, kiếm được khi dạy, dùng để học |
| **Skill Passport** | Badge xác minh năng lực, có thể chia sẻ công khai |
| **Learning Pod** | Nhóm nhỏ 4–8 người cùng chủ đề |
| **Soulbound Token (SBT)** | NFT không thể chuyển nhượng, dùng để chứng minh credential |
| **Cold Start** | Vấn đề khi platform mới chưa có đủ user |
| **Network Effect** | Giá trị tăng theo số lượng user |
| **PMF** | Product-Market Fit — sản phẩm đáp ứng đúng nhu cầu thị trường |

---

## Phụ lục C: Next Steps cho Founder

### Tuần 1
- [ ] Đọc lại tài liệu này 2 lần
- [ ] List ra 30 người quen thuộc 5 nhóm persona để phỏng vấn
- [ ] Tạo Notion workspace cho dự án

### Tuần 2
- [ ] Phỏng vấn 5–10 người (30 phút/người)
- [ ] Tạo landing page + waitlist form
- [ ] Sketch wireframe MVP (Whimsical)

### Tuần 3
- [ ] Phỏng vấn tiếp 10–15 người
- [ ] Đăng ký domain + hosting
- [ ] Setup GitHub repo với monorepo (backend + frontend + docs)

### Tuần 4
- [ ] Quyết định Go/No-Go dựa trên data
- [ ] Nếu Go: bắt đầu code backend
- [ ] Tuyển 1 frontend freelancer
- [ ] Chia sẻ dự án lên LinkedIn để tạo accountability

---

> **Ghi chú cuối:** Tài liệu này là *living document*. Cập nhật mỗi 2 tuần dựa trên learnings từ user research và data thực tế. Đừng cố perfect ngay từ đầu — cứ launch, học, iterate.

> **Tác giả:** SkillSeed Product Team — Phiên bản 1.0 — 2026-09-06
