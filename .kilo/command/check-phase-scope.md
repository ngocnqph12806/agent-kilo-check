---
description: Check if a feature is in scope for the current phase
agent: code
---
Determine whether a proposed feature/scope change is allowed for the current SkillSeed phase.

Steps:
1. Read `.kiro/specs/README.md` to identify current phase.
2. Read `.kiro/specs/phase-{current}/{requirements,design}.md` for scope.
3. Check feature against:
   - "Out of Scope" section.
   - Phase tech-stack canonical list (`AGENTS.md` §2).
   - Existing ADRs in `docs/ADR/`.
4. Search for the feature in later phase specs to see if it's planned.
5. Return verdict:
   - ALLOWED: in scope for current phase, proceed.
   - DEFER: planned for later phase (specify which), park it.
   - SCOPE_CREEP: not in any spec, needs ADR + spec update first.
6. Always cite specific FR/US/Out-of-Scope line that supports the verdict.
