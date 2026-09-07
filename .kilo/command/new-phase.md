---
description: Initialize spec for a new phase (Kiro format)
agent: code
---
Create the spec trio (requirements.md, design.md, tasks.md) for a new SkillSeed phase in Kiro format.

Steps:
1. Read `.kiro/specs/README.md` for conventions.
2. Ask user: phase number, phase code (V/M/A/S/X/5-), short name.
3. Create `.kiro/specs/phase-{N}-{name}/{requirements,design,tasks}.md`.
4. Each file header follows Kiro convention.
5. Update `.kiro/specs/README.md` index.
6. Cross-link with `SKILLSEED.md` and `SKILLSEED_API_AND_DB.md`.

Phase codes: V=0, M=1, A=2, S=3, X=4, 5-=5. Never add tech outside the canonical stack in `AGENTS.md` §2.
