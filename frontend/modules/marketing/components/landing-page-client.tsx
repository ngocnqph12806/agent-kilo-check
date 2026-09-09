'use client';

import { CheckCircle2, Sparkles } from 'lucide-react';
import Link from 'next/link';
import { useState } from 'react';

import { BrandLogo } from '@/components/shared/brand-logo';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

import { useJoinWaitlist } from '../hooks/use-join-waitlist';

const TRUST_BADGES = [
  { icon: '🌱', text: '30 free starter seeds' },
  { icon: '✓', text: 'No credit card required' },
  { icon: '🌍', text: '8,000+ verified mentors worldwide' }
] as const;

const STATS = [
  { value: '200K+', label: 'learners' },
  { value: '8K+', label: 'verified mentors' },
  { value: '1.2M', label: 'sessions completed' },
  { value: '4.8★', label: 'avg session rating' }
] as const;

const NAV_LINKS = [
  { label: 'Discover', href: '/discover' },
  { label: 'How it works', href: '#how-it-works' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'About', href: '/about' },
  { label: 'Blog', href: '/blog' }
] as const;

/**
 * Marketing landing page (Phase 1, T-M423). Matches
 * {@code screens-svg/00-marketing/01-landing.svg}: badge, dual-line
 * headline, email-capture hero, trust badges, floating mentor cards,
 * stats bar, and footer.
 */
export function LandingPageClient() {
  const [email, setEmail] = useState('');
  const join = useJoinWaitlist();

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!email || join.isPending) return;
    try {
      await join.mutateAsync({
        email,
        source: 'landing',
        referrer:
          typeof document !== 'undefined' ? document.referrer : undefined
      });
      setEmail('');
    } catch {
      // surfaced via join.error
    }
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b border-brand-divider bg-background">
        <div className="container mx-auto flex h-[72px] max-w-6xl items-center justify-between">
          <Link href="/" className="flex items-center gap-2">
            <BrandLogo variant="app" size="md" />
          </Link>
          <nav className="hidden items-center gap-8 text-sm font-medium text-brand-strong lg:flex">
            {NAV_LINKS.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className="transition-colors hover:text-primary"
              >
                {link.label}
              </Link>
            ))}
          </nav>
          <div className="flex items-center gap-3">
            <Link
              href="/login"
              className="text-sm font-medium text-brand-strong transition-colors hover:text-primary"
            >
              Login
            </Link>
            <Button asChild variant="brand" className="rounded-full px-5">
              <Link href="/register">Get started</Link>
            </Button>
          </div>
        </div>
      </header>

      {/* Hero */}
      <section className="bg-brand-hero-soft">
        <div className="container mx-auto max-w-6xl px-6 pb-24 pt-20">
          <div className="mx-auto max-w-3xl text-center">
            <span className="inline-flex items-center gap-2 rounded-full border border-emerald-200 bg-white px-4 py-1.5 text-xs font-semibold text-emerald-700 shadow-sm">
              <Sparkles className="h-3.5 w-3.5" aria-hidden />
              🌱 Now in 4 countries
            </span>
            <h1 className="mt-6 text-5xl font-extrabold leading-[1.05] tracking-tight text-brand-strong sm:text-6xl">
              Teach what you know.
              <br />
              Learn what you love.
            </h1>
            <p className="mx-auto mt-6 max-w-xl text-lg leading-relaxed text-brand-muted">
              Pay with your time, not your wallet. AI-matched mentors teach you
              skills 1-on-1 via video call.
            </p>

            {/* Email-capture form */}
            <form
              onSubmit={handleSubmit}
              className="mx-auto mt-10 flex max-w-xl items-center gap-2 rounded-full bg-card p-2 shadow-brand-card"
            >
              <span className="pl-3 text-lg" aria-hidden>
                📧
              </span>
              <Input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="your@email.com"
                className="h-12 flex-1 border-0 bg-transparent shadow-none focus-visible:ring-0"
                aria-label="Email address"
              />
              <Button
                type="submit"
                variant="brand"
                className="h-12 rounded-full px-6"
                disabled={join.isPending}
              >
                {join.isPending ? 'Joining…' : 'Join waitlist →'}
              </Button>
            </form>

            {join.isSuccess ? (
              <p
                className="mx-auto mt-4 inline-flex items-center gap-2 rounded-full bg-emerald-50 px-4 py-1.5 text-sm font-semibold text-emerald-700"
                role="status"
              >
                <CheckCircle2 className="h-4 w-4" aria-hidden />
                {join.data?.message ?? "You're on the list!"}
                {join.data?.position ? (
                  <span className="text-xs font-medium text-emerald-600">
                    #{join.data.position}
                  </span>
                ) : null}
              </p>
            ) : null}

            {join.isError ? (
              <p className="mx-auto mt-4 text-sm text-destructive" role="alert">
                {extractErrorMessage(join.error) ?? 'Could not join — try again.'}
              </p>
            ) : null}

            {/* Trust badges */}
            <ul className="mx-auto mt-8 flex flex-wrap items-center justify-center gap-x-6 gap-y-2 text-xs font-medium text-brand-muted">
              {TRUST_BADGES.map((badge) => (
                <li key={badge.text} className="inline-flex items-center gap-1">
                  <span aria-hidden>{badge.icon}</span>
                  {badge.text}
                </li>
              ))}
            </ul>
          </div>
        </div>
      </section>

      {/* Stats bar */}
      <section className="border-y border-brand-divider bg-background">
        <div className="container mx-auto grid max-w-6xl grid-cols-2 gap-8 px-6 py-10 sm:grid-cols-4">
          {STATS.map((stat) => (
            <div key={stat.label} className="text-center sm:text-left">
              <p className="text-3xl font-extrabold text-brand-strong">
                {stat.value}
              </p>
              <p className="mt-1 text-xs text-brand-muted">
                {stat.label}
              </p>
            </div>
          ))}
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-brand-divider/40">
        <div className="container mx-auto max-w-6xl px-6 py-12">
          <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
            <div>
              <BrandLogo variant="app" size="md" />
              <p className="mt-3 text-sm text-brand-muted">
                Teach what you know. Learn what you love.
              </p>
              <p className="mt-4 text-xs text-brand-subtle">
                © 2026 SkillSeed · Privacy · Terms · Contact
              </p>
            </div>
            <FooterColumn
              title="Product"
              links={['Discover', 'Pricing', 'For Business']}
            />
            <FooterColumn
              title="Company"
              links={['About', 'Blog', 'Careers']}
            />
            <FooterColumn
              title="Connect"
              links={['Twitter', 'LinkedIn', 'Discord']}
            />
          </div>
        </div>
      </footer>
    </div>
  );
}

function FooterColumn({
  title,
  links
}: {
  title: string;
  links: ReadonlyArray<string>;
}) {
  return (
    <div>
      <h3 className="text-sm font-bold text-brand-strong">
        {title}
      </h3>
      <ul className="mt-4 space-y-2 text-sm text-brand-muted">
        {links.map((link) => (
          <li key={link}>
            <Link
              href="#"
              className="transition-colors hover:text-primary"
            >
              {link}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

function extractErrorMessage(err: unknown): string | undefined {
  const apiErr = err as { response?: { data?: { message?: string } } } | undefined;
  return apiErr?.response?.data?.message;
}
