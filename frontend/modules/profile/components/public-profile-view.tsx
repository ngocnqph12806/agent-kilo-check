'use client';

import { useMemo, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import {
  Award,
  Calendar,
  Languages,
  MapPin,
  MessageSquare,
  Sparkles,
  Star
} from 'lucide-react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { BookingModal } from '@/modules/booking/components/booking-modal';
import { ReviewsList } from '@/modules/rating/components/reviews-list';
import { useWalletSummary } from '@/modules/wallet/hooks/use-wallet';

import { useCurrentUser } from '@/modules/auth/hooks/use-auth-mutations';
import {
  usePublicProfile,
  useUserFreeSlots
} from '../hooks/use-public-profile';
import type { PublicOfferedSkill, PublicUserProfile } from '../lib/schemas';

export interface PublicProfileViewProps {
  userId: string;
}

export function PublicProfileView({ userId }: PublicProfileViewProps) {
  const profile = usePublicProfile(userId);
  const slots = useUserFreeSlots(userId, 14);
  const me = useCurrentUser();
  const wallet = useWalletSummary();
  const router = useRouter();
  const [bookingSkill, setBookingSkill] = useState<string>('');
  const [bookingOpen, setBookingOpen] = useState(false);

  if (profile.isLoading) {
    return <CenteredLoading />;
  }

  if (profile.isError || !profile.data) {
    return (
      <CenteredError
        message={
          profile.error instanceof Error
            ? profile.error.message
            : 'Profile not found.'
        }
      />
    );
  }

  const user = profile.data;
  const isSelf = me.data?.id === user.id;
  const currentBalance = wallet.data?.balance;

  return (
    <main className="bg-[var(--brand-surface)] pb-16">
      <ProfileHero
        user={user}
        isSelf={isSelf}
        isAuthenticated={Boolean(me.data)}
        canBook={user.offeredSkills.length > 0 && !isSelf}
        onBookClick={() => setBookingOpen(true)}
      />

      <div className="container mx-auto max-w-5xl space-y-10 px-4 py-10">
        <StatsRow user={user} />

        <div className="grid gap-6 lg:grid-cols-3">
          <div className="space-y-6 lg:col-span-2">
            {user.bio ? (
              <Card>
                <h2 className="mb-3 text-lg font-bold text-[var(--brand-text-strong)]">
                  About {user.fullName.split(' ')[0]}
                </h2>
                <p className="whitespace-pre-line text-sm leading-relaxed text-[var(--brand-text-muted)]">
                  {user.bio}
                </p>
                {user.languages.length > 0 ? (
                  <div className="mt-4 flex flex-wrap items-center gap-2">
                    <span className="inline-flex items-center gap-1 text-xs font-semibold uppercase tracking-wide text-[var(--brand-text-subtle)]">
                      <Languages className="h-3.5 w-3.5" aria-hidden /> Speaks
                    </span>
                    {user.languages.map((lang) => (
                      <span
                        key={lang}
                        className="rounded-full bg-[var(--brand-hero-soft)] px-3 py-1 text-xs font-medium text-[var(--brand-cta-to)]"
                      >
                        {lang}
                      </span>
                    ))}
                  </div>
                ) : null}
              </Card>
            ) : null}

            <SkillsSection
              title="Skills I teach"
              emoji="🍝"
              emptyMessage={
                user.onboardingCompleted
                  ? 'No skills listed yet.'
                  : 'Onboarding incomplete.'
              }
              skills={user.offeredSkills}
            />

            <Card>
              <div className="mb-4 flex items-center justify-between">
                <h2 className="text-lg font-bold text-[var(--brand-text-strong)]">
                  Reviews
                </h2>
                <p className="text-xs text-[var(--brand-text-muted)]">
                  {user.sessionsCompleted} completed session
                  {user.sessionsCompleted === 1 ? '' : 's'} ·{' '}
                  {user.ratingAvg > 0
                    ? `${user.ratingAvg.toFixed(1)} ⭐ average`
                    : 'no rating yet'}
                </p>
              </div>
              <ReviewsList userId={user.id} />
            </Card>
          </div>

          <aside className="space-y-4">
            <BookSessionPanel
              user={user}
              slots={slots.data ?? []}
              slotsLoading={slots.isLoading}
              bookingSkill={bookingSkill}
              setBookingSkill={setBookingSkill}
              isSelf={isSelf}
              isAuthenticated={Boolean(me.data)}
              onOpenBooking={() => setBookingOpen(true)}
            />
            <Card className="space-y-3">
              <h3 className="text-sm font-bold text-[var(--brand-text-strong)]">
                Location & timezone
              </h3>
              <p className="inline-flex items-center gap-2 text-sm text-[var(--brand-text-muted)]">
                <MapPin className="h-4 w-4 text-[var(--brand-cta-from)]" aria-hidden />
                {user.countryCode ? `${user.countryCode}` : 'Location not listed'}
              </p>
              <p className="inline-flex items-center gap-2 text-sm text-[var(--brand-text-muted)]">
                <Calendar className="h-4 w-4 text-[var(--brand-cta-from)]" aria-hidden />
                {user.timezone}
              </p>
            </Card>
          </aside>
        </div>
      </div>

      <BookingModal
        teacherId={user.id}
        teacherName={user.fullName}
        skills={user.offeredSkills.map((s) => ({
          id: s.id,
          name: s.name,
          hourlySeedRate: s.hourlySeedRate
        }))}
        slots={slots.data ?? []}
        defaultSkillId={bookingSkill || user.offeredSkills[0]?.id}
        currentBalance={currentBalance}
        open={bookingOpen}
        onClose={() => setBookingOpen(false)}
        onCreated={(bookingId) => {
          setBookingOpen(false);
          router.push(`/bookings/${bookingId}`);
        }}
      />
    </main>
  );
}

interface ProfileHeroProps {
  user: PublicUserProfile;
  isSelf: boolean;
  isAuthenticated: boolean;
  canBook: boolean;
  onBookClick: () => void;
}

function ProfileHero({ user, isSelf, isAuthenticated, canBook, onBookClick }: ProfileHeroProps) {
  const memberSince = useMemo(
    () => new Date(user.createdAt).toLocaleDateString(undefined, { month: 'long', year: 'numeric' }),
    [user.createdAt]
  );

  return (
    <section className="relative">
      <div
        aria-hidden
        className="h-32 w-full bg-brand-cta md:h-40"
      />
      <div className="container mx-auto max-w-5xl px-4">
        <div className="-mt-12 flex flex-col gap-4 sm:-mt-16 sm:flex-row sm:items-end sm:justify-between">
          <div className="flex flex-col items-start gap-4 sm:flex-row sm:items-end">
            <Avatar name={user.fullName} url={user.avatarUrl} />
            <div className="space-y-1 pb-1">
              <div className="flex flex-wrap items-center gap-2">
                <h1 className="text-2xl font-bold tracking-tight text-[var(--brand-text-strong)]">
                  {user.fullName}
                </h1>
                {user.verified ? (
                  <span className="inline-flex items-center gap-1 rounded-full bg-[var(--brand-hero-soft)] px-2 py-0.5 text-xs font-medium text-[var(--brand-cta-to)]">
                    <Sparkles className="h-3 w-3" aria-hidden /> Verified
                  </span>
                ) : null}
                {user.ratingAvg >= 4.8 && user.sessionsCompleted >= 50 ? (
                  <span className="inline-flex items-center gap-1 rounded-full bg-[var(--brand-warn)] px-2 py-0.5 text-xs font-medium text-[var(--brand-warn-text)]">
                    <Award className="h-3 w-3" aria-hidden /> Top Mentor
                  </span>
                ) : null}
              </div>
              {user.bio ? (
                <p className="max-w-xl text-sm text-[var(--brand-text-muted)]">
                  {user.bio.length > 140 ? `${user.bio.slice(0, 140)}…` : user.bio}
                </p>
              ) : null}
              <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-[var(--brand-text-muted)]">
                {user.countryCode ? (
                  <span className="inline-flex items-center gap-1">
                    <MapPin className="h-3.5 w-3.5" aria-hidden />
                    {user.countryCode}
                  </span>
                ) : null}
                {user.languages.length > 0 ? (
                  <span className="inline-flex items-center gap-1">
                    <Languages className="h-3.5 w-3.5" aria-hidden />
                    {user.languages.slice(0, 3).join(', ')}
                  </span>
                ) : null}
                <span className="inline-flex items-center gap-1">
                  <Calendar className="h-3.5 w-3.5" aria-hidden />
                  Joined {memberSince}
                </span>
              </div>
            </div>
          </div>

          <div className="flex flex-wrap gap-2 pb-2">
            <Button
              type="button"
              variant="outline"
              className="rounded-full"
              disabled={!isAuthenticated}
              onClick={onBookClick}
            >
              <MessageSquare className="mr-2 h-4 w-4" aria-hidden />
              Message
            </Button>
            {!isSelf && canBook ? (
              <Button
                type="button"
                variant="brand"
                className="rounded-full"
                onClick={onBookClick}
                disabled={!isAuthenticated}
              >
                {isAuthenticated ? 'Book a session →' : 'Sign in to book'}
              </Button>
            ) : (
              <Button asChild variant="outline" className="rounded-full">
                <Link href="/discover">← Back to Discover</Link>
              </Button>
            )}
          </div>
        </div>
      </div>
    </section>
  );
}

function StatsRow({ user }: { user: PublicUserProfile }) {
  const stats = [
    {
      label: 'Rating',
      icon: Star,
      value: user.ratingAvg > 0 ? user.ratingAvg.toFixed(1) : '—',
      sub: user.ratingAvg > 0 ? '⭐⭐⭐⭐⭐ average' : 'No ratings yet'
    },
    {
      label: 'Sessions',
      icon: Calendar,
      value: user.sessionsCompleted.toLocaleString(),
      sub: 'completed'
    },
    {
      label: 'Skills taught',
      icon: Award,
      value: user.offeredSkills.length.toString(),
      sub: user.offeredSkills.length === 1 ? 'skill' : 'skills'
    },
    {
      label: 'Member since',
      icon: Sparkles,
      value: new Date(user.createdAt).getFullYear().toString(),
      sub: 'on SkillSeed'
    }
  ];

  return (
    <section className="grid grid-cols-2 gap-3 md:grid-cols-4">
      {stats.map((stat) => {
        const Icon = stat.icon;
        return (
          <Card key={stat.label} className="flex flex-col gap-1 p-4">
            <span className="inline-flex items-center gap-1 text-[10px] font-semibold uppercase tracking-wide text-[var(--brand-text-subtle)]">
              <Icon className="h-3 w-3" aria-hidden /> {stat.label}
            </span>
            <span className="text-2xl font-extrabold text-[var(--brand-text-strong)]">
              {stat.value}
            </span>
            <span className="text-xs text-[var(--brand-text-muted)]">{stat.sub}</span>
          </Card>
        );
      })}
    </section>
  );
}

interface SkillsSectionProps {
  title: string;
  emoji: string;
  emptyMessage: string;
  skills: PublicOfferedSkill[];
}

function SkillsSection({ title, emoji, emptyMessage, skills }: SkillsSectionProps) {
  return (
    <Card>
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-bold text-[var(--brand-text-strong)]">{title}</h2>
        <span className="text-xs text-[var(--brand-text-muted)]">
          {skills.length} {skills.length === 1 ? 'skill' : 'skills'}
        </span>
      </div>
      {skills.length === 0 ? (
        <p className="rounded-lg border border-dashed border-[var(--brand-border)] bg-[var(--brand-surface)] px-4 py-6 text-sm text-[var(--brand-text-muted)]">
          {emptyMessage}
        </p>
      ) : (
        <ul className="grid gap-3 sm:grid-cols-2">
          {skills.map((skill, idx) => (
            <li
              key={skill.id}
              className={cn(
                'rounded-xl border border-[var(--brand-border)] p-4',
                SKILL_TILE_BG[idx % SKILL_TILE_BG.length]
              )}
            >
              <div className="flex items-start gap-3">
                <span aria-hidden className="text-3xl">
                  {emoji}
                </span>
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-bold">{skill.name}</p>
                  <p className="text-xs capitalize">{skill.category}</p>
                  <div className="mt-1 flex items-center gap-2 text-xs">
                    <span aria-label={`Level ${skill.level} of 5`} className="text-amber-500">
                      {renderStars(skill.level)}
                    </span>
                    <span className="font-medium">{skill.hourlySeedRate} seeds/hr</span>
                  </div>
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}
    </Card>
  );
}

const SKILL_TILE_BG = [
  'bg-[var(--brand-warn)]/60 text-[var(--brand-warn-text)]',
  'bg-[var(--brand-hero-soft)] text-[var(--brand-cta-to)]',
  'bg-rose-50 text-rose-700',
  'bg-emerald-50 text-emerald-700'
];

function renderStars(level: number): string {
  const clamped = Math.max(0, Math.min(5, Math.round(level)));
  return '★'.repeat(clamped) + '☆'.repeat(5 - clamped);
}

interface BookSessionPanelProps {
  user: PublicUserProfile;
  slots: Array<{ startsAt: string; endsAt: string; timezone: string }>;
  slotsLoading: boolean;
  bookingSkill: string;
  setBookingSkill: (next: string) => void;
  isSelf: boolean;
  isAuthenticated: boolean;
  onOpenBooking: () => void;
}

function BookSessionPanel({
  user,
  slots,
  slotsLoading,
  bookingSkill,
  setBookingSkill,
  isSelf,
  isAuthenticated,
  onOpenBooking
}: BookSessionPanelProps) {
  const canBook = user.offeredSkills.length > 0 && !isSelf;
  return (
    <Card className="space-y-3">
      <h2 className="text-base font-bold text-[var(--brand-text-strong)]">
        Book a session
      </h2>

      {canBook ? (
        <>
          <div className="space-y-1">
            <label className="text-xs font-medium" htmlFor="book-skill">
              Skill
            </label>
            <select
              id="book-skill"
              className="flex h-10 w-full rounded-md border border-[var(--brand-border)] bg-background px-3 text-sm"
              value={bookingSkill || user.offeredSkills[0]?.id || ''}
              onChange={(e) => setBookingSkill(e.target.value)}
            >
              {user.offeredSkills.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name}
                </option>
              ))}
            </select>
          </div>

          <div className="space-y-2">
            <p className="text-xs font-medium text-[var(--brand-text-strong)]">
              Upcoming availability
            </p>
            {slotsLoading ? (
              <p className="text-xs text-[var(--brand-text-muted)]">Loading slots…</p>
            ) : slots.length === 0 ? (
              <p className="text-xs text-[var(--brand-text-muted)]">
                No free slots in the next 14 days.
              </p>
            ) : (
              <ul className="max-h-48 space-y-1 overflow-auto pr-1 text-xs">
                {slots.slice(0, 8).map((slot) => (
                  <li
                    key={slot.startsAt}
                    className="rounded border border-[var(--brand-border)] bg-[var(--brand-surface)] px-2 py-1 text-[var(--brand-text-muted)]"
                  >
                    {new Date(slot.startsAt).toLocaleString()} –{' '}
                    {new Date(slot.endsAt).toLocaleTimeString([], {
                      hour: '2-digit',
                      minute: '2-digit'
                    })}
                  </li>
                ))}
              </ul>
            )}
          </div>

          <Button
            type="button"
            variant="brand"
            className="w-full rounded-full"
            disabled={!isAuthenticated}
            onClick={onOpenBooking}
          >
            {!isAuthenticated
              ? 'Sign in to book'
              : slots.length === 0
                ? 'No available slots'
                : 'Book session'}
          </Button>
          {isAuthenticated && slots.length > 0 ? (
            <p className="text-center text-xs text-[var(--brand-text-muted)]">
              Seeds are held in escrow and released to the teacher when the session completes.
            </p>
          ) : null}
        </>
      ) : (
        <p className="text-sm text-[var(--brand-text-muted)]">
          {isSelf
            ? "You can't book yourself."
            : "This teacher hasn't added any skills yet."}
        </p>
      )}
    </Card>
  );
}

interface CardProps {
  children: React.ReactNode;
  className?: string;
}

function Card({ children, className }: CardProps) {
  return (
    <section
      className={cn(
        'rounded-2xl border border-[var(--brand-border)] bg-card p-6 shadow-brand-card',
        className
      )}
    >
      {children}
    </section>
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
        className="h-24 w-24 rounded-full border-4 border-white object-cover shadow-brand-card"
      />
    );
  }
  return (
    <div className="flex h-24 w-24 items-center justify-center rounded-full border-4 border-white bg-[var(--brand-warn)] text-2xl font-bold text-[var(--brand-warn-text)] shadow-brand-card">
      {initials || '?'}
    </div>
  );
}

function CenteredLoading() {
  return (
    <main className="container mx-auto max-w-3xl py-20 text-center text-sm text-[var(--brand-text-muted)]">
      Loading profile…
    </main>
  );
}

function CenteredError({ message }: { message: string }) {
  return (
    <main className="container mx-auto max-w-3xl py-20">
      <p className="rounded-md border border-[var(--brand-rose)]/30 bg-[var(--brand-rose)]/5 p-4 text-sm text-[var(--brand-rose)]">
        {message}
      </p>
    </main>
  );
}