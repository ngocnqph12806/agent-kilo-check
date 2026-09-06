# 08 — Search · Design

## 1. Module
```
search/
├── controller/{SearchController}
├── service/{PersonSearchService, SearchIndexer}
├── dto/{SearchRequest, SearchResponse, SearchHit, SuggestResponse}
├── repository/PersonSearchRepository  // custom native queries
└── indexer/{FullTextIndexer, IndexRebuildJob}
```

## 2. Postgres full-text
- Generated column:
  ```sql
  ALTER TABLE persons ADD COLUMN search_doc tsvector
    GENERATED ALWAYS AS (
      setweight(to_tsvector('simple', coalesce(given_name,'')), 'A') ||
      setweight(to_tsvector('simple', coalesce(middle_name,'')), 'B') ||
      setweight(to_tsvector('simple', coalesce(surname,'')), 'A') ||
      setweight(to_tsvector('simple', coalesce(alias,'')), 'B') ||
      setweight(to_tsvector('simple', coalesce(occupation,'')), 'C') ||
      setweight(to_tsvector('simple', coalesce(biography,'')), 'D')
    ) STORED;
  CREATE INDEX idx_persons_search ON persons USING GIN(search_doc);
  ```
- Trigram index:
  ```sql
  CREATE INDEX idx_persons_name_trgm ON persons USING GIN(given_name gin_trgm_ops, surname gin_trgm_ops);
  ```
- Search query:
  ```sql
  SELECT p.*, ts_rank(search_doc, plainto_tsquery('simple', :q)) AS rank,
         similarity(given_name || ' ' || surname, :q) AS sim
  FROM persons p
  WHERE p.family_id = :familyId
    AND p.deleted_at IS NULL
    AND (search_doc @@ plainto_tsquery('simple', :q)
         OR similarity(given_name || ' ' || surname, :q) > 0.3)
  ORDER BY rank DESC, sim DESC
  LIMIT :size;
  ```

## 3. Suggest
- Dùng `pg_trgm` similarity + `LIMIT 10`.
- Cache suggest theo prefix trong Redis 5 phút (key `sug:{familyId}:{prefix}`).

## 4. Cross-family search
- Query dùng `IN (SELECT family_id FROM family_members WHERE user_id = :uid)`.
- Group kết quả trong service layer.

## 5. Indexer
- Trigger AFTER INSERT/UPDATE/DELETE trên `persons` tự động refresh `search_doc` (handled bởi generated column).
- Bulk rebuild job chạy qua Spring `@Scheduled` nếu cần (hiếm khi cần).

## 6. Pagination
- Cursor format: `<sortValue>:<personId>` encode base64.
- Sort `relevance`: dùng rank + id tie-break.

## 7. Endpoints
| Method | Path |
|---|---|
| GET | `/families/{slug}/search?q=&filter...&sort=&cursor=&size=` |
| GET | `/families/{slug}/search/suggest?q=` |
| GET | `/search?q=&familyId?=` |

## 8. FE
- Global search bar trong Topbar → gọi `/search`.
- Page `/families/[slug]/search` với filter sidebar + result grid.
- Suggest popup dùng cmdk (`Command` + `CommandList`).
