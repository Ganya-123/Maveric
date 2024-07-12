package com.maveric.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class CharArrayToStringSerializer extends JsonSerializer<char[]> {

  @Override
  public void serialize(char[] value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeString(new String(value));
  }
}
