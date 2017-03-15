package com.github.vigneshperiasami.persisto.client;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Subscriber<T> {
  private AtomicBoolean isAlive = new AtomicBoolean(true);

  public abstract void onNext(T data);

  public void onError(Throwable err) {
  }

  public void unsubscribe() {
    isAlive.set(false);
  }

  final boolean isAlive() {
    return isAlive.get();
  }
}
