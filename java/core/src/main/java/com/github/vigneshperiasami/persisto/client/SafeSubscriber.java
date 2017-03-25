package com.github.vigneshperiasami.persisto.client;

class SafeSubscriber<T> extends Subscriber<T> {
  private Subscriber<T> subscriber;

  SafeSubscriber(Subscriber<T> subscriber) {
    this.subscriber = subscriber;
  }

  @Override
  public void onNext(T data) {
    try {
      subscriber.onNext(data);
    } catch (Exception e) {
      subscriber.onError(e);
    }
  }

  @Override
  public void unsubscribe() {
    subscriber.unsubscribe();
  }

  @Override
  public void onError(Throwable err) {
    subscriber.onError(err);
    subscriber.unsubscribe();
  }

  @Override
  public boolean isAlive() {
    return subscriber.isAlive();
  }
}
