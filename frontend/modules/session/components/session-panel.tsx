'use client';

import { useEffect, useState } from 'react';

import { Button } from '@/components/ui/button';
import { useCreateSessionRoom, useReportSessionIssue } from '../hooks/use-session';
import type { SessionRoom } from '../lib/schemas';
import { SessionChat } from './session-chat';
import { VideoCall } from './video-call';
import { Whiteboard } from './whiteboard';

export interface SessionPanelProps {
  bookingId: string;
  scheduledAt: string;
  status: string;
  currentUserId: string;
  joinWindowMinutes?: number;
  onClose?: () => void;
}

type View = 'idle' | 'in_call';

/**
 * Combines the video call, chat panel, and whiteboard into a single
 * overlay rendered on the booking detail page (T-M154). The
 * "Join Session" button is only enabled within {@code joinWindowMinutes}
 * of the scheduled start time (default 10 minutes), per the design.
 */
export function SessionPanel({
  bookingId,
  scheduledAt,
  status,
  currentUserId,
  joinWindowMinutes = 10,
  onClose
}: SessionPanelProps) {
  const [view, setView] = useState<View>('idle');
  const [room, setRoom] = useState<SessionRoom | null>(null);
  const [showWhiteboard, setShowWhiteboard] = useState(false);
  const [now, setNow] = useState(() => Date.now());
  const createRoom = useCreateSessionRoom();
  const reportIssue = useReportSessionIssue();

  useEffect(() => {
    const interval = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(interval);
  }, []);

  const scheduledMs = new Date(scheduledAt).getTime();
  const openFromMs = scheduledMs - joinWindowMinutes * 60 * 1000;
  const withinWindow = now >= openFromMs;
  const sessionActive = status === 'confirmed' || status === 'in_progress';
  const canJoin = withinWindow && sessionActive && !createRoom.isPending;

  const join = async () => {
    if (!canJoin) return;
    const result = await createRoom.mutateAsync(bookingId);
    setRoom(result);
    setView('in_call');
  };

  const leave = () => {
    setRoom(null);
    setView('idle');
    onClose?.();
  };

  const report = async () => {
    try {
      await reportIssue.mutateAsync({
        bookingId,
        payload: { category: 'general', description: 'Reported from video call' }
      });
    } catch (err) {
      console.warn('[session] report-issue failed', err);
    }
  };

  return (
    <section className="space-y-4 rounded-lg border bg-muted/30 p-4 shadow-sm">
      <header className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <h2 className="text-base font-semibold">Live session</h2>
          <p className="text-xs text-muted-foreground">
            {sessionActive
              ? withinWindow
                ? 'You can join now.'
                : `Available from ${new Date(openFromMs).toLocaleString()} (${joinWindowMinutes}m before start).`
              : 'Waiting for the teacher to confirm.'}
          </p>
        </div>
        {view === 'idle' ? (
          <Button onClick={join} disabled={!canJoin}>
            {createRoom.isPending ? 'Joining…' : 'Join session'}
          </Button>
        ) : (
          <Button variant="outline" onClick={leave}>
            Leave session
          </Button>
        )}
      </header>
      {view === 'in_call' && room ? (
        <div className="grid gap-4 lg:grid-cols-[2fr_1fr]">
          <VideoCall
            room={room}
            currentUserId={currentUserId}
            onLeave={leave}
            onReportIssue={report}
          />
          <div className="space-y-3">
            <SessionChat
              bookingId={bookingId}
              currentUserId={currentUserId}
              enabled
            />
            <Button
              variant={showWhiteboard ? 'default' : 'outline'}
              size="sm"
              onClick={() => setShowWhiteboard((prev) => !prev)}
            >
              {showWhiteboard ? 'Hide whiteboard' : 'Open whiteboard'}
            </Button>
            {showWhiteboard ? <Whiteboard bookingId={bookingId} /> : null}
          </div>
        </div>
      ) : (
        <p className="rounded-md border border-dashed bg-background p-4 text-sm text-muted-foreground">
          {createRoom.error
            ? createRoom.error.message
            : 'Press “Join session” to spin up a private Daily.co room for this booking.'}
        </p>
      )}
    </section>
  );
}
