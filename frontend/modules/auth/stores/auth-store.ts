'use client';

import { create } from 'zustand';

import type { AuthUserSummary } from '../lib/schemas';
import {
  clearAccessToken,
  clearStoredUser,
  getStoredUser,
  setStoredUser
} from '../lib/token-storage';

interface AuthState {
  user: AuthUserSummary | null;
  isHydrated: boolean;
  setUser: (user: AuthUserSummary | null) => void;
  hydrate: () => void;
  reset: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isHydrated: false,
  setUser: (user) => {
    if (user) {
      setStoredUser(user);
    } else {
      clearStoredUser();
    }
    set({ user });
  },
  hydrate: () => {
    const stored = getStoredUser<AuthUserSummary>();
    set({ user: stored, isHydrated: true });
  },
  reset: () => {
    clearAccessToken();
    clearStoredUser();
    set({ user: null });
  }
}));

export const selectUser = (state: AuthState) => state.user;
export const selectIsHydrated = (state: AuthState) => state.isHydrated;
