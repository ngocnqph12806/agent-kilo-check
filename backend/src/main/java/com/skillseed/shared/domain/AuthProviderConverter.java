package com.skillseed.shared.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AuthProviderConverter implements AttributeConverter<AuthProvider, String> {

    @Override
    public String convertToDatabaseColumn(AuthProvider attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public AuthProvider convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (AuthProvider a : AuthProvider.values()) {
            if (a.getDbValue().equals(dbValue)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Unknown AuthProvider: " + dbValue);
    }
}
