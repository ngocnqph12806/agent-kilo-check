import type { LucideIcon } from 'lucide-react';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';

export interface EmptyStateProps {
  icon?: LucideIcon;
  emoji?: string;
  title: string;
  description?: string;
  action?: {
    label: string;
    href?: string;
    onClick?: () => void;
  };
  className?: string;
}

export function EmptyState({ icon: Icon, emoji, title, description, action, className }: EmptyStateProps) {
  return (
    <div
      className={cn(
        'flex flex-col items-center justify-center rounded-2xl border border-dashed border-[var(--brand-border)] bg-white px-6 py-16 text-center shadow-brand-card',
        className
      )}
    >
      <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-[var(--brand-hero-soft)] text-2xl">
        {emoji ? (
          <span aria-hidden>{emoji}</span>
        ) : Icon ? (
          <Icon className="h-6 w-6 text-primary" aria-hidden />
        ) : (
          <span aria-hidden>✨</span>
        )}
      </div>
      <h2 className="text-lg font-semibold text-[var(--brand-text-strong)]">{title}</h2>
      {description ? (
        <p className="mt-2 max-w-md text-sm text-[var(--brand-text-muted)]">{description}</p>
      ) : null}
      {action ? (
        <Button asChild={Boolean(action.href)} className="mt-6 rounded-full" {...(action.onClick ? { onClick: action.onClick } : {})}>
          {action.href ? <a href={action.href}>{action.label}</a> : <span>{action.label}</span>}
        </Button>
      ) : null}
    </div>
  );
}
