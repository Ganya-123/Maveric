package com.maveric.utils;

import com.maveric.constants.Constants;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PasswordConverter implements AttributeConverter<char[], String> {

  @Override
  public String convertToDatabaseColumn(char[] attribute) {
    if (attribute == null || attribute.length == 0 || isOnlySpaces(attribute)) {
      throw new IllegalArgumentException(Constants.PASSWORDS_IMPROPER_FORMAT);
    }
    return new String(attribute);
  }

  @Override
  public char[] convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty() || dbData.trim().isEmpty()) {
      throw new IllegalArgumentException(Constants.PASSWORDS_IMPROPER_FORMAT);
    }
    return dbData.toCharArray();
  }

  private boolean isOnlySpaces(char[] attribute) {
    for (char c : attribute) {
      if (!Character.isWhitespace(c)) {
        return false;
      }
    }
    return true;
  }
}
