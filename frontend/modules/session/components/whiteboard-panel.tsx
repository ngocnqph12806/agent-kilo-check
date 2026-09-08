'use client';

import { useCallback, useEffect, useRef, useState } from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

type Tool = 'pen' | 'eraser';

interface Point {
  x: number;
  y: number;
}

interface Stroke {
  tool: Tool;
  color: string;
  width: number;
  points: Point[];
}

const PALETTE = ['#111827', '#ef4444', '#10b981', '#3b82f6', '#a855f7'] as const;

export interface WhiteboardPanelProps {
  open: boolean;
  onClose: () => void;
}

export function WhiteboardPanel({ open, onClose }: WhiteboardPanelProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const drawingRef = useRef(false);
  const currentRef = useRef<Stroke | null>(null);
  const [strokes, setStrokes] = useState<Stroke[]>([]);
  const [color, setColor] = useState<string>(PALETTE[0]);
  const [tool, setTool] = useState<Tool>('pen');
  const [width] = useState(3);

  const drawStroke = useCallback((ctx: CanvasRenderingContext2D, stroke: Stroke) => {
    if (stroke.points.length === 0) return;
    ctx.strokeStyle = stroke.tool === 'eraser' ? '#ffffff' : stroke.color;
    ctx.lineWidth = stroke.tool === 'eraser' ? stroke.width * 3 : stroke.width;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.beginPath();
    const [first, ...rest] = stroke.points;
    ctx.moveTo(first.x, first.y);
    for (const p of rest) {
      ctx.lineTo(p.x, p.y);
    }
    ctx.stroke();
  }, []);

  const redraw = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    for (const stroke of strokes) {
      drawStroke(ctx, stroke);
    }
    if (currentRef.current) {
      drawStroke(ctx, currentRef.current);
    }
  }, [strokes, drawStroke]);

  useEffect(() => {
    if (!open) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ratio = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    canvas.width = rect.width * ratio;
    canvas.height = rect.height * ratio;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.scale(ratio, ratio);
    }
    redraw();
  }, [open, redraw]);

  const toLocal = (e: React.PointerEvent<HTMLCanvasElement>): Point => {
    const canvas = canvasRef.current!;
    const rect = canvas.getBoundingClientRect();
    return {
      x: e.clientX - rect.left,
      y: e.clientY - rect.top
    };
  };

  const onPointerDown = (e: React.PointerEvent<HTMLCanvasElement>) => {
    e.currentTarget.setPointerCapture(e.pointerId);
    drawingRef.current = true;
    currentRef.current = {
      tool,
      color,
      width,
      points: [toLocal(e)]
    };
    redraw();
  };

  const onPointerMove = (e: React.PointerEvent<HTMLCanvasElement>) => {
    if (!drawingRef.current || !currentRef.current) return;
    currentRef.current.points.push(toLocal(e));
    redraw();
  };

  const onPointerUp = () => {
    if (!drawingRef.current || !currentRef.current) return;
    drawingRef.current = false;
    setStrokes((prev) => [...prev, currentRef.current as Stroke]);
    currentRef.current = null;
  };

  const clear = () => {
    setStrokes([]);
  };

  const undo = () => {
    setStrokes((prev) => prev.slice(0, -1));
  };

  if (!open) return null;

  return (
    <div className="fixed inset-y-0 right-0 z-40 flex w-full max-w-xl flex-col border-l border-zinc-200 bg-white shadow-xl">
      <header className="flex items-center justify-between border-b border-zinc-200 px-4 py-3">
        <h2 className="text-sm font-semibold">Whiteboard</h2>
        <button
          type="button"
          onClick={onClose}
          className="text-xs text-muted-foreground hover:text-foreground"
        >
          Close
        </button>
      </header>

      <div className="flex items-center gap-2 border-b border-zinc-200 px-4 py-2">
        <Button
          type="button"
          size="sm"
          variant={tool === 'pen' ? 'default' : 'secondary'}
          onClick={() => setTool('pen')}
        >
          Pen
        </Button>
        <Button
          type="button"
          size="sm"
          variant={tool === 'eraser' ? 'default' : 'secondary'}
          onClick={() => setTool('eraser')}
        >
          Eraser
        </Button>
        <div className="ml-2 flex items-center gap-1">
          {PALETTE.map((c) => (
            <button
              key={c}
              type="button"
              aria-label={`Choose colour ${c}`}
              onClick={() => {
                setColor(c);
                setTool('pen');
              }}
              className={cn(
                'h-5 w-5 rounded-full border-2',
                color === c ? 'border-zinc-900' : 'border-transparent'
              )}
              style={{ backgroundColor: c }}
            />
          ))}
        </div>
        <div className="ml-auto flex items-center gap-2">
          <Button type="button" size="sm" variant="ghost" onClick={undo} disabled={strokes.length === 0}>
            Undo
          </Button>
          <Button type="button" size="sm" variant="ghost" onClick={clear} disabled={strokes.length === 0}>
            Clear
          </Button>
        </div>
      </div>

      <div className="relative flex-1 bg-white">
        <canvas
          ref={canvasRef}
          className="absolute inset-0 h-full w-full touch-none"
          onPointerDown={onPointerDown}
          onPointerMove={onPointerMove}
          onPointerUp={onPointerUp}
          onPointerLeave={onPointerUp}
        />
      </div>

      <footer className="border-t border-zinc-200 px-4 py-2 text-xs text-muted-foreground">
        Draw freely during the session. Whiteboard is local-only in Sprint 3 — sync to other participant arrives in Sprint 4.
      </footer>
    </div>
  );
}
