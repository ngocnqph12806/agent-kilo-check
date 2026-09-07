'use client';

import {
  DailyAudio,
  DailyProvider,
  useDaily,
  useLocalSessionId,
  useScreenShare,
  useVideoTrack
} from '@daily-co/daily-react';
import Daily, { type DailyCall } from '@daily-co/daily-js';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

import type { SessionRoom } from '../lib/schemas';

export interface VideoCallProps {
  room: SessionRoom;
  currentUserId: string;
  onLeave?: () => void;
  onReportIssue?: () => void;
}

const COLORS = ['bg-primary', 'bg-secondary', 'bg-accent', 'bg-muted'];

function pickColor(seed: string): string {
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = (hash * 31 + seed.charCodeAt(i)) >>> 0;
  }
  return COLORS[hash % COLORS.length];
}

function VideoCallInner({ room, currentUserId, onLeave, onReportIssue }: VideoCallProps) {
  const daily = useDaily();
  const localSessionId = useLocalSessionId();
  const { isSharingScreen, startScreenShare, stopScreenShare } = useScreenShare();

  const localVideo = useVideoTrack(localSessionId ?? undefined);
  const [muted, setMuted] = useState(false);
  const [cameraOff, setCameraOff] = useState(false);
  const [participants, setParticipants] = useState<string[]>([]);
  const localVideoRef = useRef<HTMLVideoElement | null>(null);

  const join = useCallback(async () => {
    if (!daily) return;
    await daily.join({ url: room.roomUrl, token: room.token });
  }, [daily, room.roomUrl, room.token]);

  useEffect(() => {
    if (!daily) return;
    join().catch((err) => console.error('[video] join failed', err));
    return () => {
      daily.leave().catch(() => undefined);
    };
  }, [daily, join]);

  useEffect(() => {
    if (!daily) return;
    const handler = () => {
      setParticipants(Object.keys(daily.participants()));
    };
    handler();
    daily.on('participant-joined', handler);
    daily.on('participant-left', handler);
    return () => {
      daily.off('participant-joined', handler);
      daily.off('participant-left', handler);
    };
  }, [daily]);

  useEffect(() => {
    if (!localVideo || !localVideoRef.current) return;
    const track = localVideo.persistentTrack;
    if (track) {
      const stream = new MediaStream([track]);
      localVideoRef.current.srcObject = stream;
    }
  }, [localVideo]);

  const toggleMute = useCallback(() => {
    if (!daily) return;
    const next = !muted;
    daily.setLocalAudio(!next);
    setMuted(next);
  }, [daily, muted]);

  const toggleCamera = useCallback(() => {
    if (!daily) return;
    const next = !cameraOff;
    daily.setLocalVideo(!next);
    setCameraOff(next);
  }, [daily, cameraOff]);

  const toggleScreenShare = useCallback(() => {
    if (isSharingScreen) {
      stopScreenShare();
    } else {
      startScreenShare();
    }
  }, [isSharingScreen, startScreenShare, stopScreenShare]);

  const leave = useCallback(() => {
    daily?.leave().catch(() => undefined);
    onLeave?.();
  }, [daily, onLeave]);

  const otherIds = useMemo(
    () => participants.filter((id) => id !== localSessionId),
    [participants, localSessionId]
  );

  return (
    <div className="flex flex-col gap-3 rounded-lg border bg-card p-4 shadow-sm">
      <div className="flex items-center justify-between">
        <p className="text-sm font-medium">
          Daily.co room · {room.role === 'owner' ? 'Host' : 'Guest'}
        </p>
        <p className="text-xs text-muted-foreground">
          Booking {room.bookingId.slice(0, 8)}…
        </p>
      </div>
      <div className="grid gap-2 sm:grid-cols-2">
        <VideoTile
          label={`You (${currentUserId.slice(0, 6)})`}
          videoRef={localVideoRef}
          muted={muted}
          cameraOff={cameraOff}
          colorClass={pickColor(currentUserId)}
        />
        {otherIds.length === 0 ? (
          <EmptyTile label="Waiting for the other participant…" />
        ) : (
          otherIds.map((id) => (
            <RemoteTile key={id} sessionId={id} colorClass={pickColor(id)} />
          ))
        )}
      </div>
      <DailyAudio />
      <div className="flex flex-wrap items-center gap-2">
        <Button
          variant={muted ? 'destructive' : 'secondary'}
          size="sm"
          onClick={toggleMute}
          disabled={!daily}
        >
          {muted ? 'Unmute mic' : 'Mute mic'}
        </Button>
        <Button
          variant={cameraOff ? 'destructive' : 'secondary'}
          size="sm"
          onClick={toggleCamera}
          disabled={!daily}
        >
          {cameraOff ? 'Turn camera on' : 'Turn camera off'}
        </Button>
        <Button
          variant={isSharingScreen ? 'destructive' : 'secondary'}
          size="sm"
          onClick={toggleScreenShare}
          disabled={!daily}
        >
          {isSharingScreen ? 'Stop sharing' : 'Share screen'}
        </Button>
        <Button variant="ghost" size="sm" onClick={() => onReportIssue?.()}>
          Report issue
        </Button>
        <Button variant="outline" size="sm" onClick={leave}>
          Leave call
        </Button>
      </div>
      <p className="text-xs text-muted-foreground">
        Room expires at {new Date(room.expiresAt).toLocaleString()}.
      </p>
    </div>
  );
}

