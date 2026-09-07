# SkillSeed — Gotchas & Workarounds

> **Các lỗi / bẫy thường gặp khi triển khai SkillSeed, và cách xử lý.**
> **Đối tượng:** AI agent + developer mới.
> **Last updated:** 2026-09-07

> ⚠️ Section này sẽ được populate dần sau mỗi sprint. Hiện tại chỉ chứa các cảnh báo "phase-level" rút ra từ spec.

---

## 1. Phase Boundaries (Dễ vượt rào phase)

| Gotcha | Tại sao sai | Cách tránh |
|---|---|---|
| Thêm blockchain/SBT vào Phase 1 | Phase 4 mới có Polygon | Passport Phase 1 chỉ là record DB. Không thêm smart contract. |
| Viết AI matching service (Python + Qdrant) ở Phase 1 | Phase 1 chỉ filter cơ bản | Phase 1 dùng PostgreSQL FTS + filter rule đơn giản. |
| Tách microservices ngay từ đầu | Team 1–2 người, không đủ sức vận hành | Phase 1: monolith. Tách ở Phase 2+ khi MAU > 10K hoặc team > 5. |
| Thêm Twilio / Agora / raw WebRTC | Đã chốt Daily.co cho Phase 1 | Dùng Daily.co SDK; chỉ thay đổi khi có ADR phê duyệt. |
| Thêm Material UI / Ant Design | Đã chốt shadcn/ui | Dùng component shadcn/ui đã scaffold trong `SKILLSEED_CODE_SKELETON.md`. |
| Dùng MongoDB / MySQL | Canonical là PostgreSQL 16 | Mọi lý do "thấy quen hơn" không phải để đổi stack. |

---

## 2. Spring Boot & JPA

| Gotcha | Triệu chứng | Cách tránh |
|---|---|---|
| Lombok `@Data` trên entity | `equals/hashCode` bị lazy-load → bug khi compare entity detached | Dùng `@Getter @Setter` + manual `equals`/`hashCode` dựa trên `@Id`. Hoặc dùng `@Entity` record (Hibernate 6.2+). |
| Eager fetch trên collection | N+1 query, OOM | Mặc định `LAZY`. Khi cần join → dùng `@EntityGraph` hoặc `JOIN FETCH` trong JPQL. |
| `open-in-view: true` (mặc định Spring Boot) | Giữ DB connection đến tầng view → leak | Set `spring.jpa.open-in-view: false` trong `application.yml`. |
| Đặt tên bảng `users` | Convention SkillSeed: **số ít** | Đặt `user`, `booking`, `skill`, … |
| Sửa file Flyway migration đã chạy | Production sẽ crash | **LUÔN** tạo file migration mới (`V{next}__{desc}.sql`). Không sửa file cũ. |
| Quên index cho FK / cột hay query | Query chậm khi data lớn | Review query plan trước khi merge. Index FK bắt buộc. |
| Dùng wildcard import (`import java.util.*`) | Vi phạm convention | Style check sẽ fail. Import rõ ràng từng class. |

---

## 3. Next.js & Frontend

| Gotcha | Triệu chứng | Cách tránh |
|---|---|---|
| Quên `"use client"` cho component có `useState`/event | Server component error | Thêm directive khi component dùng hook/browser API. |
| `useEffect` cho data fetching thay vì TanStack Query | Cache kém, race condition | Dùng `useQuery` / `useMutation`. |
| Dùng `any` trong TypeScript | Strict mode fail, mất type safety | Dùng `unknown` + type guard. Nếu bắt buộc `any`, comment lý do. |
| Bundle nặng vì import cả lodash | Build chậm | Import từng method: `import debounce from 'lodash/debounce'`. |
| Không validate form phía client | UX tệ, request spam server | React Hook Form + Zod schema. |
| Đặt state nhạy cảm trong Zustand persist | Lộ token ra localStorage | Chỉ persist state không nhạy cảm. Token nên ở httpOnly cookie. |
| Render `<Image>` không có `width/height` | CLS warning | Luôn khai báo dimensions hoặc dùng `fill`. |

---

## 4. Auth & Security

| Gotcha | Triệu chứng | Cách tránh |
|---|---|---|
| Commit `.env` chứa secret | Leak | `.env` phải ở `.gitignore`. Dùng `.env.example` với giá trị rỗng. |
| JWT secret dưới 256 bit | Brute-force dễ | Secret ≥ 256 bit random; rotate theo lịch. |
| Trust client-sent `userId` | Privilege escalation | Luôn lấy `userId` từ JWT trong SecurityContext. |
| Không rate-limit API login/register | Brute force / spam | Redis-based rate limit (Bucket4j hoặc custom filter). |
| Đặt CORS `*` cho production | Cross-origin attack | Whitelist domain cụ thể. |
| Log full request body | Lộ PII (password, token) | Log chỉ metadata; mask field nhạy cảm. |

