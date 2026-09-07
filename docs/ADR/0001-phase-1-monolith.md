# ADR-0001: Phase 1 dùng Monolith (Spring Boot Modular), không Microservices

- **Status:** Accepted
- **Date:** 2026-09-07
- **Phase:** 1 (MVP)
- **Deciders:** SkillSeed Product Team

---

## Context

Khi bắt đầu Phase 1 (MVP), cần quyết định kiến trúc backend:

- **Option A:** Microservices (mỗi module = 1 service riêng, k8s, API gateway, service mesh, …)
- **Option B:** Modular Monolith (1 Spring Boot app, chia module rõ ràng theo package, sẵn sàng tách ở Phase 2+)

## Decision

**Chọn Option B — Modular Monolith.**

## Rationale

### Lý do chọn Monolith
1. **Team size:** 1–2 người ở Phase 1. Microservices tốn ≥ 1 SRE chỉ để vận hành.
2. **Time-to-market:** Single deploy, single DB, debug nhanh hơn nhiều.
3. **Domain chưa ổn định:** Phase 1 vẫn đang validate product-market fit. Tách service khi domain chưa cứng → phải re-shuffle liên tục.
4. **Cost:** 1 service + 1 DB rẻ hơn 10 service + 10 DB + mesh + monitoring.
5. **Modular:** Code vẫn chia module rõ ràng (`com.skillseed.{module}`). Mỗi module có bounded context, có thể tách ra service sau mà không phải viết lại nhiều.

### Lý do KHÔNG chọn Microservices
1. Distributed tracing, eventual consistency, service discovery… là gánh nặng không cần thiết cho MVP.
2. Network latency giữa service sẽ ăn vào latency end-to-end (quan trọng cho booking real-time).
3. Multi-language stack (Java + Python cho matching) dễ "đông cứng" ranh giới sai.

## Consequences

### Positive
- Triển khai nhanh, debug dễ.
- Code tổ chức rõ ràng theo module, có thể tách dần ở Phase 2.
- Phù hợp với skillset Java backend thông thường.

### Negative
- Một bug ở module có thể kéo sập toàn bộ app → cần health check, graceful shutdown, circuit breaker cho external calls.
- Scale không granular: phải scale cả app khi chỉ 1 module nóng.
- Phải kỷ luật module boundary (không gọi chéo module tùy tiện).

### Mitigation
- **Module isolation:** Mỗi module có package riêng, public API rõ ràng (qua interface/facade), gọi chéo qua event bus in-process (Spring `ApplicationEvent`).
- **Tách module thành Maven multi-module** nếu build time tăng.
- **Sẵn sàng tách:** Các module được thiết kế "port + adapter" để có thể tách ra service riêng khi MAU > 10K.

## Alternatives Considered

| Phương án | Tại sao không chọn |
|---|---|
| Microservices thuần (1 service / module) | Tốn vận hành, không phù hợp team 1–2 người |
| Serverless (Lambda / Cloud Functions) | Cold start không chấp nhận được cho real-time booking |
| Single-file Node.js / Python monolith | Stack canonical là Java/Spring — thay vì tốn effort retrain toàn team |

## When to Revisit

ADR này cần review khi **bất kỳ** điều nào sau xảy ra:
- MAU > 10K (xem `SKILLSEED_CLOUD_COST.md` để tính ngưỡng)
- Team > 5 người
- 1 module cần scale độc lập (VD: `notification` phải gửi 1M email/ngày)
- Cần tách `matching` ra Python (Phase 2)

Lúc đó tạo ADR mới về việc tách service cụ thể.

## References

- `.kiro/specs/phase-1-mvp/design.md` §1
- `SKILLSEED.md` §6 (System Architecture)
- `SKILLSEED_CODE_SKELETON.md`
- Sam Newman, *Monolith to Microservices*, 2019
- Simon Brown, *Modular Monoliths*, 2022 (tư tưởng "screaming architecture")
