package com.skillseed.shared.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class SkillStatusConverter implements AttributeConverter<SkillStatus, String> {

    @Override
    public String convertToDatabaseColumn(SkillStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public SkillStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        for (SkillStatus value : SkillStatus.values()) {
            if (value.getDbValue().equals(dbData)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown skill status: " + dbData);
    }
}