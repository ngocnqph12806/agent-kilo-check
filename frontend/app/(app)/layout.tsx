import { AuthGuard } from '@/modules/auth/components/auth-guard';

export default function ProtectedAppLayout({ children }: { children: React.ReactNode }) {
  return <AuthGuard requireVerified requireOnboarding>{children}</AuthGuard>;
}
