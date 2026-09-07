import type { Skill, SkillCategory } from '@/modules/skills/lib/schemas';
import type { AvailabilitySlot, AvailabilityState } from '@/modules/availability/lib/schemas';

export interface OfferedSkillDraft {
  skill: Skill;
  level: number;
  yearsExperience?: number;
  description?: string;
  hourlySeedRate: number;
}

export interface WantedSkillDraft {
  skill: Skill;
  priority: number;
  targetLevel: number;
  notes?: string;
}

export interface OnboardingProfile {
  fullName: string;
  bio: string;
  countryCode: string;
  timezone: string;
  languages: string[];
  learningStyle: 'visual' | 'auditory' | 'reading' | 'kinesthetic' | '';
  goals: string;
  interests: string;
}

export interface OnboardingDraft {
  profile: OnboardingProfile;
  offered: OfferedSkillDraft[];
  wanted: WantedSkillDraft[];
  availability: AvailabilityState;
}

export const EMPTY_PROFILE: OnboardingProfile = {
  fullName: '',
  bio: '',
  countryCode: '',
  timezone: 'UTC',
  languages: [],
  learningStyle: '',
  goals: '',
  interests: ''
};

export const EMPTY_DRAFT: OnboardingDraft = {
  profile: EMPTY_PROFILE,
  offered: [],
  wanted: [],
  availability: { slots: [], timezone: 'UTC' }
};

export const LANGUAGE_OPTIONS = [
  { value: 'en', label: 'English' },
  { value: 'vi', label: 'Tiếng Việt' },
  { value: 'es', label: 'Español' },
  { value: 'zh', label: '中文' },
  { value: 'ja', label: '日本語' },
  { value: 'ko', label: '한국어' },
  { value: 'fr', label: 'Français' },
  { value: 'de', label: 'Deutsch' }
];

export const COUNTRY_OPTIONS = [
  { value: 'US', label: 'United States' },
  { value: 'VN', label: 'Vietnam' },
  { value: 'GB', label: 'United Kingdom' },
  { value: 'DE', label: 'Germany' },
  { value: 'SG', label: 'Singapore' },
  { value: 'JP', label: 'Japan' },
  { value: 'KR', label: 'South Korea' },
  { value: 'AU', label: 'Australia' }
];

export const LEARNING_STYLES = [
  { value: 'visual', label: 'Visual' },
  { value: 'auditory', label: 'Auditory' },
  { value: 'reading', label: 'Reading / writing' },
  { value: 'kinesthetic', label: 'Hands-on' }
] as const;

export const SKILL_CATEGORIES_FOR_ONBOARDING: SkillCategory[] = [
  'tech',
  'business',
  'art',
  'language',
  'life',
  'health',
  'music',
  'sport'
];

export type AvailabilityDraft = AvailabilitySlot;
