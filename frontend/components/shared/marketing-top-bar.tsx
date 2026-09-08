import Link from 'next/link';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';

export interface MarketingTopBarProps {
  current?: 'discover' | 'how' | 'pricing' | 'about' | 'blog';
  authSlot?: React.ReactNode;
  className?: string;
}

const navItems = [
  { key: 'discover', label: 'Discover', href: '/discover' },
  { key: 'how', label: 'How it works', href: '/#how' },
  { key: 'pricing', label: 'Pricing', href: '/pricing' },
  { key: 'about', label: 'About', href: '/about' },
  { key: 'blog', label: 'Blog', href: '/blog' }
] as const;

export function MarketingTopBar({ current, authSlot, className }: MarketingTopBarProps) {
  return (
    <header
      className={cn(
        'sticky top-0 z-40 border-b border-[var(--brand-border)] bg-background/95 backdrop-blur',
        className
      )}
    >
      <div className="mx-auto flex h-[72px] w-full max-w-[1280px] items-center justify-between px-6 md:px-10">
        <div className="flex items-center gap-12">
          <Link href="/" aria-label="SkillSeed home" className="inline-flex items-center">
            <span className="inline-flex h-7 w-7 items-center justify-center rounded-full bg-brand-cta text-white shadow-brand-cta">
              <span className="text-sm">🌱</span>
            </span>
            <span className="ml-2 text-xl font-bold text-[var(--brand-text-strong)]">SkillSeed</span>
          </Link>
          <nav className="hidden items-center gap-8 text-sm font-medium text-[var(--brand-text-muted)] md:flex">
            {navItems.map((item) => {
              const active = current === item.key;
              return (
                <Link
                  key={item.key}
                  href={item.href}
                  className={cn(
                    'transition-colors hover:text-[var(--brand-text-strong)]',
                    active && 'font-semibold text-primary'
                  )}
                >
                  {item.label}
                </Link>
              );
            })}
          </nav>
        </div>

        <div className="flex items-center gap-3">
          {authSlot ?? (
            <>
              <Button asChild variant="ghost" size="sm" className="text-sm font-medium">
                <Link href="/login">Login</Link>
              </Button>
              <Button
                asChild
                variant="brand"
                size="sm"
                className="rounded-full px-5 text-sm font-semibold"
              >
                <Link href="/register">Sign up free</Link>
              </Button>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
