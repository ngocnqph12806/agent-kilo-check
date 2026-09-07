'use client';

import { useEffect, useRef, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

import { SkillsAutocomplete } from '@/modules/skills/components/skills-autocomplete';
import { Input } from '@/components/ui/input';
import type { Skill } from '@/modules/skills/lib/schemas';

/**
 * Compact skills-autocomplete button mounted in the top bar.
 * Clicking the input expands into the full picker; selecting a skill
 * navigates to /discover?skill={id} (T-M82 already honours the filter).
 */
export function TopBarSearch() {
  const [expanded, setExpanded] = useState(false);
  const router = useRouter();
  const containerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    function onClick(e: MouseEvent) {
      if (!containerRef.current?.contains(e.target as Node)) setExpanded(false);
    }
    document.addEventListener('mousedown', onClick);
    return () => document.removeEventListener('mousedown', onClick);
  }, []);

  return (
    <div ref={containerRef} className="relative">
      {!expanded ? (
        <button
          type="button"
          onClick={() => setExpanded(true)}
          className="flex items-center gap-2 rounded-md border bg-background px-3 py-1.5 text-sm text-muted-foreground hover:bg-accent"
          aria-label="Search skills"
        >
          <SearchIcon />
          <span className="hidden md:inline">Search skills…</span>
        </button>
      ) : (
        <div className="flex items-center gap-2">
          <SkillsAutocomplete
            value={null}
            onChange={(skill: Skill | null) => {
              if (skill) {
                router.push(`/discover?skill=${skill.id}`);
                setExpanded(false);
              }
            }}
            placeholder="Search skills…"
            className="w-56"
            autoFocus
          />
          <Input
            className="hidden"
            value=""
            readOnly
          />
        </div>
      )}
    </div>
  );
}

function SearchIcon() {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="14"
      height="14"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <circle cx="11" cy="11" r="8" />
      <path d="m21 21-4.3-4.3" />
    </svg>
  );
}
