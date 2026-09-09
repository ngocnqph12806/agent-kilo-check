import { Suspense } from 'react';

import { ForgotPasswordForm } from '@/modules/auth/components/forgot-password-form';

export default function ForgotPasswordPage() {
  return (
    <Suspense fallback={null}>
      <ForgotPasswordForm />
    </Suspense>
  );
}
