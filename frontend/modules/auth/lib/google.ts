export const GOOGLE_CLIENT_ID = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID ?? '';

const GIS_SRC = 'https://accounts.google.com/gsi/client';
let scriptPromise: Promise<void> | null = null;

interface GoogleAccountsId {
  initialize: (config: {
    client_id: string;
    callback: (response: { credential: string }) => void;
    auto_select?: boolean;
    cancel_on_tap_outside?: boolean;
  }) => void;
  prompt: () => void;
  renderButton: (
    parent: HTMLElement,
    options: {
      type?: 'standard' | 'icon';
      theme?: 'outline' | 'filled_blue' | 'filled_black';
      size?: 'large' | 'medium' | 'small';
      text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
      shape?: 'rectangular' | 'pill' | 'circle' | 'square';
      width?: number;
      locale?: string;
    }
  ) => void;
}

interface GoogleAccounts {
  id: GoogleAccountsId;
}

declare global {
  interface Window {
    google?: { accounts: GoogleAccounts };
  }
}

function loadGisScript(): Promise<void> {
  if (typeof window === 'undefined') return Promise.resolve();
  if (scriptPromise) return scriptPromise;
  if (window.google?.accounts?.id) {
    scriptPromise = Promise.resolve();
    return scriptPromise;
  }
  scriptPromise = new Promise<void>((resolve, reject) => {
    const existing = document.querySelector<HTMLScriptElement>(`script[src="${GIS_SRC}"]`);
    if (existing) {
      existing.addEventListener('load', () => resolve(), { once: true });
      existing.addEventListener('error', () => reject(new Error('Failed to load Google Identity Services')), {
        once: true
      });
      return;
    }
    const script = document.createElement('script');
    script.src = GIS_SRC;
    script.async = true;
    script.defer = true;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error('Failed to load Google Identity Services'));
    document.head.appendChild(script);
  });
  return scriptPromise;
}

export function isGoogleConfigured(): boolean {
  return GOOGLE_CLIENT_ID.length > 0;
}

export async function initializeGoogle(onCredential: (idToken: string) => void) {
  if (!isGoogleConfigured()) {
    throw new Error('Google sign-in is not configured. Set NEXT_PUBLIC_GOOGLE_CLIENT_ID.');
  }
  await loadGisScript();
  if (!window.google?.accounts?.id) {
    throw new Error('Google Identity Services failed to initialise.');
  }
  window.google.accounts.id.initialize({
    client_id: GOOGLE_CLIENT_ID,
    callback: (response) => {
      if (response?.credential) {
        onCredential(response.credential);
      }
    },
    cancel_on_tap_outside: true
  });
}

export async function renderGoogleButton(
  parent: HTMLElement,
  onCredential: (idToken: string) => void,
  options: {
    type?: 'standard' | 'icon';
    theme?: 'outline' | 'filled_blue' | 'filled_black';
    text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
    size?: 'large' | 'medium' | 'small';
  } = {}
) {
  await initializeGoogle(onCredential);
  if (!window.google?.accounts?.id) return;
  window.google.accounts.id.renderButton(parent, {
    type: options.type ?? 'standard',
    theme: options.theme ?? 'outline',
    size: options.size ?? 'large',
    text: options.text ?? 'continue_with',
    shape: 'rectangular',
    width: parent.clientWidth || 320
  });
}

export async function promptGoogleOneTap(onCredential: (idToken: string) => void) {
  await initializeGoogle(onCredential);
  if (!window.google?.accounts?.id) return;
  window.google.accounts.id.prompt();
}
