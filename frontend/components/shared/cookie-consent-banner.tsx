'use client';

import * as React from 'react';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

const STORAGE_KEY = 'skillseed.cookie-consent.v1';

type ConsentValue = 'accepted' | 'rejected' | 'essential-only';

interface StoredConsent {
  value: ConsentValue;
  decidedAt: string;
}

function readStoredConsent(): StoredConsent | null {
  if (typeof window === 'undefined') return null;
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as StoredConsent;
    if (!parsed.value || !parsed.decidedAt) return null;
    return parsed;
  } catch {
    return null;
  }
}

function writeConsent(value: ConsentValue) {
  if (typeof window === 'undefined') return;
  const payload: StoredConsent = {
    value,
    decidedAt: new Date().toISOString()
  };
  try {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
  } catch {
    // ignore quota errors
  }
  window.dispatchEvent(new CustomEvent('skillseed:cookie-consent', { detail: payload }));
}

export interface CookieConsentBannerProps {
  className?: string;
  policyHref?: string;
}

/**
 * GDPR / ePrivacy cookie-consent banner (T-M202).
 *
 * Shows a fixed bottom-of-screen banner the first time a visitor lands,
 * remembers the choice in {@code localStorage} and exposes the value via
 * the {@code skillseed:cookie-consent} DOM event so analytics scripts can
 * listen.
 */
export function CookieConsentBanner({
  className,
  policyHref = '/privacy'
}: CookieConsentBannerProps) {
  const [visible, setVisible] = React.useState(false);
  const [mounted, setMounted] = React.useState(false);

  React.useEffect(() => {
    setMounted(true);
    setVisible(readStoredConsent() == null);
  }, []);

  const decide = (value: ConsentValue) => {
    writeConsent(value);
    setVisible(false);
  };

  if (!mounted || !visible) return null;

  return (
    <div
      role="region"
      aria-label="Cookie consent"
      className={cn(
        'fixed inset-x-0 bottom-0 z-50 border-t bg-background/95 p-4 backdrop-blur supports-[backdrop-filter]:bg-background/80',
        className
      )}
    >
      <div className="container flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <p className="max-w-2xl text-sm text-muted-foreground">
          We use essential cookies to keep you signed in and a small set of analytics cookies to
          improve SkillSeed. You can accept all, reject the optional ones, or keep things to the
          bare minimum. See our{' '}
          <Link href={policyHref} className="font-medium text-primary underline-offset-4 hover:underline">
            cookie &amp; privacy policy
          </Link>
          .
        </p>
        <div className="flex flex-wrap gap-2">
          <Button
            type="button"
            variant="ghost"
            size="sm"
            onClick={() => decide('essential-only')}
          >
            Essential only
          </Button>
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={() => decide('rejected')}
          >
            Reject optional
          </Button>
          <Button type="button" size="sm" onClick={() => decide('accepted')}>
            Accept all
          </Button>
        </div>
      </div>
    </div>
  );
}

export function getStoredConsent(): StoredConsent | null {
  return readStoredConsent();
}

export function clearStoredConsent() {
  if (typeof window === 'undefined') return;
  try {
    window.localStorage.removeItem(STORAGE_KEY);
  } catch {
    // ignore
  }
  window.dispatchEvent(new CustomEvent('skillseed:cookie-consent', { detail: null }));
}