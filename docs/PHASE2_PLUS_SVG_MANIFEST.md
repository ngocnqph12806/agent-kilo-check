# Phase 2+ SVG Manifest — Orphan Mockups (T-M421)

> **Status:** Snapshot 2026-09-09.
> Tracks every `screens-svg/*.svg` file that **does not yet back a live UI / DTO**
> at the close of Sprint 5. These are scheduled for Phase 2+ sprints; any work on
> them must follow AGENTS.md §5.3 (Visual Fidelity Rule).

## Phase 1 — wired (12 SVGs)
These are already implemented per §5.3 references in code:

| Path | Status |
|---|---|
| `00-marketing/01-landing.svg` | Live (T-M423) |
| `01-auth/01-signup.svg` | Live (FE register flow, T-M406) |
| `01-auth/02-login.svg` | Live (T-M405 SIWE + Apple/Google/email) |
| `03-discover/01-discover.svg` | Live (T-M404 shared states) |
| `04-booking/01-booking-modal.svg` | Live |
| `04-booking/02-booking-confirmation.svg` | Live (T-M400 duration 15–90) |
| `04-booking/04-booking-detail.svg` | Live (T-M403 gradient header) |
| `04-booking/06-cancel.svg` | Live (T-M401 enriched cancel reasons) |
| `09-pods/01-discover.svg` | Empty-state (T-M424) |
| `10-events/01-list.svg` | Empty-state (T-M424) |
| `99-special-states/01-empty-discover.svg` | Live (`<EmptyState>`) |
| `99-special-states/02-empty-bookings.svg` | Live (`<EmptyState>`) |

## Phase 2 — scheduled (next sprints)
- `00-marketing/02-privacy.svg`, `04-pricing`, `05-about`, `06-blog-list`, `08-cookie-banner`
- `01-auth/03-verify-email`, `04-forgot-reset-password`
- `02-onboarding/01..08` (welcome → done)
- `03-discover/02-filters`, `03-search-results`, `04-cross-border`
- `04-booking/03-bookings-list`, `05-reschedule`, `06-cancel` (pro)
- `05-video-session/01..06`
- `06-profile/01..05`
- `07-wallet/01..05`
- `08-notifications/01-list`
- `11-settings/01..12`

## Phase 3 — premium / growth
- `12-premium/01..06`

## Phase 4 — marketplace / B2B / admin / voice / AR-VR / support
- `13-marketplace/01..06`
- `14-b2b/01..14`
- `15-admin/01..12`
- `16-voice/01..04`
- `17-ar-vr/01..06`
- `18-support/01..05`

## Special states backlog
- `99-special-states/03-empty-wallet`, `04-empty-notif`
- `99-special-states/05-loading-discover`, `06-loading-profile`
- `99-special-states/07-404`, `08-500`, `09-offline`, `10-maintenance`
- `99-special-states/11-toast-success`, `12-toast-error`

**Total tracked:** 130 SVGs. **Wired:** 12. **Backlog:** 118 across Phase 2+.
