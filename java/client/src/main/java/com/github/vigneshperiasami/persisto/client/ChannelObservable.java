package com.github.vigneshperiasami.persisto.client;

public interface ChannelObservable<T> {
  Subscription listen(Subscriber<T> subscriber);
}
