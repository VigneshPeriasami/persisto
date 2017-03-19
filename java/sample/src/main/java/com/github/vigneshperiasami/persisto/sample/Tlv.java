package com.github.vigneshperiasami.persisto.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Tlv {

  public static byte[] create(byte[] value) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write(1);
    outputStream.write(value.length);
    outputStream.write(value);
    return outputStream.toByteArray();
  }
}
