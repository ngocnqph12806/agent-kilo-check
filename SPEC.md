# SPEC: Hệ Thống Quản Lý Gia Phả (Family Tree Management System)

> Tên dự án: **GiaPhaOnline** — Nền tảng quản lý, lưu trữ và chia sẻ gia phả trực tuyến hiện đại.

---

## 1. Tổng quan dự án

### 1.1 Mục tiêu
Xây dựng một nền tảng web cho phép các gia đình Việt Nam số hóa và quản lý cây gia phả nhiều thế hệ, hỗ trợ:
- Lưu trữ thông tin chi tiết từng thành viên (họ tên, ngày sinh/mất, nghề nghiệp, ảnh, ghi chú tiểu sử...).
- Vẽ và trực quan hóa cây gia phả (nhiều thế hệ, nhiều chi nhánh).
- Quản lý quan hệ huyết thống: cha–mẹ–con, vợ–chồng, anh–chị–em, ông bà...
- Phân quyền: quản trị viên dòng họ, thành viên đóng góp, khách xem công khai.
- Tìm kiếm nâng cao, lọc theo thế hệ/chi nhánh.
- Xuất bản PDF, chia sẻ qua link, nhúng vào website khác.
- Thông báo sự kiện: giỗ, kỷ niệm ngày mất, sinh nhật.
- Đa ngôn ngữ (Việt/Anh), đa giao diện sáng/tối.

### 1.2 Phạm vi
- **Frontend:** Next.js 14+ (App Router, RSC, Server Actions), TypeScript, Tailwind CSS, shadcn/ui, TanStack Query, Zustand, react-flow cho cây gia phả.
- **Backend:** Spring Boot 3.x, Java 21, Spring Web, Spring Security (JWT + OAuth2), Spring Data JPA, Hibernate, Flyway migration, MapStruct, Lombok, OpenAPI 3 (springdoc-openapi).
- **Database:** PostgreSQL 16, lưu ảnh trên Object Storage (S3/MinIO) hoặc local volume.
- **DevOps:** Docker Compose, GitHub Actions CI/CD, Nginx reverse proxy.
- **Triển khai:** Hỗ trợ cả self-hosted lẫn cloud (AWS/GCP/Azure).

---

## 2. Kiến trúc hệ thống

### 2.1 Sơ đồ tổng quan
```
┌─────────────────────────────────────────────────────────────┐
│                      Next.js (FE)                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │  App Router  │  │  shadcn/ui   │  │  React Flow Tree │  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
└────────────────────────┬────────────────────────────────────┘
                         │  REST API + JWT
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Boot (BE)                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────────┐ │
│  │  Auth    │  │ Person   │  │ Family   │  │  Media/OCR  │ │
│  │ Service  │  │ Service  │  │ Service  │  │   Service   │ │
│  └──────────┘  └──────────┘  └──────────┘  └─────────────┘ │
└────────────────────────┬────────────────────────────────────┘
                         │  JDBC
                         ▼
              ┌──────────────────────┐
              │   PostgreSQL 16      │
              │  + MinIO (Storage)   │
              │  + Redis (Cache)     │
              └──────────────────────┘
```

### 2.2 Cấu trúc monorepo
```
giaphaonline/
├── apps/
│   ├── web/                    # Next.js
│   └── api/                    # Spring Boot
├── packages/
│   ├── shared-types/           # OpenAPI generated types
│   └── eslint-config/
├── docker/
│   ├── docker-compose.yml
│   └── nginx/
├── .github/workflows/
├── docs/
└── README.md
```

---

## 3. Mô hình dữ liệu (Database Schema)

### 3.1 Sơ đồ ER (rút gọn)
- `users` — tài khoản hệ thống
- `families` — một dòng họ/cây gia phả
- `family_members` — quan hệ user ↔ family (role: OWNER/EDITOR/VIEWER)
- `persons` — cá nhân trong cây gia phả
- `relationships` — quan hệ giữa persons (SPOUSE, PARENT_CHILD, SIBLING)
- `events` — sự kiện đời người (sinh, mất, kết hôn, giỗ...)
- `media_assets` — ảnh/tài liệu
- `person_media` — gắn media với person
- `notifications` — thông báo sự kiện sắp tới
- `audit_logs` — lịch sử chỉnh sửa
- `comments` — bình luận trên một person

