import axios, {
  AxiosError,
  type AxiosInstance,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig
} from 'axios';

import { getAccessToken, clearAccessToken } from '@/modules/auth/lib/token-storage';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

interface ErrorPayload {
  code?: string;
  message?: string;
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

apiClient.interceptors.response.use(
  (response) => response,
  (error: ApiError) => {
    if (error.response?.status === 401) {
      clearAccessToken();
      if (onUnauthorized) {
        onUnauthorized();
      }
    }
    return Promise.reject(error);
  }
);

export type { AxiosRequestConfig };
