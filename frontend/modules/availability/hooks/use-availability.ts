'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import {
  listMyAvailability,
  replaceMyAvailability
} from '../lib/availability-api';
import type { AvailabilityState } from '../lib/schemas';

const KEY = ['availability', 'me'] as const;

export function useMyAvailability(enabled = true) {
  return useQuery({
    queryKey: KEY,
    queryFn: listMyAvailability,
    enabled,
    staleTime: 60 * 1000
  });
}

export function useReplaceAvailability() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (state: AvailabilityState) => replaceMyAvailability(state),
    onSuccess: (data) => {
      qc.setQueryData(KEY, data);
    }
  });
}
