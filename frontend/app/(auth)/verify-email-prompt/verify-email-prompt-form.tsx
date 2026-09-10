'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

import { Button } from '@/components/ui/button';

import { AuthShell } from '@/modules/auth/components/auth-shell';
import { FormAlert, FormError } from '@/modules/auth/components/form-status';
import { useAuthStore } from '@/modules/auth/stores/auth-store';

export function VerifyEmailPromptForm() {
  const [resent, setResent] = useState(false);
  const reset = useAuthStore((state) => state.reset);
  const router = useRouter();

  const handleResend = () => {
    setResent(true);
  };

  // The user is technically logged in here (AuthGuard bounces them in once
  // they hold an access token but are unverified). A naked `<Link href="/login">`
  // would re-enter GuestGuard, which sees the still-present user and redirects
  // to /discover, which AuthGuard then bounces right back here — so the page
  // appears to reload itself. Clear the session locally before navigating so
  // /login can actually render.
  const handleBackToSignIn = (
    event?: React.MouseEvent<HTMLAnchorElement | HTMLButtonElement>
  ) => {
    event?.preventDefault();
    reset();
    router.replace('/login');
  };

  return (
    <AuthShell
      title="Verify your email"
      subtitle="Check your inbox for the SkillSeed welcome email and click the verification link to activate your account."
      footer={
        <p>
          Already verified?{' '}
          <Link
            href="/login"
            onClick={handleBackToSignIn}
            className="font-semibold text-primary underline-offset-4 hover:underline"
          >
            Back to sign in
          </Link>
        </p>
      }
      hero={{ variant: 'auth' }}
    >
      <FormAlert message="We sent a verification link to your email right after you signed up. The link expires after 24 hours." />

      {resent ? (
        <FormAlert message="If your account is still pending verification, please check your inbox (and spam folder) for the original email." />
      ) : null}

      <ErrorResendHint visible={!resent} />

      <div className="space-y-2">
        <Button
          type="button"
          variant="brand" className="h-12 w-full rounded-full text-base font-semibold"
          onClick={handleResend}
        >
          Resend verification email
        </Button>
        <Button asChild variant="outline" className="h-11 w-full rounded-full">
          <Link href="/login" onClick={handleBackToSignIn}>
            Back to sign in
          </Link>
        </Button>
      </div>
    </AuthShell>
  );
}

function ErrorResendHint({ visible }: { visible: boolean }) {
  if (!visible) return null;
  return (
    <FormError message="Resending verification emails is not available yet. Use the original link from your signup email, or contact support if it has expired." />
  );
}