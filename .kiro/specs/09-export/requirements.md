# 09 — Export · Requirements

### REQ-09-01 — PDF export
- **REQ-09-01-01** WHEN user GET `/families/{slug}/export/pdf` THE hệ thống SHALL sinh file PDF chứa cover page, cây gia phả (rendered as vector), danh sách person với tiểu sử, ghi chú cuối.
- **REQ-09-01-02** THE hệ thống SHALL hỗ trợ options: `?includeMedia=false&paperSize=A4&orientation=portrait&lang=vi`.
- **REQ-09-01-03** THE hệ thống SHALL stream PDF qua response (`Content-Type: application/pdf`).

### REQ-09-02 — GEDCOM export
- **REQ-09-02-01** WHEN user GET `/families/{slug}/export/gedcom` THE hệ thống SHALL sinh file `.ged` chuẩn GEDCOM 5.5.1.
- **REQ-09-02-02** THE hệ thống SHALL map: `INDI` cho person, `FAM` cho relationship, `BIRT/DEAT/MARR` cho events, `OBJE` cho media, `NOTE` cho biography.
- **REQ-09-02-03** THE hệ thống SHALL đảm bảo file GEDCOM parse được bằng Gramps/Ancestry.

### REQ-09-03 — JSON export
- **REQ-09-03-01** WHEN user GET `/families/{slug}/export/json` THE hệ thống SHALL trả JSON đầy đủ persons, relationships, events, media.
- **REQ-09-03-02** JSON SHALL tuân theo schema `family-export.schema.json` (commit trong repo).

### REQ-09-04 — PDF import (optional)
- **REQ-09-04-01** WHERE user upload PDF scan chứa cây gia phả THE hệ thống SHALL gọi OCR (Tesseract) trích text, parse heuristic và preview import (không tự save).
- **REQ-09-04-02** THE hệ thống SHALL cho user review + map từng dòng sang Person/Relationship trước khi commit.

### REQ-09-05 — Rate limit
- **REQ-09-05-01** THE hệ thống SHALL giới hạn export: 5 lần/giờ/user/family cho PDF; không giới hạn JSON/GEDCOM nhỏ.
- **REQ-09-05-02** IF vượt limit THEN THE hệ thống SHALL trả 429.

### REQ-09-06 — Async large export
- **REQ-09-06-01** IF family > 5000 persons THEN THE hệ thống SHALL đẩy job vào Redis queue và trả 202 với `jobId`; FE poll `/export/jobs/{jobId}` để lấy kết quả.
- **REQ-09-06-02** WHEN job xong THE hệ thống SHALL gửi notification + email link download (TTL 24h).

## Acceptance
- Export PDF 50 persons < 5s.
- GEDCOM file mở được trong Gramps không lỗi.
- JSON re-import đúng persons + relationships.
- Export 6000 persons async → 202 + email download.
