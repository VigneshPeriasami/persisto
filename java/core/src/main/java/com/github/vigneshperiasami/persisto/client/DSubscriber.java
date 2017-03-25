package com.github.vigneshperiasami.persisto.client;

class DSubscriber<T> extends Subscriber<T> {
  private final Func<T> onNext;
  private final Func<Throwable> onError;

  DSubscriber(Func<T> onNext, Func<Throwable> onError) {
    this.onNext = onNext;
    this.onError = onError;
  }

  @Override
  public void onNext(T data) {
    onNext.call(data);
  }

  @Override
  public void onError(Throwable err) {
    onError.call(err);
    unsubscribe();
  }
}
