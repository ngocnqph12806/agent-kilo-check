# SkillSeed Pull Request

> **AI Agent:** Trước khi mở PR, đọc `AGENTS.md` §7 (Definition of Done) và §8 (Workflow).

## Summary
<!-- 1–3 câu tóm tắt thay đổi -->

## Related
- Issue: #
- Spec: `.kiro/specs/phase-?/requirements.md` → FR-?, US-?
- Task ID: T-?
- ADR (nếu có): `docs/ADR/NNNN-*.md`

## Type of Change
- [ ] Feature (non-breaking)
- [ ] Bug fix (non-breaking)
- [ ] Breaking change (cần migration / spec update)
- [ ] Docs / Spec update
- [ ] Refactor (no behavior change)
- [ ] CI / Infra

## Scope
- Module(s) affected: `auth`, `booking`, `wallet`, `frontend`, …

## Test Plan
<!-- Mô tả cách reviewer có thể verify thay đổi -->
- [ ] Đã chạy `mvn verify` (backend) — pass
- [ ] Đã chạy `npm run lint && npm test` (frontend) — pass
- [ ] Đã chạy `mvn checkstyle:check` — pass
- [ ] Unit test mới cho code mới
- [ ] Integration test nếu thay đổi API
- [ ] Đã test thủ công trên local (kèm steps)
- [ ] Migration script test rollback (nếu có)

## UI Changes (nếu có)
- [ ] Mockup trong `mockups/{category}/` đã update
- [ ] Screenshot / video đính kèm

## Documentation
- [ ] API contract update trong `SKILLSEED_API_AND_DB.md` (nếu đổi endpoint)
- [ ] DB schema change → Flyway migration mới trong `backend/src/main/resources/db/migration/`
- [ ] Spec update (nếu thay đổi scope) — link PR spec
- [ ] `AGENTS.md` / `docs/GLOSSARY.md` update (nếu thêm convention)

## Checklist
- [ ] Code theo convention (`AGENTS.md` §5)
- [ ] Commit theo Conventional Commits
- [ ] Branch theo format `{type}/{phase}-{short-desc}`
- [ ] Không có secret / API key / password
- [ ] Không có `console.log` / `System.out.println` / TODO / FIXME
- [ ] Không có import wildcard
- [ ] Không thêm dependency ngoài stack canonical (`AGENTS.md` §2)
- [ ] Self-review trước khi request review

## Screenshots / Recordings
<!-- Nếu UI, paste ảnh / GIF / link video -->

---

**Reviewer:** @skillseed/backend-lead | @skillseed/frontend-lead | @skillseed/product-team
