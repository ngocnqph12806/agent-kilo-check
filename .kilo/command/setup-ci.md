---
description: Set up or verify CI/CD pipeline for a SkillSeed PR
agent: code
---
Run CI/CD verification for the current state of the SkillSeed project.

Steps:
1. Read `docs/CI_CD.md` for the full pipeline overview.
2. Identify what currently exists vs. what the spec requires:
   - `.github/workflows/ci.yml` exists?
   - `.github/workflows/cd.yml` exists?
   - `docker-compose.yml` exists?
   - `checkstyle.xml` exists?
   - `.editorconfig` exists?
3. If `src/` does not exist yet, report:
   - "CI configs and Docker templates are in place, but `src/` (backend + frontend) does not exist yet."
   - "When the developer scaffolds `src/`, they need to wire checkstyle + ESLint to match these configs."
   - Reference `.kiro/specs/phase-1-mvp/tasks.md` T-M05, T-M215.
4. If `src/` exists, do NOT run anything destructive. Just verify:
   - Backend: read `backend/pom.xml`, check that `maven-checkstyle-plugin` references `../checkstyle.xml`.
   - Frontend: read `frontend/package.json`, check that `lint` and `typecheck` scripts exist and call the right commands.
5. Check required GitHub Secrets (per `docs/CI_CD.md` §6):
   - RAILWAY_TOKEN, VERCEL_TOKEN, SLACK_WEBHOOK_URL, DAILY_API_KEY, RESEND_API_KEY.
   - If user asks about a specific secret, show docs but never reveal the value.
6. Report findings as:
   - READY: all configs + src/ wired correctly.
   - GAP_FOUND: list exactly what's missing.
   - NEVER touch secrets, never run deploys, never modify CI files without user confirmation.