### 3.2 Bảng chính (DDL tóm tắt)

```sql
-- Users
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255),
  full_name VARCHAR(255),
  avatar_url TEXT,
  oauth_provider VARCHAR(50),
  oauth_provider_id VARCHAR(255),
  locale VARCHAR(10) DEFAULT 'vi',
  theme VARCHAR(20) DEFAULT 'system',
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);

-- Families (root trees)
CREATE TABLE families (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug VARCHAR(120) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  origin_location VARCHAR(255),
  founder_id UUID REFERENCES persons(id),
  visibility VARCHAR(20) DEFAULT 'PRIVATE', -- PUBLIC, UNLISTED, PRIVATE
  cover_image_url TEXT,
  created_by UUID NOT NULL REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);

-- Persons (individuals in tree)
CREATE TABLE persons (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  given_name VARCHAR(120) NOT NULL,
  middle_name VARCHAR(120),
  surname VARCHAR(120),
  alias VARCHAR(120),                -- tên khác / tên hiệu
  gender VARCHAR(10),                -- MALE, FEMALE, OTHER, UNKNOWN
  birth_date DATE,
  birth_date_lunar BOOLEAN DEFAULT FALSE,
  death_date DATE,
  death_date_lunar BOOLEAN DEFAULT FALSE,
  is_living BOOLEAN DEFAULT TRUE,
  birth_place VARCHAR(255),
  death_place VARCHAR(255),
  occupation VARCHAR(255),
  education TEXT,
  biography TEXT,
  avatar_media_id UUID REFERENCES media_assets(id),
  generation INT,                   -- đời thứ (1, 2, 3...)
  branch VARCHAR(120),               -- chi (chi 1, chi 2...)
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);

-- Relationships
CREATE TABLE relationships (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  type VARCHAR(30) NOT NULL,         -- SPOUSE, PARENT_CHILD, SIBLING, ADOPTED, GODPARENT
  person_a_id UUID NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
  person_b_id UUID NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
  start_date DATE,                   -- ngày cưới / ngày nhận nuôi
  end_date DATE,
  notes TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- Events
CREATE TABLE events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  person_id UUID NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
  type VARCHAR(40) NOT NULL,         -- BIRTH, DEATH, MARRIAGE, FUNERAL, ACHIEVEMENT, CUSTOM
  title VARCHAR(255),
  description TEXT,
  event_date DATE,
  event_date_lunar BOOLEAN DEFAULT FALSE,
  location VARCHAR(255),
  media_id UUID REFERENCES media_assets(id)
);

-- Media assets
CREATE TABLE media_assets (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  family_id UUID REFERENCES families(id) ON DELETE CASCADE,
  uploader_id UUID REFERENCES users(id),
  storage_key VARCHAR(500) NOT NULL, -- MinIO/S3 key
  url TEXT,
  mime_type VARCHAR(100),
  width INT,
  height INT,
  size_bytes BIGINT,
  caption TEXT,
  taken_at DATE,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- Family memberships & roles
CREATE TABLE family_members (
  family_id UUID REFERENCES families(id) ON DELETE CASCADE,
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  role VARCHAR(20) NOT NULL,         -- OWNER, EDITOR, CONTRIBUTOR, VIEWER
  joined_at TIMESTAMPTZ DEFAULT now(),
  PRIMARY KEY (family_id, user_id)
);

-- Notifications
CREATE TABLE notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  type VARCHAR(50) NOT NULL,         -- DEATH_ANNIVERSARY, BIRTHDAY, INVITE, EDIT_REQUEST
  payload JSONB,
  scheduled_for TIMESTAMPTZ,
  sent_at TIMESTAMPTZ,
  read_at TIMESTAMPTZ
);

-- Audit log
CREATE TABLE audit_logs (
  id BIGSERIAL PRIMARY KEY,
  actor_id UUID REFERENCES users(id),
  family_id UUID REFERENCES families(id),
  entity_type VARCHAR(50),
  entity_id UUID,
  action VARCHAR(50),                -- CREATE, UPDATE, DELETE
  changes JSONB,
  created_at TIMESTAMPTZ DEFAULT now()
);
```

