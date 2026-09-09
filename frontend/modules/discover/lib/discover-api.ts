import { apiClient } from '@/lib/api-client';

import type { DiscoverFilters, DiscoverPage, DiscoverSortKey } from './schemas';

const SORT_TO_BACKEND: Record<DiscoverSortKey, string> = {
  rating: 'ratingAvg',
  sessions: 'sessionsCompleted',
  recent: 'updatedAt'
};

/**
 * Raw wire shape returned by the backend. The backend uses Spring's Page
 * convention and serializes the row list as `content` (see
 * DiscoverPageResponse.@JsonProperty("content")), so we have to translate
 * it into the frontend's `items` field here.
 */
interface DiscoverPageWire {
  content?: unknown[];
  items?: unknown[];
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
  first?: boolean;
  last?: boolean;
  hasNext?: boolean;
  hasPrevious?: boolean;
}

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

  const { data } = await apiClient.get<DiscoverPageWire>('/discover', { params: cleaned });
  const items = Array.isArray(data.items)
    ? data.items
    : Array.isArray(data.content)
      ? data.content
      : [];
  return {
    items: items as DiscoverPage['items'],
    page: data.page ?? 0,
    size: data.size ?? items.length,
    totalElements: data.totalElements ?? items.length,
    totalPages: data.totalPages ?? 1
  };
}
