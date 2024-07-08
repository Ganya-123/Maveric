package com.maveric.config;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class EncryptDecrypt {

    public String encode(char[] input) {
        return Base64.getEncoder().encodeToString(new String(input).getBytes());
    }

    public char[] decode(String input) {
        return new String(Base64.getDecoder().decode(input)).toCharArray();
    }
}