### 3.3 Index quan trọng
- `persons(family_id, surname, given_name)`
- `persons(family_id, generation)`
- `relationships(family_id, person_a_id)`, `relationships(family_id, person_b_id)`
- `events(person_id, event_date)`
- Full-text search trên `persons(biography, occupation)` dùng `tsvector`.

---

## 4. Tính năng chi tiết (Functional Requirements)

### 4.1 Module Xác thực & Phân quyền
- Đăng ký/Đăng nhập bằng email + mật khẩu (BCrypt), OAuth2 (Google, Facebook).
- JWT access token + refresh token (xoay vòng, blacklist khi đăng xuất).
- Quên mật khẩu qua email token.
- Phân quyền theo role trên từng `family`:
  - **OWNER**: toàn quyền, xóa family, mời thành viên.
  - **EDITOR**: CRUD persons, relationships, events.
  - **CONTRIBUTOR**: gửi yêu cầu chỉnh sửa, đính kèm ảnh, chờ duyệt.
  - **VIEWER**: chỉ xem.
- Bảo vệ bằng Spring Security với `@PreAuthorize` và policy trong DB.

### 4.2 Module Quản lý Cây Gia Phả (Family Tree)
- Tạo nhiều family (mỗi user có thể quản lý nhiều dòng họ).
- Cây gia phả trực quan dùng **React Flow**:
  - Node tùy biến (avatar, họ tên, năm sinh–mất, icon giới tính).
  - Edge theo quan hệ cha–con, vợ–chồng (màu khác nhau).
  - Pan/zoom, mini-map, layout auto (dùng `dagre`/`elkjs`).
  - Filter theo chi/branches, generation, giới tính, người đã mất/còn sống.
  - Xuất PNG/SVG toàn bộ cây hoặc từng phần.
- Hỗ trợ nhiều view: **Vertical (tiền nhân trên đỉnh)**, **Horizontal**, **Radial**.
- Add person qua modal hoặc double-click vào edge.

### 4.3 Module Quản lý Cá Nhân (Person)
- CRUD hồ sơ chi tiết, bao gồm cả ngày âm lịch (chuyển đổi dương↔âm).
- Upload nhiều ảnh + OCR tiếng Việt để trích xuất thông tin từ giấy khai sinh/CMND (tùy chọn, dùng Tesseract/Vision API).
- Timeline sự kiện đời người.
- Ghi chú tiểu sử dạng rich-text (Markdown/TipTap).
- Liên kết giữa các persons (cha, mẹ, anh chị em...).
- Lịch sử thay đổi (audit log per person).

### 4.4 Module Sự Kiện & Lịch
- Lịch âm/dương hiển thị: sinh nhật, ngày giỗ, kỷ niệm cưới.
- Cron job (Spring `@Scheduled`) mỗi ngày:
  - Tạo `notifications` cho thành viên family.
- Gửi email thông báo trước 7 ngày và trong ngày.
- Tạo sự kiện gia đình (họp họ, giỗ tổng) chia sẻ cho cả family.

### 4.5 Module Tìm Kiếm & Lọc
- Tìm theo tên, năm sinh, nghề, địa phương.
- Full-text search (`pg_trgm` + `to_tsvector('simple', ...)`).
- Lọc nâng cao: theo thế hệ, chi, giới tính, còn sống/đã mất.
- Gợi ý quan hệ huyết thống (suggest parent/child/sibling dựa trên độ tuổi).

