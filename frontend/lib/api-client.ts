import axios, {
  AxiosError,
  type AxiosInstance,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig
} from 'axios';

import {
  getAccessToken,
  setAccessToken,
  clearAccessToken
} from '@/modules/auth/lib/token-storage';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

interface ErrorPayload {
  code?: string;
  message?: string;
}

interface RefreshResponse {
  accessToken: string;
  refreshToken?: string;
  tokenType?: string;
  expiresIn?: number;
}

let onUnauthorized: (() => void) | null = null;

export function setOnUnauthorized(handler: (() => void) | null) {
  onUnauthorized = handler;
}

export const apiClient: AxiosInstance = axios.create({
  baseURL: `${API_BASE_URL}/api/v1`,
  timeout: 15000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getAccessToken();
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export type ApiError = AxiosError<ErrorPayload>;

function shouldSkipAuthHeader(url?: string): boolean {
  if (!url) return false;
  return url.includes('/auth/login') || url.includes('/auth/register') || url.includes('/auth/refresh');
}

apiClient.interceptors.request.use((config) => {
  if (shouldSkipAuthHeader(config.url)) {
    if (config.headers) {
      delete (config.headers as Record<string, unknown>).Authorization;
    }
  }
  return config;
});

// ---------------------------------------------------------------------------
// Refresh-on-401 interceptor
//
// On any 401 response from the API we POST /auth/refresh (relying on the
// skillseed_refresh_token cookie carried by `withCredentials`), stash the
// new access token, and replay the original request once. Concurrent 401s
// share a single in-flight refresh promise so we never fan out to the
// server with stampedes of duplicates. If the refresh itself fails we
// clear the session and surface the original error.
//
// Endpoints that already deal with auth (login/register/refresh) never
// trigger this — their Authorization header is stripped in the request
// interceptor above, and we explicitly skip retry for /auth/refresh below.
// ---------------------------------------------------------------------------

interface RetryableRequest extends InternalAxiosRequestConfig {
  __retried?: boolean;
}

let refreshInFlight: Promise<string | null> | null = null;

async function performRefresh(): Promise<string | null> {
  try {
    // Bypass the baseURL/api-version prefix by using a raw axios call.
    const { data } = await axios.post<RefreshResponse>(
      `${API_BASE_URL}/api/v1/auth/refresh`,
      null,
      { withCredentials: true, timeout: 15000 }
    );
    if (data?.accessToken) {
      setAccessToken(data.accessToken);
      return data.accessToken;
    }
    clearAccessToken();
    return null;
  } catch {
    clearAccessToken();
    return null;
  }
}

function getRefreshToken(): Promise<string | null> {
  if (!refreshInFlight) {
    refreshInFlight = performRefresh().finally(() => {
      refreshInFlight = null;
    });
  }
  return refreshInFlight;
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error: ApiError) => {
    const original = error.config as RetryableRequest | undefined;
    const status = error.response?.status;

    if (status !== 401 || !original || original.__retried) {
      if (status === 401) {
        if (onUnauthorized) onUnauthorized();
      }
      return Promise.reject(error);
    }

    // Don't try to refresh if the failing call *was* the refresh.
    if (original.url?.includes('/auth/refresh')) {
      clearAccessToken();
      if (onUnauthorized) onUnauthorized();
      return Promise.reject(error);
    }

    const newToken = await getRefreshToken();
    if (!newToken) {
      if (onUnauthorized) onUnauthorized();
      return Promise.reject(error);
    }

    original.__retried = true;
    original.headers = original.headers ?? {};
    (original.headers as Record<string, unknown>).Authorization = `Bearer ${newToken}`;
    return apiClient.request(original);
  }
);

export type { AxiosRequestConfig };
