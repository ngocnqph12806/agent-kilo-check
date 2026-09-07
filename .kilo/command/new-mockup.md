---
description: Create a new Mermaid mockup for a UI screen
agent: code
---
Create a new UI mockup file in `mockups/` following the SkillSeed convention.

Steps:
1. Read `mockups/README.md` for conventions (classDef tokens, subgraph style, annotations).
2. Ask user: category (00-marketing, 01-auth, …, 99-special-states), screen name (kebab-case), purpose.
3. Pick next NN sequence number within the chosen category.
4. Create `mockups/{category}/{NN}-{screen-name}.md` with:
   - Header: title, description, related US/FR IDs.
   - Mermaid `flowchart TB` with proper `subgraph` grouping.
   - `classDef` tokens (primary, secondary, danger, warning, muted, popular, success, privacy).
   - Annotations after diagram: validation rules, state variants, technical notes, tracking events.
5. Update `mockups/README.md` index table with new screen.
6. If SVG render needed, add placeholder in `screens-svg/{category}/{NN}-{screen-name}.svg`.

Conventions:
- File name: `{NN}-{screen-name}.md` (kebab-case).
- Use `==>` for primary flow, `-.->` for optional, `-->` for default.
- Decisions: `{(text)}` diamond.
- Inputs: `[/text/]` parallelogram.
- Outputs: `[\text\]` trapezoid.
