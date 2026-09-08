# ADR-002: Daily.co as the video provider

- **Status:** Accepted
- **Date:** 2026-08-15
- **Deciders:** Founder, Tech Lead
- **Related:** `.kiro/specs/phase-1-mvp/design.md` §5

## Context

Every SkillSeed session is a 15–60 minute live video call between a
teacher and a learner. We need:

1. WebRTC-based rooms that work cross-browser with no install.
3. Recording capability for dispute resolution.
3. Reasonable latency in Vietnam / Singapore.
4. A simple, well-documented REST API for room creation.
5. Predictable per-minute pricing for budgeting.

Alternatives considered: Jitsi (self-host), Twilio Video, Agora,
100ms, LiveKit Cloud.

## Decision

Use **Daily.co** for video rooms in Phase 1. Each booking gets a
short-lived room via the Daily REST API; the frontend mounts the room
via `@daily-co/daily-js`.

## Consequences

**Positive**
- One SDK, ~30 KB gzipped, integrates cleanly with React.
- Prebuilt UI component available (`@daily-co/daily-react`) — covers
  mute / camera / leave / device picker.
- Vietnam latency ~150–250 ms (measured).
- Recording can be enabled per-room via Daily REST.

**Negative**
- Vendor lock-in. We mitigate by abstracting calls behind
  `VideoProvider` interface (see `session/client/`).
- ~$0.004 / participant-minute. Phase 1 budget: $50/mo.
- No first-class whiteboard — we built an in-house canvas panel
  (`WhiteboardPanel`, T-M161).

**Reversible?** Yes — `VideoProvider` is an interface. A Jitsi
adapter is a Phase 2 task if costs become an issue.