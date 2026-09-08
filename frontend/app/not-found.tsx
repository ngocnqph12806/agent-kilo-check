import Link from 'next/link';

import { Button } from '@/components/ui/button';

export const metadata = {
  title: 'Page not found · SkillSeed',
  robots: { index: false, follow: false }
};

export default function NotFound() {
  return (
    <main className="container flex min-h-screen flex-col items-center justify-center gap-6 text-center">
      <span className="rounded-full border bg-muted px-3 py-1 text-xs font-medium uppercase tracking-wide text-muted-foreground">
        404
      </span>
      <h1 className="text-balance text-4xl font-bold tracking-tight">
        We couldn&apos;t find that page.
      </h1>
      <p className="max-w-md text-balance text-muted-foreground">
        The link might be broken, or the page may have moved. Head back home and try a different
        route.
      </p>
      <div className="flex gap-3">
        <Button asChild>
          <Link href="/">Back to home</Link>
        </Button>
        <Button asChild variant="outline">
          <Link href="/discover">Browse skills</Link>
        </Button>
      </div>
    </main>
  );
}