### 4.6 Module Media
- Upload ảnh/video lên MinIO/S3.
- Tự resize sinh thumbnail (300px, 800px, 1920px) dùng **imagemagick/Java ImageIO**.
- Album theo từng person hoặc toàn family.
- Hỗ trợ EXIF (GPS, taken date) lưu vào `media_assets`.
- Quyền media kế thừa từ family visibility.

### 4.7 Module Xuất bản & Chia sẻ
- Xuất PDF cây gia phả (dùng **OpenPDF** hoặc **iText** + headless Chrome cho SVG→PDF).
- Xuất GEDCOM (.ged) để import vào các phần mềm gia phả khác (Ancestry, Gramps...).
- Chia sẻ qua link public với token:
  - `UNLISTED`: cần link mới xem được.
  - `PUBLIC`: có thể index bởi Google.
  - `PRIVATE`: chỉ thành viên.
- Nhúng (embed) iframe vào website khác.

### 4.8 Module Bình luận & Tương tác
- Bình luận trên trang person (Markdown, mention `@user`).
- Reaction (❤️, 🙏, 🎉).
- Bài viết/blog dòng họ (kỷ yếu, truyền thống) đăng kèm ảnh.

### 4.9 Module Thông báo
- In-app realtime qua WebSocket (Spring + STOMP).
- Email queue dùng **Redis** + worker.
- Push notification (tùy chọn Firebase FCM).

### 4.10 Module Quản trị
- Dashboard cho OWNER: thống kê số thành viên, ảnh, lượt truy cập.
- Backup/restore database (nút bấm hoặc cron).
- Xuất toàn bộ family ra JSON/CSV.

### 4.11 Đa ngôn ngữ & Theme
- i18n với `next-intl` (vi/en).
- Dark/Light/System theme; lưu vào `users.theme`.

---

## 5. API Specification (REST + OpenAPI)

> Base URL: `/api/v1`. Tất cả endpoint (trừ auth public) yêu cầu `Authorization: Bearer <JWT>`.

### 5.1 Auth
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/auth/register` | Đăng ký |
| POST | `/auth/login` | Đăng nhập |
| POST | `/auth/refresh` | Làm mới token |
| POST | `/auth/logout` | Đăng xuất |
| POST | `/auth/forgot-password` | Yêu cầu reset |
| POST | `/auth/reset-password` | Đặt lại mật khẩu |
| GET  | `/auth/oauth2/{provider}` | OAuth2 redirect |

### 5.2 Families
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/families` | Danh sách family của user |
| POST | `/families` | Tạo family |
| GET | `/families/{slug}` | Chi tiết |
| PATCH | `/families/{slug}` | Cập nhật |
| DELETE | `/families/{slug}` | Xóa |
| POST | `/families/{slug}/members` | Mời thành viên |
| PATCH | `/families/{slug}/members/{userId}` | Đổi role |
| DELETE | `/families/{slug}/members/{userId}` | Xóa thành viên |

### 5.3 Persons
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/families/{slug}/persons` | Danh sách (filter, page) |
| POST | `/families/{slug}/persons` | Tạo |
| GET | `/persons/{id}` | Chi tiết |
| PATCH | `/persons/{id}` | Cập nhật |
| DELETE | `/persons/{id}` | Xóa (soft) |
| GET | `/persons/{id}/timeline` | Timeline sự kiện |
| GET | `/persons/{id}/relations` | Quan hệ |

### 5.4 Relationships
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/families/{slug}/relationships` | Tạo quan hệ |
| DELETE | `/relationships/{id}` | Xóa |

### 5.5 Events
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/persons/{id}/events` | Danh sách sự kiện của person |
| POST | `/persons/{id}/events` | Tạo |
| PATCH | `/events/{id}` | Cập nhật |
| DELETE | `/events/{id}` | Xóa |

### 5.6 Media
| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/families/{slug}/media/upload` | Upload (multipart) |
| GET | `/media/{id}` | Lấy thông tin |
| DELETE | `/media/{id}` | Xóa |

