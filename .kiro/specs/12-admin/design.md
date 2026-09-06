# 12 — Admin & Audit · Design

## 1. Module
```
admin/
├── controller/{FamilyAdminController, SystemAdminController, AuditController, BackupController}
├── service/{AuditService, BackupService, RestoreService, StatsService, RetentionService, ImpersonationService}
├── dto/{DashboardResponse, AuditEntryDto, BackupResponse, RestorePreviewResponse}
├── entity/{AuditLog, PageView}
├── repository/{AuditLogRepository, BackupJobRepository}
└── scheduler/{RetentionScheduler}
```

## 2. Schema (Flyway V12)
```sql
CREATE TABLE audit_logs (
  id BIGSERIAL PRIMARY KEY,
  family_id UUID REFERENCES families(id) ON DELETE SET NULL,
  actor_id UUID REFERENCES users(id),
  impersonator_id UUID REFERENCES users(id),
  entity_type VARCHAR(50) NOT NULL,
  entity_id UUID,
  action VARCHAR(50) NOT NULL,
  changes JSONB,
  ip VARCHAR(45),
  user_agent TEXT,
  created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_audit_family ON audit_logs(family_id, created_at DESC);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_actor ON audit_logs(actor_id);

CREATE TABLE backup_jobs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
  requested_by UUID REFERENCES users(id),
  storage_key VARCHAR(500),
  status VARCHAR(20), -- QUEUED, RUNNING, DONE, FAILED
  size_bytes BIGINT,
  expires_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ, finished_at TIMESTAMPTZ
);
```

## 3. AOP audit
```java
@Aspect @Component
public class AuditAspect {
  @AfterReturning("@annotation(audited)")
  public void audit(JoinPoint jp, Audited audited) {
    var entry = AuditLog.builder()
      .familyId(currentFamily())
      .actorId(currentUser())
      .entityType(audited.entity())
      .entityId(extractId(jp))
      .action(audited.action())
      .changes(diff(jp))
      .build();
    repo.save(entry);
  }
}
```
- Annotation `@Audited(entity="Person", action="UPDATE")` đặt trên service method.

## 4. Backup
- `pg_dump --data-only --table=persons --table=relationships ...` qua ProcessBuilder.
- Compress gzip → upload MinIO.
- Async qua Redis queue tương tự export.
- Lưu schema version vào file header để restore đúng version.

## 5. Restore
- Step 1: parse SQL file, extract families/ persons / relationships count.
- Step 2: so sánh với DB hiện tại, preview conflicts.
- Step 3: confirm → chạy trong transaction, nếu fail → rollback.

## 6. Impersonation
- Endpoint trả JWT mới với claim `impersonator=<adminId>`.
- Mọi audit log tự động capture cả actor và impersonator.
- JWT TTL ngắn (1h) + revoke sau khi admin logout impersonation.

## 7. Retention
```java
@Scheduled(cron = "0 0 3 * * *")  // 03:00 daily
public void purgeDeletedFamilies() {
  familyRepo.findAllDeletedBefore(now().minusDays(30))
    .forEach(this::purgeFamily);
}
```
- Purge: xóa persons, relationships, events, media (MinIO), share tokens, audit log giữ lại ở level system.

## 8. Stats queries
- Top persons viewed: query `page_views` aggregate.
- Time-series: dùng `generate_series` + count audit_logs theo ngày.

## 9. Endpoints
| Method | Path | Role |
|---|---|---|
| GET | `/families/{slug}/admin/dashboard` | OWNER |
| GET | `/families/{slug}/admin/audit` | OWNER |
| POST | `/families/{slug}/admin/backup` | OWNER |
| GET | `/families/{slug}/admin/backups` | OWNER |
| POST | `/families/{slug}/admin/restore` | OWNER |
| GET | `/admin/families` | SYSTEM_ADMIN |
| DELETE | `/admin/families/{id}` | SYSTEM_ADMIN |
| POST | `/admin/impersonate/{userId}` | SYSTEM_ADMIN |
| GET | `/admin/health` | SYSTEM_ADMIN |

## 10. FE
- Tab "Admin" trong family settings (chỉ OWNER).
- Dashboard cards + chart (Recharts).
- Audit table với filter, JSON diff viewer (drawer).
- Backup list + restore wizard 2-step.
- System admin page riêng `/admin` (chỉ SYSTEM_ADMIN).
