import Link from 'next/link';
import { Bell, Mail, Smartphone } from 'lucide-react';

export default function SettingsNotificationsPage() {
  return (
    <main className="container max-w-3xl space-y-8 py-10">
      <header className="space-y-2">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Settings</p>
        <h1 className="text-3xl font-bold tracking-tight">Notifications</h1>
        <p className="text-sm text-muted-foreground">
          SkillSeed sends booking reminders, chat pings and weekly recaps. Pick what should reach you.
        </p>
      </header>

      <ul className="divide-y divide-border rounded-2xl border bg-card shadow-sm">
        <li className="flex items-start justify-between gap-4 px-5 py-4">
          <div className="flex items-start gap-3">
            <span className="mt-0.5 rounded-full bg-emerald-100 p-2 text-emerald-700">
              <Mail className="h-4 w-4" aria-hidden />
            </span>
            <div>
              <p className="font-medium">Email</p>
              <p className="text-sm text-muted-foreground">
                Booking confirmations, reschedule notices, and session reminders 30 minutes
                before start. We never share your address.
              </p>
            </div>
          </div>
          <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-medium text-emerald-700">
            Always on
          </span>
        </li>
        <li className="flex items-start justify-between gap-4 px-5 py-4">
          <div className="flex items-start gap-3">
            <span className="mt-0.5 rounded-full bg-sky-100 p-2 text-sky-700">
              <Bell className="h-4 w-4" aria-hidden />
            </span>
            <div>
              <p className="font-medium">In-app notifications</p>
              <p className="text-sm text-muted-foreground">
                Bell-icon alerts for new chat messages, ratings received and pod invitations.
                Browse them in{' '}
                <Link href="/notifications" className="text-primary underline-offset-4 hover:underline">
                  Notifications
                </Link>
                .
              </p>
            </div>
          </div>
          <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-medium text-emerald-700">
            Always on
          </span>
        </li>
        <li className="flex items-start justify-between gap-4 px-5 py-4">
          <div className="flex items-start gap-3">
            <span className="mt-0.5 rounded-full bg-violet-100 p-2 text-violet-700">
              <Smartphone className="h-4 w-4" aria-hidden />
            </span>
            <div>
              <p className="font-medium">Push (mobile)</p>
              <p className="text-sm text-muted-foreground">
                Push notifications are not yet available on web — we&apos;re building the
                companion app first. Email and in-app cover everything in the meantime.
              </p>
            </div>
          </div>
          <span className="rounded-full bg-muted px-3 py-1 text-xs font-medium text-muted-foreground">
            Coming soon
          </span>
        </li>
      </ul>

      <p className="text-xs text-muted-foreground">
        Granular per-category preferences (e.g. &quot;don&apos;t email me about pod activity&quot;)
        will arrive alongside the launch of pods — see{' '}
        <Link href="/pods" className="text-primary underline-offset-4 hover:underline">
          /pods
        </Link>
        .
      </p>
    </main>
  );
}
