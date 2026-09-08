'use client';

import { Client, type IMessage } from '@stomp/stompjs';
import { Loader2, MessageSquare, Send, X } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';

import { cn } from '@/lib/utils';
import { getAccessToken, getStoredUser } from '@/modules/auth/lib/token-storage';

import type { SessionChatMessage } from '../lib/chat-types';

type ConnStatus = 'connecting' | 'connected' | 'disconnected' | 'error';

interface ChatPanelProps {
  bookingId: string;
  open: boolean;
  onClose: () => void;
}

function apiBase(): string {
  if (typeof window === 'undefined') return '';
  return process.env.NEXT_PUBLIC_API_BASE_URL || `${window.location.protocol}//${window.location.host}`;
}

/**
 * In-session chat (FR-M55). Connects to the STOMP endpoint exposed by
 * SessionWebSocketConfig at /ws/sessions (SockJS fallback), sends on
 * /app/sessions/{bookingId}/chat, and listens on /topic/sessions/{bookingId}.
 *
 * Authorization uses the same JWT access token the REST client uses, sent
 * on the STOMP CONNECT frame so StompAuthChannelInterceptor can stamp a
 * Principal onto the WebSocket session.
 */
export function ChatPanel({ bookingId, open, onClose }: ChatPanelProps) {
  const [status, setStatus] = useState<ConnStatus>('disconnected');
  const [messages, setMessages] = useState<SessionChatMessage[]>([]);
  const [draft, setDraft] = useState('');
  const listRef = useRef<HTMLDivElement>(null);
  const clientRef = useRef<Client | null>(null);

  const me = (() => {
    const u = getStoredUser<{ id?: string }>();
    return u?.id ?? null;
  })();

  useEffect(() => {
    if (!open) return;
    const token = getAccessToken();
    if (!token) {
      setStatus('error');
      return;
    }

    setStatus('connecting');
    setMessages([]);

    const subscriptionDestination = `/topic/sessions/${bookingId}`;

    const client = new Client({
      webSocketFactory: () =>
        // eslint-disable-next-line @typescript-eslint/no-require-imports
        new (require('sockjs-client'))(`${apiBase()}/ws/sessions`),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      debug: () => {
        // Keep quiet in production; uncomment for local debugging.
        // console.debug('[stomp]', str);
      },
      onConnect: () => {
        setStatus('connected');
        client.subscribe(subscriptionDestination, (frame: IMessage) => {
          try {
            const payload = JSON.parse(frame.body) as SessionChatMessage;
            if (!payload || !payload.body) return;
            setMessages((prev) => [...prev, payload]);
          } catch {
            // Ignore malformed frames.
          }
        });
      },
      onWebSocketClose: () => setStatus('disconnected'),
      onStompError: () => setStatus('error')
    });

    client.activate();
    clientRef.current = client;

    return () => {
      void client.deactivate();
      clientRef.current = null;
    };
  }, [bookingId, open]);

  // Auto-scroll to bottom when new messages arrive.
  useEffect(() => {
    const el = listRef.current;
    if (el) el.scrollTop = el.scrollHeight;
  }, [messages, open]);

  if (!open) return null;

  const send = () => {
    const body = draft.trim();
    if (!body || !clientRef.current?.connected) return;
    clientRef.current.publish({
      destination: `/app/sessions/${bookingId}/chat`,
      body: JSON.stringify({ body })
    });
    setDraft('');
  };

  return (
    <aside
      role="dialog"
      aria-labelledby="chat-panel-title"
      className="fixed inset-y-0 right-0 z-50 flex w-full max-w-sm flex-col border-l border-white/10 bg-[var(--brand-surface)]/95 text-zinc-50 shadow-2xl backdrop-blur"
    >
      <header className="flex items-center justify-between border-b border-white/10 bg-black/40 px-4 py-3">
        <div className="flex items-center gap-2">
          <MessageSquare className="h-4 w-4" aria-hidden />
          <h2 id="chat-panel-title" className="text-sm font-semibold">
            Session chat
          </h2>
          <ConnDot status={status} />
        </div>
        <button
          type="button"
          onClick={onClose}
          aria-label="Close chat"
          className="rounded-full p-1 text-zinc-300 transition hover:bg-white/10 hover:text-white"
        >
          <X className="h-4 w-4" />
        </button>
      </header>

      <div
        ref={listRef}
        className="flex-1 space-y-2 overflow-y-auto bg-black/30 px-4 py-3 text-sm"
        data-testid="chat-messages"
      >
        {messages.length === 0 ? (
          <p className="mt-4 text-center text-xs text-zinc-500">
            No messages yet. Say hi to your session partner.
          </p>
        ) : null}
        {messages.map((m, idx) => {
          const own = me != null && m.senderId === me;
          return (
            <div
              key={`${m.sentAt ?? idx}-${idx}`}
              className={cn('flex', own ? 'justify-end' : 'justify-start')}
            >
              <div
                className={cn(
                  'max-w-[80%] rounded-2xl px-3 py-2 text-sm leading-relaxed shadow-sm',
                  own
                    ? 'rounded-br-sm bg-[var(--brand-emerald)] text-white'
                    : 'rounded-bl-sm bg-white/10 text-zinc-100'
                )}
              >
                {!own ? (
                  <p className="mb-1 text-[10px] font-medium uppercase tracking-wide text-zinc-400">
                    {m.senderName ?? 'Guest'}
                  </p>
                ) : null}
                <p className="whitespace-pre-wrap break-words">{m.body}</p>
                {m.sentAt ? (
                  <p className={cn('mt-1 text-[10px]', own ? 'text-emerald-100/80' : 'text-zinc-500')}>
                    {new Date(m.sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </p>
                ) : null}
              </div>
            </div>
          );
        })}
      </div>

      <footer className="border-t border-white/10 bg-black/40 p-3">
        <div className="flex items-end gap-2">
          <label htmlFor="chat-input" className="sr-only">
            Type a message
          </label>
          <textarea
            id="chat-input"
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                send();
              }
            }}
            placeholder="Type a message"
            rows={1}
            disabled={status !== 'connected'}
            className="flex-1 resize-none rounded-xl border border-white/10 bg-white/5 px-3 py-2 text-sm text-zinc-50 placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-[var(--brand-emerald)]"
          />
          <button
            type="button"
            onClick={send}
            disabled={!draft.trim() || status !== 'connected'}
            aria-label="Send"
            className="inline-flex h-10 w-10 items-center justify-center rounded-xl bg-[var(--brand-emerald)] text-white transition hover:opacity-95 disabled:cursor-not-allowed disabled:opacity-40"
          >
            <Send className="h-4 w-4" />
          </button>
        </div>
        {status === 'error' ? (
          <p className="mt-2 text-xs text-rose-300">
            Chat is offline. Check your connection or sign in again.
          </p>
        ) : null}
        {status === 'connecting' ? (
          <p className="mt-2 inline-flex items-center gap-1 text-xs text-zinc-400">
            <Loader2 className="h-3 w-3 animate-spin" aria-hidden /> Connecting…
          </p>
        ) : null}
      </footer>
    </aside>
  );
}

function ConnDot({ status }: { status: ConnStatus }) {
  const tone =
    status === 'connected'
      ? 'bg-emerald-400'
      : status === 'connecting'
        ? 'bg-amber-400 animate-pulse'
        : status === 'error'
          ? 'bg-rose-500'
          : 'bg-zinc-500';
  const label =
    status === 'connected'
      ? 'Connected'
      : status === 'connecting'
        ? 'Connecting'
        : status === 'error'
          ? 'Disconnected'
          : 'Offline';
  return (
    <span
      aria-label={label}
      title={label}
      className={cn('ml-1 inline-block h-2 w-2 rounded-full', tone)}
    />
  );
}
