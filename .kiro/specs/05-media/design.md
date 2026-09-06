# 05 — Media · Design

## 1. Module
```
media/
├── controller/{MediaController, MediaUploadController}
├── service/{MediaService, ThumbnailService, ExifService, StorageService, VirusScanService}
├── dto/{MediaResponse, MediaListItem, UploadResult, AttachMediaRequest}
├── entity/{MediaAsset, PersonMedia}
├── repository/{MediaAssetRepository, PersonMediaRepository}
└── config/{MinioConfig, ImageMagickConfig}
```

## 2. Storage layout
```
bucket: family-media
  {familyId}/
    original/{uuid}.{ext}
    thumb-300/{uuid}.jpg
    thumb-800/{uuid}.jpg
    thumb-1920/{uuid}.jpg
```
- Key sinh UUIDv7 để sort theo thời gian.
- Bucket policy: anonymous read cho objects có tag `public=true`; còn lại yêu cầu presigned.

## 3. Upload pipeline
```
multipart → validate (size, MIME)
  → store bytes to temp file
  → virus scan (optional)
  → MinIO put "original"
  → async: ThumbnailService + ExifService (worker pool 4 threads)
  → persist MediaAsset
  → return 201
```

## 4. Thumbnail service
- Dùng **imagemagick** CLI (`convert`) hoặc **thumbnailator** Java lib.
- EXIF rotation: dùng `exiftool` hoặc metadata-extractor Java.
- Lưu DB: 3 cột `thumb_300_key, thumb_800_key, thumb_1920_key` + URL đầy đủ.
- Failure strategy: nếu thumbnail fail → log warn, vẫn trả original URL.

## 5. EXIF
- Lib: `com.drewnoakes:metadata-extractor`.
- Field extracted: DateTimeOriginal, GPSLatitude/Longitude, Make, Model, ImageWidth/Height.
- Lưu JSONB.

## 6. URL signing
- MinIO Java SDK `getPresignedObjectUrl(GetObjectArgs, ttl)`.
- TTL = 15 phút cho PRIVATE.
- PUBLIC: lưu CDN URL static (qua nginx reverse proxy `/_media/{familyId}/...`).

## 7. PersonMedia entity
```sql
CREATE TABLE person_media (
  person_id UUID REFERENCES persons(id) ON DELETE CASCADE,
  media_id UUID REFERENCES media_assets(id) ON DELETE CASCADE,
  role VARCHAR(20) NOT NULL,  -- AVATAR, GALLERY, DOCUMENT, COVER
  position INT DEFAULT 0,
  PRIMARY KEY (person_id, media_id, role)
);
CREATE INDEX idx_person_media_person ON person_media(person_id);
```

## 8. Endpoints
| Method | Path | Role |
|---|---|---|
| POST | `/families/{slug}/media/upload` | CONTRIBUTOR+ |
| GET | `/families/{slug}/media` | MEMBER |
| GET | `/persons/{id}/media` | MEMBER |
| POST | `/families/{slug}/media/{mediaId}/attach` | EDITOR+ |
| DELETE | `/media/{id}` | EDITOR+ |
| PATCH | `/media/{id}` (caption) | EDITOR+ |

## 9. ImageMagick Dockerfile addon
- Thêm package: `apk add imagemagick imagemagick-heic libheif exiftool` trong Dockerfile api.
- Hoặc tách worker container `media-worker` nếu workload nặng.
