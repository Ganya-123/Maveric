package com.maveric.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Base64;

@Converter
public class PasswordConverter implements AttributeConverter<char[], String> {

    @Override
    public String convertToDatabaseColumn(char[] attribute) {
        if (attribute == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(new String(attribute).getBytes());
    }

    @Override
    public char[] convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new String(Base64.getDecoder().decode(dbData)).toCharArray();
    }
}
