import Link from 'next/link';

import {
  Bell,
  Calendar,
  KeyRound,
  Lock,
  Mail,
  Shield,
  User
} from 'lucide-react';

const SECTIONS = [
  {
    href: '/settings/account',
    title: 'Account',
    description: 'Update your name, email, and manage your profile.',
    icon: User
  },
  {
    href: '/settings/security',
    title: 'Security',
    description: 'Change your password and review active sessions.',
    icon: KeyRound
  },
  {
    href: '/settings/notifications',
    title: 'Notifications',
    description: 'Choose how SkillSeed reaches you about bookings and messages.',
    icon: Bell
  },
  {
    href: '/settings/connected-calendars',
    title: 'Connected calendars',
    description: 'Link Google Calendar so sessions show up on your schedule.',
    icon: Calendar
  },
  {
    href: '/settings/privacy',
    title: 'Privacy & data',
    description: 'Export or delete your account, and review our data policy.',
    icon: Shield
  }
];

export default function SettingsIndexPage() {
  return (
    <main className="container max-w-3xl space-y-8 py-10">
      <header className="space-y-2">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Settings</p>
        <h1 className="text-3xl font-bold tracking-tight">Your account</h1>
        <p className="text-sm text-muted-foreground">
          Manage your profile, security and how SkillSeed keeps in touch.
        </p>
      </header>

      <ul className="divide-y divide-border rounded-2xl border bg-card shadow-sm">
        {SECTIONS.map(({ href, title, description, icon: Icon }) => (
          <li key={href}>
            <Link
              href={href}
              className="flex items-center justify-between gap-4 px-5 py-4 transition hover:bg-muted/40"
            >
              <div className="flex items-start gap-3">
                <span className="mt-0.5 rounded-full bg-muted p-2 text-muted-foreground">
                  <Icon className="h-4 w-4" aria-hidden />
                </span>
                <div>
                  <p className="font-medium">{title}</p>
                  <p className="text-sm text-muted-foreground">{description}</p>
                </div>
              </div>
              <span aria-hidden className="text-muted-foreground">→</span>
            </Link>
          </li>
        ))}
        <li className="flex items-center gap-3 px-5 py-4 text-xs text-muted-foreground">
          <Mail className="h-4 w-4" aria-hidden />
          Need to change the email you sign in with?{' '}
          <Link href="/settings/account" className="font-semibold text-primary underline-offset-4 hover:underline">
            Update your account
          </Link>
        </li>
        <li className="flex items-center gap-3 px-5 py-4 text-xs text-muted-foreground">
          <Lock className="h-4 w-4" aria-hidden />
          Lost access to your 2FA codes?{' '}
          <Link href="/settings/security" className="font-semibold text-primary underline-offset-4 hover:underline">
            Visit security
          </Link>
        </li>
      </ul>
    </main>
  );
}
