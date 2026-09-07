import { apiClient } from '@/lib/api-client';

import type { DiscoverFilters, DiscoverPage } from './schemas';

export async function discover(filters: DiscoverFilters): Promise<DiscoverPage> {
  const cleaned: Record<string, string | number> = {};
  if (filters.skill) cleaned.skill = filters.skill;
  if (filters.language) cleaned.language = filters.language;
  if (filters.country) cleaned.country = filters.country;
  if (filters.minRating != null && filters.minRating > 0) cleaned.minRating = filters.minRating;
  if (filters.page != null) cleaned.page = filters.page;
  if (filters.size != null) cleaned.size = filters.size;

  const { data } = await apiClient.get<DiscoverPage>('/discover', { params: cleaned });
  return data;
}
