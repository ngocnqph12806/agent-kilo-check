'use client';

import { useEffect, useState } from 'react';

import { VideoCall } from '@/modules/session/components/video-call';
import { useOpenSessionRoom } from '@/modules/session/hooks/use-open-session-room';

interface JoinSessionButtonProps {
  bookingId: string;
  scheduledAt: string;
}

const JOIN_WINDOW_MS = 10 * 60 * 1000;

export function JoinSessionButton({ bookingId, scheduledAt }: JoinSessionButtonProps) {
  const [now, setNow] = useState(() => Date.now());
  const [active, setActive] = useState<{ roomUrl: string; roomName: string; token: string;
    role: 'owner' | 'participant'; expiresAt: string;
    scheduledAt: string; durationMinutes: number } | null>(null);
  const openRoom = useOpenSessionRoom();

  useEffect(() => {
    const id = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(id);
  }, []);

  const target = new Date(scheduledAt).getTime();
  const withinWindow = Number.isFinite(target) && now >= target - JOIN_WINDOW_MS;

  const handleJoin = () => {
    openRoom.mutate(bookingId, {
      onSuccess: (room) => setActive(room),
      onError: (err) => {
        // Surface error as alert for Sprint 3 simplicity; can be replaced
        // with toast/snackbar in Sprint 4 polish.
        // eslint-disable-next-line no-alert
        alert(err.message);
      }
    });
  };

  if (active) {
    return <VideoCall room={active} onLeave={() => setActive(null)} />;
  }

  return (
    <button
      type="button"
      onClick={handleJoin}
      disabled={!withinWindow || openRoom.isPending}
      className="inline-flex items-center justify-center rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-colors hover:bg-primary/90 disabled:pointer-events-none disabled:opacity-50"
    >
      {openRoom.isPending
        ? 'Opening session…'
        : withinWindow
          ? 'Join session'
          : 'Join session (opens 10 min before start)'}
    </button>
  );
}
