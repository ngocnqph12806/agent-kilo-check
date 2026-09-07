'use client';

import { useSearchParams } from 'next/navigation';

export interface LoginSearchState {
  registered: boolean;
  reset: boolean;
  verified: boolean;
}

const EMPTY: LoginSearchState = { registered: false, reset: false, verified: false };

export function useLoginSearchState(): LoginSearchState {
  const params = useSearchParams();
  if (!params) return EMPTY;
  return {
    registered: params.get('registered') === '1',
    reset: params.get('reset') === '1',
    verified: params.get('verified') === '1'
  };
}
