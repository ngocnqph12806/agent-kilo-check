---
description: Create a new ADR (Architecture Decision Record)
agent: code
---
Create a new ADR file in `docs/ADR/` following the SkillSeed convention.

Steps:
1. Read existing ADRs in `docs/ADR/` to match style.
2. Determine next sequence number (zero-padded 4 digits, e.g., `0003`).
3. Ask user: title (kebab-case short), context (why deciding), options considered, decision, consequences, when to revisit.
4. Create `docs/ADR/{NNNN}-{slug}.md` with frontmatter:
   - Status: Proposed | Accepted | Deprecated | Superseded
   - Date: YYYY-MM-DD
   - Phase: 1 | 2 | 3 | 4 | 5
   - Deciders: list
5. Sections: Context, Decision, Rationale, Consequences (Positive/Negative), Alternatives Considered, When to Revisit, References.
6. Cross-link relevant phase specs (`.kiro/specs/phase-N-*/design.md`) and master docs.

Conventions:
- Filename: `{NNNN}-{kebab-title}.md` (NNNN = 4-digit sequence).
- One decision per ADR. If a decision supersedes another, mark old as `Superseded by NNNN`.
- Include "When to Revisit" with measurable triggers (MAU, team size, latency, etc.).
