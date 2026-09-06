# 09 — Export · Design

## 1. Module
```
export/
├── controller/{ExportController, ExportJobController}
├── service/{PdfExportService, GedcomExportService, JsonExportService, OcrImportService}
├── template/{family-pdf.html, tree-svg-template.svg}
├── worker/{ExportWorker}  // @RabbitListener hoặc @Scheduled pull
├── job/{ExportJobRepository, ExportJob}
└── queue/{RedisExportQueue}
```

## 2. PDF generation
- Dùng **OpenPDF** (LGPL) hoặc **iText 7 Community** — render HTML qua flying-saucer.
- Pipeline:
  1. Generate SVG tree (server-side dùng `batik` hoặc reuse layout JSON từ epic 06).
  2. Convert SVG → PNG (via batik transcoder) cho PDF nếu cần.
  3. Compose HTML template (Thymeleaf): cover + tree SVG + person list.
  4. flying-saucer render HTML → PDF stream.
- Font: embed `Be Vietnam Pro` + hỗ trợ CJK fallback.

## 3. GEDCOM 5.5.1 mapping
```
0 HEAD
1 SOUR GIAPHAONLINE
1 GEDC
2 VERS 5.5.1
2 FORM LINEAGE-LINKED
1 CHAR UTF-8
@I1@ INDI
1 NAME Nguyễn Văn A
1 SEX M
1 BIRT
2 DATE 1 JAN 1900
2 PLAC Hà Nội
1 DEAT
2 DATE 5 MAY 1975
1 OBJE @M1@
1 NOTE Tiểu sử...
@F1@ FAM
1 HUSB @I1@
1 WIFE @I2@
1 CHIL @I3@
1 MARR
2 DATE 10 FEB 1925
0 TRLR
```
- ID: `@I{personIdShort}@`, `@F{relIdShort}@`.

## 4. JSON schema
- File `family-export.schema.json` commit trong `docs/schemas/`.
- Validate bằng `networknt/json-schema-validator` trước khi ghi DB.

## 5. Async export
- Threshold: >5000 persons → vào queue.
- Redis queue key `export:jobs`, consumer là `ExportWorker` (`@Scheduled` poll hoặc Redis Streams).
- Job table:
  ```sql
  CREATE TABLE export_jobs (
    id UUID PRIMARY KEY,
    family_id UUID, user_id UUID, type VARCHAR(20),
    status VARCHAR(20), -- QUEUED, RUNNING, DONE, FAILED
    file_key VARCHAR(500), error TEXT,
    created_at TIMESTAMPTZ, started_at TIMESTAMPTZ, finished_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ
  );
  ```

## 6. OCR (optional)
- Trigger: PDF upload → Tesseract Vietnamese traineddata → text → heuristic parser (regex cho pattern "Nguyễn Văn A (1900-1975)").
- Kết quả trả về preview JSON; user review qua wizard.

## 7. Endpoints
| Method | Path | Role |
|---|---|---|
| GET | `/families/{slug}/export/pdf` | MEMBER |
| GET | `/families/{slug}/export/gedcom` | MEMBER |
| GET | `/families/{slug}/export/json` | MEMBER |
| POST | `/families/{slug}/import/pdf` (multipart) | EDITOR+ |
| GET | `/export/jobs/{id}` | owner |
| GET | `/export/jobs/{id}/download` | owner |

## 8. Storage
- Output PDF/JSON lưu MinIO bucket `family-export/{familyId}/{jobId}.pdf`.
- Presigned URL TTL 24h.
