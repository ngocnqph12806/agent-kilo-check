import { Compass } from 'lucide-react';

import { EmptyState } from '@/components/shared';

// Visual Fidelity (AGENTS.md 5.3): Matches screens-svg/09-pods/01-discover.svg
// Pods feature is part of Phase 2 — for Phase 1 we render an empty-state
// with a clear CTA to /discover so users land on something actionable.

export default function PodsPage() {
  return (
    <main className="container max-w-4xl py-12">
      <EmptyState
        icon={Compass}
        emoji="🧭"
        title="Pods is coming in Phase 2"
        description="Group learning circles of 3-8 members rotating teaching duties each week. Browse teachers now and we'll notify you when Pods opens."
        action={{ label: 'Browse teachers', href: '/discover' }}
      />
    </main>
  );
}
