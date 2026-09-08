import Link from 'next/link';

import { cn } from '@/lib/utils';
import { BrandLogo } from '@/components/shared/brand-logo';
import { MarketingHero, type MarketingHeroProps } from '@/components/shared/marketing-hero';

export interface AuthShellProps {
  title: string;
  description?: React.ReactNode;
  subtitle?: React.ReactNode;
  children: React.ReactNode;
  footer?: React.ReactNode;
  hero?: MarketingHeroProps;
  mobileLogo?: boolean;
  className?: string;
}

export function AuthShell({
  title,
  description,
  subtitle,
  children,
  footer,
  hero,
  mobileLogo = true,
  className
}: AuthShellProps) {
  return (
    <main className={cn('min-h-screen bg-[var(--brand-surface)] lg:flex', className)}>
      {hero ? <MarketingHero {...hero} /> : null}

      <section className="flex flex-1 items-center justify-center px-4 py-12 lg:py-16">
        <div className="w-full max-w-[500px] rounded-2xl border border-[var(--brand-border)] bg-white p-8 shadow-brand-card sm:p-10">
          {mobileLogo ? (
            <div className="mb-6 lg:hidden">
              <BrandLogo href="/" size="md" />
            </div>
          ) : null}

          <header className="mb-6 space-y-1">
            <h1 className="text-3xl font-extrabold tracking-tight text-[var(--brand-text-strong)]">
              {title}
            </h1>
            {subtitle ? (
              <p className="text-sm text-[var(--brand-text-muted)]">{subtitle}</p>
            ) : null}
            {description ? (
              <p className="text-sm text-[var(--brand-text-muted)]">{description}</p>
            ) : null}
          </header>

          <div className="space-y-4">{children}</div>

          {footer ? (
            <div className="mt-6 border-t border-[var(--brand-border)] pt-4 text-center text-sm text-[var(--brand-text-muted)]">
              {footer}
            </div>
          ) : null}

          <p className="mt-8 text-center text-xs text-[var(--brand-text-subtle)]">
            <Link href="/privacy" className="hover:text-[var(--brand-text-muted)]">
              Privacy
            </Link>{' '}
            ·{' '}
            <Link href="/terms" className="hover:text-[var(--brand-text-muted)]">
              Terms
            </Link>
          </p>
        </div>
      </section>
    </main>
  );
}
