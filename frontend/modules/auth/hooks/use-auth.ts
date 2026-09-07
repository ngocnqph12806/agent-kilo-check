'use client';

import { useAuthStore, selectIsHydrated, selectUser } from '../stores/auth-store';

export function useAuth() {
  const user = useAuthStore(selectUser);
  const isHydrated = useAuthStore(selectIsHydrated);
  return {
    user,
    isHydrated,
    isAuthenticated: Boolean(user),
    isVerified: Boolean(user?.verified),
    hasCompletedOnboarding: Boolean(user?.onboardingCompleted)
  };
}
