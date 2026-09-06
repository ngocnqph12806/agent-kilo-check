# 00 — Foundation · Requirements

> EARS-format requirements cho epic Foundation.

## Glossary
- **System** = toàn bộ monorepo `GiaPhaOnline` gồm FE, BE, Docker, CI.
- **Healthy** = service trả HTTP 200 tại health endpoint trong thời gian quy định.

---

### REQ-00-01 — Monorepo Layout
- **REQ-00-01-01** WHEN người dùng clone repo THE hệ thống SHALL cung cấp cấu trúc `apps/web`, `apps/api`, `docker/`, `docs/`, `.github/`, `packages/shared-types/`.
- **REQ-00-01-02** WHEN chạy `pnpm install` tại root THE hệ thống SHALL cài dependencies cho tất cả workspaces.
- **REQ-00-01-03** WHERE package `shared-types` tồn tại THE hệ thống SHALL tự động generate TypeScript types từ OpenAPI của BE qua script `build`.
- **REQ-00-01-04** IF thiếu file `.env` THEN THE hệ thống SHALL dùng `.env.example` làm fallback và log warning.

### REQ-00-02 — Docker Compose Stack
- **REQ-00-02-01** WHEN chạy `docker compose -f docker/docker-compose.yml up -d` THE hệ thống SHALL khởi động đầy đủ `postgres`, `redis`, `minio`, `minio-init`, `api`, `web`, `nginx`.
- **REQ-00-02-02** WHEN postgres khởi động lần đầu THE hệ thống SHALL tạo database `giapha`, user `giapha` với password lấy từ biến môi trường.
- **REQ-00-02-03** WHEN minio-init chạy THE hệ thống SHALL tạo bucket `family-media` và set policy `download` cho các objects trong bucket.
- **REQ-00-02-04** WHILE service `api` chưa healthy THE nginx SHALL trả HTTP 502 với trang thông báo "Service starting…".
- **REQ-00-02-05** IF port đã bị chiếm THEN Docker Compose SHALL fail với thông báo rõ ràng tên port xung đột.

### REQ-00-03 — Backend Skeleton
- **REQ-00-03-01** WHEN BE start THE hệ thống SHALL expose endpoint `/api/v1/actuator/health` trả HTTP 200 khi database & Redis reachable.
- **REQ-00-03-02** WHEN BE start THE hệ thống SHALL tự chạy Flyway migrations từ `classpath:db/migration` và ghi log version đã apply.
- **REQ-00-03-03** WHEN truy cập `GET /swagger-ui.html` THE hệ thống SHALL render OpenAPI 3 UI cho toàn bộ controllers.
- **REQ-00-03-04** IF exception không được xử lý xảy ra THEN THE hệ thống SHALL trả response `{ code, message, traceId }` HTTP phù hợp và log stacktrace kèm traceId ở level ERROR.
- **REQ-00-03-05** WHEN BE start THE hệ thống SHALL validate JPA schema bằng `ddl-auto: validate` (không tự tạo bảng ngoài Flyway).

### REQ-00-04 — Frontend Skeleton
- **REQ-00-04-01** WHEN truy cập `GET /` THE hệ thống SHALL render landing page responsive ở cả desktop và mobile (≥360px).
- **REQ-00-04-02** WHEN user chưa xác thực truy cập route thuộc `(dashboard)` THE hệ thống SHALL redirect về `/login?next=...`.
- **REQ-00-04-03** WHEN người dùng đổi theme (light/dark/system) THE hệ thống SHALL persist lựa chọn vào `localStorage` và áp dụng ngay không reload.
- **REQ-00-04-04** WHEN người dùng đổi ngôn ngữ THE hệ thống SHALL persist `NEXT_LOCALE` vào cookie và load message bundle tương ứng.
- **REQ-00-04-05** WHERE landing page render THE hệ thống SHALL áp dụng font `Inter` (UI) và `Be Vietnam Pro` (nội dung) qua `next/font`.

### REQ-00-05 — CI/CD
- **REQ-00-05-01** WHEN push mở PR vào branch bất kỳ THE GitHub Actions SHALL chạy job: lint (BE & FE), typecheck, unit test, build artifact.
- **REQ-00-05-02** IF CI fail THEN THE hệ thống SHALL chặn merge (status check required).
- **REQ-00-05-03** WHEN push/merge vào branch `main` THE GitHub Actions SHALL build Docker images cho `api` và `web` rồi push lên GHCR với tag `latest` và `<short-sha>`.
- **REQ-00-05-04** WHERE workflow `release` chạy THE hệ thống SHALL ký image bằng cosign key từ GitHub Secrets.

### REQ-00-06 — Observability
- **REQ-00-06-01** WHERE BE chạy THE hệ thống SHALL expose Prometheus metrics tại `/api/v1/actuator/prometheus`.
- **REQ-00-06-02** WHEN log được ghi THE hệ thống SHALL dùng JSON format chứa `timestamp`, `level`, `service`, `traceId`, `message`, `mdc`.
- **REQ-00-06-03** WHEN request HTTP đến BE THE hệ thống SHALL inject `traceId` vào MDC và echo lại qua header `X-Trace-Id` cho FE log.

## Acceptance
- `docker compose up` thành công, `curl localhost/api/v1/actuator/health` trả `{"status":"UP"}`.
- `curl localhost/` trả 200 với HTML landing.
- `curl localhost/swagger-ui.html` (qua nginx) trả 200.
- CI chạy xanh trên PR mẫu.
