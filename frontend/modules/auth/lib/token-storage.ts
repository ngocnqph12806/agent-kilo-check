const ACCESS_TOKEN_STORAGE_KEY = 'skillseed.access-token';
const REFRESH_TOKEN_COOKIE_KEY = 'skillseed_refresh_token';
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

export function setRefreshTokenCookie(token: string): void {
  if (!isBrowser()) return;
  const maxAge = 60 * 60 * 24 * 30;
  const secure = window.location.protocol === 'https:' ? '; Secure' : '';
  document.cookie = `${REFRESH_TOKEN_COOKIE_KEY}=${encodeURIComponent(token)}; Path=/; Max-Age=${maxAge}; SameSite=Lax${secure}`;
}

export function clearRefreshTokenCookie(): void {
  if (!isBrowser()) return;
  document.cookie = `${REFRESH_TOKEN_COOKIE_KEY}=; Path=/; Max-Age=0; SameSite=Lax`;
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
