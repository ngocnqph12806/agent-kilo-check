# 10 — Notifications · Requirements

### REQ-10-01 — Channels
- **REQ-10-01-01** THE hệ thống SHALL hỗ trợ 3 channel: `in_app` (inbox), `email`, `realtime` (WebSocket).
- **REQ-10-01-02** WHEN user thay đổi preference `/users/me/notification-preferences` THE hệ thống SHALL lưu per-channel ON/OFF.

### REQ-10-02 — Types
- **REQ-10-02-01** THE hệ thống SHALL sinh notifications cho: `DEATH_ANNIVERSARY` (trước 7 ngày và trong ngày), `BIRTHDAY`, `MARRIAGE_ANNIVERSARY`, `FAMILY_INVITE`, `MEMBER_JOINED`, `PERSON_CREATED` (cho editor trong family), `EXPORT_READY`.

### REQ-10-03 — In-app inbox
- **REQ-10-03-01** WHEN user GET `/notifications` THE hệ thống SHALL trả danh sách 20 mới nhất, phân trang cursor.
- **REQ-10-03-02** WHEN user POST `/notifications/{id}/read` THE hệ thống SHALL đánh dấu đã đọc.
- **REQ-10-03-03** WHEN user POST `/notifications/read-all` THE hệ thống SHALL đánh dấu tất cả đã đọc.

### REQ-10-04 — Realtime
- **REQ-10-04-01** WHEN notification mới được tạo cho user THE hệ thống SHALL push qua WebSocket `/user/queue/notifications`.
- **REQ-10-04-02** FE SHALL cập nhật badge unread count trong Topbar.

### REQ-10-05 — Email
- **REQ-10-05-01** WHEN notification có channel email ON THE hệ thống SHALL enqueue job gửi email (Redis queue).
- **REQ-10-05-02** THE hệ thống SHALL có worker pull và gửi qua SMTP (Brevo/SES/SMTP custom).
- **REQ-10-05-03** IF gửi fail THEN THE hệ thống SHALL retry 3 lần exponential backoff; vẫn fail → đánh dấu `failed` và không retry.

### REQ-10-06 — Preferences
- **REQ-10-06-01** THE hệ thống SHALL cung cấp UI Settings > Notifications với toggle cho từng type × channel.
- **REQ-10-06-02** WHEN user tắt 1 type THE hệ thống SHALL không sinh notification đó nữa.

### REQ-10-07 — Digest (optional)
- **REQ-10-07-01** THE hệ thống SHALL cung cấp weekly digest email tổng hợp các sự kiện sắp tới (opt-in).

## Acceptance
- Cron sinh notification → user nhận trong in-app + email.
- Đánh dấu đã đọc → badge giảm.
- WS connect thấy notification trong <2s sau khi tạo.
- Tắt BIRTHDAY → cron không tạo notification.
