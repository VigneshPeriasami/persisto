package com.github.vigneshperiasami.persisto.client;

public class Flowable<T> {
  private OnSubscribe<T> onSubscribe;

  public Flowable(OnSubscribe<T> onSubscribe) {
    this.onSubscribe = onSubscribe;
  }

  public final void listen(Subscriber<T> subscriber) {
    onSubscribe.call(new SafeSubscriber<>(subscriber));
  }

  public final <N> Flowable<N> lift(Operator<N, T> operator) {
    return new OperatorFlowable<>(this, operator);
  }

  public final Flowable<T> filter(final FuncB<T> filterFunc) {
    return new OperatorFlowable<>(this, new Operator<T, T>() {
      @Override
      public Subscriber<T> call(final Subscriber<T> nSubscriber) {
        return new Subscriber<T>() {
          @Override
          public void onNext(T data) {
            if (filterFunc.call(data)) {
              nSubscriber.onNext(data);
            }
          }

          @Override
          public void onError(Throwable err) {
            nSubscriber.onError(err);
          }
        };
      }
    });
  }

  public interface OnSubscribe<T> {
    void call(Subscriber<T> subscriber);
  }
}
