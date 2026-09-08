import { apiClient, type ApiError } from '@/lib/api-client';

import type {
  AuthTokens,
  AuthUserSummary,
  ForgotPasswordInput,
  GoogleOAuthInput,
  LoginInput,
  RegisterInput,
  ResetPasswordInput,
  VerifyEmailInput
} from './schemas';

interface AuthEnvelope {
  user: AuthUserSummary;
  accessToken: string;
  refreshToken: string;
}

interface EmptyEnvelope {}

interface BackendRegisterResponse {
  user: AuthUserSummary;
}

interface BackendLoginResponse extends AuthEnvelope {}

interface BackendRefreshResponse {
  accessToken: string;
  refreshToken: string;
}

interface BackendMeResponse {
  id: string;
  email: string;
  fullName: string;
  verified: boolean;
  onboardingCompleted: boolean;
}

function extractErrorMessage(error: unknown, fallback: string): string {
  const apiError = error as ApiError;
  return apiError?.response?.data?.message ?? apiError?.message ?? fallback;
}

export const authApi = {
  async register(input: RegisterInput): Promise<BackendRegisterResponse> {
    const { data } = await apiClient.post<BackendRegisterResponse>('/auth/register', {
      email: input.email,
      password: input.password,
      fullName: input.fullName
    });
    return data;
  },

  async login(input: LoginInput): Promise<BackendLoginResponse> {
    const { data } = await apiClient.post<BackendLoginResponse>('/auth/login', {
      email: input.email,
      password: input.password
    });
    return data;
  },

  async refresh(): Promise<BackendRefreshResponse> {
    const { data } = await apiClient.post<BackendRefreshResponse>('/auth/refresh', {});
    return data;
  },

  async logout(): Promise<EmptyEnvelope> {
    const { data } = await apiClient.post<EmptyEnvelope>('/auth/logout', {});
    return data;
  },

  async verifyEmail(input: VerifyEmailInput): Promise<EmptyEnvelope> {
    const { data } = await apiClient.post<EmptyEnvelope>('/auth/verify-email', {
      token: input.token
    });
    return data;
  },

  async forgotPassword(input: ForgotPasswordInput): Promise<EmptyEnvelope> {
    const { data } = await apiClient.post<EmptyEnvelope>('/auth/forgot-password', {
      email: input.email
    });
    return data;
  },

  async resetPassword(input: ResetPasswordInput): Promise<EmptyEnvelope> {
    const { data } = await apiClient.post<EmptyEnvelope>('/auth/reset-password', {
      token: input.token,
      newPassword: input.newPassword
    });
    return data;
  },

  async loginWithGoogle(input: GoogleOAuthInput): Promise<BackendLoginResponse> {
    const { data } = await apiClient.post<BackendLoginResponse>('/auth/oauth/google', {
      idToken: input.idToken
    });
    return data;
  },

  async loginWithApple(idToken: string): Promise<BackendLoginResponse> {
    const { data } = await apiClient.post<BackendLoginResponse>('/auth/oauth/apple', {
      idToken
    });
    return data;
  },

  async getCurrentUser(): Promise<BackendMeResponse> {
    const { data } = await apiClient.get<BackendMeResponse>('/users/me');
    return data;
  }
};

export function getAuthErrorMessage(error: unknown, fallback: string): string {
  return extractErrorMessage(error, fallback);
}
