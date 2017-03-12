package com.github.vigneshperiasami.persisto.client;

public interface Encoder<T> {
  String encode(T data);
}
