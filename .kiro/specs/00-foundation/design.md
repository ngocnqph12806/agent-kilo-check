# 00 — Foundation · Design

> Thiết kế kỹ thuật chi tiết cho epic Foundation.

## 1. Monorepo Strategy

- **Quản lý gốc:** pnpm workspaces (Node 20).
- **BE độc lập:** Gradle wrapper commit vào repo, không qua pnpm.
- **packages/shared-types:** script `pnpm --filter shared-types build` đọc `http://localhost:8080/v3/api-docs` (sau khi BE chạy), generate bằng `openapi-typescript`.
- **Sync:** FE import types qua path alias `@giapha/types`.

```jsonc
// root package.json
{
  "name": "giaphaonline",
  "private": true,
  "scripts": {
    "dev:web": "pnpm --filter web dev",
    "dev:api": "pnpm --filter api dev",
    "build:types": "pnpm --filter shared-types build",
    "lint": "pnpm -r lint",
    "test": "pnpm -r test"
  },
  "packageManager": "pnpm@9"
}
```

## 2. Docker Compose

### Services & healthchecks

| Service | Image | Healthcheck |
|---|---|---|
| postgres | postgres:16-alpine | `pg_isready -U giapha` |
| redis | redis:7-alpine | `redis-cli ping` |
| minio | minio/minio:latest | `curl -f http://localhost:9000/minio/health/live` |
| minio-init | minio/mc:latest | run once then exit 0 |
| api | ghcr.io/.../api:main | `curl -f http://localhost:8080/actuator/health` |
| web | ghcr.io/.../web:main | `curl -f http://localhost:3000` |
| nginx | nginx:1.27-alpine | `nginx -t` |

### Volume
- `pgdata:/var/lib/postgresql/data`
- `miniodata:/data`
- `redisdata:/data`

### Network
- Single bridge `giapha-net`; FE gọi BE qua `http://api:8080` (container name), user gọi qua `http://localhost/api`.

## 3. Backend skeleton

### application.yml (multi-profile)
```yaml
spring:
  application.name: giapha-api
  profiles.active: ${SPRING_PROFILES_ACTIVE:dev}
  datasource:
    url: jdbc:postgresql://${DB_HOST:postgres}:5432/giapha
    username: ${DB_USER:giapha}
    password: ${DB_PASSWORD:giapha}
  jpa:
    hibernate.ddl-auto: validate
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
  redis:
    host: ${REDIS_HOST:redis}
    port: 6379

server:
  port: 8080
  forward-headers-strategy: framework
  error.include-message: always

management:
  endpoints.web.exposure.include: health,info,prometheus,metrics
  endpoint.health.probes.enabled: true

springdoc:
  swagger-ui.path: /swagger-ui.html
  packagescan: com.giaphaonline.api

logging:
  config: classpath:logback-spring.xml
```

### Global exception flow
```
Controller → throws BusinessException
  → @RestControllerAdvice GlobalExceptionHandler
  → ResponseEntity<ApiResponse<T>>
  → MDC traceId echoed in header `X-Trace-Id`
```

### ApiResponse shape
```json
{ "code": "string", "message": "string", "data": T, "traceId": "uuid" }
```

## 4. Frontend skeleton

### Providers chain (root layout)
```
<html>
  <ThemeProvider>      // next-themes
    <NextIntlClientProvider>
      <QueryClientProvider>
        <AuthProvider>
          {children}
        </AuthProvider>
      </QueryClientProvider>
    </NextIntlClientProvider>
  </ThemeProvider>
</html>
```

### Theming
- CSS variables trong `globals.css` (light + dark), shadcn/ui defaults.
- Font: `Inter` (UI), `Be Vietnam Pro` (nội dung tiếng Việt) load qua `next/font`.

### API client
- `lib/api/client.ts` dùng axios instance:
  - baseURL `/api/v1`
  - interceptor `Authorization: Bearer ${token}` từ `useAuthStore`
  - interceptor refresh token khi 401.
- React Query keys: `['resource', id, filters]`.

### Route guards
- `(dashboard)/layout.tsx` server-side check session cookie; nếu fail → `redirect('/login')`.
- `(public)/p/[slug]/page.tsx` không guard.

## 5. CI matrix

| Job | Trigger | Steps |
|---|---|---|
| api-lint | push/PR | `./gradlew checkstyleMain testClasses` |
| api-test | push/PR | `./gradlew test` với Postgres + Redis service container |
| web-lint | push/PR | `pnpm lint && pnpm typecheck` |
| web-test | push/PR | `pnpm test -- --run` |
| docker-build | main | Build & push images với `docker/build-push-action@v5` |

## 6. Open Questions / Risks
- R1: Dùng Turborepo hay chỉ pnpm workspaces? → Chốt pnpm workspaces đơn giản.
- R2: BE image base — `eclipse-temurin:21-jre-alpine` (chạy) + `eclipse-temurin:21-jdk-alpine` (build).
- R3: FE build standalone hay output `standalone`? → Output `standalone` cho image nhỏ.
