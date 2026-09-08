import { Suspense } from 'react';

import { VerifyEmailPromptForm } from './verify-email-prompt-form';

export default function VerifyEmailPromptPage() {
  return (
    <Suspense fallback={null}>
      <VerifyEmailPromptForm />
    </Suspense>
  );
}