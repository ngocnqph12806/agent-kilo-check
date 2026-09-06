# 00 — Foundation

> Khởi tạo monorepo, Docker Compose, CI, skeleton FE/BE, Flyway baseline.

## Requirements (EARS)

### REQ-00-01 — Monorepo Layout
- **REQ-00-01-01** WHEN người dùng clone repo THE hệ thống SHALL cung cấp cấu trúc `apps/web`, `apps/api`, `docker/`, `docs/`, `.github/`.
- **REQ-00-01-02** WHEN chạy `pnpm install` (root) THE hệ thống SHALL cài dependencies cho cả web và api qua workspaces.
- **REQ-00-01-03** WHERE workspace shared THE hệ thống SHALL có package `packages/shared-types` sinh tự động từ OpenAPI của BE.

### REQ-00-02 — Docker Compose
- **REQ-00-02-01** WHEN chạy `docker compose up -d` THE hệ thống SHALL khởi động `postgres`, `redis`, `minio`, `minio-init`, `api`, `web`, `nginx` đầy đủ.
- **REQ-00-02-02** WHEN postgres khởi động lần đầu THE hệ thống SHALL tự tạo database `giapha`, role `giapha` với password từ `.env`.
- **REQ-00-02-03** WHEN minio-init chạy THE hệ thống SHALL tự tạo bucket `family-media` và set policy `download` cho objects public.
- **REQ-00-02-04** WHILE api chưa healthy THE nginx SHALL trả 502 với custom error page.

### REQ-00-03 — Backend Skeleton
- **REQ-00-03-01** WHEN BE start THE hệ thống SHALL expose `/api/v1/actuator/health` trả 200 OK khi DB & Redis reachable.
- **REQ-00-03-02** WHEN BE start THE hệ thống SHALL tự chạy Flyway migrations từ `db/migration`.
- **REQ-00-03-03** WHEN truy cập `/swagger-ui.html` THE hệ thống SHALL hiển thị OpenAPI 3 docs.
- **REQ-00-03-04** IF exception không xử lý được xảy ra THEN THE hệ thống SHALL trả response chuẩn `{ code, message, traceId }` và log stacktrace với traceId.

### REQ-00-04 — Frontend Skeleton
- **REQ-00-04-01** WHEN truy cập `/` THE hệ thống SHALL render landing page responsive với theme mặc định `system`.
- **REQ-00-04-02** WHEN user chưa đăng nhập truy cập route `(dashboard)` THE hệ thống SHALL redirect về `/login`.
- **REQ-00-04-03** WHEN người dùng đổi theme THE hệ thống SHALL persist vào `localStorage` và áp dụng không reload.
- **REQ-00-04-04** WHEN người dùng đổi ngôn ngữ THE hệ thống SHALL persist vào cookie `NEXT_LOCALE`.

### REQ-00-05 — CI/CD
- **REQ-00-05-01** WHEN push PR THE GitHub Actions SHALL chạy: lint (BE & FE), typecheck, unit test, build.
- **REQ-00-05-02** IF CI fail THEN THE hệ thống SHALL chặn merge vào `main`.
- **REQ-00-05-03** WHEN merge vào `main` THE GitHub Actions SHALL build Docker images và push lên GHCR với tag `latest` và `sha`.

### REQ-00-06 — Observability
- **REQ-00-06-01** WHERE BE chạy production THE hệ thống SHALL expose Prometheus metrics tại `/actuator/prometheus`.
- **REQ-00-06-02** WHEN log ghi THE hệ thống SHALL dùng JSON format gồm `timestamp, level, service, traceId, message, mdc`.

## Design

### Tech
- Monorepo: `pnpm` workspaces cho FE; BE độc lập Gradle.
- BE: Spring Boot 3.3, Java 21, Gradle Kotlin DSL.
- FE: Next.js 14 (App Router), TypeScript 5 strict, Tailwind, shadcn/ui.
- Infra local: Docker Compose với profiles `core`, `observability`.

### Project tree
```
apps/
  api/
    build.gradle.kts
    Dockerfile
    src/main/java/com/giaphaonline/api/GiaphaOnlineApplication.java
    src/main/resources/{application.yml, db/migration/}
  web/
    package.json
    next.config.mjs
    Dockerfile
    src/app/{layout.tsx, page.tsx, (auth)/, (dashboard)/}
docker/
  docker-compose.yml
  docker-compose.observability.yml
  nginx/nginx.conf
  minio/init.sh
packages/
  shared-types/
.github/workflows/{ci.yml, release.yml}
```

### Ports (default)
| Service | Port | Host |
|---|---|---|
| Web (Next.js dev) | 3000 | localhost |
| API | 8080 | api.localhost |
| Postgres | 5432 | localhost |
| Redis | 6379 | localhost |
| MinIO API | 9000 | localhost |
| MinIO Console | 9001 | localhost |
| Nginx (prod) | 80/443 | localhost |

### BE package layout
```
com.giaphaonline.api
├── GiaphaOnlineApplication
├── config.{SecurityConfig, JwtConfig, OpenApiConfig, CorsConfig, RedisConfig, ObservabilityConfig}
├── common.exception.GlobalExceptionHandler, BusinessException, NotFoundException
├── common.response.ApiResponse, PageResponse
├── common.util.SlugGenerator
└── (các module nghiệp vụ sẽ thêm ở epic sau)
```

### FE layout
```
src/
├── app/layout.tsx (ThemeProvider, NextIntlProvider, QueryProvider, AuthProvider)
├── app/page.tsx (Landing)
├── app/(auth)/login, register
├── app/(dashboard)/layout.tsx (Sidebar + Topbar)
├── components/ui/ (shadcn)
├── lib/{api/client.ts, queryClient.ts, auth/store.ts}
├── messages/{vi.json, en.json}
└── styles/globals.css
```

## Tasks

- [ ] Tạo root `package.json` (pnpm workspaces) + `.npmrc`.
- [ ] Tạo `.gitignore`, `.editorconfig`, `.nvmrc`, `.java-version`, `AGENTS.md`.
- [ ] Tạo skeleton `apps/api` (Gradle, Main class, application.yml).
- [ ] Tạo skeleton `apps/web` (Next.js, Tailwind, shadcn/ui init).
- [ ] Tạo `docker/docker-compose.yml` với 6 services core.
- [ ] Tạo `docker/nginx/nginx.conf` reverse proxy `/api` → api:8080.
- [ ] Tạo `docker/minio/init.sh` tạo bucket.
- [ ] Tạo Flyway baseline `V1__init_extensions.sql` (uuid-ossp, pgcrypto, pg_trgm).
- [ ] Cấu hình springdoc-openapi + Swagger UI security scheme JWT.
- [ ] Cấu hình GlobalExceptionHandler + ApiResponse wrapper.
- [ ] Cấu hình Prometheus actuator.
- [ ] Cấu hình Logback JSON encoder + MDC traceId filter.
- [ ] Tạo `.github/workflows/ci.yml` (lint, test, build matrix).
- [ ] Tạo `.github/workflows/release.yml` (build & push images).
- [ ] Viết `README.md` và `SPEC.md` (đã có).
- [ ] Verify `docker compose up` chạy thành công; Swagger + Landing OK.
