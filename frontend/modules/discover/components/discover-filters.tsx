'use client';

import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

import type { DiscoverFilters, DiscoverSortKey } from '../lib/schemas';

export interface DiscoverFiltersProps {
  value: DiscoverFilters;
  onChange: (next: DiscoverFilters) => void;
  className?: string;
}

const TIMEZONE_OFFSETS: { value: number; label: string }[] = [
  { value: -12, label: 'UTC−12 (Baker Island)' },
  { value: -11, label: 'UTC−11 (American Samoa)' },
  { value: -10, label: 'UTC−10 (Hawaii)' },
  { value: -9, label: 'UTC−9 (Alaska)' },
  { value: -8, label: 'UTC−8 (Pacific)' },
  { value: -7, label: 'UTC−7 (Mountain)' },
  { value: -6, label: 'UTC−6 (Central)' },
  { value: -5, label: 'UTC−5 (Eastern)' },
  { value: -4, label: 'UTC−4 (Atlantic)' },
  { value: -3, label: 'UTC−3 (Buenos Aires)' },
  { value: -2, label: 'UTC−2 (Mid-Atlantic)' },
  { value: -1, label: 'UTC−1 (Cape Verde)' },
  { value: 0, label: 'UTC+0 (London)' },
  { value: 1, label: 'UTC+1 (Berlin)' },
  { value: 2, label: 'UTC+2 (Cairo)' },
  { value: 3, label: 'UTC+3 (Moscow)' },
  { value: 4, label: 'UTC+4 (Dubai)' },
  { value: 5, label: 'UTC+5 (Karachi)' },
  { value: 6, label: 'UTC+6 (Dhaka)' },
  { value: 7, label: 'UTC+7 (Bangkok)' },
  { value: 8, label: 'UTC+8 (Singapore)' },
  { value: 9, label: 'UTC+9 (Tokyo)' },
  { value: 10, label: 'UTC+10 (Sydney)' },
  { value: 11, label: 'UTC+11 (Solomon)' },
  { value: 12, label: 'UTC+12 (Auckland)' },
  { value: 13, label: 'UTC+13 (Samoa)' },
  { value: 14, label: 'UTC+14 (Kiribati)' }
];

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
        <div className="space-y-1">
          <Label htmlFor="timezoneOffset">Timezone</Label>
          <select
            id="timezoneOffset"
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
            value={value.timezoneOffset ?? ''}
            onChange={(e) => {
              const raw = e.target.value;
              update({
                timezoneOffset: raw === '' ? undefined : Number(raw)
              });
            }}
          >
            <option value="">Any</option>
            {TIMEZONE_OFFSETS.map((tz) => (
              <option key={tz.value} value={tz.value}>
                {tz.label}
              </option>
            ))}
          </select>
          <p className="text-xs text-muted-foreground">
            Only teachers whose timezone matches this UTC offset.
          </p>
        </div>
        <div className="space-y-1">
          <Label htmlFor="sort">Sort by</Label>
          <select
            id="sort"
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
            value={value.sort ?? 'rating'}
            onChange={(e) =>
              update({ sort: e.target.value as DiscoverSortKey })
            }
          >
            <option value="rating">Top rated</option>
            <option value="sessions">Most sessions</option>
            <option value="recent">Recent activity</option>
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
