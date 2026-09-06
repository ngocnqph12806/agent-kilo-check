# 07 — Events & Calendar · Requirements

### REQ-07-01 — Event types
- **REQ-07-01-01** THE hệ thống SHALL hỗ trợ các loại event: `BIRTH, DEATH, MARRIAGE, FUNERAL, ACHIEVEMENT, CUSTOM`.
- **REQ-07-01-02** WHEN `BIRTH` được tạo THE hệ thống SHALL tự động mirror vào `persons.birthDate` (nếu chưa có).
- **REQ-07-01-03** WHEN `DEATH` được tạo THE hệ thống SHALL tự đặt `persons.is_living=false` và mirror `persons.deathDate`.

### REQ-07-02 — CRUD
- **REQ-07-02-01** WHEN user role `EDITOR+` POST `/persons/{id}/events` với `{type, title, eventDate, eventDateLunar?, location?, description?, mediaId?}` THE hệ thống SHALL tạo event, validate type-specific và trả 201.
- **REQ-07-02-02** WHEN user PATCH `/events/{id}` THE hệ thống SHALL cập nhật và audit.
- **REQ-07-02-03** WHEN user DELETE `/events/{id}` THE hệ thống SHALL soft-delete.
- **REQ-07-02-04** IF user role < EDITOR THEN THE hệ thống SHALL trả 403.

### REQ-07-03 — Calendar view
- **REQ-07-03-01** WHEN user GET `/families/{slug}/calendar?month=&year=&view=lunar|solar` THE hệ thống SHALL trả danh sách events trong tháng theo view.
- **REQ-07-03-02** WHEN view=lunar THE hệ thống SHALL convert solar dates sang lunar để hiển thị.

### REQ-07-04 — Anniversary & birthday
- **REQ-07-04-01** THE hệ thống SHALL tính các sự kiện sắp tới trong 30 ngày tới: sinh nhật (lặp lại hàng năm theo birthDate), ngày giỗ (lặp lại theo deathDate, lunar option), kỷ niệm cưới (startDate của SPOUSE relationship).
- **REQ-07-04-02** WHEN user GET `/families/{slug}/upcoming?days=30` THE hệ thống SHALL trả danh sách sắp xếp theo ngày.

### REQ-07-05 — Cron jobs
- **REQ-07-05-01** THE hệ thống SHALL chạy Spring `@Scheduled` cron `0 0 1 * * *` (mỗi ngày 01:00) tạo `notifications` cho tất cả event sắp tới trong 7 ngày (nếu chưa tạo).
- **REQ-07-05-02** THE hệ thống SHALL gửi email thông báo qua Redis queue + worker cho user có notification preference ON.

## Acceptance
- Tạo BIRTH event → persons.birthDate tự động set.
- Calendar lunar hiển thị đúng ngày âm (test case 15/8 âm = 10/9 dương 2024).
- Upcoming list có sinh nhật của tất cả person trong 30 ngày.
- Cron job chạy → có notification mới trong DB.
