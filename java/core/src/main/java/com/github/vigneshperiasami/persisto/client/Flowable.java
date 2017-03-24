package com.github.vigneshperiasami.persisto.client;

public abstract class Flowable<T> {
  public abstract void listen(Subscriber<T> subscriber);

  public <N> Flowable<N> lift(Operator<N, T> operator) {
    return new OperatorFlowable<>(this, operator);
  }

  public Flowable<T> filter(final FuncB<T> filterFunc) {
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
}