### 5.7 Search
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/families/{slug}/search?q=&generation=&gender=&branch=&living=` | Tìm kiếm |

### 5.8 Export
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/families/{slug}/export/pdf` | Xuất PDF |
| GET | `/families/{slug}/export/gedcom` | Xuất GEDCOM |
| GET | `/families/{slug}/export/json` | Xuất JSON |

### 5.9 Notifications
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/notifications` | Danh sách |
| POST | `/notifications/{id}/read` | Đánh dấu đã đọc |
| WS | `/ws/notifications` | Realtime |

### 5.10 Admin
| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/admin/audit-logs?familyId=...` | Lịch sử |
| POST | `/admin/backup` | Tạo backup |
| POST | `/admin/restore` | Khôi phục |

---

## 6. Thiết kế UI/UX

### 6.1 Bố cục chính
- **Sidebar trái**: navigation (Dashboard, Trees, Calendar, Media, Settings).
- **Topbar**: search toàn cục, theme switcher, locale, avatar menu.
- **Main content**: dùng grid `12 cột`, gap `gap-4` trên `lg`, `gap-2` trên mobile.

### 6.2 Các trang chính
1. **Landing** (`/`) — Hero, tính năng, CTA đăng ký.
2. **Dashboard** (`/dashboard`) — danh sách gia phả, hoạt động gần đây.
3. **Family Tree** (`/families/[slug]/tree`) — React Flow canvas.
4. **Person Detail** (`/persons/[id]`) — ảnh đại diện, tiểu sử, timeline, quan hệ, bình luận.
5. **Members** (`/families/[slug]/members`) — bảng thành viên + role.
6. **Calendar** (`/calendar`) — lịch âm/dương sự kiện.
7. **Media Library** (`/media`) — album, gallery masonry.
8. **Search** (`/search`) — kết quả có filter chips.
9. **Settings** (`/settings`) — profile, security, notifications.
10. **Public Tree** (`/p/[slug]`) — view-only cho khách.

### 6.3 Design tokens
- Font: `Inter` (UI), `Be Vietnam Pro` (tiếng Việt đẹp hơn).
- Bảng màu (Tailwind extended):
  - Primary: `#B45309` (gỗ trầm, mang phong cách cổ điển).
  - Accent: `#FCD34D` (vàng gỗ).
  - Background light: `#FAF7F2`, dark: `#1A1815`.
- Border radius: `rounded-xl` cho card, `rounded-full` cho avatar.
- Animation: framer-motion cho chuyển trang.

### 6.4 Accessibility
- Keyboard navigation đầy đủ cho React Flow.
- Contrast AA cho mọi text.
- ARIA labels cho icon buttons.
- Hỗ trợ screen reader cho timeline.

---

## 7. Tech Stack chi tiết

### 7.1 Frontend
| Hạng mục | Công nghệ |
|---|---|
| Framework | Next.js 14+ (App Router, RSC) |
| Ngôn ngữ | TypeScript 5.x (strict mode) |
| UI Library | shadcn/ui (Radix + Tailwind) |
| Styling | Tailwind CSS 3.x |
| State server | TanStack Query v5 |
| State client | Zustand |
| Form | React Hook Form + Zod |
| Tree viz | @xyflow/react (React Flow) |
| Charts | Recharts |
| Calendar | FullCalendar hoặc custom |
| i18n | next-intl |
| Auth fetch | axios + interceptors |
| Markdown | @uiw/react-md-editor |
| Testing | Vitest + React Testing Library + Playwright |
| Lint/Format | ESLint, Prettier, lint-staged |

### 7.2 Backend
| Hạng mục | Công nghệ |
|---|---|
| Framework | Spring Boot 3.3.x |
| Ngôn ngữ | Java 21 |
| ORM | Spring Data JPA + Hibernate |
| Migration | Flyway |
| Validation | Jakarta Validation (Hibernate Validator) |
| Mapping | MapStruct |
| Boilerplate reduction | Lombok |
| Security | Spring Security 6 + JWT (jjwt) + OAuth2 client |
| API docs | springdoc-openapi (Swagger UI) |
| Storage | MinIO Java SDK |
| Email | Spring Mail + Thymeleaf templates |
| Cache | Spring Cache + Redis |
| Realtime | Spring WebSocket + STOMP |
| Scheduling | Spring Scheduling (Quartz nếu cần cluster) |
| Logging | Logback + JSON (Logstash encoder) |
| Testing | JUnit 5, Mockito, Testcontainers, RestAssured |
| Build | Gradle (Kotlin DSL) hoặc Maven |

