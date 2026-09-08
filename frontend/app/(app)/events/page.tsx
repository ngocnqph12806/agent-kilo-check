import Link from 'next/link';
import { CalendarClock, Compass, MapPin } from 'lucide-react';

// Visual Fidelity (AGENTS.md 5.3): Matches screens-svg/10-events/01-list.svg

const EVENT_FEATURES = [
  {
    icon: Compass,
    title: 'Skill-themed meetups',
    description: 'Multi-hour gatherings organised around one topic — language tables, co-working, and study jams.'
  },
  {
    icon: MapPin,
    title: 'Online & in-person',
    description: 'Each event picks a format up-front so you know whether you need a desk or just your browser.'
  },
  {
    icon: CalendarClock,
    title: 'Calendar-friendly RSVPs',
    description: 'Accepted RSVPs slot into your upcoming-bookings view so nothing slips past you.'
  }
];

export default function EventsPage() {
  return (
    <main className="container max-w-4xl space-y-12 py-12">
      <header className="space-y-3 text-center">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Coming soon</p>
        <h1 className="text-4xl font-bold tracking-tight">Events</h1>
        <p className="mx-auto max-w-xl text-base text-muted-foreground">
          SkillSeed Events bring the community together around shared topics — short workshops,
          language tables, co-working sessions, and study jams. They&apos;re launching after Pods.
        </p>
      </header>

      <section className="grid gap-4 sm:grid-cols-3">
        {EVENT_FEATURES.map(({ icon: Icon, title, description }) => (
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
        <h2 className="text-xl font-semibold">First events land with Pods</h2>
        <p className="mx-auto mt-2 max-w-md text-sm text-muted-foreground">
          We&apos;re sequencing Events right behind Pods in our roadmap. Want to host one?
          Tell us what you&apos;d run and we&apos;ll be in touch.
        </p>
        <Link
          href="/discover"
          className="mt-6 inline-flex items-center gap-2 rounded-full border border-primary bg-card px-5 py-2 text-sm font-medium text-primary transition hover:bg-[var(--brand-hero-soft)]"
        >
          Browse individual sessions
        </Link>
      </section>
    </main>
  );
}
