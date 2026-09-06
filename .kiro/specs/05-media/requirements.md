# 05 — Media · Requirements

### REQ-05-01 — Upload
- **REQ-05-01-01** WHEN user role `CONTRIBUTOR+` POST `/families/{slug}/media/upload` multipart với file và `?personId?` THE hệ thống SHALL upload lên MinIO bucket `family-media/{familyId}/{uuid}.{ext}`, sinh thumbnail 3 size (300, 800, 1920), trả 201 MediaResponse.
- **REQ-05-01-02** IF file > 25MB hoặc MIME không phải image/jpeg|png|webp|heic|video/mp4 THEN THE hệ thống SHALL trả 400.
- **REQ-05-01-03** IF user không phải member THEN THE hệ thống SHALL trả 403.
- **REQ-05-01-04** WHILE upload THE hệ thống SHALL scan virus bằng ClamAV (nếu cấu hình); nếu phát hiện SHALL reject và log security alert.

### REQ-05-02 — Thumbnails
- **REQ-05-02-01** WHEN upload thành công THE hệ thống SHALL tạo 3 thumbnail JPEG (300/800/1920px theo cạnh dài) preserve aspect ratio, EXIF rotate trước khi resize.
- **REQ-05-02-02** IF file gốc là HEIC THE hệ thống SHALL convert sang JPEG bằng `libheif` (qua ddcq/imagemagick fallback).

### REQ-05-03 — EXIF
- **REQ-05-05-01** WHEN upload ảnh THE hệ thống SHALL extract EXIF (DateTimeOriginal, GPS lat/lng, Camera) và lưu vào `media_assets.exif JSONB`.

### REQ-05-04 — Attach to Person
- **REQ-05-04-01** WHEN user POST `/families/{slug}/media/{mediaId}/attach?personId=` THE hệ thống SHALL tạo row `person_media(person_id, media_id, role)` với role mặc định `GALLERY`.
- **REQ-05-04-02** WHEN user chọn `role=AVATAR` THE hệ thống SHALL set `persons.avatar_media_id = mediaId` và unmark avatar cũ cùng person.

### REQ-05-05 — Read & List
- **REQ-05-05-01** WHEN member GET `/families/{slug}/media` THE hệ thống SHALL trả danh sách phân trang (mặc định 40/page) với thumbnail URL.
- **REQ-05-05-02** WHEN member GET `/persons/{id}/media` THE hệ thống SHALL trả danh sách media của person phân theo role (avatar, gallery, documents).
- **REQ-05-05-03** WHEN public GET media trong family visibility=PUBLIC THE hệ thống SHALL trả URL public.

### REQ-05-06 — Delete
- **REQ-05-06-01** WHEN user role `EDITOR+` DELETE `/media/{id}` THE hệ thống SHALL xóa object trên MinIO, soft-delete DB record, gỡ khỏi person_media và clear `persons.avatar_media_id` nếu là avatar.
- **REQ-05-06-02** IF user role < EDITOR THEN THE hệ thống SHALL trả 403.

### REQ-05-07 — URL signing (private assets)
- **REQ-05-07-01** WHEN family visibility=PRIVATE THE hệ thống SHALL trả URL media kèm chữ ký Presigned URL (TTL 15 phút) cho member; URL hết hạn SHALL trả 403.
- **REQ-05-07-02** WHEN family visibility=PUBLIC THE hệ thống SHALL trả URL static public.

## Acceptance
- Upload ảnh 5MB → có 3 thumbnail + EXIF parsed.
- Attach làm avatar → persons.avatar_media_id set; attach avatar mới unmark avatar cũ.
- File >25MB → 400.
- Family PRIVATE: URL trả về có query signature; URL hết hạn → 403 từ MinIO.
