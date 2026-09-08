'use client';

import * as React from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

declare global {
  interface Window {
    Intercom?: (...args: unknown[]) => void;
    intercomSettings?: Record<string, unknown>;
  }
}

const APP_ID = process.env.NEXT_PUBLIC_INTERCOM_APP_ID;

interface IntercomFeedbackProps {
  className?: string;
}

/**
 * Lightweight feedback launcher (T-M222). When
 * {@code NEXT_PUBLIC_INTERCOM_APP_ID} is set we boot the official
 * Intercom widget; otherwise we fall back to a mailto link.
 */
export function IntercomFeedback({ className }: IntercomFeedbackProps) {
  const [ready, setReady] = React.useState(false);

  React.useEffect(() => {
    if (!APP_ID) return;
    if (typeof window === 'undefined') return;
    if (window.Intercom) {
      setReady(true);
      return;
    }

    window.intercomSettings = {
      app_id: APP_ID,
      custom_launcher_selector: '#skillseed-feedback-launcher'
    };

    const script = document.createElement('script');
    script.async = true;
    script.src = `https://widget.intercom.io/widget/${APP_ID}`;
    script.onload = () => setReady(true);
    document.head.appendChild(script);

    return () => {
      script.remove();
    };
  }, []);

  const handleClick = () => {
    if (ready && typeof window !== 'undefined' && window.Intercom) {
      window.Intercom('show');
    }
  };

  if (!APP_ID) {
    return (
      <Button
        id="skillseed-feedback-launcher"
        type="button"
        variant="outline"
        size="sm"
        className={cn('fixed bottom-4 right-4 z-40 shadow-lg', className)}
        asChild
      >
        <a href="mailto:feedback@skillseed.app">Send feedback</a>
      </Button>
    );
  }

  return (
    <Button
      id="skillseed-feedback-launcher"
      type="button"
      variant="default"
      size="sm"
      onClick={handleClick}
      className={cn('fixed bottom-4 right-4 z-40 shadow-lg', className)}
    >
      Send feedback
    </Button>
  );
}

export default IntercomFeedback;