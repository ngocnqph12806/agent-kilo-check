'use client';

import { useCallback, useEffect, useRef, useState } from 'react';

import { Button } from '@/components/ui/button';

export interface WhiteboardProps {
  bookingId: string;
}

interface Stroke {
  color: string;
  size: number;
  points: Array<{ x: number; y: number }>;
}

const COLORS = ['#111827', '#dc2626', '#2563eb', '#16a34a', '#ca8a04', '#7c3aed'];
const SIZES = [2, 4, 8];

/**
 * Lightweight canvas whiteboard (T-M161). State is kept locally for the
 * MVP — Phase 2 can swap in Excalidraw or sync strokes via WebSocket for
 * a collaborative experience.
 */
export function Whiteboard({ bookingId }: WhiteboardProps) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const drawingRef = useRef<boolean>(false);
  const currentStrokeRef = useRef<Stroke | null>(null);
  const [color, setColor] = useState(COLORS[0]);
  const [size, setSize] = useState(SIZES[1]);
  const [strokes, setStrokes] = useState<Stroke[]>([]);

  const redraw = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    for (const stroke of strokes) {
      drawStroke(ctx, stroke);
    }
    if (currentStrokeRef.current) {
      drawStroke(ctx, currentStrokeRef.current);
    }
  }, [strokes]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const parent = canvas.parentElement;
    if (!parent) return;
    canvas.width = parent.clientWidth;
    canvas.height = parent.clientHeight;
  }, []);

  useEffect(() => {
    redraw();
  }, [redraw]);

  const pointerToCanvas = (event: React.PointerEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current;
    if (!canvas) return { x: 0, y: 0 };
    const rect = canvas.getBoundingClientRect();
    return {
      x: event.clientX - rect.left,
      y: event.clientY - rect.top
    };
  };

  const onPointerDown = (event: React.PointerEvent<HTMLCanvasElement>) => {
    event.preventDefault();
    drawingRef.current = true;
    currentStrokeRef.current = {
      color,
      size,
      points: [pointerToCanvas(event)]
    };
    redraw();
  };

  const onPointerMove = (event: React.PointerEvent<HTMLCanvasElement>) => {
    if (!drawingRef.current || !currentStrokeRef.current) return;
    currentStrokeRef.current.points.push(pointerToCanvas(event));
    redraw();
  };

  const finishStroke = () => {
    if (currentStrokeRef.current) {
      setStrokes((prev) => [...prev, currentStrokeRef.current!]);
    }
    drawingRef.current = false;
    currentStrokeRef.current = null;
  };

  const clear = () => {
    setStrokes([]);
  };

  return (
    <div className="flex h-80 flex-col rounded-lg border bg-card shadow-sm" data-booking={bookingId}>
      <div className="flex items-center justify-between border-b px-4 py-2">
        <h2 className="text-sm font-semibold">Whiteboard</h2>
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-1">
            {COLORS.map((swatch) => (
              <button
                key={swatch}
                aria-label={`Color ${swatch}`}
                onClick={() => setColor(swatch)}
                className="h-5 w-5 rounded-full border"
                style={{
                  background: swatch,
                  borderColor: swatch === color ? '#111827' : 'transparent'
                }}
              />
            ))}
          </div>
          <div className="flex items-center gap-1">
            {SIZES.map((brushSize) => (
              <Button
                key={brushSize}
                variant={size === brushSize ? 'default' : 'ghost'}
                size="sm"
                onClick={() => setSize(brushSize)}
              >
                {brushSize}px
              </Button>
            ))}
          </div>
          <Button variant="outline" size="sm" onClick={clear}>
            Clear
          </Button>
        </div>
      </div>
      <div className="relative flex-1">
        <canvas
          ref={canvasRef}
          onPointerDown={onPointerDown}
          onPointerMove={onPointerMove}
          onPointerUp={finishStroke}
          onPointerLeave={finishStroke}
          className="absolute inset-0 h-full w-full touch-none cursor-crosshair"
        />
      </div>
    </div>
  );
}

function drawStroke(
  ctx: CanvasRenderingContext2D,
  stroke: Stroke
) {
  if (stroke.points.length < 1) return;
  ctx.strokeStyle = stroke.color;
  ctx.lineWidth = stroke.size;
  ctx.lineCap = 'round';
  ctx.lineJoin = 'round';
  ctx.beginPath();
  ctx.moveTo(stroke.points[0].x, stroke.points[0].y);
  for (let i = 1; i < stroke.points.length; i++) {
    ctx.lineTo(stroke.points[i].x, stroke.points[i].y);
  }
  ctx.stroke();
}
