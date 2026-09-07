package com.skillseed.shared.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class SkillCategoryConverter implements AttributeConverter<SkillCategory, String> {

    @Override
    public String convertToDatabaseColumn(SkillCategory attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public SkillCategory convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (SkillCategory c : SkillCategory.values()) {
            if (c.getDbValue().equals(dbValue)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown SkillCategory: " + dbValue);
    }
}
