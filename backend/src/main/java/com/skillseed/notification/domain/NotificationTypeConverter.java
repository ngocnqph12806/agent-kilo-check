package com.skillseed.notification.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Stores {@link NotificationType} as its lowercase {@code dbValue}
 * (e.g. {@code "booking_request"}) so it matches the
 * {@code chk_notifications_type} DB CHECK constraint defined in
 * {@code V4__add_notifications.sql}. Hibernate's default
 * {@code EnumType.STRING} would write the Java enum name
 * (e.g. {@code "BOOKING_REQUEST"}) and fail that CHECK.
 */
@Converter(autoApply = false)
public class NotificationTypeConverter implements AttributeConverter<NotificationType, String> {

    @Override
    public String convertToDatabaseColumn(NotificationType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public NotificationType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        for (NotificationType value : NotificationType.values()) {
            if (value.getDbValue().equals(dbData)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + dbData);
    }
}
