'use client';

import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import type { DiscoverMatch } from '../lib/schemas';

export interface DiscoverCardProps {
  match: DiscoverMatch;
  className?: string;
}

export function DiscoverCard({ match, className }: DiscoverCardProps) {
  return (
    <article
      className={cn(
        'flex flex-col gap-4 rounded-lg border bg-card p-5 text-card-foreground shadow-sm transition hover:border-primary/40 hover:shadow-md',
        className
      )}
    >
      <header className="flex items-start gap-3">
        <Avatar name={match.fullName} url={match.avatarUrl} />
        <div className="flex-1">
          <h3 className="text-base font-semibold leading-tight">
            <Link href={`/users/${match.userId}`} className="hover:underline">
              {match.fullName}
            </Link>
          </h3>
          <p className="mt-0.5 text-xs text-muted-foreground">
            {match.countryCode ? `${match.countryCode} · ` : ''}
            {(match.languages ?? []).slice(0, 3).join(', ') || 'no languages listed'}
          </p>
          {match.bio ? (
            <p className="mt-2 line-clamp-2 text-sm text-muted-foreground">{match.bio}</p>
          ) : null}
        </div>
      </header>

      {match.topSkills.length > 0 ? (
        <ul className="flex flex-wrap gap-1.5">
          {match.topSkills.map((skill) => (
            <li
              key={skill.skillId}
              className="rounded-full border bg-muted px-2 py-0.5 text-xs"
              title={`${skill.name} · level ${skill.level} · ${skill.hourlySeedRate} seeds/h`}
            >
              {skill.name}
            </li>
          ))}
        </ul>
      ) : null}

      <footer className="mt-auto flex items-center justify-between border-t pt-3 text-sm">
        <div>
          <p className="font-medium">
            {match.ratingAvg > 0 ? match.ratingAvg.toFixed(1) : 'New'}
            {match.sessionsCompleted > 0 ? (
              <span className="ml-1 text-xs text-muted-foreground">
                · {match.sessionsCompleted} sessions
              </span>
            ) : null}
          </p>
          <p className="text-xs text-muted-foreground">
            {match.matchedSkillCount} matching skill
            {match.matchedSkillCount === 1 ? '' : 's'}
          </p>
        </div>
        <Button asChild size="sm">
          <Link href={`/users/${match.userId}`}>View profile</Link>
        </Button>
      </footer>
    </article>
  );
}

function Avatar({ name, url }: { name: string; url?: string | null }) {
  const initials = name
    .split(' ')
    .map((part) => part[0])
    .filter(Boolean)
    .slice(0, 2)
    .join('')
    .toUpperCase();
  if (url) {
    return (
      // eslint-disable-next-line @next/next/no-img-element
      <img
        src={url}
        alt={name}
        className="h-12 w-12 shrink-0 rounded-full object-cover"
      />
    );
  }
  return (
    <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-primary/10 text-sm font-semibold text-primary">
      {initials || '?'}
    </div>
  );
}
