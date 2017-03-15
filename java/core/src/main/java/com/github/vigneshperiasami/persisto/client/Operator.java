package com.github.vigneshperiasami.persisto.client;

public interface Operator <I, O> {
  Subscriber<O> call(Subscriber<I> subscriber);
}
