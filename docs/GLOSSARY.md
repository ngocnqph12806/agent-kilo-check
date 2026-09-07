# SkillSeed — Glossary

> **Định nghĩa chuẩn cho mọi thuật ngữ domain trong SkillSeed.**
> **Mục đích:** Agent (AI) và developer mới tra cứu nhanh, tránh hiểu sai nghiệp vụ.
> **Last updated:** 2026-09-07

---

## A. Người dùng & Phân khúc

| Thuật ngữ | Định nghĩa | Phase |
|---|---|---|
| **User** | Một tài khoản SkillSeed. Có thể vừa là Learner vừa là Teacher. | 1+ |
| **Learner** | User đăng ký học (tiêu Seed). | 1+ |
| **Teacher** | User dạy người khác (được cộng Seed). | 1+ |
| **Verified Expert** | Teacher đã qua identity + skill verification, được phép bán dịch vụ trên Marketplace (nhận tiền USD). | 3+ |
| **Creator** | Verified Expert tạo khoá học / nội dung premium. | 3+ |
| **Subscriber** | User trả Premium để xem nội dung Creator. | 3+ |
| **NGO / Gifter** | Tổ chức/tình nguyện viên mua Seed gửi tặng người dùng khác. | 3+ |
| **Org Admin** | Quản trị B2B workspace (doanh nghiệp). | 3+ |
| **Platform Admin** | Nhân viên SkillSeed quản trị nền tảng. | 1+ |

---

## B. Tiền tệ & Giá trị

| Thuật ngữ | Định nghĩa | Phase |
|---|---|---|
| **Seed** | Đơn vị giá trị nội bộ. 1 Seed ≈ 1 phút dạy. Sinh ra khi dạy thành công. Hết hạn sau 12 tháng không dùng. | 1+ |
| **Seed Pack** | Gói Seed mua bằng tiền fiat (USD/VND). Có giá trị tiền thật. | 1+ |
| **Seed Transaction** | Một dòng ghi ledger Seed: `+N` (nhận) hoặc `-N` (tiêu). Có `from_user`, `to_user`, `booking_id`, `reason`. | 1+ |
| **Premium** | Gói thuê bao trả phí USD để mở khoá tính năng (xem nội dung Creator, advanced matching, …). **Không** dùng Seed. | 3+ |
| **Wallet** | Tài khoản Seed của user (balance + lịch sử giao dịch). | 1+ |
| **Marketplace Revenue** | Doanh thu fiat từ Creator subscription / Expert session. Chia theo tỉ lệ SkillSeed ↔ Creator. | 3+ |

---

## C. Kỹ năng & Học tập

| Thuật ngữ | Định nghĩa | Phase |
|---|---|---|
| **Skill** | Một kỹ năng trong catalog (VD: "Python", "Guitar", "Public Speaking"). Có category, level, tags. | 1+ |
| **User Skill** | Mối quan hệ `user × skill` với `role` (teach / learn / both) và `level` (beginner → expert). | 1+ |
| **Skill DNA** | Vector đa chiều mô tả user: skills + goals + learning style + availability + personality. **Phase 1** lưu dạng JSON; **Phase 2+** chuyển sang embedding. | 1 (stub) → 2+ |
| **Learning Style** | Cách user học hiệu quả: visual / auditory / kinesthetic / reading-writing + interactive / solo. | 1+ |
| **Goal** | Mục tiêu học tập ngắn hạn / dài hạn user đặt ra. | 1+ |
| **Availability** | Lịch rảnh user khai báo (timezone, recurring slots). | 1+ |
| **Rating** | Đánh giá sau session, multi-criteria (knowledge, communication, helpfulness, …). | 1+ |

---

## D. Booking & Session

| Thuật ngữ | Định nghĩa | Phase |
|---|---|---|
| **Booking** | Lịch hẹn giữa Learner ↔ Teacher cho 1 skill, 1 khoảng thời gian. Có state machine: `pending → confirmed → in_session → completed → rated` hoặc `cancelled`. | 1+ |
| **Session** | Buổi học thực tế qua video call. Diễn ra trong khoảng thời gian của Booking. | 1+ |
| **Pod** | Nhóm học tập nhiều người (multi-participant session). Có thể đóng (invite-only) hoặc mở (join any). | 3+ |
| **Event** | Buổi offline/offline-mixed do community tổ chức. | 1+ |
| **Daily Room** | Phòng video trên Daily.co, tạo programmatic khi session bắt đầu. Token có thời hạn. | 1+ |
| **AI Notes** | Ghi chú tự động từ transcript buổi học (Phase 1: stub; Phase 2+: Whisper + LLM). | 1 (stub) → 2+ |
| **Whiteboard** | Bảng vẽ chia sẻ trong session (Daily.co có sẵn SDK). | 1+ |

