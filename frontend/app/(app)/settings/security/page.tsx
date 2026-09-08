import Link from 'next/link';

import { Button } from '@/components/ui/button';

export default function SettingsSecurityPage() {
  return (
    <main className="container max-w-3xl space-y-8 py-10">
      <header className="space-y-2">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Settings</p>
        <h1 className="text-3xl font-bold tracking-tight">Security</h1>
        <p className="text-sm text-muted-foreground">
          Protect your account with a strong password and review active sessions.
        </p>
      </header>

      <section className="space-y-4 rounded-2xl border bg-card p-6 shadow-sm">
        <div>
          <h2 className="text-lg font-semibold">Password</h2>
          <p className="text-sm text-muted-foreground">
            SkillSeed doesn&apos;t store passwords you set here in plain text, but we recommend a
            pass-phrase you only use on SkillSeed.
          </p>
        </div>
        <ol className="list-decimal space-y-2 pl-5 text-sm">
          <li>
            Use the <em>Forgot password</em> link on the sign-in page to receive a reset
            email at the address on your account.
          </li>
          <li>Pick a new password of at least 8 characters with one letter and one number.</li>
          <li>Sign back in on every device you use.</li>
        </ol>
        <div className="flex justify-end">
          <Button asChild variant="brand">
            <Link href="/forgot-password">Reset my password</Link>
          </Button>
        </div>
      </section>

      <section className="space-y-3 rounded-2xl border bg-card p-6 shadow-sm">
        <h2 className="text-lg font-semibold">Active sessions</h2>
        <p className="text-sm text-muted-foreground">
          Each session you sign into stores a short-lived refresh token in a Secure / HttpOnly
          cookie on that browser. You can revoke any of them by signing out from that device, or
          all of them at once from the button below.
        </p>
        <form action="/api/v1/auth/logout" method="post" className="pt-2">
          <Button type="submit" variant="destructive">
            Sign out this browser
          </Button>
        </form>
      </section>

      <section className="space-y-3 rounded-2xl border bg-card p-6 shadow-sm">
        <h2 className="text-lg font-semibold">Two-factor authentication</h2>
        <p className="text-sm text-muted-foreground">
          Coming soon — we&apos;re launching TOTP-based 2FA in the next release. Until then,
          please use the password reset flow above if you suspect your account is compromised.
        </p>
      </section>
    </main>
  );
}
