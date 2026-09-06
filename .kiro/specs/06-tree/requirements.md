# 06 — Tree Visualization · Requirements

### REQ-06-01 — Render tree
- **REQ-06-01-01** WHEN user GET `/families/{slug}/tree` THE hệ thống SHALL render React Flow canvas hiển thị tất cả persons trong family.
- **REQ-06-01-02** THE hệ thống SHALL dùng node custom hiển thị avatar, họ tên, năm sinh–mất, icon giới tính; edge phân biệt màu theo loại quan hệ (parent-child: đen, spouse: đỏ, adopted: xanh dashed, godparent: tím).

### REQ-06-02 — Layout
- **REQ-06-02-01** THE hệ thống SHALL cung cấp 3 layout: `vertical` (tiền nhân trên đỉnh, default), `horizontal`, `radial` (node gốc ở giữa).
- **REQ-06-02-02** WHEN layout được áp dụng THE hệ thống SHALL dùng `dagre` hoặc `elkjs` để auto-arrange nodes không overlap.

### REQ-06-03 — Interaction
- **REQ-06-03-01** THE hệ thống SHALL hỗ trợ pan (drag canvas), zoom (mouse wheel), fit-view (button), mini-map.
- **REQ-06-03-02** WHEN user click vào person node THE hệ thống SHALL mở panel phải hiển thị detail + actions.
- **REQ-06-03-03** WHEN user double-click vào edge parent-child THE hệ thống SHALL mở dialog "Add child" với parent pre-fill.

### REQ-06-04 — Filter
- **REQ-06-04-01** THE hệ thống SHALL có filter: `branch`, `generationRange`, `gender`, `living`, `search by name`.
- **REQ-06-04-02** WHEN filter thay đổi THE hệ thống SHALL ẩn nodes không khớp (không xóa khỏi graph) và dim edges nối tới node ẩn.

### REQ-06-05 — Performance
- **REQ-06-05-01** WHEN family có >200 persons THE hệ thống SHALL chỉ render nodes trong viewport + buffer (virtualization).
- **REQ-06-05-02** WHEN initial load THE hệ thống SHALL gọi API `/families/{slug}/tree/data` trả graph đã tính sẵn (BE pre-compute layout JSON), FE chỉ vẽ.
- **REQ-06-05-03** IF tree >2000 nodes THEN THE hệ thống SHALL cảnh báo và yêu cầu filter trước khi render.

### REQ-06-06 — Export
- **REQ-06-06-01** THE hệ thống SHALL cho phép export canvas thành PNG và SVG qua nút toolbar.
- **REQ-06-06-02** WHEN export PNG THE hệ thống SHALL scale 2x cho retina, include background theo theme.

### REQ-06-07 — Realtime updates
- **REQ-06-07-01** WHEN user khác thêm/sửa person trong family THE hệ thống SHALL cập nhật tree qua WebSocket subscription `family:{slug}:tree`.

## Acceptance
- Render family 50 persons trong < 1.5s (lần đầu).
- Filter chỉ giữ generation 2 → chỉ hiển thị node generation 2.
- Export PNG 1920x1080 đúng nội dung.
- Test với dataset 500 node — pan/zoom 60fps.
