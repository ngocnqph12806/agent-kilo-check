# SkillSeed — Visual Fidelity Rule

> **Mọi thay đổi UI (frontend) hoặc thay đổi backend ảnh hưởng đến UI (DTO, status, error states, validation messages) PHẢI tham chiếu SVG mockup tương ứng trong `screens-svg/` trước khi viết code.**

**Last updated:** 2026-09-08 (Rule added after G0–G3 visual fidelity pass)
**Owner:** SkillSeed Tech Lead
**Status:** Mandatory (enforced via AGENTS.md §5 + Workflow §8)

---

## 1. Tại sao

Repo có **130 SVG mockup** dưới `screens-svg/` — mỗi màn hình một file (login, register, discover, booking list/detail/modal, video session, wallet, notifications, profile, special states, …). SVG được render thủ công từ Mermaid mockup ở `mockups/`, dùng design system thống nhất (gradient xanh `#10B981 → #059669`, shadow `0 8px 24px -8px rgb(16 185 129 / 0.45)`, border-radius 12/16/24, font Inter, layout 1440×900).

Từ Sprint 3 retrospective: code Phase 1 đã implement đúng logic + flow nhưng **lệch visual** ~40% so với mockup (sai header, sai empty state, sai status badge, hardcode màu ngoài palette). Nguyên nhân: agent không mở SVG trước khi code, dẫn đến phải audit + refactor lại toàn bộ (commit G0–G3). Rule này ngăn lặp lại.

---

## 2. Áp dụng cho

| Agent / Dev | Khi nào | Bắt buộc |
|---|---|---|
| **Frontend** | Tạo / sửa screen mới, refactor layout, đổi typography, đổi màu | ✅ Mở SVG đối chiếu |
| **Backend** | Đổi DTO field name / type, thêm status enum, đổi error message, thêm validation rule | ✅ Mở SVG của màn bị ảnh hưởng để chắc field/status/message khớp |
| **Designer / Mockup owner** | Cập nhật SVG | ✅ Đảm bảo SVG còn khớp với Mermaid mockup gốc |
| **Code reviewer** | Review PR | ✅ Reject nếu PR có UI / DTO change mà không reference SVG path |

---

## 3. Workflow bắt buộc (BEFORE coding)

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Xác định task FE / BE thuộc screen nào                   │
│    → VD: "Implement login page" → screens-svg/01-auth/      │
│    → VD: "Add booking status CANCELLED" → screens-svg/04-booking/ (modal, detail, list) │
│                                                             │
│ 2. Mở file SVG tương ứng + Mermaid mockup gốc               │
│    → Xem layout, màu, copy text, status badge, empty state  │
│                                                             │
│ 3. Nếu BE thay đổi DTO / enum:                              │
│    → Đối chiếu SVG để confirm field name, format, value    │
│    → Nếu SVG dùng "seats" mà BE trả "seatCount" → conflict │
│                                                             │
│ 4. Nếu FE thay đổi layout:                                 │
│    → Đối chiếu SVG để copy exact spacing, color, typography│
│    → KHÔNG tự ý hardcode màu ngoài design tokens           │
│                                                             │
│ 5. Document trong PR / commit message:                       │
│    → "Matches screens-svg/01-auth/02-login.svg"             │
│    → Nếu có chỗ lệch chủ động (do chưa có API / chưa có   │
│      modal phụ): giải thích trong commit body               │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. Quy tắc cụ thể

### 4.1 Frontend component

