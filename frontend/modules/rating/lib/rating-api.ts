import { apiClient } from '@/lib/api-client';

export interface RatingInput {
  bookingId: string;
  overallScore: number;
  reviewText?: string;
  helpfulnessScore?: number;
  respectfulnessScore?: number;
}

export interface Rating {
  id: string;
  bookingId: string;
  raterId: string;
  rateeId: string;
  raterName: string;
  overallScore: number;
  reviewText?: string | null;
  helpfulnessScore?: number | null;
  respectfulnessScore?: number | null;
  autoRated: boolean;
  createdAt: string;
}

export interface RatingPage {
  items: Rating[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

// Raw wire shape returned by `GET /api/v1/users/{id}/ratings`. The
// backend uses the shared PageResponse contract and Jackson emits the
// row array as `content` (`@JsonProperty("content")`); `@JsonAlias("items")`
// only helps when Jackson is reading FROM a client-supplied payload, not
// when AXIOS is reading the server response. Components still expect
// `items`, so translate once here.
interface RatingPageWire {
  content: Rating[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export async function createRating(input: RatingInput): Promise<Rating> {
  const { data } = await apiClient.post<Rating>('/ratings', input);
  return data;
}

export async function listRatingsForUser(
  userId: string,
  page = 0,
  size = 20
): Promise<RatingPage> {
  const { data } = await apiClient.get<RatingPageWire>(
    `/users/${userId}/ratings`,
    { params: { page, size } }
  );
  return {
    items: data.content ?? [],
    page: data.page ?? 0,
    size: data.size ?? 0,
    totalElements: data.totalElements ?? 0,
    totalPages: data.totalPages ?? 0
  };
}
