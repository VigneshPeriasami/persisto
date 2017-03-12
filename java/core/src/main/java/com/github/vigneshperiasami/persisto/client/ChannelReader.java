package com.github.vigneshperiasami.persisto.client;

public interface ChannelReader<T> {
  Subscription listen(Subscriber<T> subscriber);
}
