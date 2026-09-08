import Link from 'next/link';
import { Wrench } from 'lucide-react';

import { Button } from '@/components/ui/button';

export const metadata = {
  title: 'Under maintenance · SkillSeed',
  robots: { index: false, follow: false }
};

export default function MaintenancePage() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-br from-amber-500 to-amber-600 px-4 text-white">
      <div className="flex max-w-lg flex-col items-center text-center">
        <div className="mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-white/15 shadow-brand-card">
          <Wrench className="h-12 w-12 text-white" aria-hidden />
        </div>

        <h1 className="text-3xl font-extrabold tracking-tight text-white md:text-4xl">
          We&apos;ll be right back
        </h1>
        <p className="mt-3 max-w-md text-base text-amber-100">
          SkillSeed is under maintenance. Brief downtime — we&apos;re rolling out improvements and will be back shortly.
        </p>
        <p className="mt-1 text-sm text-amber-100">
          Estimated: ~30 minutes · Started at 03:00 UTC
        </p>

        <div className="mt-8 w-full max-w-sm rounded-full border border-white/30 bg-white/15 px-4 py-3 text-left shadow-brand-card">
          <div className="flex items-center gap-3">
            <span className="relative flex h-3 w-3">
              <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-[var(--brand-rose)] opacity-75" />
              <span className="relative inline-flex h-3 w-3 rounded-full bg-[var(--brand-rose)]" />
            </span>
            <div>
              <p className="text-sm font-bold text-white">Major upgrade in progress</p>
              <p className="text-xs text-amber-100">Database migration · v2.4.1</p>
            </div>
          </div>
        </div>

        <div className="mt-4 w-full max-w-sm rounded-full border border-white/30 bg-black/20 px-4 py-3 text-left shadow-brand-card">
          <div className="flex items-center gap-3">
            <span aria-hidden className="text-xl">📊</span>
            <div>
              <p className="text-sm font-semibold text-white">Live status updates</p>
              <p className="text-xs text-amber-100">
                <a
                  href="https://status.skillseed.app"
                  className="font-medium underline underline-offset-2 hover:text-white"
                  target="_blank"
                  rel="noreferrer"
                >
                  status.skillseed.app
                </a>
                {' · '}Twitter @skillseed
              </p>
            </div>
          </div>
        </div>

        <div className="mt-8 flex flex-wrap justify-center gap-3">
          <Button asChild className="rounded-full bg-white text-amber-700 hover:bg-white/90">
            <Link href="https://status.skillseed.app" target="_blank" rel="noreferrer">
              📊 Status page
            </Link>
          </Button>
          <Button
            asChild
            variant="outline"
            className="rounded-full border-white/40 bg-white/10 text-white hover:bg-white/20"
          >
            <Link href="/">🏠 Back to home</Link>
          </Button>
        </div>

        <p className="mt-8 text-xs text-amber-100">
          🔔 Subscribe to updates — we&apos;ll email when we&apos;re back.
        </p>
      </div>
    </main>
  );
}