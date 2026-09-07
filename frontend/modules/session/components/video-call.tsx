'use client';

import { useCallback, useEffect, useRef, useState } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import { WhiteboardPanel } from './whiteboard-panel';

import type { SessionRoom } from '../lib/session-api';

type DailyCallInstance = {
  join: () => Promise<void>;
  leave: () => Promise<void>;
  setLocalAudio: (enabled: boolean) => void;
  setLocalVideo: (enabled: boolean) => void;
  startScreenShare: () => Promise<void>;
  stopScreenShare: () => Promise<void>;
  destroy: () => Promise<void>;
  on: (event: string, handler: (...args: unknown[]) => void) => void;
  off: (event: string, handler: (...args: unknown[]) => void) => void;
  localAudio: () => boolean;
  localVideo: () => boolean;
  participants: () => Record<string, unknown>;
};

interface VideoCallProps {
  room: SessionRoom;
  onLeave: () => void;
}

type CallStatus = 'joining' | 'joined' | 'left' | 'error';

export function VideoCall({ room, onLeave }: VideoCallProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const callRef = useRef<DailyCallInstance | null>(null);
  const [status, setStatus] = useState<CallStatus>('joining');
  const [error, setError] = useState<string | null>(null);
  const [muted, setMuted] = useState(false);
  const [cameraOff, setCameraOff] = useState(false);
  const [sharing, setSharing] = useState(false);
  const [whiteboardOpen, setWhiteboardOpen] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const Daily = (await import('@daily-co/daily-js')).default;
        if (cancelled) return;
        const callObject = Daily.createCallObject({
          audioSource: true,
          videoSource: true
        }) as unknown as DailyCallInstance;

        callRef.current = callObject;

        callObject.on('joined-meeting', () => setStatus('joined'));
        callObject.on('left-meeting', () => setStatus('left'));
        callObject.on('error', (event: unknown) => {
          const message =
            typeof event === 'object' && event !== null && 'message' in event
              ? String((event as { message: unknown }).message)
              : 'Daily call error';
          setError(message);
          setStatus('error');
        });

        if (containerRef.current) {
          await callObject.join({
            url: room.roomUrl,
            token: room.token
          });
        }
      } catch (err) {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : 'Failed to load Daily SDK');
        setStatus('error');
      }
    })();
    return () => {
      cancelled = true;
      const call = callRef.current;
      if (call) {
        call.leave().finally(() => call.destroy().catch(() => undefined));
        callRef.current = null;
      }
    };
  }, [room.roomUrl, room.token]);

  const toggleAudio = useCallback(() => {
    const call = callRef.current;
    if (!call) return;
    const next = !call.localAudio();
    call.setLocalAudio(!next);
    setMuted(next);
  }, []);

  const toggleVideo = useCallback(() => {
    const call = callRef.current;
    if (!call) return;
    const next = !call.localVideo();
    call.setLocalVideo(!next);
    setCameraOff(next);
  }, []);

  const toggleScreenShare = useCallback(async () => {
    const call = callRef.current;
    if (!call) return;
    try {
      if (sharing) {
        await call.stopScreenShare();
        setSharing(false);
      } else {
        await call.startScreenShare();
        setSharing(true);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Screen share failed');
    }
  }, [sharing]);

  const leave = useCallback(async () => {
    const call = callRef.current;
    if (!call) {
      onLeave();
      return;
    }
    try {
      await call.leave();
    } finally {
      onLeave();
    }
  }, [onLeave]);

  return (
    <div className="fixed inset-0 z-50 flex flex-col bg-zinc-950 text-zinc-50">
      <div
        ref={containerRef}
        className="relative flex-1 overflow-hidden"
        data-testid="daily-call-container"
      >
        {status === 'joining' && (
          <div className="absolute inset-0 flex items-center justify-center text-sm text-zinc-300">
            Connecting to {room.role === 'owner' ? 'host ' : ''}session…
          </div>
        )}
        {status === 'error' && error ? (
          <div className="absolute inset-0 flex items-center justify-center p-6 text-center text-sm text-rose-300">
            <div>
              <p className="font-semibold">Video session failed</p>
              <p className="mt-2 max-w-md text-rose-200/80">{error}</p>
            </div>
          </div>
        ) : null}
      </div>

      <div className="flex items-center justify-center gap-2 border-t border-zinc-800 bg-zinc-900/80 p-3">
        <Button
          type="button"
          size="sm"
          variant={muted ? 'destructive' : 'secondary'}
          onClick={toggleAudio}
          disabled={status !== 'joined'}
          aria-pressed={muted}
        >
          {muted ? 'Unmute' : 'Mute'}
        </Button>
        <Button
          type="button"
          size="sm"
          variant={cameraOff ? 'destructive' : 'secondary'}
          onClick={toggleVideo}
          disabled={status !== 'joined'}
          aria-pressed={cameraOff}
        >
          {cameraOff ? 'Camera on' : 'Camera off'}
        </Button>
        <Button
          type="button"
          size="sm"
          variant={sharing ? 'default' : 'secondary'}
          onClick={toggleScreenShare}
          disabled={status !== 'joined'}
          aria-pressed={sharing}
        >
          {sharing ? 'Stop sharing' : 'Share screen'}
        </Button>
        <Button
          type="button"
          size="sm"
          variant={whiteboardOpen ? 'default' : 'secondary'}
          onClick={() => setWhiteboardOpen((v) => !v)}
          disabled={status !== 'joined'}
        >
          {whiteboardOpen ? 'Hide whiteboard' : 'Whiteboard'}
        </Button>
        <Button
          type="button"
          size="sm"
          variant="destructive"
          onClick={leave}
          className={cn(status === 'joined' ? '' : 'opacity-80')}
        >
          Leave session
        </Button>
      </div>
      <WhiteboardPanel
        open={whiteboardOpen}
        onClose={() => setWhiteboardOpen(false)}
      />
    </div>
  );
}
