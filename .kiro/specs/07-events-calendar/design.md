# 07 — Events & Calendar · Design

## 1. Module
```
event/
├── controller/{EventController, CalendarController, UpcomingController}
├── service/{EventService, CalendarService, AnniversaryService, EventNotificationJob}
├── dto/{EventCreateRequest, EventResponse, CalendarDayDto, UpcomingEventDto}
├── entity/{Event}
├── repository/{EventRepository}
└── scheduler/EventScheduler.java  // @Scheduled cron

common/
└── lunar/LunarDateService.java   // shared với Person module
```

## 2. Schema (Flyway V7)
```sql
CREATE TABLE events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  person_id UUID NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
  type VARCHAR(40) NOT NULL,
  title VARCHAR(255),
  description TEXT,
  event_date DATE NOT NULL,
  event_date_lunar BOOLEAN DEFAULT FALSE,
  lunar_json JSONB,
  location VARCHAR(255),
  media_id UUID REFERENCES media_assets(id),
  is_recurring BOOLEAN DEFAULT TRUE,
  deleted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_events_person ON events(person_id);
CREATE INDEX idx_events_date ON events(event_date) WHERE deleted_at IS NULL;
CREATE INDEX idx_events_type ON events(type);
```

## 3. EventService rules
- `BIRTH`/`DEATH`: 1 event per person, enforce unique constraint `UNIQUE(person_id, type) WHERE deleted_at IS NULL`.
- Update person fields trong cùng transaction.

## 4. CalendarService
- `getMonth(familyId, year, month, view)`:
  - Query events trong tháng.
  - Convert sang lunar nếu view=lunar (dùng LunarDateService).
  - Aggregate theo ngày (lunar day → nhóm chung).
- Return `{ days: [{date, events: [...]}] }`.

## 5. AnniversaryService
- Tính toán thuần SQL bằng `EXTRACT(MONTH/DAY FROM ...)` và so sánh với `current_date ± interval '30 days'`.
- Lunar anniversary: convert `current_date` sang lunar → check match với lunar_json.day/month.
- Spouse anniversary: query relationships where start_date, so sánh month/day.

## 6. Scheduler
```java
@Scheduled(cron = "0 0 1 * * *", zone = "Asia/Ho_Chi_Minh")
public void generateUpcomingNotifications() {
  var window = LocalDate.now().plusDays(7);
  anniversaryService.findUpcoming(window).forEach(notificationService::enqueue);
}
```

## 7. Endpoints
| Method | Path | Role |
|---|---|---|
| GET | `/persons/{id}/events` | MEMBER |
| POST | `/persons/{id}/events` | EDITOR+ |
| PATCH | `/events/{id}` | EDITOR+ |
| DELETE | `/events/{id}` | EDITOR+ |
| GET | `/families/{slug}/calendar` | MEMBER |
| GET | `/families/{slug}/upcoming` | MEMBER |

## 8. FE components
- `<CalendarMonth>` grid 7 cột, lunar/solar toggle.
- `<EventDot>` color theo type.
- `<UpcomingList>` sort by days-until.
- `<EventForm>` với date picker dual (lunar/solar).
