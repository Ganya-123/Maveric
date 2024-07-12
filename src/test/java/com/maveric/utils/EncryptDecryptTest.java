package com.maveric.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EncryptDecryptTest {

  private final EncryptDecrypt encryptDecrypt = new EncryptDecrypt();

  @Test
  void testEncode() {
    char[] input = {'t', 'e', 's', 't'};
    String encoded = encryptDecrypt.encode(input);
    assertNotNull(encoded);
    assertEquals("dGVzdA==", encoded);
  }

  @Test
  void testDecode() {
    String input = "dGVzdA==";
    char[] decoded = encryptDecrypt.decode(input);
    assertNotNull(decoded);
    assertArrayEquals(new char[] {'t', 'e', 's', 't'}, decoded);
  }

  @Test
  void testEncodeDecode() {
    char[] original = {'G', 'a', 'n', 'y', 'a'};
    String encoded = encryptDecrypt.encode(original);
    char[] decoded = encryptDecrypt.decode(encoded);
    assertArrayEquals(original, decoded);
  }

  @Test
  void testEmptyInput() {
    char[] emptyInput = {};
    String encoded = encryptDecrypt.encode(emptyInput);
    assertNotNull(encoded);
    assertEquals("", encoded);

    char[] decoded = encryptDecrypt.decode(encoded);
    assertNotNull(decoded);
    assertArrayEquals(emptyInput, decoded);
  }

  @Test
  void testNullInputEncode() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              encryptDecrypt.encode(null);
            });
  }

  @Test
  void testNullInputDecode() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              encryptDecrypt.decode(null);
            });
  }
}
