---
description: Find any documentation conflict or inconsistency
agent: code
---
Audit the SkillSeed documentation for inconsistencies that could confuse AI agents or new developers.

Steps:
1. Read these docs in full:
   - `AGENTS.md`
   - `SKILLSEED.md`
   - `SKILLSEED_API_AND_DB.md`
   - `SKILLSEED_CODE_SKELETON.md`
   - `SKILLSEED_CLOUD_COST.md`
   - `.kiro/specs/README.md`
   - All `.kiro/specs/phase-*/requirements.md`, `design.md`, `tasks.md`
   - `docs/GLOSSARY.md`, `docs/ARCHITECTURE.md`, `docs/GOTCHAS.md`
   - All `docs/ADR/*.md`
2. Check for:
   - API routes mismatch between `SKILLSEED_API_AND_DB.md` and phase `design.md`.
   - DB table/column name conflicts (e.g., users vs user).
   - Tech stack contradictions (e.g., MySQL in one doc, PostgreSQL in another).
   - Phase scope violations (Phase 1 features mentioned in Phase 2 specs).
   - Stale "Last updated" dates older than 60 days.
   - Broken cross-links (`file.md` not found).
   - Missing term definitions (term used in docs but not in GLOSSARY).
3. Report findings in a structured list:
   - Severity: CRITICAL | HIGH | LOW
   - Source A vs Source B (cite exact lines)
   - Recommended fix
   - Phase spec wins by default (per `AGENTS.md` §6 rule).
4. Do NOT make changes. Just report. User decides what to fix.
