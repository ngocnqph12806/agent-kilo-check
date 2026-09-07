'use client';

import { useState } from 'react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Skill } from '@/modules/skills/lib/schemas';
import { SkillsAutocomplete } from '@/modules/skills/components/skills-autocomplete';

import { useOnboardingWizard } from '../hooks/use-onboarding-wizard';
import type { OfferedSkillDraft } from '../lib/schemas';

const DEFAULT_LEVEL = 3;
const DEFAULT_RATE = 60;

export function StepOfferedSkills() {
  const draft = useOnboardingWizard((s) => s.draft);
  const update = useOnboardingWizard((s) => s.update);
  const [selected, setSelected] = useState<Skill | null>(null);
  const [level, setLevel] = useState<number>(DEFAULT_LEVEL);
  const [years, setYears] = useState<number | ''>('');

  function add() {
    if (!selected) return;
    if (draft.offered.some((o) => o.skill.id === selected.id)) return;
    const next: OfferedSkillDraft = {
      skill: selected,
      level,
      yearsExperience: years === '' ? undefined : Number(years),
      hourlySeedRate: DEFAULT_RATE
    };
    update({ offered: [...draft.offered, next] });
    setSelected(null);
    setLevel(DEFAULT_LEVEL);
    setYears('');
  }

  function remove(skillId: string) {
    update({ offered: draft.offered.filter((o) => o.skill.id !== skillId) });
  }

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-xl font-semibold">Skills you can teach</h2>
        <p className="text-sm text-muted-foreground">
          Pick at least one. You'll set a level + rate next.
        </p>
      </header>

      <div className="space-y-3 rounded-lg border bg-card p-4">
        <SkillsAutocomplete
          value={selected}
          onChange={setSelected}
          placeholder="Type a skill (e.g. Java, Guitar)…"
        />
        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1">
            <Label htmlFor="level" className="text-xs">Level (1–5)</Label>
            <Input
              id="level"
              type="number"
              min={1}
              max={5}
              value={level}
              onChange={(e) =>
                setLevel(Math.max(1, Math.min(5, Number(e.target.value))))
              }
            />
          </div>
          <div className="space-y-1">
            <Label htmlFor="years" className="text-xs">Years of experience</Label>
            <Input
              id="years"
              type="number"
              min={0}
              value={years}
              placeholder="optional"
              onChange={(e) =>
                setYears(e.target.value === '' ? '' : Number(e.target.value))
              }
            />
          </div>
        </div>
        <Button type="button" onClick={add} disabled={!selected}>
          Add skill
        </Button>
      </div>

      {draft.offered.length > 0 ? (
        <ul className="divide-y rounded-lg border bg-card">
          {draft.offered.map((o) => (
            <li
              key={o.skill.id}
              className="flex items-center justify-between px-4 py-3"
            >
              <div>
                <p className="font-medium">{o.skill.name}</p>
                <p className="text-xs text-muted-foreground">
                  level {o.level}
                  {o.yearsExperience != null ? ` · ${o.yearsExperience}y experience` : ''}
                  {' · '}
                  {o.hourlySeedRate} seeds / hour
                </p>
              </div>
              <Button variant="ghost" size="sm" type="button" onClick={() => remove(o.skill.id)}>
                Remove
              </Button>
            </li>
          ))}
        </ul>
      ) : null}
    </div>
  );
}
