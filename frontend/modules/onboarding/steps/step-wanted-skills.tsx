'use client';

import { useState } from 'react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Skill } from '@/modules/skills/lib/schemas';
import { SkillsAutocomplete } from '@/modules/skills/components/skills-autocomplete';

import { useOnboardingWizard } from '../hooks/use-onboarding-wizard';
import type { WantedSkillDraft } from '../lib/schemas';

const DEFAULT_PRIORITY = 3;
const DEFAULT_TARGET = 4;

export function StepWantedSkills() {
  const draft = useOnboardingWizard((s) => s.draft);
  const update = useOnboardingWizard((s) => s.update);
  const [selected, setSelected] = useState<Skill | null>(null);
  const [priority, setPriority] = useState<number>(DEFAULT_PRIORITY);
  const [target, setTarget] = useState<number>(DEFAULT_TARGET);

  function add() {
    if (!selected) return;
    if (draft.wanted.some((w) => w.skill.id === selected.id)) return;
    const next: WantedSkillDraft = { skill: selected, priority, targetLevel: target };
    update({ wanted: [...draft.wanted, next] });
    setSelected(null);
    setPriority(DEFAULT_PRIORITY);
    setTarget(DEFAULT_TARGET);
  }

  function remove(skillId: string) {
    update({ wanted: draft.wanted.filter((w) => w.skill.id !== skillId) });
  }

  return (
    <div className="space-y-6">
      <header>
        <h2 className="text-xl font-semibold">Skills you want to learn</h2>
        <p className="text-sm text-muted-foreground">
          Add a few so the Discover feed can match you with teachers.
        </p>
      </header>

      <div className="space-y-3 rounded-lg border bg-card p-4">
        <SkillsAutocomplete
          value={selected}
          onChange={setSelected}
          placeholder="Type a skill to learn…"
        />
        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1">
            <Label htmlFor="priority" className="text-xs">Priority (1–5)</Label>
            <Input
              id="priority"
              type="number"
              min={1}
              max={5}
              value={priority}
              onChange={(e) => setPriority(Number(e.target.value))}
            />
          </div>
          <div className="space-y-1">
            <Label htmlFor="target" className="text-xs">Target level (1–5)</Label>
            <Input
              id="target"
              type="number"
              min={1}
              max={5}
              value={target}
              onChange={(e) => setTarget(Number(e.target.value))}
            />
          </div>
        </div>
        <Button type="button" onClick={add} disabled={!selected}>
          Add skill
        </Button>
      </div>

      {draft.wanted.length > 0 ? (
        <ul className="divide-y rounded-lg border bg-card">
          {draft.wanted.map((w) => (
            <li
              key={w.skill.id}
              className="flex items-center justify-between px-4 py-3"
            >
              <div>
                <p className="font-medium">{w.skill.name}</p>
                <p className="text-xs text-muted-foreground">
                  priority {w.priority} · target level {w.targetLevel}
                </p>
              </div>
              <Button variant="ghost" size="sm" type="button" onClick={() => remove(w.skill.id)}>
                Remove
              </Button>
            </li>
          ))}
        </ul>
      ) : null}
    </div>
  );
}
