package com.github.vigneshperiasami.persisto.client;

class OperatorFlowable<T, C> extends Flowable<T> {
  private final Flowable<C> flowable;
  private final Operator<T, C> operator;

  OperatorFlowable(Flowable<C> flowable, Operator<T, C> operator) {
    this.flowable = flowable;
    this.operator = operator;
  }

  @Override
  public void listen(Subscriber<T> subscriber) {
    flowable.listen(operator.call(subscriber));
  }
}
