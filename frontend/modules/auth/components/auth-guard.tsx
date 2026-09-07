'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

import { useAuth } from '../hooks/use-auth';
import { useCurrentUser } from '../hooks/use-auth-mutations';

export interface AuthGuardProps {
  children: React.ReactNode;
  requireVerified?: boolean;
  requireOnboarding?: boolean;
  redirectIfAuthenticated?: string;
  loginPath?: string;
  verifyPath?: string;
  onboardingPath?: string;
}

export function AuthGuard({
  children,
  requireVerified = false,
  requireOnboarding = false,
  redirectIfAuthenticated,
  loginPath = '/login',
  verifyPath = '/verify-email-prompt',
  onboardingPath = '/onboarding'
}: AuthGuardProps) {
  const router = useRouter();
  const { user, isHydrated } = useAuth();
  const meQuery = useCurrentUser(Boolean(isHydrated && user));

  useEffect(() => {
    if (!isHydrated) return;

    const accessToken =
      typeof window !== 'undefined' ? window.localStorage.getItem('skillseed.access-token') : null;

    if (!accessToken || !user) {
      if (redirectIfAuthenticated) return;
      router.replace(`${loginPath}?next=${encodeURIComponent(window.location.pathname)}`);
      return;
    }

    if (redirectIfAuthenticated && user) {
      router.replace(redirectIfAuthenticated);
      return;
    }

    if (requireVerified && !user.verified) {
      router.replace(verifyPath);
      return;
    }

    if (requireOnboarding && !user.onboardingCompleted) {
      router.replace(`${onboardingPath}?next=${encodeURIComponent(window.location.pathname)}`);
      return;
    }
  }, [
    isHydrated,
    user,
    requireVerified,
    requireOnboarding,
    redirectIfAuthenticated,
    loginPath,
    verifyPath,
    onboardingPath,
    router
  ]);

  if (!isHydrated) {
    return (
      <div className="flex min-h-screen items-center justify-center text-sm text-muted-foreground">
        Loading…
      </div>
    );
  }

  const accessToken =
    typeof window !== 'undefined' ? window.localStorage.getItem('skillseed.access-token') : null;

  if (!accessToken || !user) {
    if (redirectIfAuthenticated) return null;
    return null;
  }

  if (requireVerified && !user.verified) return null;
  if (requireOnboarding && !user.onboardingCompleted) return null;

  return <>{children}</>;
}
