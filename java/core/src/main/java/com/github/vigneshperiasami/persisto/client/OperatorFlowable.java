package com.github.vigneshperiasami.persisto.client;

class OperatorFlowable<T, C> extends Flowable<T> {
  OperatorFlowable(final Flowable<C> flowable, final Operator<T, C> operator) {
    super(new OnSubscribe<T>() {
      @Override
      public void call(Subscriber<T> subscriber) {
        flowable.listen(operator.call(subscriber));
      }
    });
  }
}
