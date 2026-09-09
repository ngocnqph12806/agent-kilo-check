import { Calendar } from 'lucide-react';

import { EmptyState } from '@/components/shared';

// Visual Fidelity (AGENTS.md 5.3): Matches screens-svg/10-events/01-list.svg
// Events feature ships after Pods in Phase 2 — render an empty-state
// consistent with 99-special-states/02-empty-bookings.svg style.

export default function EventsPage() {
  return (
    <main className="container max-w-4xl py-12">
      <EmptyState
        icon={Calendar}
        emoji="📅"
        title="Events is coming in Phase 2"
        description="Multi-hour community gatherings around shared topics — language tables, study jams, co-working. Browse sessions now and we'll let you know when Events opens."
        action={{ label: 'Browse teachers', href: '/discover' }}
      />
    </main>
  );
}
