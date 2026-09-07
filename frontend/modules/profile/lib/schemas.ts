export interface PublicOfferedSkill {
  id: string;
  skillId: string;
  slug: string;
  name: string;
  category: string;
  level: number;
  hourlySeedRate: number;
}

export interface PublicUserProfile {
  id: string;
  fullName: string;
  avatarUrl?: string | null;
  bio?: string | null;
  countryCode?: string | null;
  timezone: string;
  languages: string[];
  learningStyle?: string | null;
  verified: boolean;
  verificationLevel: number;
  onboardingCompleted: boolean;
  ratingAvg: number;
  sessionsCompleted: number;
  createdAt: string;
  offeredSkills: PublicOfferedSkill[];
}
