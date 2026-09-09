# SkillSeed — CI/CD Guide

> **Tổng quan hệ thống CI/CD, build, lint, test, deploy.**
> Đây là file **điều hướng** — chi tiết nằm ở workflow files + spec.

**Last updated:** 2026-09-07
**Owner:** Tech Lead

---

## 1. Pipeline tổng quan

```
PR opened ──→ CI (ci.yml) ──→ Review ──→ Merge to main ──→ CD (cd.yml) ──→ Production
                  │
                  ├─ Backend: mvn verify (compile + test + checkstyle)
                  ├─ Frontend: npm ci → lint → typecheck → test → build
                  ├─ Docs: markdownlint
                  └─ Status: required ✅ để merge
```

**Stack:** GitHub Actions + Railway (BE) + Vercel (FE). Chi tiết trong `.kiro/specs/phase-1-mvp/design.md` §10 và tasks `T-M05`, `T-M215`.

---

## 2. Workflow files

| File | Mục đích | Kích hoạt |
|---|---|---|
| `.github/workflows/ci.yml` | Lint + typecheck + test + build | PR, push to main/develop |
| `.github/workflows/cd.yml` | Auto-deploy backend (Railway) + frontend (Vercel) + smoke test | Push to main |

---

## 3. Local development

### 3.1. Yêu cầu
- Docker 24+ & Docker Compose v2
- (Optional) Java 21, Maven 3.9, Node 20 nếu muốn chạy native

### 3.2. Khởi động full stack
```bash
docker compose up -d
```

Sau khi các service lên:
- Backend: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Frontend: <http://localhost:3000>
- Postgres: `localhost:5432` (user/pwd: `skillseed`/`skillseed_dev_password`)
- Redis: `localhost:6379` (auth: `skillseed_dev_password`)

### 3.3. Xem logs
```bash
docker compose logs -f backend
docker compose logs -f frontend
```

### 3.4. Reset database
```bash
docker compose down -v         # Xoá volumes (data mất)
docker compose up -d
```

### 3.5. Chạy native (không qua Docker)
```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend (terminal khác)
cd frontend && npm install && npm run dev
```
Khi này cần Postgres + Redis local (hoặc vẫn dùng docker compose cho 2 service đó).

---

## 4. Lint / typecheck / test commands

> **Agent:** Mỗi lần sửa code, chạy theo đúng các lệnh này. Không tự đoán cú pháp khác.

### 4.1. Backend (Java / Maven)
```bash
cd backend

mvn compile              # type check
mvn test                 # unit test
mvn verify               # full gate (test + integration + checkstyle + jacoco)
mvn checkstyle:check     # style only
mvn spring-boot:run      # local dev server (cần Postgres + Redis)
mvn flyway:info          # xem migration state
mvn flyway:migrate       # apply migration
```

**Checkstyle config:** `checkstyle.xml` (root) + `checkstyle-suppressions.xml`.
**Convention tham chiếu:** `AGENTS.md` §5.

### 4.2. Frontend (TypeScript / Next.js)
```bash
cd frontend

npm ci                   # clean install (CI dùng)
npm run dev              # local dev server (port 3000)
npm run lint             # ESLint
npm run typecheck        # tsc --noEmit
npm test                 # unit test (Vitest/Jest)
npm run build            # production build
npm run e2e              # Playwright (khi đã setup)
```

> **Lưu ý:** Spec `T-M05` dùng `npm`. Agent có thể dùng `pnpm` local (nhanh hơn) nhưng **commit `package-lock.json` (npm)**, không commit `pnpm-lock.yaml`.

### 4.3. Docs (Markdown)
```bash
# Local (cần cài markdownlint-cli2: npm i -g markdownlint-cli2)
markdownlint-cli2 '**/*.md'

# Qua pre-commit (recommended)
pre-commit run markdownlint-cli2 --all-files
```

**Config:** `.markdownlint.jsonc`.

---

## 5. CI gate (bắt buộc xanh)

Trước khi PR được merge:
- [ ] **Backend verify** pass (`mvn verify`)
- [ ] **Backend checkstyle** pass (`mvn checkstyle:check`)
- [ ] **Frontend lint** pass (`npm run lint`)
- [ ] **Frontend typecheck** pass (`npm run typecheck`)
- [ ] **Frontend build** pass (`npm run build`)
- [ ] **Docs lint** pass (markdownlint)
- [ ] **Visual fidelity gate** — PR có UI / DTO / status-enum change đính kèm `Matches screens-svg/...` (xem §5.3 AGENTS.md + `docs/VISUAL_FIDELITY.md`)
- [ ] **Branch protection** trên `main` đã bật (xem §7)

---

## 6. Required GitHub Secrets

Đặt tại **Repo → Settings → Secrets and variables → Actions**:

