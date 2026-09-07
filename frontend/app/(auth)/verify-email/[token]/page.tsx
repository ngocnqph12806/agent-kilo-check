import { Suspense } from 'react';

import { VerifyEmailForm } from './verify-email-form';

interface VerifyEmailPageProps {
  params: Promise<{ token: string }>;
}

export const dynamic = 'force-dynamic';

export default async function VerifyEmailPage({ params }: VerifyEmailPageProps) {
  const { token } = await params;
  return (
    <Suspense fallback={null}>
      <VerifyEmailForm token={token} />
    </Suspense>
  );
}