---

## E. Uy tín & Xác minh

| Thuật ngữ | Định nghĩa | Phase |
|---|---|---|
| **Passport** | Bản ghi verifiable về các buổi đã học/đã dạy của user. **Phase 1**: bản ghi DB; **Phase 4**: SBT trên Polygon. | 1 (DB) → 4 (SBT) |
| **SBT (Soulbound Token)** | NFT không thể chuyển nhượng, đại diện cho thành tích. SkillSeed mint SBT cho Verified Expert & top Teacher. | 4+ |
| **zk-Proof (Zero-Knowledge Proof)** | Bằng chứng mật mã user biết một kỹ năng mà không tiết lộ chi tiết buổi học. | 4+ |
| **Identity Verification** | Xác minh danh tính (giấy tờ, video selfie). Bắt buộc cho Verified Expert. | 3+ |
| **Skill Verification** | Đánh giá kỹ năng qua quiz / portfolio / peer review. | 3+ |

---

## F. Kỹ thuật

| Thuật ngữ | Định nghĩa |
|---|---|
| **Skill Seed (module)** | Module backend `wallet/` — quản lý Seed balance + transactions. KHÔNG nhầm với "đơn vị Seed". |
| **Module** | Đơn vị tổ chức code backend (auth, user, skill, booking, …). Phase 1: monolith với các package module. Phase 2+: tách thành service. |
| **Phase** | Giai đoạn phát triển: 0 → 1 → 2 → 3 → 4 → 5. Mỗi phase có spec riêng. |
| **Spec (Kiro)** | Bộ 3 file `requirements.md` / `design.md` / `tasks.md` cho mỗi phase. |
| **Mockup** | Sơ đồ Mermaid flowchart mô tả UI screen. Ở `mockups/`. |
| **Wireframe** | ASCII art mô tả UI chi tiết. Ở `.kiro/wireframes/`. |
| **FR / NFR** | Functional / Non-Functional Requirement. ID theo pattern `FR-{PhaseCode}##`. |
| **US** | User Story. ID theo pattern `US-{PhaseCode}##`. |
| **Task ID** | `T-{PhaseLetter}{Serial}` — VD: `T-M01` (Phase 1 task #1). |

---

## G. Các từ viết tắt thường gặp

| Viết tắt | Nghĩa |
|---|---|
| PMF | Product-Market Fit |
| MVP | Minimum Viable Product |
| MAU | Monthly Active Users |
| ARR | Annual Recurring Revenue |
| NFR | Non-Functional Requirement |
| SSO | Single Sign-On |
| SSR | Server-Side Rendering |
| FTS | Full-Text Search |
| SBT | Soulbound Token |
| zk | Zero-Knowledge |
| LTV | Lifetime Value |
| NPS | Net Promoter Score |

---

## H. Những chỗ dễ nhầm

| Nhầm lẫn | Sự thật |
|---|---|
| "Seed" (đơn vị) ↔ "skill_seed" (file/module) | Khác nhau. Seed là tiền tệ; `skill_seed` (hoặc `seed`) là tên module/tên file. |
| User ≠ User Account | Luôn dùng "user" (số ít) cho bảng DB. |
| Booking = Session ? | **Không.** Booking là lịch hẹn; Session là buổi học thực tế. 1 Booking → 1 Session. |
| Passport = Certificate ? | Passport là *record tổng hợp* (verified lessons + skills), không phải 1 chứng chỉ. |
| Verified Expert = Teacher ? | Mọi Expert đều là Teacher, nhưng không phải Teacher nào cũng là Expert. Expert yêu cầu verify. |
| Premium ≠ Seed | Premium trả bằng USD. Seed là tiền tệ nội bộ. Hai vòng tuần hoàn tách biệt. |
