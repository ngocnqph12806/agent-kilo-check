'use client';

import { useEffect } from 'react';
import { usePathname, useRouter } from 'next/navigation';

import { useAuth } from '../hooks/use-auth';
import { useCurrentUser } from '../hooks/use-auth-mutations';
import { getAccessToken } from '../lib/token-storage';

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
  const pathname = usePathname();
  const { user, isHydrated } = useAuth();
  const meQuery = useCurrentUser(Boolean(isHydrated && user));

  // /onboarding is the destination of the onboarding-required redirect;
  // it must remain reachable even when onboardingCompleted=false.
  const onboardingRequired = requireOnboarding && pathname !== '/onboarding';

  useEffect(() => {
    if (!isHydrated) return;

    const accessToken =
      getAccessToken();

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

    if (onboardingRequired && !user.onboardingCompleted) {
      router.replace(`${onboardingPath}?next=${encodeURIComponent(window.location.pathname)}`);
      return;
    }
  }, [
    isHydrated,
    user,
    requireVerified,
    onboardingRequired,
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
    getAccessToken();

  if (!accessToken || !user) {
    if (redirectIfAuthenticated) return null;
    return null;
  }

  if (requireVerified && !user.verified) return null;
  if (onboardingRequired && !user.onboardingCompleted) return null;

  return <>{children}</>;
}

