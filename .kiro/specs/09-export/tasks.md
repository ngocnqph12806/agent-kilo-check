# 09 — Export · Tasks

- [ ] Add deps: OpenPDF, flying-saucer, json-schema-validator, Tesseract (optional).
- [ ] Tạo Flyway `V9__export_jobs.sql`.
- [ ] `JsonExportService` + `family-export.schema.json`.
- [ ] `GedcomExportService` writer (test với Gramps import).
- [ ] `PdfExportService` compose HTML template + flying-saucer.
- [ ] `ExportController` sync endpoints (size threshold check).
- [ ] `ExportWorker` async job (Spring Scheduling hoặc Redis Streams).
- [ ] Rate limit filter cho `/export/pdf`.
- [ ] `ExportJobController` poll + download.
- [ ] Email template `export-ready.html`.
- [ ] BE unit test: GEDCOM format (golden file), JSON schema validation.
- [ ] BE integration test: export family 50 persons PDF, open không lỗi.
- [ ] e2E: download PDF/GEDCOM/JSON.
- [ ] FE: page `/families/[slug]/export` với 3 nút + progress khi async.
- [ ] FE: modal hiển thị job status với polling.
- [ ] Optional: OCR import wizard với preview table.
