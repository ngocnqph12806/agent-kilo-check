import { GuestGuard } from '@/modules/auth/components/guest-guard';

import RegisterClient from './register-client';

export default function RegisterRoute() {
  return (
    <GuestGuard>
      <RegisterClient />
    </GuestGuard>
  );
}
