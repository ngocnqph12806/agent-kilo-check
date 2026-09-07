package com.skillseed.shared.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class SeedTransactionStatusConverter implements AttributeConverter<SeedTransactionStatus, String> {

    @Override
    public String convertToDatabaseColumn(SeedTransactionStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public SeedTransactionStatus convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (SeedTransactionStatus s : SeedTransactionStatus.values()) {
            if (s.getDbValue().equals(dbValue)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown SeedTransactionStatus: " + dbValue);
    }
}