### 7.3 Infrastructure
| Hạng mục | Công nghệ |
|---|---|
| Database | PostgreSQL 16 (extensions: `pg_trgm`, `pgcrypto`, `uuid-ossp`) |
| Object Storage | MinIO (self-host) hoặc AWS S3 |
| Cache | Redis 7 |
| Reverse Proxy | Nginx + Let's Encrypt |
| CI/CD | GitHub Actions |
| Container | Docker + Docker Compose |
| Monitoring | Prometheus + Grafana, Loki, Sentry |
| Search (tuỳ chọn) | OpenSearch cho full-text nâng cao |

---

## 8. Cấu trúc thư mục

### 8.1 Backend (apps/api)
```
src/main/java/com/giaphaonline/api/
├── GiaphaOnlineApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   ├── OpenApiConfig.java
│   ├── CorsConfig.java
│   └── RedisConfig.java
├── auth/
│   ├── controller/
│   ├── service/
│   ├── dto/
│   └── security/
├── user/
├── family/
├── person/
├── relationship/
├── event/
├── media/
├── notification/
├── audit/
├── common/
│   ├── exception/
│   ├── response/
│   ├── util/
│   └── base/
└── scheduler/

src/main/resources/
├── application.yml
├── application-dev.yml
├── application-prod.yml
├── db/migration/V1__init.sql, V2__seed.sql, ...
└── templates/
    ├── email/welcome.html
    └── pdf/tree.html
```

### 8.2 Frontend (apps/web)
```
src/
├── app/
│   ├── (auth)/login, register
│   ├── (dashboard)/dashboard, families, calendar, media, settings
│   ├── families/[slug]/tree, members, search
│   ├── persons/[id]
│   ├── p/[slug] (public)
│   └── api/ (BFF nếu cần)
├── components/
│   ├── ui/         # shadcn primitives
│   ├── tree/       # React Flow nodes, edges
│   ├── person/
│   ├── forms/
│   └── layout/
├── hooks/
├── lib/
│   ├── api/        # axios + react-query wrappers
│   ├── auth/
│   └── utils/
├── store/          # zustand
├── messages/       # i18n vi/en
├── styles/
└── types/          # API types (từ OpenAPI)
```

---

## 9. Bảo mật & Tuân thủ
- HTTPS bắt buộc (HSTS).
- Password policy: ≥ 10 ký tự, có chữ hoa, số, ký tự đặc biệt.
- Rate limiting (Bucket4j) cho `/auth/*`.
- CSRF protection cho form.
- Helmet header cho Spring (Strict-Transport-Security, X-Content-Type-Options, X-Frame-Options).
- Audit log mọi thao tác write trên `persons/relationships`.
- Soft-delete tất cả entity có thể khôi phục.
- Không log PII (email, phone) ở level INFO+; mask trong log.

---

## 10. Hiệu năng & Khả năng mở rộng
- Cache Redis cho: tổng quan family, dashboard stats.
- Pagination cursor-based cho danh sách persons (mặc định 20/page).
- React Flow lazy-render node ngoài viewport.
- Index DB đầy đủ (xem mục 3.3).
- Ảnh dùng CDN (Cloudflare) + `loading="lazy"` + `next/image`.
- BE chạy stateless, scale ngang bằng nhiều instance sau Nginx.
- Tách job nặng (PDF export, backup) ra queue (RabbitMQ/Kafka) nếu cần.

---

## 11. Kế hoạch triển khai (Roadmap)

