'use client';

import { useState } from 'react';

import { Button } from '@/components/ui/button';
import { useLogoutMutation } from '@/modules/auth/hooks/use-auth-mutations';
import { SkillsAutocomplete } from '@/modules/skills/components/skills-autocomplete';
import { cn } from '@/lib/utils';

import { DiscoverCard } from './discover-card';
import { DiscoverFiltersPanel } from './discover-filters';
import { useDiscover } from '../hooks/use-discover';
import type { DiscoverFilters } from '../lib/schemas';

export function DiscoverView() {
  const [filters, setFilters] = useState<DiscoverFilters>({});
  const [filtersOpen, setFiltersOpen] = useState(false);
  const discover = useDiscover(filters);
  const logout = useLogoutMutation();

  return (
    <main className="container mx-auto max-w-6xl py-6">
      <header className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Discover teachers</h1>
          <p className="text-sm text-muted-foreground">
            People who can teach skills you want to learn.
          </p>
        </div>
        <div className="flex gap-2">
          <Button asChild variant="outline" size="sm">
            <a href="/wallet">Wallet</a>
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => logout.mutate()}
            disabled={logout.isPending}
          >
            Sign out
          </Button>
        </div>
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
            <p className="py-12 text-center text-sm text-muted-foreground">Loading matches…</p>
          ) : null}

          {discover.isError ? (
            <p className="rounded-md border border-destructive/30 bg-destructive/5 p-4 text-sm text-destructive">
              Failed to load matches. {discover.error instanceof Error ? discover.error.message : ''}
            </p>
          ) : null}

          {discover.data && discover.data.items.length === 0 ? (
            <div className="rounded-lg border bg-card p-12 text-center">
              <h2 className="text-lg font-semibold">No matches yet</h2>
              <p className="mt-2 text-sm text-muted-foreground">
                Add more skills to your wanted list in onboarding to see better matches.
              </p>
              <Button asChild className="mt-4">
                <a href="/onboarding">Edit wanted skills</a>
              </Button>
            </div>
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

