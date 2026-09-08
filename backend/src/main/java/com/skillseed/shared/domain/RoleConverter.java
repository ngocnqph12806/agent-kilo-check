package com.skillseed.shared.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public Role convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (Role r : Role.values()) {
            if (r.getDbValue().equals(dbValue)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown Role: " + dbValue);
    }
}
