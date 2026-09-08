'use client';

import { useState } from 'react';
import Link from 'next/link';

import { Button } from '@/components/ui/button';

import { AuthShell } from '@/modules/auth/components/auth-shell';
import { FormAlert, FormError } from '@/modules/auth/components/form-status';

export function VerifyEmailPromptForm() {
  const [resent, setResent] = useState(false);

  const handleResend = () => {
    setResent(true);
  };

  return (
    <AuthShell
      title="Verify your email"
      description="Check your inbox for the SkillSeed welcome email and click the verification link to activate your account."
      footer={
        <p>
          Already verified?{' '}
          <Link href="/login" className="font-medium text-primary underline-offset-4 hover:underline">
            Back to sign in
          </Link>
        </p>
      }
    >
      <FormAlert message="We sent a verification link to your email right after you signed up. The link expires after 24 hours." />

      {resent ? (
        <FormAlert message="If your account is still pending verification, please check your inbox (and spam folder) for the original email." />
      ) : null}

      <ErrorResendHint visible={!resent} />

      <div className="space-y-2">
        <Button type="button" className="w-full" onClick={handleResend}>
          Resend verification email
        </Button>
        <Button asChild variant="outline" className="w-full">
          <Link href="/login">Back to sign in</Link>
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