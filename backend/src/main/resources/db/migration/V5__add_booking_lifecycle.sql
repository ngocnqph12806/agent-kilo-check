-- V5 — Booking lifecycle support (Sprint 2)
-- Adds a partial unique index to prevent the same teacher from holding
-- two active bookings that overlap on the same scheduled_at.
-- The combination with slot-conflict detection in BookingService covers
-- duration overlap, while this index guarantees at most one row per
-- scheduled slot per teacher across the active statuses.
CREATE UNIQUE INDEX IF NOT EXISTS idx_bookings_teacher_slot_unique
    ON bookings (teacher_id, scheduled_at)
    WHERE status IN ('pending', 'confirmed', 'in_progress');

-- BRIN index helps the reminder + no-show jobs that scan by
-- scheduled_at across large tables.
CREATE INDEX IF NOT EXISTS idx_bookings_scheduled_brin
    ON bookings USING brin (scheduled_at);
