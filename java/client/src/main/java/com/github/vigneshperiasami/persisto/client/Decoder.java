package com.github.vigneshperiasami.persisto.client;

public interface Decoder<T> {
  T decode(byte[] bytes);
}
