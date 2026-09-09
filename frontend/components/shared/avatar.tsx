import Image from 'next/image';

import { cn } from '@/lib/utils';
import { getInitials } from '@/lib/initials';

export interface AvatarProps {
  src?: string | null;
  name: string;
  size?: 'sm' | 'md' | 'lg' | 'xl';
  className?: string;
}

const sizeClass: Record<NonNullable<AvatarProps['size']>, string> = {
  sm: 'h-8 w-8 text-xs',
  md: 'h-10 w-10 text-sm',
  lg: 'h-12 w-12 text-base',
  xl: 'h-16 w-16 text-lg'
};

const pxSize: Record<NonNullable<AvatarProps['size']>, number> = {
  sm: 32,
  md: 40,
  lg: 48,
  xl: 64
};

/**
 * Shared circular avatar with image + initials fallback. The
 * initials background uses the brand-credit token so it stays
 * on-palette when no photo is uploaded (audit #57).
 */
export function Avatar({ src, name, size = 'md', className }: AvatarProps) {
  const dimension = pxSize[size];
  if (src) {
    return (
      <Image
        src={src}
        alt={name}
        width={dimension}
        height={dimension}
        className={cn(
          'rounded-full border border-brand-default object-cover',
          sizeClass[size],
          className
        )}
      />
    );
  }
  return (
    <span
      role="img"
      aria-label={name}
      className={cn(
        'inline-flex shrink-0 items-center justify-center rounded-full border border-brand-default bg-brand-credit-bg font-semibold text-brand-credit',
        sizeClass[size],
        className
      )}
    >
      {getInitials(name)}
    </span>
  );
}