interface VideoTileProps {
  label: string;
  videoRef: React.MutableRefObject<HTMLVideoElement | null>;
  muted: boolean;
  cameraOff: boolean;
  colorClass: string;
}

function VideoTile({ label, videoRef, muted, cameraOff, colorClass }: VideoTileProps) {
  return (
    <div
      className={cn(
        'relative aspect-video overflow-hidden rounded-md border bg-black text-white',
        colorClass
      )}
    >
      {!cameraOff ? (
        <video
          ref={videoRef}
          autoPlay
          playsInline
          muted
          className="h-full w-full object-cover"
        />
      ) : (
        <div className="flex h-full items-center justify-center text-sm font-medium">
          Camera off
        </div>
      )}
      {muted ? (
        <span className="absolute right-2 top-2 rounded bg-black/70 px-2 py-0.5 text-xs">
          Muted
        </span>
      ) : null}
      <span className="absolute bottom-2 left-2 rounded bg-black/70 px-2 py-0.5 text-xs">
        {label}
      </span>
    </div>
  );
}

function EmptyTile({ label }: { label: string }) {
  return (
    <div className="flex aspect-video items-center justify-center rounded-md border border-dashed bg-muted text-sm text-muted-foreground">
      {label}
    </div>
  );
}

function RemoteTile({ sessionId, colorClass }: { sessionId: string; colorClass: string }) {
  const videoTrack = useVideoTrack(sessionId);
  const videoRef = useRef<HTMLVideoElement | null>(null);

  useEffect(() => {
    if (!videoTrack) return;
    const track = videoTrack.persistentTrack;
    if (!track || !videoRef.current) return;
    const stream = new MediaStream([track]);
    videoRef.current.srcObject = stream;
  }, [videoTrack]);

  return (
    <VideoTile
      label={sessionId.slice(0, 6)}
      videoRef={videoRef}
      muted={false}
      cameraOff={!videoTrack}
      colorClass={colorClass}
    />
  );
}

export function VideoCall(props: VideoCallProps) {
  const callRef = useRef<DailyCall | null>(null);
  if (callRef.current === null) {
    callRef.current = Daily.createCallObject();
  }
  useEffect(() => {
    return () => {
      callRef.current?.destroy().catch(() => undefined);
      callRef.current = null;
    };
  }, []);
  if (!callRef.current) {
    return <EmptyTile label="Loading session…" />;
  }
  return (
    <DailyProvider callObject={callRef.current}>
      <VideoCallInner {...props} />
    </DailyProvider>
  );
}
