'use client';

import { useEffect, useRef } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { CheckCircle2, XCircle, Loader2 } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { AuthShell } from '@/modules/auth/components/auth-shell';
import { useVerifyEmailMutation } from '@/modules/auth/hooks/use-auth-mutations';
import { getAuthErrorMessage } from '@/modules/auth/lib/auth-api';

export interface VerifyEmailFormProps {
  token: string;
}

export function VerifyEmailForm({ token }: VerifyEmailFormProps) {
  const router = useRouter();
  const verifyMutation = useVerifyEmailMutation();
  const hasFiredRef = useRef(false);

  useEffect(() => {
    if (!token || hasFiredRef.current || verifyMutation.isPending || verifyMutation.isSuccess) return;
    hasFiredRef.current = true;
    verifyMutation.mutate({ token });
  }, [token, verifyMutation]);

  if (verifyMutation.isPending) {
    return (
      <AuthShell
        title="Verifying your email"
        subtitle="Hang tight while we activate your account."
        hero={{ variant: 'auth' }}
      >
        <div className="flex flex-col items-center justify-center gap-3 py-8 text-[var(--brand-text-muted)]">
          <Loader2 className="h-10 w-10 animate-spin text-primary" aria-hidden />
          <p className="text-sm">Just a moment…</p>
        </div>
      </AuthShell>
    );
  }

  if (verifyMutation.isSuccess) {
    return (
      <AuthShell
        title="Email verified"
        subtitle="Your account is active. You can now sign in and complete your profile."
        hero={{ variant: 'auth' }}
      >
        <div className="flex flex-col items-center gap-3 py-4">
          <CheckCircle2 className="h-12 w-12 text-primary" aria-hidden />
          <div className="flex w-full flex-col gap-2">
            <Button
              asChild
              className="h-12 w-full rounded-full bg-brand-cta text-base font-semibold text-white shadow-brand-cta hover:opacity-95"
            >
              <Link href="/login?verified=1">Go to sign in</Link>
            </Button>
            <Button asChild variant="outline" className="h-11 w-full rounded-full">
              <Link href="/onboarding">Start onboarding</Link>
            </Button>
          </div>
        </div>
      </AuthShell>
    );
  }

  if (verifyMutation.isError) {
    return (
      <AuthShell
        title="Verification failed"
        subtitle={getAuthErrorMessage(
          verifyMutation.error,
          'The link is invalid or has expired. Request a new one from the sign-in screen.'
        )}
        hero={{ variant: 'auth' }}
      >
        <div className="flex flex-col items-center gap-3 py-4">
          <XCircle className="h-12 w-12 text-[var(--brand-rose)]" aria-hidden />
          <Button asChild variant="outline" className="h-11 w-full rounded-full">
            <Link href="/login">Back to sign in</Link>
          </Button>
        </div>
      </AuthShell>
    );
  }

  return (
    <AuthShell
      title="Email verification"
      subtitle="Preparing to verify your account…"
      hero={{ variant: 'auth' }}
    >
      <Loader2 className="mx-auto h-8 w-8 animate-spin text-primary" aria-hidden />
      <Button
        variant="ghost"
        size="sm"
        className="mt-2"
        onClick={() => router.replace('/login')}
      >
        Cancel
      </Button>
    </AuthShell>
  );
}
