# SkillSeed Frontend

Next.js 15 (App Router) + TypeScript + TailwindCSS + shadcn/ui +
TanStack Query + Zustand.

> **Spec:** `.kiro/specs/phase-1-mvp/`
> **Deploy:** see `docs/DEPLOYMENT.md` §3.
> **Wireframes / mockups:** `mockups/README.md` + `screens-svg/`.

---

## Scripts

```bash
npm install
npm run dev        # localhost:3000
npm run lint       # next lint
npm run typecheck  # tsc --noEmit
npm run test       # jest --passWithNoTests
npm run build
npm start          # production
```

---

## Env

Copy `.env.example` to `.env.local` for local dev, or set values in
Vercel for production. See `docs/DEPLOYMENT.md` §1.2.

Required for local dev:
- `NEXT_PUBLIC_API_URL=http://localhost:8080`
- `NEXT_PUBLIC_APP_URL=http://localhost:3000`

---

## Layout

```
frontend/
├── app/                          # App Router routes
│   ├── (app)/                    # authenticated shell
│   │   ├── bookings/
│   │   ├── discover/
│   │   ├── onboarding/
│   │   ├── settings/privacy/     # GDPR danger zone (T-M200)
│   │   ├── users/{id}/           # public profile
│   │   └── wallet/
│   ├── (auth)/                   # guest-only shell
│   │   ├── forgot-password/
│   │   ├── login/
│   │   ├── register/
│   │   ├── reset-password/
│   │   └── verify-email/
│   ├── privacy/                  # legal page (T-M203)
│   ├── terms/                    # legal page (T-M203)
│   ├── error.tsx                 # 500 boundary (T-M204)
│   ├── not-found.tsx             # 404 page (T-M204)
│   ├── robots.ts                 # T-M207
│   ├── sitemap.ts                # T-M207
│   ├── globals.css
│   ├── layout.tsx                # root layout + QueryProvider
│   └── page.tsx                  # marketing home
│
├── components/
│   ├── ui/                       # shadcn primitives (button, input,
│   │                             # alert, skeleton, spinner)
│   └── shared/                   # cross-module components
│       ├── cookie-consent-banner.tsx    # T-M202
│       ├── network-error-screen.tsx     # T-M204
│       ├── async-boundary.tsx           # T-M205
│       ├── feedback-launcher.tsx        # T-M222
│       └── hooks/use-network-state.ts
│
├── lib/
│   ├── api-client.ts             # axios + JWT interceptor
│   ├── metadata.ts               # buildPageMetadata helper (T-M206)
│   ├── query-provider.tsx
│   └── utils.ts                  # cn() helper
│
├── modules/                      # feature modules (auth, booking,
│   │                             # discover, profile, rating,
│   │                             # session, skills, wallet, ...)
│   ├── auth/
│   ├── booking/
│   ├── discover/
│   ├── notifications/
│   ├── onboarding/
│   ├── profile/                  # includes GDPR API + hooks
│   ├── rating/
│   ├── session/
│   ├── skills/
│   ├── availability/
│   └── wallet/
│
└── stores/
    └── ui-store.ts               # Zustand stores
```

---

## Conventions

- **File naming:** kebab-case (`booking-card.tsx`, `use-wallet.ts`).
- **Component naming:** PascalCase (`BookingCard`).
- **Exports:** named for components, default only for Next.js pages.
- **Data fetching:** TanStack Query (`useQuery`/`useMutation`).
- **State:** Zustand for cross-page UI state only; everything else in
  React state or the URL.
- **Styling:** TailwindCSS + `cn()` helper + cva for variants. No
  hard-coded colors.
- **Reuse:** any UI element used in ≥ 2 places → `components/ui` or
  `components/shared`. See `AGENTS.md` §5.3.
- **Imports:** ordered (React → external → internal → relative).
- **Types:** strict mode; no `any` without explanation.
- **Comments:** no comments unless they document a non-obvious
  decision.

---

## Adding a page

1. Create `app/<route>/page.tsx`.
2. Add server `metadata = buildPageMetadata({ title, description })`.
3. For data, use TanStack Query inside a `'use client'` child component.
4. Wrap async UI in `<AsyncBoundary>` for loading + error states.
5. Add the new route to `app/sitemap.ts`.

---

## Adding a component

- Domain-specific → `modules/<feature>/components/<name>.tsx`
- Generic (≥ 2 modules use it) → `components/shared/<name>.tsx`
  (or `components/ui/` if it's a primitive)
- 1 component per file, kebab-case filename, PascalCase export.
- Add a barrel `index.ts` if the folder has ≥ 3 files.

---

## Connecting to the API

The `apiClient` (lib/api-client.ts) is preconfigured with the JWT
interceptor and 401 redirect. Just import and call:

```ts
import { apiClient } from '@/lib/api-client';

const { data } = await apiClient.get('/users/me');
```

In dev, requests to `/api/backend/*` are rewritten to
`http://localhost:8080/api/v1/*` via `next.config.mjs`. In prod they
hit `https://api.skillseed.app/api/v1/*`.

---

## Testing

- Unit tests live alongside components as `*.test.ts(x)`.
- Coverage target: **50%** lines for Phase 1 DoD.
- E2E smoke tests: see `backend/src/test/java/com/skillseed/e2e/` (they
  hit this app via HTTP).

---

## Deploying

Auto-deploys via Vercel on merge to `main`. PR previews are
automatic.

Manual promote: Vercel dashboard → Deployments → Promote to Production.

---

## Troubleshooting

| Symptom | Try |
|---|---|
| `Network Error` in dev | `docker compose up -d` then `mvn spring-boot:run` |
| 401 on every request | clear `localStorage.skillseed.access_token` and sign in again |
| Hydration mismatch | check for `Date.now()` / `Math.random()` in render |
| Build fails on typecheck | run `npm run typecheck` locally first |