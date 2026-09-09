import { Suspense } from 'react';

import { GuestGuard } from '@/modules/auth/components/guest-guard';

import { LoginForm } from '@/modules/auth/components/login-form';

export default function LoginPage() {
  return (
    <GuestGuard>
      <Suspense fallback={null}>
        <LoginForm />
      </Suspense>
    </GuestGuard>
  );
}