---

## 5. Wallet & Seed Economy

| Gotcha | Triệu chứng | Cách tránh |
|---|---|---|
| Race condition trừ Seed (double-spend) | User âm tiền, hoặc trừ 2 lần | DB transaction với `SELECT ... FOR UPDATE` hoặc optimistic lock `@Version`. |
| Quên expire Seed | Inflation Seed | Scheduled job (`@Scheduled`) quét `seed_transaction` quá hạn → đánh dấu. |
| Không audit transaction | Không truy được lịch sử | **Mọi** thay đổi Seed phải ghi `seed_transaction` (immutable). |
| Refund sai logic | User mất tiền oan | Refund phải theo policy rõ (cancellation rules xem `SKILLSEED.md`). |
| Mixing Seed ↔ Fiat balance | Bug tính toán | 2 bảng/2 ledger riêng. Không convert tự động. |

---

## 6. Daily.co / Video Session

| Gotcha | Triệu chứng | Cách tránh |
|---|---|---|
| Tạo room vĩnh viễn | Tốn quota, rò rỉ link | Tạo room programmatic, set `exp` = session end + 15 phút. |
| Hard-code API key Daily vào frontend | Lộ key | Tạo room qua backend; trả URL + token cho FE. |
| Cho participant bất kỳ join room | Spam/zoom-bombing | Daily meeting token phải có `is_owner` / role. |
| Không handle "host chưa đến" | UX tệ | Cho participant vào "waiting room" trong 10 phút; timeout → rời. |

---

## 7. Migration & Data

| Gotcha | Cách tránh |
|---|---|
| `ALTER TABLE` lớn trên bảng có data | Làm theo batch (VD: `pt-online-schema-change`) hoặc tạo bảng mới + backfill + rename. |
| Quên `created_at` / `updated_at` | Mọi bảng phải có. Dùng `@PrePersist` / `@PreUpdate` (JPA) hoặc trigger (PG). |
| Quên soft-delete | Mất data khi user yêu cầu xoá → vi phạm GDPR | Cột `deleted_at` nullable + filter ở repository. |
| Seed data chứa PII thật | Vi phạm privacy | Chỉ dùng data fake từ Faker library. |
| Schema drift giữa dev / staging / prod | Bug không tái hiện | Migrations là source of truth. Không edit DB thủ công ngoài migration. |

---

## 8. Spec & Documentation

| Gotcha | Cách tránh |
|---|---|
| Cập nhật `SKILLSEED_API_AND_DB.md` mà không sync `phase-N/design.md` | 2 nguồn mâu thuẫn → agent rối | Sửa cả 2. Phase spec thắng khi conflict. |
| Tạo task ID trùng | Audit khó | Check `.kiro/specs/phase-N/tasks.md` trước khi tạo. |
| Mockup thiếu `classDef` color tokens | Render xấu, không nhất quán | Copy từ `mockups/README.md` §"Class definitions". |
| Bỏ qua "Out of Scope" trong spec | Agent tự thêm feature không mong muốn | Luôn đọc "Out of Scope" trước khi đề xuất thêm. |

---

## 9. Testing

| Gotcha | Cách tránh |
|---|---|
| Mock quá nhiều → test trở nên vô nghĩa | Test integration với Testcontainers (Postgres + Redis thật). |
| Chỉ test happy path | Luôn test: invalid input, unauthorized, conflict (booking overlap), empty result. |
| Không test idempotency cho payment/seed | Double-charge | Test gọi API 2 lần → chỉ 1 lần ghi transaction. |
| Snapshot test quá chi tiết | Fragile, phải update mỗi thay đổi nhỏ | Snapshot chỉ component quan trọng; ưu tiên test behavior. |

---

## 10. Khi mọi thứ trông giống nhau nhưng KHÔNG phải

```
SkillSeed   ≠ Skillshare
Seed (SkillSeed) ≠ SEED (crypto token)
Pod (SkillSeed) ≠ POD (print-on-demand)
Passport (SkillSeed) ≠ GitHub Passport
B2B (SkillSeed workspace) ≠ SaaS B2B chung chung
```

Khi search Google/stackoverflow, **luôn thêm "SkillSeed"** để tránh nhầm.

---

> **Contribute:** Phát hiện gotcha mới? Thêm vào file này theo format: triệu chứng → nguyên nhân → cách tránh. PR review sẽ check.
