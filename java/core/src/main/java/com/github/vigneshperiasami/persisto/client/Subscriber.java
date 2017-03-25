package com.github.vigneshperiasami.persisto.client;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Subscriber<T> {
  private final static Func<Throwable> ERR_NONE = new Func<Throwable>() {
    @Override
    public void call(Throwable data) {
      throw new RuntimeException("Unhandled exception", data);
    }
  };

  private AtomicBoolean isAlive = new AtomicBoolean(true);

  public abstract void onNext(T data);

  public abstract void onError(Throwable err);

  public void unsubscribe() {
    isAlive.set(false);
  }

  public static <T> Subscriber<T> create(Func<T> onNext) {
    return create(onNext, ERR_NONE);
  }

  public static <T> Subscriber<T> create(Func<T> onNext, Func<Throwable> onError) {
    return new DSubscriber<>(onNext, onError);
  }

  public boolean isAlive() {
    return isAlive.get();
  }
}
