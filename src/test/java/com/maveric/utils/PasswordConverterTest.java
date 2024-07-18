package com.maveric.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordConverterTest {

  private PasswordConverter passwordConverter;

  @BeforeEach
  void setUp() {
    passwordConverter = new PasswordConverter();
  }

  @Test
  void convertToDatabaseColumn_ValidInput() {
    char[] password = "validPassword".toCharArray();
    String expected = "validPassword";
    String actual = passwordConverter.convertToDatabaseColumn(password);
    assertEquals(expected, actual);
  }

  @Test
  void convertToDatabaseColumn_NullInput() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              passwordConverter.convertToDatabaseColumn(null);
            });
    assertEquals(
        "Password cannot be null, empty, or consist only of spaces", exception.getMessage());
  }

  @Test
  void convertToDatabaseColumn_EmptyInput() {
    char[] password = "".toCharArray();
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              passwordConverter.convertToDatabaseColumn(password);
            });
    assertEquals(
        "Password cannot be null, empty, or consist only of spaces", exception.getMessage());
  }

  @Test
  void convertToDatabaseColumn_SpacesOnlyInput() {
    char[] password = "   ".toCharArray();
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              passwordConverter.convertToDatabaseColumn(password);
            });
    assertEquals(
        "Password cannot be null, empty, or consist only of spaces", exception.getMessage());
  }

  @Test
  void convertToEntityAttribute_ValidInput() {
    String dbData = "validPassword";
    char[] expected = "validPassword".toCharArray();
    char[] actual = passwordConverter.convertToEntityAttribute(dbData);
    assertArrayEquals(expected, actual);
  }

  @Test
  void convertToEntityAttribute_NullInput() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              passwordConverter.convertToEntityAttribute(null);
            });
    assertEquals(
        "Password cannot be null, empty, or consist only of spaces", exception.getMessage());
  }

  @Test
  void convertToEntityAttribute_EmptyInput() {
    String dbData = "";
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              passwordConverter.convertToEntityAttribute(dbData);
            });
    assertEquals(
        "Password cannot be null, empty, or consist only of spaces", exception.getMessage());
  }

  @Test
  void convertToEntityAttribute_SpacesOnlyInput() {
    String dbData = "   ";
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              passwordConverter.convertToEntityAttribute(dbData);
            });
    assertEquals(
        "Password cannot be null, empty, or consist only of spaces", exception.getMessage());
  }
}
