import { apiClient } from '@/lib/api-client';

import type { DiscoverFilters, DiscoverPage, DiscoverSortKey } from './schemas';

const SORT_TO_BACKEND: Record<DiscoverSortKey, string> = {
  rating: 'ratingAvg',
  sessions: 'sessionsCompleted',
  recent: 'updatedAt'
};

export async function discover(filters: DiscoverFilters): Promise<DiscoverPage> {
  const cleaned: Record<string, string | number> = {};
  if (filters.skill) cleaned.skill = filters.skill;
  if (filters.language) cleaned.language = filters.language;
  if (filters.country) cleaned.country = filters.country;
  if (filters.minRating != null && filters.minRating > 0) cleaned.minRating = filters.minRating;
  if (filters.timezoneOffset != null) cleaned.timezoneOffset = filters.timezoneOffset;
  if (filters.sort) cleaned.sort = SORT_TO_BACKEND[filters.sort];
  if (filters.page != null) cleaned.page = filters.page;
  if (filters.size != null) cleaned.size = filters.size;

  const { data } = await apiClient.get<DiscoverPage>('/discover', { params: cleaned });
  return data;
}
