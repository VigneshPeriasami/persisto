package com.github.vigneshperiasami.persisto.client;

public abstract class Flowable<T> {
  public abstract void listen(Subscriber<T> subscriber);

  public <N> Flowable<N> lift(Operator<N, T> operator) {
    return new OperatorFlowable<>(this, operator);
  }
}
