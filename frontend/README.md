# SkillSeed Frontend

Next.js 15 (App Router) + TypeScript + TailwindCSS + shadcn/ui + Zustand.

> Spec: `.kiro/specs/phase-1-mvp/`
> Sprint 0 scaffold (T-M03). Auth flow pages land in T-M51..T-M56.

## Scripts

```bash
npm install
npm run dev        # localhost:3000
npm run lint
npm run typecheck
npm run build
npm start          # production
```

## Env

Copy `.env.example` to `.env.local` and fill values.

## Layout

```
frontend/
├── app/                  # App Router routes
│   ├── layout.tsx
│   ├── page.tsx
│   └── globals.css
├── components/
│   └── ui/               # shadcn/ui primitives
├── lib/
│   ├── api-client.ts     # axios instance + JWT interceptor
│   ├── query-provider.tsx
│   └── utils.ts          # cn() helper
└── stores/
    └── ui-store.ts       # Zustand stores
```
