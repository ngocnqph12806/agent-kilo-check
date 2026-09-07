'use client';

import { useEffect, useRef, useState } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import { useGoogleLoginMutation } from '../hooks/use-auth-mutations';
import { isGoogleConfigured, renderGoogleButton } from '../lib/google';

export interface GoogleSignInButtonProps {
  mode?: 'signin' | 'signup';
  className?: string;
  label?: string;
}

export function GoogleSignInButton({ mode = 'signin', className, label }: GoogleSignInButtonProps) {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [ready, setReady] = useState(false);
  const googleMutation = useGoogleLoginMutation();

  useEffect(() => {
    let cancelled = false;
    if (!isGoogleConfigured()) {
      setError('Google sign-in is not configured.');
      return;
    }

    renderGoogleButton(
      containerRef.current!,
      (idToken) => {
        if (cancelled) return;
        googleMutation.mutate(
          { idToken },
          {
            onError: (err) => {
              setError(err instanceof Error ? err.message : 'Google sign-in failed.');
            }
          }
        );
      },
      {
        type: 'standard',
        theme: 'outline',
        text: mode === 'signup' ? 'signup_with' : 'continue_with',
        size: 'large'
      }
    )
      .then(() => {
        if (!cancelled) setReady(true);
      })
      .catch((err) => {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : 'Failed to load Google sign-in.');
      });

    return () => {
      cancelled = true;
    };
  }, [mode, googleMutation]);

  const fallbackLabel = label ?? (mode === 'signup' ? 'Sign up with Google' : 'Continue with Google');

  return (
    <div className={cn('space-y-2', className)}>
      <div
        ref={containerRef}
        className={cn('flex justify-center', ready ? 'visible' : 'invisible h-0 overflow-hidden')}
        aria-label="Google sign-in"
      />
      {(!ready || error) && !isGoogleConfigured() ? (
        <Button variant="outline" type="button" className="w-full" disabled>
          Google sign-in unavailable
        </Button>
      ) : null}
      {(!ready || error) && isGoogleConfigured() ? (
        <Button variant="outline" type="button" className="w-full" disabled>
          {error ?? fallbackLabel}
        </Button>
      ) : null}
      {error ? <p className="text-xs text-destructive">{error}</p> : null}
    </div>
  );
}
