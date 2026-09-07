# SkillSeed infrastructure assets

`docker/` already contains the active `Dockerfile.backend` and `Dockerfile.frontend`
used by `docker-compose.yml` at the repo root.

This `infra/` folder is reserved for future assets:

- `k8s/` — Kubernetes manifests (Phase 3+)
- `terraform/` — IaC for cloud resources (Phase 4+)
- `helm/` — Helm charts (Phase 3+)

See `.kiro/specs/phase-1-mvp/design.md` §2.4 for the current infra cost targets.
