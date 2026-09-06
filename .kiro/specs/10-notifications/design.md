# 10 — Notifications · Design

## 1. Module
```
notification/
├── controller/{NotificationController, NotificationPreferenceController}
├── service/{NotificationService, NotificationDispatcher, EmailWorker}
├── dto/{NotificationResponse, NotificationPreferenceRequest}
├── entity/{Notification, NotificationPreference}
├── repository/{NotificationRepository, NotificationPreferenceRepository}
├── queue/{RedisEmailQueue, EmailJob}
├── ws/{NotificationWsController}
└── scheduler/{EventNotificationJob, WeeklyDigestJob}
```

## 2. Schema (Flyway V10)
```sql
CREATE TABLE notifications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  type VARCHAR(50) NOT NULL,
  family_id UUID REFERENCES families(id),
  payload JSONB NOT NULL,
  channels VARCHAR(20)[] DEFAULT '{in_app}',
  read_at TIMESTAMPTZ,
  email_sent_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_notif_user_unread ON notifications(user_id, created_at DESC) WHERE read_at IS NULL;

CREATE TABLE notification_preferences (
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  type VARCHAR(50) NOT NULL,
  in_app BOOLEAN DEFAULT TRUE,
  email BOOLEAN DEFAULT TRUE,
  realtime BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (user_id, type)
);
```

## 3. NotificationService.create
```
for each (user, type, payload):
  if preference[type][channel] == OFF: skip
  insert notification row
  if channel == realtime: wsTemplate.convertAndSendToUser(userId, "/queue/notifications", dto)
  if channel == email:    emailQueue.push({notificationId})
  return notification
```

## 4. EmailWorker
- `@Scheduled(fixedDelay = 5000)` pull batch từ Redis list `email:queue`.
- Rate limit: 50 emails/min để tránh spam provider.
- Render template (Thymeleaf) theo type.
- Gọi `JavaMailSender.send()`.
- Retry: catch exception → re-enqueue với delay tăng dần (1m, 5m, 15m).

## 5. Realtime
- Spring WebSocket + STOMP.
- Endpoint: `/ws` với SockJS fallback.
- FE subscribe `/user/queue/notifications` sau khi connect (gửi JWT qua STOMP CONNECT headers).
- BE `ChannelInterceptor` validate JWT.

## 6. Preferences
- Default: tất cả ON.
- Migration: khi thêm type mới, auto-insert preferences row ON cho existing user.

## 7. Endpoints
| Method | Path |
|---|---|
| GET | `/notifications` |
| GET | `/notifications/unread-count` |
| POST | `/notifications/{id}/read` |
| POST | `/notifications/read-all` |
| GET/PUT | `/users/me/notification-preferences` |
| WS | `/ws` (STOMP) |

## 8. FE
- Bell icon trong Topbar với badge.
- Dropdown list 10 mới nhất + "View all".
- Page `/notifications` đầy đủ với filter theo type.
- Toast realtime khi nhận WS message.
