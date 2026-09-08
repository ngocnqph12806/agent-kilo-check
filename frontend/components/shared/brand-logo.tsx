import Link from 'next/link';

import { cn } from '@/lib/utils';

export interface BrandLogoProps {
  href?: string;
  variant?: 'app' | 'hero';
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

const sizeMap = {
  sm: { circle: 10, gap: 8, text: 'text-base' },
  md: { circle: 14, gap: 10, text: 'text-xl' },
  lg: { circle: 18, gap: 12, text: 'text-2xl' }
} as const;

export function BrandLogo({ href = '/discover', variant = 'app', size = 'md', className }: BrandLogoProps) {
  const s = sizeMap[size];
  const isHero = variant === 'hero';

  const inner = (
    <span className={cn('inline-flex items-center font-bold tracking-tight', className)}>
      <span
        className="inline-flex shrink-0 items-center justify-center rounded-full bg-brand-cta text-white shadow-brand-cta"
        style={{ width: s.circle * 2, height: s.circle * 2 }}
        aria-hidden
      >
        <span style={{ fontSize: s.circle }}>🌱</span>
      </span>
      <span className="ml-2" style={{ marginLeft: s.gap }} />
      <span
        className={cn(s.text, isHero ? 'text-white' : 'text-[var(--brand-text-strong)]')}
      >
        SkillSeed
      </span>
    </span>
  );

  if (!href) return inner;
  return (
    <Link href={href} className="inline-flex items-center" aria-label="SkillSeed home">
      {inner}
    </Link>
  );
}
