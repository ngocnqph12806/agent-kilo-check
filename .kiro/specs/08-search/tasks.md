# 08 — Search · Tasks

- [ ] Tạo Flyway `V8__search_indexes.sql` (generated column + GIN indexes).
- [ ] `PersonSearchRepository` (native queries với tsvector + trigram).
- [ ] `PersonSearchService` (filter parser, pagination cursor).
- [ ] `SuggestService` (Redis cache prefix).
- [ ] `SearchController` endpoints.
- [ ] BE unit test search ranking (multi match).
- [ ] BE integration test với dataset 1000 persons mẫu (Testcontainers + seed script).
- [ ] k6 perf script 10k persons, P95 < 300ms.
- [ ] FE: Global search bar trong Topbar với cmdk suggest.
- [ ] FE: page `/families/[slug]/search` (filter sidebar, result grid, pagination).
- [ ] FE: highlight match trong kết quả (dùng `<mark>`).
- [ ] FE: empty state, loading skeleton.
- [ ] e2E: search "Nguyễn" → assert có kết quả; filter gender=MALE giảm kết quả.
- [ ] Verify GIN index được sử dụng (EXPLAIN ANALYZE).
