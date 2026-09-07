'use client';

import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

import type { DiscoverFilters } from '../lib/schemas';

export interface DiscoverFiltersProps {
  value: DiscoverFilters;
  onChange: (next: DiscoverFilters) => void;
  className?: string;
}

export function DiscoverFiltersPanel({ value, onChange, className }: DiscoverFiltersProps) {
  function update(patch: Partial<DiscoverFilters>) {
    onChange({ ...value, ...patch, page: 0 });
  }

  return (
    <aside className={className}>
      <div className="space-y-4 rounded-lg border bg-card p-4">
        <div className="space-y-1">
          <Label htmlFor="language">Language</Label>
          <Input
            id="language"
            value={value.language ?? ''}
            placeholder="en, vi, es…"
            onChange={(e) => update({ language: e.target.value })}
          />
        </div>
        <div className="space-y-1">
          <Label htmlFor="country">Country (ISO-2)</Label>
          <Input
            id="country"
            maxLength={2}
            value={value.country ?? ''}
            placeholder="VN, US…"
            onChange={(e) =>
              update({ country: e.target.value.toUpperCase() })
            }
          />
        </div>
        <div className="space-y-1">
          <Label htmlFor="minRating">Minimum rating</Label>
          <select
            id="minRating"
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
            value={value.minRating ?? 0}
            onChange={(e) =>
              update({ minRating: Number(e.target.value) })
            }
          >
            <option value={0}>Any</option>
            <option value={3}>3+ ⭐</option>
            <option value={4}>4+ ⭐</option>
            <option value={5}>5 ⭐</option>
          </select>
        </div>
        <button
          type="button"
          onClick={() => onChange({})}
          className="text-xs font-medium text-primary hover:underline"
        >
          Reset filters
        </button>
      </div>
    </aside>
  );
}
