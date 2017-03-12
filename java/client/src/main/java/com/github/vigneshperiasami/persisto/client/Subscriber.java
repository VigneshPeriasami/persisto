package com.github.vigneshperiasami.persisto.client;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Subscriber<T> implements Subscription {
  private AtomicBoolean isAlive = new AtomicBoolean(true);

  protected abstract void onNext(T data);

  protected void onError(Throwable err) {

  }

  public void unsubscribe() {
    isAlive.set(false);
  }

  final boolean isAlive() {
    return isAlive.get();
  }
}
