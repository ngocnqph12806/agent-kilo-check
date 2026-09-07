'use client';

import { useEffect, useRef } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

import { Button } from '@/components/ui/button';

import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
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

  return (
    <main className="flex min-h-screen items-center justify-center bg-muted/40 px-4 py-12">
      <div className="w-full max-w-md rounded-lg border bg-card p-8 text-center shadow-sm">
        <h1 className="mb-4 text-2xl font-semibold tracking-tight">Email verification</h1>

        {verifyMutation.isPending ? (
          <Alert>
            <AlertDescription>Verifying your email…</AlertDescription>
          </Alert>
        ) : null}

        {verifyMutation.isSuccess ? (
          <Alert>
            <AlertTitle>Email verified</AlertTitle>
            <AlertDescription>
              Your account is active. You can now sign in and complete your profile.
            </AlertDescription>
            <div className="mt-4 flex justify-center gap-2">
              <Button asChild>
                <Link href="/login?verified=1">Go to sign in</Link>
              </Button>
              <Button variant="outline" onClick={() => router.replace('/onboarding')} type="button">
                Start onboarding
              </Button>
            </div>
          </Alert>
        ) : null}

        {verifyMutation.isError ? (
          <Alert variant="destructive">
            <AlertTitle>Verification failed</AlertTitle>
            <AlertDescription>
              {getAuthErrorMessage(
                verifyMutation.error,
                'The link is invalid or has expired. Request a new one from the sign-in screen.'
              )}
            </AlertDescription>
            <div className="mt-4">
              <Button asChild variant="outline">
                <Link href="/login">Back to sign in</Link>
              </Button>
            </div>
          </Alert>
        ) : null}
      </div>
    </main>
  );
}
