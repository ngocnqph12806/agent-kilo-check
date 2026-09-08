import Link from 'next/link';

import { MarketingTopBar } from '@/components/shared';
import { Button } from '@/components/ui/button';

const features = [
  {
    title: 'Teach what you know',
    body: 'Share your skills in 15–60 minute sessions. Earn Seeds when you teach.'
  },
  {
    title: 'Learn what you love',
    body: 'Find a teacher across 2,000+ skills. Pay only with Seeds you earn.'
  },
  {
    title: 'No money, no friction',
    body: 'SkillSeed uses an internal Seed economy. One hour of teaching = 60 Seeds.'
  }
];

export default function HomePage() {
  return (
    <>
      <MarketingTopBar />
      <main className="container flex min-h-screen flex-col items-center justify-center gap-12 py-16">
        <section className="flex max-w-2xl flex-col items-center gap-6 text-center">
          <span className="rounded-full border bg-muted px-3 py-1 text-xs font-medium uppercase tracking-wide text-muted-foreground">
            MVP · Phase 1
          </span>
          <h1 className="text-balance text-5xl font-bold tracking-tight">
            Trade skills, not money.
          </h1>
          <p className="text-balance text-lg text-muted-foreground">
            SkillSeed is a peer-to-peer skill-exchange platform. Teach for 60 minutes,
            learn for 60 minutes — no cash involved.
          </p>
          <div className="flex gap-3">
            <Button asChild size="lg">
              <Link href="/register">Get started</Link>
            </Button>
            <Button asChild size="lg" variant="outline">
              <Link href="/login">I have an account</Link>
            </Button>
          </div>
        </section>

        <section className="grid w-full max-w-5xl gap-4 sm:grid-cols-3">
          {features.map((feature) => (
            <article
              key={feature.title}
              className="rounded-2xl border bg-card p-6 text-card-foreground shadow-brand-card"
            >
              <h2 className="text-lg font-semibold">{feature.title}</h2>
              <p className="mt-2 text-sm text-muted-foreground">{feature.body}</p>
            </article>
          ))}
        </section>
      </main>
    </>
  );
}
