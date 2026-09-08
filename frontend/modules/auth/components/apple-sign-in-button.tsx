'use client';

import { useEffect, useState } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import { useAppleLoginMutation } from '../hooks/use-auth-mutations';

declare global {
  interface Window {
    AppleID?: {
      auth?: {
        init: (config: {
          clientId: string;
          scope?: string;
          redirectURI?: string;
          state?: string;
          usePopup?: boolean;
        }) => void;
        signIn: () => Promise<{
          authorization?: {
            id_token?: string;
            code?: string;
          };
          user?: { name?: { firstName?: string; lastName?: string }; email?: string };
        }>;
      };
    };
  }
}

const APPLE_CLIENT_ID = process.env.NEXT_PUBLIC_APPLE_CLIENT_ID;
const APPLE_SDK_SRC = 'https://appleid.cdn-apple.com/appleauth/static/jsapi/appleid/1/en_US/appleid.auth.js';

export interface AppleSignInButtonProps {
  mode?: 'signin' | 'signup';
  className?: string;
  label?: string;
}

export function AppleSignInButton({ mode = 'signin', className, label }: AppleSignInButtonProps) {
  const [error, setError] = useState<string | null>(null);
  const [ready, setReady] = useState(false);
  const [sdkFailed, setSdkFailed] = useState(false);
  const mutation = useAppleLoginMutation();

  useEffect(() => {
    if (!APPLE_CLIENT_ID) {
      setSdkFailed(true);
      setError('Apple sign-in is not configured (missing NEXT_PUBLIC_APPLE_CLIENT_ID).');
      return;
    }
    if (typeof window === 'undefined') return;
    if (window.AppleID?.auth) {
      try {
        window.AppleID.auth.init({
          clientId: APPLE_CLIENT_ID,
          scope: 'name email',
          usePopup: true
        });
        setReady(true);
      } catch {
        setSdkFailed(true);
      }
      return;
    }
    const existing = document.querySelector<HTMLScriptElement>(`script[src="${APPLE_SDK_SRC}"]`);
    const onLoad = () => {
      try {
        window.AppleID?.auth?.init({ clientId: APPLE_CLIENT_ID!, scope: 'name email', usePopup: true });
        setReady(true);
      } catch {
        setSdkFailed(true);
      }
    };
    if (existing) {
      existing.addEventListener('load', onLoad);
      return () => existing.removeEventListener('load', onLoad);
    }
    const script = document.createElement('script');
    script.src = APPLE_SDK_SRC;
    script.async = true;
    script.onload = onLoad;
    script.onerror = () => {
      setSdkFailed(true);
      setError('Apple sign-in is only available on Apple-approved domains or iOS/macOS apps.');
    };
    document.head.appendChild(script);
    return () => {
      script.onload = null;
    };
  }, []);

  const fallbackLabel = label ?? (mode === 'signup' ? 'Sign up with Apple' : 'Continue with Apple');

  const handleClick = async () => {
    setError(null);
    if (!ready || !window.AppleID?.auth) return;
    try {
      const result = await window.AppleID.auth.signIn();
      const idToken = result.authorization?.id_token;
      if (!idToken) {
        setError('Apple sign-in did not return an id_token.');
        return;
      }
      mutation.mutate(idToken, {
        onError: (err) => {
          setError(err instanceof Error ? err.message : 'Apple sign-in failed.');
        }
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Apple sign-in failed.');
    }
  };

  return (
    <div className={cn('space-y-2', className)}>
      <Button
        type="button"
        variant="outline"
        className="w-full"
        onClick={handleClick}
        disabled={!ready || sdkFailed || mutation.isPending}
      >
        {mutation.isPending ? 'Signing in…' : fallbackLabel}
      </Button>
      {error ? <p className="text-xs text-destructive">{error}</p> : null}
    </div>
  );
}
