'use client';

import { useEffect, useMemo, useRef, useState } from 'react';

import { Input } from '@/components/ui/input';
import { cn } from '@/lib/utils';

import { useSkillSearch } from '../hooks/use-skill-search';
import type { Skill } from '../lib/schemas';

export interface SkillsAutocompleteProps {
  value: Skill | null;
  onChange: (skill: Skill | null) => void;
  category?: Skill['category'];
  placeholder?: string;
  className?: string;
  disabled?: boolean;
  autoFocus?: boolean;
}

const DEBOUNCE_MS = 250;

export function SkillsAutocomplete({
  value,
  onChange,
  category,
  placeholder = 'Search skills…',
  className,
  disabled,
  autoFocus
}: SkillsAutocompleteProps) {
  const [query, setQuery] = useState(value?.name ?? '');
  const [open, setOpen] = useState(false);
  const [debounced, setDebounced] = useState(query);
  const containerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const handle = setTimeout(() => setDebounced(query), DEBOUNCE_MS);
    return () => clearTimeout(handle);
  }, [query]);

  useEffect(() => {
    function onClickOutside(e: MouseEvent) {
      if (!containerRef.current?.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    document.addEventListener('mousedown', onClickOutside);
    return () => document.removeEventListener('mousedown', onClickOutside);
  }, []);

  const search = useSkillSearch(
    { query: debounced.trim() || undefined, category, size: 8 },
    debounced.trim().length >= 1
  );

  const suggestions = useMemo(() => {
    const items = search.data?.content ?? [];
    if (!value) return items;
    return items.filter((s) => s.id !== value.id);
  }, [search.data, value]);

  function select(skill: Skill) {
    onChange(skill);
    setQuery(skill.name);
    setOpen(false);
  }

  function clear() {
    onChange(null);
    setQuery('');
  }

  return (
    <div ref={containerRef} className={cn('relative', className)}>
      <Input
        type="text"
        role="combobox"
        autoComplete="off"
        spellCheck={false}
        value={query}
        autoFocus={autoFocus}
        disabled={disabled}
        placeholder={placeholder}
        onFocus={() => setOpen(true)}
        onChange={(e) => {
          setQuery(e.target.value);
          setOpen(true);
          if (e.target.value === '') onChange(null);
        }}
        aria-autocomplete="list"
        aria-expanded={open}
        aria-controls="skills-autocomplete-listbox"
      />
      {value && query === value.name ? (
        <button
          type="button"
          onClick={clear}
          className="absolute right-2 top-1/2 -translate-y-1/2 text-xs text-muted-foreground hover:text-foreground"
          aria-label="Clear selection"
        >
          ✕
        </button>
      ) : null}

      {open && suggestions.length > 0 ? (
        <ul
          id="skills-autocomplete-listbox"
          role="listbox"
          className="absolute left-0 right-0 z-50 mt-1 max-h-72 overflow-auto rounded-md border bg-popover p-1 text-sm shadow-lg"
        >
          {suggestions.map((skill) => (
            <li
              key={skill.id}
              role="option"
              aria-selected={value?.id === skill.id}
              className={cn(
                'flex cursor-pointer flex-col gap-0.5 rounded-sm px-2 py-1.5 hover:bg-accent',
                value?.id === skill.id && 'bg-accent'
              )}
              onClick={() => select(skill)}
            >
              <span className="font-medium">{skill.name}</span>
              <span className="text-xs uppercase text-muted-foreground">
                {skill.category}
              </span>
            </li>
          ))}
        </ul>
      ) : null}

      {open && debounced && !search.isLoading && suggestions.length === 0 ? (
        <p className="absolute left-0 right-0 z-50 mt-1 rounded-md border bg-popover px-3 py-2 text-sm text-muted-foreground shadow-lg">
          No skills match. Try a different keyword or submit a custom one below.
        </p>
      ) : null}
    </div>
  );
}
