import { Suspense } from 'react';

import { GuestGuard } from '@/modules/auth/components/guest-guard';

import { LoginForm } from './login-form';

export default function LoginPage() {
  return (
    <GuestGuard>
      <Suspense fallback={null}>
        <LoginForm />
      </Suspense>
    </GuestGuard>
  );
}
