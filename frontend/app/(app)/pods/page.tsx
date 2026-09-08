import Link from 'next/link';
import { ArrowRight, Compass, Sparkles, Users } from 'lucide-react';

// Visual Fidelity (AGENTS.md 5.3): Matches screens-svg/09-pods/01-discover.svg

const POD_FEATURES = [
  {
    icon: Users,
    title: 'Group learning circles',
    description: 'Run a recurring skill-share with 3-8 members who rotate teaching duties each week.'
  },
  {
    icon: Compass,
    title: 'Curated by facilitators',
    description: 'Pods are seeded by experienced members who keep momentum and onboarding friction low.'
  },
  {
    icon: Sparkles,
    title: 'Earn bonus seeds',
    description: 'Pod completions add a small bonus to your wallet on top of normal session payouts.'
  }
];

export default function PodsPage() {
  return (
    <main className="container max-w-4xl space-y-12 py-12">
      <header className="space-y-3 text-center">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Coming soon</p>
        <h1 className="text-4xl font-bold tracking-tight">Pods</h1>
        <p className="mx-auto max-w-xl text-base text-muted-foreground">
          Pods are mini-communities of 3-8 members who share a learning topic across weeks.
          They&apos;re the next big thing on SkillSeed and we&apos;re just finishing the last
          details before we open them up.
        </p>
      </header>

      <section className="grid gap-4 sm:grid-cols-3">
        {POD_FEATURES.map(({ icon: Icon, title, description }) => (
          <article
            key={title}
            className="rounded-2xl border bg-card p-5 shadow-sm"
          >
            <span className="mb-3 inline-flex h-10 w-10 items-center justify-center rounded-full bg-[var(--brand-hero-soft)] text-primary">
              <Icon className="h-5 w-5" aria-hidden />
            </span>
            <h2 className="text-base font-semibold">{title}</h2>
            <p className="mt-1 text-sm text-muted-foreground">{description}</p>
          </article>
        ))}
      </section>

      <section className="rounded-2xl border border-dashed bg-muted/30 p-8 text-center">
        <h2 className="text-xl font-semibold">Want first dibs?</h2>
        <p className="mx-auto mt-2 max-w-md text-sm text-muted-foreground">
          We&apos;re piloting Pods with a small group of members. Drop your email and we&apos;ll
          save you a seat.
        </p>
        <div className="mt-6 flex justify-center">
          <Link
            href="/notifications"
            className="inline-flex items-center gap-2 rounded-full bg-primary px-5 py-2 text-sm font-medium text-primary-foreground shadow transition hover:bg-primary/90"
          >
            Join the waitlist
            <ArrowRight className="h-4 w-4" aria-hidden />
          </Link>
        </div>
      </section>

      <p className="text-center text-xs text-muted-foreground">
        While you wait — try a 1:1 session from{' '}
        <Link href="/discover" className="font-semibold text-primary underline-offset-4 hover:underline">
          Discover
        </Link>
        .
      </p>
    </main>
  );
}
