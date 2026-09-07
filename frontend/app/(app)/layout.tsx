import { AuthGuard } from '@/modules/auth/components/auth-guard';
import { AppTopBar } from '@/modules/auth/components/app-top-bar';

export default function ProtectedAppLayout({
  children
}: {
  children: React.ReactNode;
}) {
  return (
    <AuthGuard requireVerified requireOnboarding>
      <div className="min-h-screen bg-muted/20">
        <AppTopBar />
        {children}
      </div>
    </AuthGuard>
  );
}