| Secret | Dùng cho | Source |
|---|---|---|
| `RAILWAY_TOKEN` | Backend deploy (CD) | <https://railway.app/account/tokens> |
| `VERCEL_TOKEN` | Frontend deploy (CD) | <https://vercel.com/account/tokens> |
| `SLACK_WEBHOOK_URL` | Alert khi deploy fail | Slack → Apps → Incoming Webhooks |
| `DAILY_API_KEY` | Backend test (integration) | Daily dashboard |
| `RESEND_API_KEY` | Backend test (integration) | Resend dashboard |

**Biến môi trường cho frontend (CD):**

| Variable | Value |
|---|---|
| `NEXT_PUBLIC_API_URL` | `https://api.skillseed.app` |

> ⚠️ **KHÔNG BAO GIỜ** commit secret vào repo. Nếu lỡ commit, **rotate ngay** và dùng `git filter-repo` để xoá khỏi history.

---

## 7. Branch protection (settings)

Bật tại **Repo → Settings → Branches → Add rule → Branch name pattern: `main`**:

- ✅ Require a pull request before merging
  - Require approvals: **2**
  - Dismiss stale pull request approvals when new commits are pushed
  - Require review from Code Owners
- ✅ Require status checks to pass before merging
  - `Backend · Test`
  - `Backend · Style (Checkstyle)`
  - `Backend · Package (Jar)`
  - `Frontend · Lint + Typecheck + Build`
  - `Docs · Markdown Lint`
- ✅ Require conversation resolution before merging
- ✅ Require signed commits (Phase 2+, khi đông người)
- ✅ Require linear history (Phase 2+)
- ❌ Do not allow force pushes
- ❌ Do not allow deletions

---

## 8. Environments (CD)

| Env | Auto-deploy | URL | Approvals |
|---|---|---|---|
| `production` | Khi push `main` | <https://api.skillseed.app>, <https://skillseed.app> | 1 (Tech Lead) |
| `staging` | Manual trigger | <https://staging.skillseed.app> | 0 |

---

## 9. Pre-commit hooks

Bật 1 lần cho mỗi contributor:
```bash
pip install pre-commit
pre-commit install
```

Hooks đang chạy (xem `.pre-commit-config.yaml`):
- Trailing whitespace, EOF newline
- YAML/JSON/XML syntax check
- **🚨 Detect private keys / secrets**
- **🚨 No direct commit to `main`** — chỉ merge qua PR
- **🚨 Block `.env` files**
- Markdown lint
- Large file check (>500KB)

---

## 10. Khi CI fail — debug nhanh

| Triệu chứng | Nguyên nhân thường gặp | Cách xử lý |
|---|---|---|
| `mvn verify` fail: test timeout | DB chưa ready trong Testcontainers | Kiểm tra service health trong `ci.yml` |
| `mvn checkstyle:check` fail | Vi phạm style | Xem report trong artifact `backend-checkstyle-report` |
| `npm run lint` fail | ESLint rule | Xem log, sửa hoặc `// eslint-disable-next-line` có lý do |
| `npm run typecheck` fail | TS error | Sửa hoặc dùng `unknown` + type guard |
| `npm run build` fail | Environment variable thiếu | Set `NEXT_PUBLIC_API_URL` trong workflow |
| `markdownlint` fail | MD format | Xem `.markdownlint.jsonc` config |
| Docker build fail | Dependency snapshot cũ | Clear cache: gh actions cache delete |

---

## 11. Roadmap (khi scale)

| Phase | Thay đổi CI/CD |
|---|---|
| 1 (hiện tại) | GitHub Actions + Railway + Vercel + branch protection |
| 2 | Thêm `sonarcloud` cho code coverage gate; nightly scheduled test |
| 3 | K8s cluster + ArgoCD; staging environment per PR |
| 4 | Multi-region deploy + blue/green; chaos testing trong CI |
| 5 | ML model validation trong CI (data drift check) |

---

## 12. Cross-reference

| File | Vai trò |
|---|---|
| `.github/workflows/ci.yml` | CI pipeline |
| `.github/workflows/cd.yml` | CD pipeline |
| `.github/CODEOWNERS` | Auto-assign reviewers |
| `.github/dependabot.yml` | Auto-update deps |
| `checkstyle.xml` | Java style |
| `.editorconfig` | Universal editor config |
| `.gitattributes` | Line endings |
| `.dockerignore` | Docker build exclusions |
| `docker-compose.yml` | Local full stack |
| `docker/Dockerfile.backend` | Backend image |
| `docker/Dockerfile.frontend` | Frontend image |
| `.pre-commit-config.yaml` | Local pre-commit hooks |
| `.markdownlint.jsonc` | Markdown style |
| `.kiro/specs/phase-1-mvp/tasks.md` | Tasks `T-M05`, `T-M215` |
| `AGENTS.md` | Agent rules (Section 5, 7) |
