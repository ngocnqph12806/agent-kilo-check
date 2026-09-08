# ADR-004: Spring Boot 3.3 + Java 21

- **Status:** Accepted
- **Date:** 2026-08-22
- **Deciders:** Founder, Tech Lead
- **Related:** `backend/pom.xml`

## Context

SkillSeed backend needs a mature, productive framework with strong
typing, good DB / migration / security / testing batteries included.

Options:
- Spring Boot 3 + Java 21
- Spring Boot 3 + Java 17
- Quarkus / Micronaut (cloud-native)
- Node.js (NestJS / Fastify)

## Decision

Use **Spring Boot 3.3 + Java 21**. Pin to the latest LTS JDK for
virtual-thread support and pattern-matching ergonomics.

## Consequences

**Positive**
- Spring Boot ecosystem covers everything we need (Web, JPA, Security,
  Data Redis, Validation, WebSocket, Actuator).
- Java 21 virtual threads simplify the WebSocket chat and Daily.co
  webhook handlers.
- Java records remove Lombok from DTOs.

**Negative**
- Cold-start in Docker is slower than Go (~1.5 s).
- Some libraries still don't have Jakarta EE 10 native packages — we
  pin versions and test.

**Reversible?** No — code base is too large. We accept the lock-in.