| Quy tắc | Ví dụ đúng | Ví dụ sai |
|---|---|---|
| Dùng design tokens thay vì hardcode màu | `bg-brand-cta text-white` | `bg-[#10B981] text-[#fff]` |
| Dùng Button variant thay vì utility override | `<Button variant="brand">` | `<Button className="bg-brand-cta hover:opacity-95">` |
| Dùng shared components (BrandLogo, AppTopBar, AuthShell, EmptyState) | `<AuthShell hero={{ variant: 'auth' }}>` | Custom layout 5 lần cho 5 auth page |
| Empty / loading / error state từ shared component | `<EmptyState icon={Calendar} title="No bookings yet" />` | Inline `<div>No bookings</div>` |
| Match gradient direction SVG dùng | `bg-gradient-to-r from-emerald-500 to-emerald-600` (horizontal) | `bg-gradient-to-b` (vertical) sai hướng |
| Spacing theo SVG (gap-6, p-8, rounded-2xl) | `gap-6 p-8 rounded-2xl shadow-brand-card` | `gap-4 p-4 rounded-lg shadow-sm` |

### 4.2 Backend DTO / enum / error

| Quy tục | Ví dụ đúng | Ví dụ sai |
|---|---|---|
| Field name trong JSON khớp SVG label | DTO field `displayName` ↔ SVG "Display name" | `fullName` khi SVG hiển thị "Display name" |
| Status enum value khớp status badge trong SVG | `status: 'PENDING' \| 'CONFIRMED' \| ...` ↔ badge "Pending" / "Confirmed" | Thêm `WAITING_FOR_PAYMENT` không có badge trong SVG |
| Currency / unit khớp SVG | Field `amount: number` (số seeds) ↔ SVG suffix "seeds" | `amount` trả 100 mà SVG hiển thị "100 VND" |
| Error message khớp copy trong SVG empty / error state | 404 state copy: "The link you followed may be broken…" | Backend error "Not Found" (lệch tone + casing) |
| Empty array vs null | Trả `[]` khi không có item ↔ SVG hiển thị empty card | Trả `null` → FE crash khi `.map()` |

### 4.3 Khi SVG chưa có hoặc lỗi thời

1. **Không tự ý sáng tác UI** — flag trong `tasks.md` của phase: "SVG cần update cho màn X".
2. Nếu task urgent, dùng `<EmptyState>` chung với copy placeholder: "Screen này đang được thiết kế".
3. **Không commit code SVG / mockup** trừ khi thuộc team design.

---

## 5. Checklist trước khi merge (DoD bổ sung)

Thêm vào `AGENTS.md §7.1` cho mọi PR có UI / DTO change:

- [ ] PR description liệt kê đường dẫn SVG(s) tham chiếu
- [ ] Nếu có lệch chủ động so với SVG, giải thích lý do trong commit body
- [ ] Component dùng design tokens (không hardcode hex)
- [ ] Empty / loading / error state dùng shared component
- [ ] BE DTO field name khớp label trong SVG
- [ ] BE status enum value hiển thị đúng badge trong SVG
- [ ] Chạy `npm run lint` + `npm run typecheck` + `mvn checkstyle:check` pass

---

## 6. Reference nhanh

| Cần | Đọc |
|---|---|
| Index + convention của SVG | `mockups/README.md` + `screens-svg/` (130 file, đã phân category `00-marketing` → `99-special-states`) |
| Mermaid mockup gốc của 1 SVG | File `.md` cùng tên trong `mockups/{category}/` (VD: `mockups/01-auth/02-login.md`) |
| Design tokens (color, shadow, radius) | `frontend/app/globals.css` + `frontend/tailwind.config.ts` |
| Shared components sẵn có | `frontend/components/shared/` (BrandLogo, MarketingTopBar, MarketingHero, EmptyState, LoadingState, ErrorState) |
| Button variants (default, brand, brand-outline, destructive, destructive-soft, outline, secondary, ghost, link) | `frontend/components/ui/button.tsx` |
| Workflow agent khi implement | `AGENTS.md` §5 (Conventions) + §8 (Workflow) |

---

## 7. Lịch sử thay đổi

| Ngày | Thay đổi |
|---|---|
| 2026-09-08 | Khởi tạo rule sau G0–G3 visual fidelity pass. 25 file refactor, 1 bug nghiêm trọng (button trắng do vars CSS ngoài `:root` + tailwind-merge conflict). |
