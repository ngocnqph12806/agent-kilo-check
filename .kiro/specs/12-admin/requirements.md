# 12 — Admin & Audit · Requirements

### REQ-12-01 — Admin dashboard
- **REQ-12-01-01** WHEN user role `OWNER` GET `/families/{slug}/admin/dashboard` THE hệ thống SHALL trả stats: tổng persons, generations, branches, media count, member count, view 30 ngày, top persons xem nhiều.
- **REQ-12-01-02** THE hệ thống SHALL trả chart time-series 12 tháng (thêm/xóa person).

### REQ-12-02 — Audit log
- **REQ-12-02-01** THE hệ thống SHALL ghi audit mọi thao tác: CREATE/UPDATE/DELETE trên `families`, `persons`, `relationships`, `events`, `media_assets`; role change; visibility change; member add/remove.
- **REQ-12-02-02** WHEN OWNER GET `/families/{slug}/admin/audit?entity=&actor=&from=&to=&page=` THE hệ thống SHALL trả danh sách phân trang (40/page), filter theo entity/actor/thời gian.
- **REQ-12-02-03** THE hệ thống SHALL lưu trữ audit ≥ 2 năm (không xóa theo family delete).

### REQ-12-03 — Backup & Restore
- **REQ-12-03-01** WHEN OWNER POST `/families/{slug}/admin/backup` THE hệ thống SHALL trigger `pg_dump` family data ra file lưu MinIO bucket `family-backup/{familyId}/{timestamp}.sql.gz`, trả 202 + jobId.
- **REQ-12-03-02** WHEN backup xong THE hệ thống SHALL notification + email link download (TTL 7 ngày).
- **REQ-12-03-03** WHEN OWNER POST `/families/{slug}/admin/restore` với `{backupId}` THE hệ thống SHALL chạy dry-run trước (preview), yêu cầu confirm rồi apply.

### REQ-12-04 — Platform admin (system)
- **REQ-12-04-01** THE hệ thống SHALL có role `SYSTEM_ADMIN` (cấp qua env `SYSTEM_ADMIN_EMAILS`) cho phép truy cập `/admin/*` global.
- **REQ-12-04-02** WHEN SYSTEM_ADMIN GET `/admin/families` THE hệ thống SHALL trả danh sách tất cả family với filter, pagination.
- **REQ-12-04-03** WHEN SYSTEM_ADMIN DELETE `/admin/families/{id}` THE hệ thống SHALL force-delete family (cascade persons, relationships, media) sau khi ghi audit + email cảnh báo OWNER.
- **REQ-12-04-04** WHEN SYSTEM_ADMIN POST `/admin/impersonate/{userId}` THE hệ thống SHALL trả JWT tạm (TTL 1 giờ) có flag `impersonated=true`; mọi action log ghi `impersonator`.

### REQ-12-05 — Retention
- **REQ-12-05-01** THE hệ thống SHALL có cron `@Scheduled` xóa soft-deleted family sau 30 ngày nếu OWNER không khôi phục.
- **REQ-12-05-02** THE hệ thống SHALL purge refresh tokens hết hạn sau 30 ngày.

### REQ-12-06 — Health & Metrics
- **REQ-12-06-01** WHEN GET `/admin/health` THE hệ thống SHALL trả trạng thái DB, Redis, MinIO, queue depth.
- **REQ-12-06-02** THE hệ thống SHALL expose `/actuator/prometheus` metrics (đã có từ epic 00).

## Acceptance
- Owner xem dashboard có chart + stats đúng.
- Audit log có đầy đủ các action.
- Backup tạo file .sql.gz, download được.
- Restore dry-run preview trước khi apply.
- SYSTEM_ADMIN impersonate user → audit ghi rõ ai impersonate.
