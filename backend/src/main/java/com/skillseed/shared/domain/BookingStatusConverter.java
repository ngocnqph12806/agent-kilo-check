package com.skillseed.shared.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class BookingStatusConverter implements AttributeConverter<BookingStatus, String> {

    @Override
    public String convertToDatabaseColumn(BookingStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public BookingStatus convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (BookingStatus s : BookingStatus.values()) {
            if (s.getDbValue().equals(dbValue)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown BookingStatus: " + dbValue);
    }
}
