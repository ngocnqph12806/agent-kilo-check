# 07 — Events & Calendar · Tasks

- [ ] Tạo Flyway `V7__events.sql`.
- [ ] Entity + repository + MapStruct mapper.
- [ ] `EventService` (CRUD + mirror person fields for BIRTH/DEATH).
- [ ] `CalendarService.getMonth` (solar + lunar view).
- [ ] `AnniversaryService.findUpcoming` (birthday, death anniversary lunar, spouse anniversary).
- [ ] `EventScheduler` @Scheduled cron 01:00.
- [ ] Tích hợp với `NotificationService` (enqueue notifications).
- [ ] Controllers + Swagger.
- [ ] BE unit test anniversary calculation với các ngày âm đặc biệt (30 Tết, tháng nhuận).
- [ ] BE integration test cron job (dùng `@MockBean` scheduler).
- [ ] FE: page `/families/[slug]/calendar` với view switcher.
- [ ] FE: `CalendarMonth` component (Custom grid 7 cột).
- [ ] FE: `EventForm` với LunarDatePicker dual.
- [ ] FE: `UpcomingList` page/section.
- [ ] FE: tab Events trong person detail (timeline view).
- [ ] e2E: tạo BIRTH event → assert person.birthDate set.
- [ ] e2E: scheduler test (chạy manual trigger qua endpoint admin).
