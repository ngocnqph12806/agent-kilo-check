import Link from 'next/link';
import { FileQuestion } from 'lucide-react';

import { Button } from '@/components/ui/button';

export const metadata = {
  title: 'Page not found · SkillSeed',
  robots: { index: false, follow: false }
};

export default function NotFound() {
  return (
    <main className="flex min-h-[80vh] flex-col items-center justify-center bg-[var(--brand-surface)] px-4">
      <div className="flex max-w-md flex-col items-center text-center">
        <div className="mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-[var(--brand-hero-soft)] text-6xl shadow-brand-card">
          <FileQuestion className="h-12 w-12 text-[var(--brand-cta-from)]" aria-hidden />
        </div>
        <p className="mb-2 inline-flex items-center rounded-full border border-[var(--brand-border)] bg-card px-3 py-1 text-xs font-semibold uppercase tracking-wide text-[var(--brand-text-muted)] shadow-brand-card">
          404
        </p>
        <h1 className="text-3xl font-extrabold tracking-tight text-[var(--brand-text-strong)]">
          Page not found
        </h1>
        <p className="mt-3 max-w-md text-base text-[var(--brand-text-muted)]">
          The link you followed may be broken, or the page may have been removed.
        </p>
        <div className="mt-6 flex flex-wrap justify-center gap-3">
          <Button asChild variant="brand" className="rounded-full">
            <Link href="/">🏠 Back to home</Link>
          </Button>
          <Button asChild variant="outline" className="rounded-full">
            <Link href="/discover">🔍 Browse teachers</Link>
          </Button>
        </div>
        <p className="mt-8 text-xs text-[var(--brand-text-subtle)]">
          Need help?{' '}
          <Link href="/support" className="font-semibold text-[var(--brand-cta-from)]">
            Contact support
          </Link>
        </p>
      </div>
    </main>
  );
}