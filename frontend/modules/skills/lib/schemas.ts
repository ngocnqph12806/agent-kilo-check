export type SkillCategory =
  | 'tech'
  | 'business'
  | 'art'
  | 'language'
  | 'life'
  | 'health'
  | 'music'
  | 'sport';

export interface Skill {
  id: string;
  slug: string;
  name: string;
  category: SkillCategory;
  custom: boolean;
  status: 'pending_review' | 'approved' | 'rejected';
  parentId?: string | null;
}

export interface SkillPage {
  content: Skill[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export const SKILL_CATEGORIES: SkillCategory[] = [
  'tech',
  'business',
  'art',
  'language',
  'life',
  'health',
  'music',
  'sport'
];
