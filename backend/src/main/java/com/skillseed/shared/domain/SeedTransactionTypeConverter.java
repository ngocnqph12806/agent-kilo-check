package com.skillseed.shared.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class SeedTransactionTypeConverter implements AttributeConverter<SeedTransactionType, String> {

    @Override
    public String convertToDatabaseColumn(SeedTransactionType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public SeedTransactionType convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (SeedTransactionType t : SeedTransactionType.values()) {
            if (t.getDbValue().equals(dbValue)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown SeedTransactionType: " + dbValue);
    }
}
