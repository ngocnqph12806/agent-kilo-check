import { Calendar, Clock } from 'lucide-react';

export default function ConnectedCalendarsPage() {
  return (
    <main className="container max-w-3xl space-y-8 py-10">
      <header className="space-y-2">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Settings</p>
        <h1 className="text-3xl font-bold tracking-tight">Connected calendars</h1>
        <p className="text-sm text-muted-foreground">
          When a calendar is connected, SkillSeed automatically adds your confirmed sessions and
          updates them when something changes.
        </p>
      </header>

      <section className="space-y-4 rounded-2xl border bg-card p-6 shadow-sm">
        <div className="flex items-start gap-3">
          <span className="rounded-full bg-blue-100 p-2 text-blue-700">
            <Calendar className="h-5 w-5" aria-hidden />
          </span>
          <div className="flex-1 space-y-1">
            <h2 className="text-lg font-semibold">Google Calendar</h2>
            <p className="text-sm text-muted-foreground">
              Two-way sync: events you accept on SkillSeed appear on your Google Calendar with
              the join link, and busy / free status flows back so we don&apos;t double-book.
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-900">
          <Clock className="h-4 w-4" aria-hidden />
          Google Calendar sync is a <em>should-have</em> on our roadmap. Hold tight — it&apos;ll
          land in the next sprint.
        </div>
      </section>

      <section className="space-y-3 rounded-2xl border bg-card p-6 shadow-sm">
        <h2 className="text-lg font-semibold">Other providers</h2>
        <p className="text-sm text-muted-foreground">
          Outlook and Apple Calendar sync are also on our roadmap. We&apos;ll let everyone know
          when they ship — for now, sessions are stored inside SkillSeed and visible from the
          upcoming bookings page.
        </p>
      </section>
    </main>
  );
}
