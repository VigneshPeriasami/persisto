package com.github.vigneshperiasami.persisto.client;

import java.io.IOException;

public interface ChannelWriter<T> {
  void write(T message) throws IOException;
}
