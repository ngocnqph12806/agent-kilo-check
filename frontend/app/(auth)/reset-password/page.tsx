import { Suspense } from 'react';

import { ResetPasswordForm } from '@/modules/auth/components/reset-password-form';

export default function ResetPasswordPage() {
  return (
    <Suspense fallback={null}>
      <ResetPasswordForm />
    </Suspense>
  );
}
