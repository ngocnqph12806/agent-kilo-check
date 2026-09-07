import { cn } from '@/lib/utils';

export interface AuthShellProps {
  title: string;
  description?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
  className?: string;
}

export function AuthShell({ title, description, children, footer, className }: AuthShellProps) {
  return (
    <main className={cn('flex min-h-screen items-center justify-center bg-muted/40 px-4 py-12', className)}>
      <div className="w-full max-w-md rounded-lg border bg-card p-8 shadow-sm">
        <header className="mb-6 space-y-1 text-center">
          <h1 className="text-2xl font-semibold tracking-tight">{title}</h1>
          {description ? <p className="text-sm text-muted-foreground">{description}</p> : null}
        </header>
        <div className="space-y-4">{children}</div>
        {footer ? <div className="mt-6 border-t pt-4 text-center text-sm text-muted-foreground">{footer}</div> : null}
      </div>
    </main>
  );
}
