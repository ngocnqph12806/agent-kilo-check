'use client';

import { useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';

import { setOnUnauthorized } from '@/lib/api-client';

import { useAuthStore } from '../stores/auth-store';

export function AuthInitializer() {
  const hydrate = useAuthStore((state) => state.hydrate);
  const reset = useAuthStore((state) => state.reset);
  const hydratedRef = useRef(false);
  const router = useRouter();

  useEffect(() => {
    if (hydratedRef.current) return;
    hydratedRef.current = true;
    hydrate();
  }, [hydrate]);

  useEffect(() => {
    setOnUnauthorized(() => {
      reset();
      if (typeof window !== 'undefined' && !window.location.pathname.startsWith('/login')) {
        router.replace('/login');
      }
    });
    return () => setOnUnauthorized(null);
  }, [reset, router]);

  return null;
}
