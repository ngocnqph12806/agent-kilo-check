'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { BookingModal } from '@/modules/booking/components/booking-modal';
import { ReviewsTab } from '@/modules/rating';
import { useWalletSummary } from '@/modules/wallet/hooks/use-wallet';

import {
  usePublicProfile,
  useUserFreeSlots
} from '../hooks/use-public-profile';
import { useCurrentUser } from '@/modules/auth/hooks/use-auth-mutations';
import type { PublicUserProfile } from '../lib/schemas';

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
    <main className="container mx-auto max-w-4xl space-y-8 py-10">
      <ProfileHeader user={user} />

      <section className="grid gap-6 lg:grid-cols-[1fr_320px]">
        <div className="space-y-6">
          {user.bio ? (
            <Block title="About">
              <p className="whitespace-pre-line text-sm">{user.bio}</p>
            </Block>
          ) : null}

          <Block
            title="Skills they teach"
            emptyMessage={user.onboardingCompleted ? 'No skills listed yet.' : 'Onboarding incomplete.'}
          >
            <ul className="divide-y rounded-lg border bg-card">
              {user.offeredSkills.map((skill) => (
                <li
                  key={skill.id}
                  className="flex items-center justify-between px-4 py-3 text-sm"
                >
                  <div>
                    <p className="font-medium">{skill.name}</p>
                    <p className="text-xs uppercase text-muted-foreground">
                      {skill.category} · level {skill.level}
                    </p>
                  </div>
                  <span className="text-xs text-muted-foreground">
                    {skill.hourlySeedRate} seeds / hour
                  </span>
                </li>
              ))}
            </ul>
          </Block>

          <Block
            title="Reviews"
            emptyMessage="No reviews yet — be the first to book a session."
          >
            <p className="text-sm text-muted-foreground">
              {user.sessionsCompleted} completed session
              {user.sessionsCompleted === 1 ? '' : 's'} ·{' '}
              {user.ratingAvg > 0 ? `${user.ratingAvg.toFixed(1)} ⭐ average` : 'no rating yet'}
            </p>
            <div className="mt-4">
              <ReviewsTab userId={user.id} />
            </div>
          </Block>
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
          <Block title="Languages">
            <p className="text-sm">
              {user.languages.length > 0 ? user.languages.join(', ') : 'Not listed'}
            </p>
          </Block>
          <Block title="Timezone">
            <p className="text-sm">{user.timezone}</p>
          </Block>
        </aside>
      </section>

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

function ProfileHeader({ user }: { user: PublicUserProfile }) {
  return (
    <header className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex items-center gap-4">
        <Avatar name={user.fullName} url={user.avatarUrl} />
        <div>
          <h1 className="text-2xl font-bold tracking-tight">{user.fullName}</h1>
          <p className="text-sm text-muted-foreground">
            {user.countryCode ? `${user.countryCode} · ` : ''}joined{' '}
            {new Date(user.createdAt).toLocaleDateString()}
          </p>
          <p className="mt-1 text-xs text-muted-foreground">
            {user.ratingAvg > 0 ? (
              <span>{user.ratingAvg.toFixed(1)} ⭐ · {user.sessionsCompleted} sessions</span>
            ) : (
              <span>New to SkillSeed</span>
            )}
            {user.verified ? (
              <span className="ml-2 rounded-full border border-emerald-300 px-2 py-0.5 text-emerald-700">
                Verified
              </span>
            ) : null}
          </p>
        </div>
      </div>
      <div>
        <Button asChild variant="outline" size="sm">
          <Link href="/discover">← Back to Discover</Link>
        </Button>
      </div>
    </header>
  );
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
    <div className="space-y-3 rounded-lg border bg-card p-4 shadow-sm">
      <h2 className="text-base font-semibold">Book a session</h2>

      {canBook ? (
        <>
          <div className="space-y-1">
            <label className="text-xs font-medium" htmlFor="book-skill">
              Skill
            </label>
            <select
              id="book-skill"
              className="flex h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
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
            <p className="text-xs font-medium">Upcoming availability</p>
            {slotsLoading ? (
              <p className="text-xs text-muted-foreground">Loading slots…</p>
            ) : slots.length === 0 ? (
              <p className="text-xs text-muted-foreground">
                No free slots in the next 14 days.
              </p>
            ) : (
              <ul className="max-h-48 space-y-1 overflow-auto pr-1 text-xs">
                {slots.slice(0, 8).map((slot) => (
                  <li
                    key={slot.startsAt}
                    className="rounded border bg-muted/40 px-2 py-1"
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

          <Button className="w-full" disabled={!isAuthenticated} onClick={onOpenBooking}>
            {!isAuthenticated
              ? 'Sign in to book'
              : slots.length === 0
                ? 'No available slots'
                : 'Book session'}
          </Button>
          {isAuthenticated && slots.length > 0 ? (
            <p className="text-center text-xs text-muted-foreground">
              Seeds are held in escrow and released to the teacher when the
              session completes.
            </p>
          ) : null}
        </>
      ) : (
        <p className="text-sm text-muted-foreground">
          {isSelf
            ? "You can't book yourself."
            : "This teacher hasn't added any skills yet."}
        </p>
      )}
    </div>
  );
}

function Block({
  title,
  children,
  emptyMessage
}: {
  title: string;
  children?: React.ReactNode;
  emptyMessage?: string;
}) {
  const isEmpty = !children;
  return (
    <section className="space-y-3">
      <h2 className="text-base font-semibold">{title}</h2>
      {isEmpty ? (
        <p className="text-sm text-muted-foreground">{emptyMessage}</p>
      ) : (
        children
      )}
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
        className="h-16 w-16 rounded-full object-cover"
      />
    );
  }
  return (
    <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10 text-lg font-semibold text-primary">
      {initials || '?'}
    </div>
  );
}

function CenteredLoading() {
  return (
    <main className="container mx-auto max-w-3xl py-20 text-center text-sm text-muted-foreground">
      Loading profile…
    </main>
  );
}

function CenteredError({ message }: { message: string }) {
  return (
    <main className="container mx-auto max-w-3xl py-20">
      <p className="rounded-md border border-destructive/30 bg-destructive/5 p-4 text-sm text-destructive">
        {message}
      </p>
    </main>
  );
}
