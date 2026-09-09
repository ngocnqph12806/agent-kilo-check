# ADR-007 — Visual Fidelity Rule (mọi UI / DTO change phải tham chiếu `screens-svg/`)

## Status
Accepted — 2026-09-09 (Sprint 5 retrospective, T-M422)

## Context
- Repo SkillSeed đã có 130 SVG mockup trong `screens-svg/` được tái sử dụng để review & onboard.
- Trước Sprint 5, không có quy tắc bắt buộc giữa code và SVG: copy lệch (label, status value, duration, colour tone) xuất hiện ở cả FE lẫn BE — VD T-M400 (duration 90 chưa được BE hỗ trợ), T-M401 (CancelReason không khớp SVG), T-M402 (SkillCategory thiếu COOKING / ACADEMICS), T-M403 (booking-detail header lệch tone emerald so với SVG).
- Hệ quả: review kéo dài, drift giữa design và impl, designer tốn thời gian đối chiếu lại.

## Decision
1. **Mọi PR có thay đổi UI (FE) hoặc DTO / status-enum / error-message (BE) PHẢI tham chiếu SVG mockup tương ứng** trước khi merge:
   ```
   Matches screens-svg/{category}/{NN}-{name}.svg
   ```
   Nếu cố ý lệch, giải thích lý do trong commit body.
2. **CI gate bổ sung mục "Visual fidelity gate"** trong `docs/CI_CD.md` §5. Một PR không đính kèm đường dẫn SVG phù hợp → blocked cho đến khi được amend.
3. **Tracking orphan SVGs** trong `docs/PHASE2_PLUS_SVG_MANIFEST.md`. Mỗi sprint phải chọn file trong manifest backlog trước khi mở rộng scope.
4. **Drift detection bắt buộc** cho: status-enum (`BookingStatus`, `CancelReason`, `SkillCategory`), duration values, label casing (Title case / Sentence case), tone colour (emerald/brand-cta cho primary CTA, rose cho destructive, indigo cho web3).

## Consequences
- **Positive:**
  - Designer / developer luôn nhìn chung một nguồn sự thật.
  - Drift bị surface sớm thay vì sau khi đã merge.
  - PR có template chuẩn cho UI/DTO work, giảm thời gian review.
- **Negative:**
  - Thêm overhead ~1–2 phút / PR (mở SVG, đối chiếu).
  - Cần duy trì `PHASE2_PLUS_SVG_MANIFEST.md` mỗi sprint.
- **Mitigation:**
  - Visual Fidelity rule đã có sẵn từ trước (AGENTS.md §5.3); ADR này chỉ nâng cấp thành **enforced gate** thay vì **suggestion**.

## References
- AGENTS.md §5.3
- `docs/VISUAL_FIDELITY.md`
- `docs/CI_CD.md` §5 (CI gate)
- `docs/PHASE2_PLUS_SVG_MANIFEST.md`
- Sprint 5 retrospective (G3)
