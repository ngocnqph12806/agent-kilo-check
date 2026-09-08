# ADR-001: Monolith-first architecture for Phase 1

- **Status:** Accepted
- **Date:** 2026-08-15
- **Deciders:** Founder, Tech Lead
- **Related:** `SKILLSEED.md` §6, `.kiro/specs/phase-1-mvp/design.md` §1

## Context

SkillSeed MVP needs auth, profile, discover, booking, video, wallet,
rating, notifications — 8+ bounded contexts, but we expect < 5 000
MAU in the first 6 months. We have one backend engineer (founder) and
a part-time frontend freelancer.

We need to ship Phase 1 in ~10 weeks with a team of 1.5 FTE.

## Decision

Build Phase 1 as a **single Spring Boot monolith**, packaged into one
Docker container and deployed to Railway. Cross-module shared logic
lives under `com.skillseed.shared.*`. Module-specific code lives under
`com.skillseed.{module}.*`.

Service boundaries are enforced through **package naming + Spring
component scan + review**, not network hops.

## Consequences

**Positive**
- Single deploy unit — fast iteration, one CI pipeline.
- ACID transactions across modules (e.g. booking + wallet + rating)
  without sagas.
- Easier local dev: one `docker compose up`.
- Lower cloud bill in Phase 1.

**Negative**
- Hard to scale individual modules — until we hit ~50 000 MAU we don't
  need to.
- A bug in any module can take the whole service down — mitigated with
  resilience patterns (rate limiting, circuit breakers) in Phase 2.
- Big code surface for new contributors — mitigated with strict
  package rules (`shared.*` vs `{module}.*`).

**Reversible?** Yes, but expensive. Plan: split into `auth-service`,
`marketplace-service`, `wallet-service` when traffic justifies it.