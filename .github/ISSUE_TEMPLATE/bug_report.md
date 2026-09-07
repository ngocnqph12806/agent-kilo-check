name: Bug Report
description: Báo cáo bug hoặc regression
labels: ["bug", "needs-triage"]
assignees: []

body:
  - type: markdown
    attributes:
      value: |
        Cảm ơn bạn đã báo cáo bug. Vui lòng điền đầy đủ thông tin bên dưới.

  - type: input
    id: phase
    attributes:
      label: Phase
      description: Project phase
      options:
        - "Phase 0 — Validation"
        - "Phase 1 — MVP"
        - "Phase 2 — AI"
        - "Phase 3 — Scale"
        - "Phase 4 — Expansion"
        - "Phase 5 — Vision"
        - "Không xác định"

  - type: textarea
    id: repro
    attributes:
      label: Steps to Reproduce
      description: Mô tả chi tiết các bước để tái hiện bug
      placeholder: |
        1. Truy cập '...'
        2. Click vào '...'
        3. Scroll đến '...'
        4. Thấy error
    validations:
      required: true

  - type: textarea
    id: expected
    attributes:
      label: Expected Behavior
      placeholder: Mô tả hành vi mong đợi
    validations:
      required: true

  - type: textarea
    id: actual
    attributes:
      label: Actual Behavior
      placeholder: Mô tả hành vi thực tế (kèm screenshot / log nếu có)
    validations:
      required: true

  - type: input
    id: env
    attributes:
      label: Environment
      description: |
        Browser / OS / App version / User ID (nếu có)
      placeholder: |
        - Chrome 124 / macOS 14.4
        - SkillSeed v0.1.2
        - User ID: 12345
    validations:
      required: true

  - type: textarea
    id: spec
    attributes:
      label: Spec / Acceptance Criteria liên quan
      description: Link tới FR/US/Acceptance trong `.kiro/specs/` nếu có
      placeholder: |
        - `.kiro/specs/phase-1-mvp/requirements.md` FR-M05

  - type: checkboxes
    id: checklist
    attributes:
      label: Triage Checklist
      options:
        - label: Bug tái hiện được trên main
          required: false
        - label: Có screenshot / video / log
          required: false
        - label: Đã check không phải duplicate issue
          required: true
        - label: Gắn label severity (P0/P1/P2)
          required: false
