'use client';

import { useSearchParams } from 'next/navigation';
import { useEffect, useState } from 'react';

export function useResetTokenFromQuery(): string {
  const params = useSearchParams();
  const [token, setToken] = useState<string>('');
  useEffect(() => {
    if (params) {
      setToken(params.get('token') ?? '');
    }
  }, [params]);
  return token;
}
