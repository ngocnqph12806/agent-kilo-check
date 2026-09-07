name: Feature Request
description: Đề xuất tính năng mới
labels: ["enhancement", "needs-triage"]

body:
  - type: markdown
    attributes:
      value: |
        **Trước khi tạo:** Đã check `.kiro/specs/phase-*/requirements.md` của phase liên quan chưa?
        Nếu feature đã có trong spec, tạo PR thay vì issue.

  - type: input
    id: phase
    attributes:
      label: Phase
      options:
        - "Phase 0 — Validation"
        - "Phase 1 — MVP"
        - "Phase 2 — AI"
        - "Phase 3 — Scale"
        - "Phase 4 — Expansion"
        - "Phase 5 — Vision"

  - type: textarea
    id: problem
    attributes:
      label: Problem Statement
      description: Vấn đề user gặp là gì?
    validations:
      required: true

  - type: textarea
    id: solution
    attributes:
      label: Proposed Solution
      description: Đề xuất cách giải quyết
    validations:
      required: true

  - type: textarea
    id: alternatives
    attributes:
      label: Alternatives Considered
      description: Phương án khác đã cân nhắc

  - type: textarea
    id: user-impact
    attributes:
      label: User Impact
      description: |
        - Ai bị ảnh hưởng? (Learner / Teacher / Expert / Admin / …)
        - Bao nhiêu user ước tính?
        - Có phải premium feature không?

  - type: textarea
    id: success-metrics
    attributes:
      label: Success Metrics
      description: Đo lường thế nào là thành công?
      placeholder: |
        - Tăng X% conversion
        - Giảm Y% support tickets
        - Z MAU mới

  - type: textarea
    id: out-of-scope
    attributes:
      label: Out of Scope
      description: Cái gì nằm NGOÀI phạm vi feature này

  - type: textarea
    id: spec-link
    attributes:
      label: Spec / Task IDs liên quan
      placeholder: |
        - `.kiro/specs/phase-2-ai-polish/design.md` §3
        - Task T-A12
