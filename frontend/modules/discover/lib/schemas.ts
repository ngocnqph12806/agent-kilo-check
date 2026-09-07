export interface DiscoverMatchedSkill {
  skillId: string;
  slug: string;
  name: string;
  category: string;
  level: number;
  hourlySeedRate: number;
}

export interface DiscoverMatch {
  userId: string;
  fullName: string;
  avatarUrl?: string | null;
  countryCode?: string | null;
  languages: string[];
  bio?: string | null;
  ratingAvg: number;
  sessionsCompleted: number;
  matchedSkillCount: number;
  topSkills: DiscoverMatchedSkill[];
}

export interface DiscoverPage {
  items: DiscoverMatch[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface DiscoverFilters {
  skill?: string;
  language?: string;
  country?: string;
  minRating?: number;
  page?: number;
  size?: number;
}
