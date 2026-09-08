'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import {
  Mic,
  MicOff,
  Video,
  VideoOff,
  ScreenShare,
  PhoneOff,
  PenTool,
  Clock,
  Circle,
  X,
  Loader2
} from 'lucide-react';

import { cn } from '@/lib/utils';

import { WhiteboardPanel } from './whiteboard-panel';

import type { SessionRoom } from '../lib/session-api';

type DailyCallInstance = {
  join: (args?: { url?: string; token?: string; userName?: string }) => Promise<void>;
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
  sessionTitle?: string;
  counterpartyName?: string;
  onLeave: () => void;
}

type CallStatus = 'joining' | 'joined' | 'left' | 'error';

function formatElapsed(seconds: number): string {
  const m = Math.floor(seconds / 60).toString().padStart(2, '0');
  const s = (seconds % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
}

export function VideoCall({ room, sessionTitle = 'Session', counterpartyName, onLeave }: VideoCallProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const callRef = useRef<DailyCallInstance | null>(null);
  const [status, setStatus] = useState<CallStatus>('joining');
  const [error, setError] = useState<string | null>(null);
  const [muted, setMuted] = useState(false);
  const [cameraOff, setCameraOff] = useState(false);
  const [sharing, setSharing] = useState(false);
  const [whiteboardOpen, setWhiteboardOpen] = useState(false);
  const [elapsed, setElapsed] = useState(0);
  const [recording] = useState(false);

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

  useEffect(() => {
    if (status !== 'joined') return;
    const id = window.setInterval(() => setElapsed((e) => e + 1), 1000);
    return () => window.clearInterval(id);
  }, [status]);

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
    <div className="fixed inset-0 z-50 flex flex-col bg-[var(--brand-text-strong)] text-zinc-50">
      <header className="flex h-14 items-center justify-between border-b border-white/5 bg-black/40 px-4 backdrop-blur">
        <div className="flex items-center gap-3 truncate">
          <span className="text-sm font-semibold truncate">{sessionTitle}</span>
          {counterpartyName ? (
            <>
              <span className="text-zinc-500">·</span>
              <span className="truncate text-sm text-zinc-400">with {counterpartyName}</span>
            </>
          ) : null}
        </div>

        <div className="hidden items-center gap-3 sm:flex">
          {status === 'joined' ? (
            <div className="inline-flex items-center gap-1.5 rounded-full bg-white/10 px-3 py-1 text-xs">
              <Clock className="h-3.5 w-3.5" aria-hidden />
              {formatElapsed(elapsed)}
            </div>
          ) : null}
          {recording ? (
            <div className="inline-flex items-center gap-1.5 rounded-full bg-white/10 px-3 py-1 text-xs">
              <Circle className="h-2.5 w-2.5 fill-[var(--brand-rose)] text-[var(--brand-rose)]" aria-hidden />
              REC {formatElapsed(elapsed)}
            </div>
          ) : null}
        </div>

        <button
          type="button"
          onClick={leave}
          aria-label="Leave session"
          className="rounded-full p-2 text-zinc-300 transition hover:bg-white/10 hover:text-white"
        >
          <X className="h-5 w-5" />
        </button>
      </header>

      <div
        ref={containerRef}
        className="relative flex-1 overflow-hidden"
        data-testid="daily-call-container"
      >
        {status === 'joining' ? (
          <div className="absolute inset-0 flex flex-col items-center justify-center gap-3 text-sm text-zinc-300">
            <Loader2 className="h-10 w-10 animate-spin text-emerald-400" />
            <p>Connecting to {room.role === 'owner' ? 'host ' : ''}session…</p>
          </div>
        ) : null}
        {status === 'error' && error ? (
          <div className="absolute inset-0 flex items-center justify-center p-6 text-center text-sm text-rose-300">
            <div>
              <p className="text-base font-semibold">Video session failed</p>
              <p className="mt-2 max-w-md text-rose-200/80">{error}</p>
            </div>
          </div>
        ) : null}
      </div>

      <div className="flex items-center justify-center gap-2 border-t border-white/5 bg-black/60 p-4 backdrop-blur">
        <ControlButton
          active={!muted}
          destructive={muted}
          disabled={status !== 'joined'}
          onClick={toggleAudio}
          label={muted ? 'Unmute' : 'Mute'}
        >
          {muted ? <MicOff className="h-5 w-5" /> : <Mic className="h-5 w-5" />}
        </ControlButton>
        <ControlButton
          active={!cameraOff}
          destructive={cameraOff}
          disabled={status !== 'joined'}
          onClick={toggleVideo}
          label={cameraOff ? 'Camera on' : 'Camera off'}
        >
          {cameraOff ? <VideoOff className="h-5 w-5" /> : <Video className="h-5 w-5" />}
        </ControlButton>
        <ControlButton
          active={sharing}
          disabled={status !== 'joined'}
          onClick={toggleScreenShare}
          label={sharing ? 'Stop sharing' : 'Share screen'}
        >
          <ScreenShare className="h-5 w-5" />
        </ControlButton>
        <ControlButton
          active={whiteboardOpen}
          disabled={status !== 'joined'}
          onClick={() => setWhiteboardOpen((v) => !v)}
          label={whiteboardOpen ? 'Hide whiteboard' : 'Whiteboard'}
        >
          <PenTool className="h-5 w-5" />
        </ControlButton>
        <button
          type="button"
          onClick={leave}
          className="ml-2 inline-flex h-12 items-center gap-2 rounded-full bg-[var(--brand-rose)] px-5 text-sm font-semibold text-white shadow-lg transition hover:opacity-95"
        >
          <PhoneOff className="h-5 w-5" />
          Leave
        </button>
      </div>

      <WhiteboardPanel
        open={whiteboardOpen}
        onClose={() => setWhiteboardOpen(false)}
      />
    </div>
  );
}

interface ControlButtonProps {
  active: boolean;
  destructive?: boolean;
  disabled?: boolean;
  onClick: () => void;
  label: string;
  children: React.ReactNode;
}

function ControlButton({ active, destructive, disabled, onClick, label, children }: ControlButtonProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      aria-label={label}
      aria-pressed={!active}
      className={cn(
        'inline-flex h-12 w-12 items-center justify-center rounded-full transition',
        destructive
          ? 'bg-[var(--brand-rose)] text-white'
          : active
            ? 'bg-white/10 text-white hover:bg-white/20'
            : 'bg-white/5 text-zinc-400 hover:bg-white/10',
        disabled && 'cursor-not-allowed opacity-50'
      )}
    >
      {children}
    </button>
  );
}
