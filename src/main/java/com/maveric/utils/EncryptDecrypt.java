package com.maveric.utils;

import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class EncryptDecrypt {

  public String encode(char[] input) {
    if (input == null) {
      throw new IllegalArgumentException("Input cannot be null");
    }
    return Base64.getEncoder().encodeToString(new String(input).getBytes());
  }

  public char[] decode(String input) {
    if (input == null) {
      throw new IllegalArgumentException("Input cannot be null");
    }
    return new String(Base64.getDecoder().decode(input)).toCharArray();
  }
}
