'use client';

import { Client, type IMessage } from '@stomp/stompjs';
import { useCallback, useEffect, useRef, useState } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { getAccessToken } from '@/modules/auth/lib/token-storage';

import type { ChatMessage } from '../lib/schemas';

export interface SessionChatProps {
  bookingId: string;
  currentUserId: string;
  apiBaseUrl?: string;
  enabled?: boolean;
}

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

function resolveWsBaseUrl(apiBase: string): string {
  if (apiBase.startsWith('https://')) {
    return apiBase.replace(/^https/, 'wss');
  }
  return apiBase.replace(/^http/, 'ws');
}

/**
 * STOMP-over-WebSocket chat panel (T-M155). Subscribes to
 * {@code /topic/sessions/{bookingId}} and publishes on
 * {@code /app/sessions/{bookingId}/chat}.
 */
export function SessionChat({
  bookingId,
  currentUserId,
  apiBaseUrl = API_BASE_URL,
  enabled = true
}: SessionChatProps) {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [draft, setDraft] = useState('');
  const [connected, setConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    if (!enabled) {
      return undefined;
    }
    const token = getAccessToken();
    if (!token) {
      setError('Sign in to join the chat');
      return undefined;
    }
    const client = new Client({
      brokerURL: `${resolveWsBaseUrl(apiBaseUrl)}/ws/sessions`,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);
        setError(null);
        client.subscribe(`/topic/sessions/${bookingId}`, (frame: IMessage) => {
          try {
            const payload = JSON.parse(frame.body) as ChatMessage;
            setMessages((prev) => [...prev, payload].slice(-200));
          } catch (err) {
            console.warn('[chat] failed to parse message', err);
          }
        });
      },
      onWebSocketClose: () => setConnected(false),
      onStompError: (frame) => {
        setError(frame.headers['message'] || 'STOMP error');
      }
    });
    client.activate();
    clientRef.current = client;
    return () => {
      client.deactivate();
      clientRef.current = null;
      setConnected(false);
    };
  }, [apiBaseUrl, bookingId, enabled]);

  const send = useCallback(() => {
    const trimmed = draft.trim();
    if (!trimmed || !clientRef.current || !connected) return;
    clientRef.current.publish({
      destination: `/app/sessions/${bookingId}/chat`,
      body: JSON.stringify({ text: trimmed })
    });
    setDraft('');
  }, [bookingId, connected, draft]);

  return (
    <div className="flex h-80 flex-col rounded-lg border bg-card shadow-sm">
      <div className="flex items-center justify-between border-b px-4 py-2">
        <h2 className="text-sm font-semibold">Session chat</h2>
        <span
          className={cn(
            'rounded-full px-2 py-0.5 text-xs',
            connected ? 'bg-green-100 text-green-700' : 'bg-muted text-muted-foreground'
          )}
        >
          {connected ? 'Connected' : error ?? 'Offline'}
        </span>
      </div>
      <ul className="flex-1 space-y-2 overflow-y-auto px-4 py-3 text-sm">
        {messages.length === 0 ? (
          <li className="text-xs text-muted-foreground">
            No messages yet. Say hi 👋
          </li>
        ) : null}
        {messages.map((m, idx) => {
          const mine = m.userId === currentUserId;
          return (
            <li
              key={`${m.sentAt}-${idx}`}
              className={cn(
                'flex flex-col rounded-md border px-3 py-1',
                mine
                  ? 'self-end border-primary/30 bg-primary/5 text-primary-foreground/90'
                  : 'bg-muted/40'
              )}
            >
              <span className="text-xs text-muted-foreground">
                {mine ? 'You' : m.userId.slice(0, 6)} · {new Date(m.sentAt).toLocaleTimeString()}
              </span>
              <span>{m.text}</span>
            </li>
          );
        })}
      </ul>
      <form
        onSubmit={(event) => {
          event.preventDefault();
          send();
        }}
        className="flex items-center gap-2 border-t px-4 py-2"
      >
        <input
          value={draft}
          onChange={(event) => setDraft(event.target.value)}
          placeholder={connected ? 'Type a message…' : 'Waiting for connection…'}
          disabled={!connected}
          className="flex-1 rounded border bg-background px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
        />
        <Button type="submit" size="sm" disabled={!connected || draft.trim().length === 0}>
          Send
        </Button>
      </form>
    </div>
  );
}
