# 06 — Tree Visualization · Design

## 1. Module (FE)
```
components/tree/
├── TreeCanvas.tsx          // React Flow wrapper
├── nodes/
│   ├── PersonNode.tsx      // custom node component
│   ├── SpouseHandle.tsx
│   └── NodeToolbar.tsx
├── edges/
│   ├── ParentChildEdge.tsx
│   ├── SpouseEdge.tsx
│   └── AdoptedEdge.tsx
├── layouts/
│   ├── vertical.ts          // dagre config
│   ├── horizontal.ts
│   └── radial.ts            // custom radial algorithm
├── filters/TreeFilterBar.tsx
├── export/exportToPng.ts
└── hooks/{useTreeData.ts, useTreeLayout.ts, useTreeRealtime.ts}
```

## 2. API
| Method | Path | Trả |
|---|---|---|
| GET | `/families/{slug}/tree/data` | `{ nodes: [...], edges: [...], stats }` |
| GET | `/families/{slug}/tree/data?layout=vertical&filter=...` | filtered graph |
| WS | `/ws/families/{slug}/tree` | events: `person.created/updated/deleted` |

### TreeDataResponse shape
```ts
{
  nodes: {
    id: string;
    givenName: string; surname: string; fullName: string;
    avatarUrl: string | null;
    gender: 'MALE'|'FEMALE'|'OTHER'|'UNKNOWN';
    birthYear: number | null; deathYear: number | null;
    generation: number; branch: string | null;
    isLiving: boolean;
  }[];
  edges: {
    id: string; source: string; target: string;
    type: 'PARENT_CHILD'|'SPOUSE'|'SIBLING'|'ADOPTED'|'GODPARENT';
    startYear?: number; endYear?: number;
  }[];
  stats: { totalNodes: number; generations: number; branches: string[]; }
}
```

## 3. BE pre-compute layout
- Service `TreeLayoutService`:
  - Build adjacency từ relationships.
  - Compute generations bằng BFS từ founders (persons không có parent).
  - Tính position theo layout algorithm trên server trả về `{x,y}` đã gán.
- Cache layout JSON trong Redis 5 phút, invalidate khi person/relationship thay đổi.

## 4. Layout algorithms
- **Vertical/Horizontal:** `dagre` với `rankdir` TB hoặc LR, `nodesep=60`, `ranksep=120`.
- **Radial:** custom — founder ở tâm, các thế hệ xếp trên vòng tròn theo generation, góc phân bổ đều cho mỗi node.

## 5. Virtualization
- Dùng `@xyflow/react` built-in `onlyRenderVisibleElements={true}`.
- Threshold: nếu nodes > 200 → bật, ngược lại tắt.

## 6. Realtime
- BE publish event lên Redis pub/sub khi person/relationship thay đổi.
- WS endpoint subscribe và forward tới clients trong family room.
- FE React Query `invalidateQueries(['tree', slug])` khi nhận event.

## 7. Export
- PNG: `html-to-image` hoặc `dom-to-image-more`, scale 2x.
- SVG: serialize React Flow `toSvg()` rồi download blob.

## 8. Performance budget
| Quy mô | Initial render | Pan/zoom |
|---|---|---|
| 50 nodes | <1.5s | 60fps |
| 500 nodes | <3s + virtualization | 30fps |
| 2000 nodes | cảnh báo + filter required | 15fps |
