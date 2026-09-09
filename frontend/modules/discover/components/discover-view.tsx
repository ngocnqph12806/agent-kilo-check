'use client';

import { Search } from 'lucide-react';
import { Suspense, useEffect, useState } from 'react';
import { useSearchParams } from 'next/navigation';

import { EmptyState, ErrorState, LoadingState } from '@/components/shared';
import { SkillsAutocomplete } from '@/modules/skills/components/skills-autocomplete';
import { cn } from '@/lib/utils';

import { DiscoverCard } from './discover-card';
import { DiscoverFiltersPanel } from './discover-filters';
import { useDiscover } from '../hooks/use-discover';
import type { DiscoverFilters } from '../lib/schemas';

export function DiscoverView() {
  return (
    <Suspense fallback={null}>
      <DiscoverContent />
    </Suspense>
  );
}

function DiscoverContent() {
  const searchParams = useSearchParams();
  const [filters, setFilters] = useState<DiscoverFilters>({});
  const [filtersOpen, setFiltersOpen] = useState(false);
  const discover = useDiscover(filters);

  useEffect(() => {
    const param = searchParams?.get('skill');
    if (param && filters.skill !== param) {
      setFilters((prev) => ({ ...prev, skill: param }));
    }
  }, [searchParams, filters.skill]);

  return (
    <main className="container mx-auto max-w-6xl py-6">
      <header className="mb-6">
        <h1 className="text-3xl font-bold tracking-tight">Discover teachers</h1>
        <p className="text-sm text-muted-foreground">
          People who can teach skills you want to learn.
        </p>
      </header>

      <div className="grid gap-6 lg:grid-cols-[260px_1fr]">
        <aside className="hidden lg:block">
          <DiscoverFiltersPanel value={filters} onChange={setFilters} />
        </aside>

        <section className="space-y-4">
          <div className="rounded-lg border bg-card p-4">
            <label htmlFor="skill-filter" className="mb-2 block text-sm font-medium">
              Looking for a specific skill?
            </label>
            <SkillsAutocomplete
              value={null}
              onChange={(skill) => setFilters({ ...filters, skill: skill?.id, page: 0 })}
              placeholder="Type to filter by skill…"
            />
          </div>

          <div className="rounded-lg border bg-card lg:hidden">
            <button
              type="button"
              className="flex w-full items-center justify-between px-4 py-3 text-sm font-medium"
              onClick={() => setFiltersOpen((open) => !open)}
            >
              Filters
              <span aria-hidden>{filtersOpen ? '−' : '＋'}</span>
            </button>
            <div className={cn('border-t p-4', !filtersOpen && 'hidden')}>
              <DiscoverFiltersPanel value={filters} onChange={setFilters} />
            </div>
          </div>

          {discover.isLoading ? (
            <LoadingState label="Loading matches…" rows={3} />
          ) : null}

          {discover.isError ? (
            <ErrorState
              title="Could not load matches"
              message={
                discover.error instanceof Error
                  ? discover.error.message
                  : 'Please try again in a moment.'
              }
              onRetry={() => discover.refetch()}
            />
          ) : null}

          {discover.data && discover.data.items.length === 0 ? (
            <EmptyState
              icon={Search}
              title="No matches yet"
              description="Add more skills to your wanted list in onboarding to see better matches."
              action={{ label: 'Edit wanted skills', href: '/onboarding' }}
            />
          ) : null}

          {discover.data && discover.data.items.length > 0 ? (
            <>
              <p className="text-xs text-muted-foreground">
                Showing {discover.data.items.length} of {discover.data.totalElements} match
                {discover.data.totalElements === 1 ? '' : 'es'}
              </p>
              <div className="grid gap-4 sm:grid-cols-2">
                {discover.data.items.map((match) => (
                  <DiscoverCard key={match.userId} match={match} />
                ))}
              </div>
            </>
          ) : null}
        </section>
      </div>
    </main>
  );
}


