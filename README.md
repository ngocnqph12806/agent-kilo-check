# GiaPhaOnline

Hệ thống quản lý gia phả trực tuyến — full-stack: **Next.js 14 (FE)** · **Spring Boot 3 (BE)** · **PostgreSQL 16** · **Redis** · **MinIO**.

Xem chi tiết spec tại [SPEC.md](./SPEC.md).

## Khởi động nhanh

```bash
docker compose -f docker/docker-compose.yml up -d
```

Sau khi stack lên:

- Web: <http://localhost:3000>
- API: <http://localhost:8080/api/v1>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- MinIO Console: <http://localhost:9001> (`minioadmin / minioadmin`)

## Cấu trúc

```
apps/
  web/      # Next.js
  api/      # Spring Boot
docker/     # Docker Compose + Nginx
docs/       # Tài liệu
```

## Tài liệu

- [SPEC.md](./SPEC.md) — đặc tả chi tiết toàn hệ thống.
