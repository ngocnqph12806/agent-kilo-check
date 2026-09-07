'use client';

import { useQuery } from '@tanstack/react-query';

import { discover } from '../lib/discover-api';
import type { DiscoverFilters } from '../lib/schemas';

export function useDiscover(filters: DiscoverFilters, enabled = true) {
  return useQuery({
    queryKey: ['discover', filters],
    queryFn: () => discover(filters),
    enabled,
    staleTime: 30 * 1000,
    placeholderData: (previous) => previous
  });
}
