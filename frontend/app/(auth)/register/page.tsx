import { GuestGuard } from '@/modules/auth/components/guest-guard';

import { RegisterForm } from '@/modules/auth/components/register-form';

export default function RegisterRoute() {
  return (
    <GuestGuard>
      <RegisterForm />
    </GuestGuard>
  );
}
