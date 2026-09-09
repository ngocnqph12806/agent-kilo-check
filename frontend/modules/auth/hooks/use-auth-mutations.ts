'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';

import { authApi, getAuthErrorMessage } from '../lib/auth-api';
import type {
  AuthUserSummary,
  ForgotPasswordInput,
  GoogleOAuthInput,
  LoginInput,
  RegisterInput,
  ResetPasswordInput,
  VerifyEmailInput
} from '../lib/schemas';
import {
  setAccessToken
} from '../lib/token-storage';
import { useAuthStore } from '../stores/auth-store';

const CURRENT_USER_QUERY_KEY = ['auth', 'me'] as const;

function persistSession(
  setUser: (user: AuthUserSummary | null) => void,
  user: AuthUserSummary,
  accessToken: string
) {
  setAccessToken(accessToken);
  // Refresh token is now stored exclusively as an HttpOnly cookie
  // set by the backend on /auth/login and rotated on /auth/refresh.
  // See backend/src/main/java/com/skillseed/auth/security/RefreshTokenCookie.java
  setUser(user);
}

function clearSession(reset: () => void) {
  reset();
}

export function useCurrentUser(enabled = true) {
  return useQuery({
    queryKey: CURRENT_USER_QUERY_KEY,
    queryFn: async () => {
      const me = await authApi.getCurrentUser();
      const user: AuthUserSummary = {
        id: me.id,
        email: me.email,
        fullName: me.fullName,
        verified: me.verified,
        onboardingCompleted: me.onboardingCompleted
      };
      return user;
    },
    enabled,
    staleTime: 60 * 1000
  });
}

export function useLoginMutation() {
  const setUser = useAuthStore((state) => state.setUser);
  const router = useRouter();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (input: LoginInput) => {
      const response = await authApi.login(input);
      return response;
    },
    onSuccess: (response) => {
      persistSession(setUser, response.user, response.accessToken);
      queryClient.setQueryData(CURRENT_USER_QUERY_KEY, response.user);
      router.replace('/discover');
    },
    onError: (error) => {
      throw new Error(getAuthErrorMessage(error, 'Unable to sign in. Please try again.'));
    }
  });
}

/** Form-side type (includes client-only fields like acceptTerms). */
type RegisterFormInput = Omit<RegisterInput, 'acceptTerms'> & {
  acceptTerms?: boolean;
};

export function useRegisterMutation() {
  const router = useRouter();

  return useMutation({
    mutationFn: async (input: RegisterFormInput) => {
      const { acceptTerms: _acceptTerms, ...payload } = input;
      return authApi.register(payload);
    },
    onSuccess: () => {
      router.replace('/login?registered=1');
    },
    onError: (error) => {
      throw new Error(getAuthErrorMessage(error, 'Unable to create account. Please try again.'));
    }
  });
}

export function useLogoutMutation() {
  const reset = useAuthStore((state) => state.reset);
  const router = useRouter();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      try {
        await authApi.logout();
      } catch {
        /* ignore — client-side clear anyway */
      }
    },
    onSettled: () => {
      clearSession(reset);
      queryClient.clear();
      router.replace('/login');
    }
  });
}

export function useVerifyEmailMutation() {
  const queryClient = useQueryClient();
  const setUser = useAuthStore((state) => state.setUser);

  return useMutation({
    mutationFn: async (input: VerifyEmailInput) => authApi.verifyEmail(input),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: CURRENT_USER_QUERY_KEY });
      const refreshed = await authApi.getCurrentUser();
      setUser({
        id: refreshed.id,
        email: refreshed.email,
        fullName: refreshed.fullName,
        verified: refreshed.verified,
        onboardingCompleted: refreshed.onboardingCompleted
      });
    },
    onError: (error) => {
      throw new Error(getAuthErrorMessage(error, 'Unable to verify email. The link may be expired.'));
    }
  });
}

export function useForgotPasswordMutation() {
  return useMutation({
    mutationFn: async (input: ForgotPasswordInput) => authApi.forgotPassword(input),
    onError: (error) => {
      throw new Error(getAuthErrorMessage(error, 'Unable to request password reset.'));
    }
  });
}

export function useResetPasswordMutation() {
  const router = useRouter();

  return useMutation({
    mutationFn: async (input: ResetPasswordInput) => authApi.resetPassword(input),
    onSuccess: () => {
      router.replace('/login?reset=1');
    },
    onError: (error) => {
      throw new Error(getAuthErrorMessage(error, 'Unable to reset password.'));
    }
  });
}

export function useGoogleLoginMutation() {
  const setUser = useAuthStore((state) => state.setUser);
  const router = useRouter();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (input: GoogleOAuthInput) => authApi.loginWithGoogle(input),
    onSuccess: (response) => {
      persistSession(setUser, response.user, response.accessToken);
      queryClient.setQueryData(CURRENT_USER_QUERY_KEY, response.user);
      router.replace(response.user.onboardingCompleted ? '/discover' : '/onboarding');
    },
    onError: (error) => {
      throw new Error(getAuthErrorMessage(error, 'Google sign-in failed.'));
    }
  });
}

export function useAppleLoginMutation() {
  const setUser = useAuthStore((state) => state.setUser);
  const router = useRouter();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (idToken: string) => authApi.loginWithApple(idToken),
    onSuccess: (response) => {
      persistSession(setUser, response.user, response.accessToken);
      queryClient.setQueryData(CURRENT_USER_QUERY_KEY, response.user);
      router.replace(response.user.onboardingCompleted ? '/discover' : '/onboarding');
    },
    onError: (error) => {
      throw new Error(getAuthErrorMessage(error, 'Apple sign-in failed.'));
    }
  });
}