| Sprint | Thời lượng | Nội dung |
|---|---|---|
| 0 | 1 tuần | Khởi tạo monorepo, CI, Docker Compose, skeleton FE/BE |
| 1 | 2 tuần | Auth (email + OAuth2), user, family CRUD, phân quyền cơ bản |
| 2 | 2 tuần | Person CRUD, relationship CRUD, upload ảnh, media service |
| 3 | 2 tuần | React Flow tree visualization, layout, filter, search cơ bản |
| 4 | 1.5 tuần | Events, calendar âm/dương, notification scheduler + email |
| 5 | 1.5 tuần | Export PDF/GEDCOM, public share, embed |
| 6 | 1.5 tuần | Bình luận, audit log, dashboard thống kê |
| 7 | 1 tuần | i18n, theme, polish UI/UX, accessibility pass |
| 8 | 1 tuần | Hardening: security audit, perf test, backup/restore, docs |
| 9 | 1 tuần | Beta test, fix bug, release v1.0 |

Tổng thời gian dự kiến: **~12 tuần** với 1 FE + 1 BE full-time.

---

## 12. Tiêu chí chấp nhận (Acceptance Criteria)
- ✅ Đăng ký/đăng nhập/refresh/logout hoạt động; JWT an toàn.
- ✅ Tạo được family, mời thành viên, phân quyền theo role.
- ✅ Thêm/sửa/xóa person, tạo quan hệ cha–mẹ–con, vợ–chồng đúng.
- ✅ Cây gia phả render đúng quan hệ, hỗ trợ filter và 3 chế độ hiển thị.
- ✅ Upload ảnh, resize thumbnail, gắn vào person.
- ✅ Lịch âm/dương hiển thị đúng sự kiện, gửi email thông báo trước 7 ngày.
- ✅ Tìm kiếm full-text trả kết quả trong <300ms với dataset 10k persons.
- ✅ Xuất PDF, GEDCOM, JSON đúng chuẩn, mở được bằng phần mềm khác.
- ✅ Giao diện responsive ≥ 360px, dark/light, vi/en.
- ✅ Test coverage BE ≥ 70%, FE ≥ 60%, e2E Playwright cho flow chính.
- ✅ Docker Compose chạy 1 lệnh `docker compose up` lên full stack.
- ✅ CI chạy build + test + lint tự động, không cho merge khi fail.

---

## 13. Phụ lục

### 13.1 Schema quan hệ nhanh
- 1 Family ↔ N Persons
- 1 Person ↔ N Relationships (với n Persons khác)
- 1 Person ↔ N Events
- 1 Person ↔ N Media
- 1 Family ↔ N Users (qua `family_members`)

### 13.2 Ví dụ request/response (OpenAPI snippet)
```yaml
/api/v1/families/{slug}/persons:
  post:
    summary: Tạo person mới
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/PersonCreateRequest'
    responses:
      '201':
        description: Tạo thành công
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PersonResponse'
      '403': { $ref: '#/components/responses/Forbidden' }
```

### 13.3 ERD (PlantUML/DBML — dùng cho tài liệu)
```dbml
Table families {
  id uuid [pk]
  slug varchar [unique]
  name varchar
  visibility varchar
}
Table persons {
  id uuid [pk]
  family_id uuid [ref: > families.id]
  given_name varchar
  generation int
}
Table relationships {
  id uuid [pk]
  family_id uuid [ref: > families.id]
  person_a_id uuid [ref: > persons.id]
  person_b_id uuid [ref: > persons.id]
  type varchar
}
Table events {
  id uuid [pk]
  person_id uuid [ref: > persons.id]
  type varchar
  event_date date
}
Table media_assets {
  id uuid [pk]
  family_id uuid [ref: > families.id]
  storage_key varchar
}
```

### 13.4 File cấu hình mẫu
- `apps/api/src/main/resources/application.yml`
- `apps/web/.env.example`
- `docker-compose.yml`
- `.github/workflows/ci.yml`

> Tài liệu này là **living document**; cập nhật mỗi sprint theo thay đổi thực tế.
