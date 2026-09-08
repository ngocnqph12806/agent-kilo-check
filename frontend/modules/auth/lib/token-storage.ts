const ACCESS_TOKEN_STORAGE_KEY = 'skillseed.access-token';
const USER_STORAGE_KEY = 'skillseed.user';

function isBrowser(): boolean {
  return typeof window !== 'undefined';
}

export function getAccessToken(): string | null {
  if (!isBrowser()) return null;
  return window.localStorage.getItem(ACCESS_TOKEN_STORAGE_KEY);
}

export function setAccessToken(token: string): void {
  if (!isBrowser()) return;
  window.localStorage.setItem(ACCESS_TOKEN_STORAGE_KEY, token);
}

export function clearAccessToken(): void {
  if (!isBrowser()) return;
  window.localStorage.removeItem(ACCESS_TOKEN_STORAGE_KEY);
}

export function getStoredUser<T>(): T | null {
  if (!isBrowser()) return null;
  const raw = window.localStorage.getItem(USER_STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as T;
  } catch {
    return null;
  }
}

export function setStoredUser<T>(user: T): void {
  if (!isBrowser()) return;
  window.localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user));
}

export function clearStoredUser(): void {
  if (!isBrowser()) return;
  window.localStorage.removeItem(USER_STORAGE_KEY);
}
