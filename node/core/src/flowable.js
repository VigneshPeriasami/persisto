// @flow
"use strict";

export class Flowable<T> {
  listenFunc: funcListen<T>;

  constructor(listenFunc: funcListen<T>) {
    this.listenFunc = listenFunc;
  }

  lift<N>(operatorFunc: funcOperator<T, N>): Flowable<N> {
    return new OperatorFlowable(this, operatorFunc);
  }

  filter(filterFunc: funcFilter<T>): Flowable<T> {
    return new OperatorFlowable(this, (onNext: funcOnNext<T>): funcOnNext<T> => {
      return (data: T) => {
        if (filterFunc(data)) {
          onNext(data)
        }
      };
    });
  }

  listen(onNext: funcOnNext<T>, onError: funcOnNext<Error>, onComplete: funcOnNext<void>) {
    this.listenFunc(onNext, onError, onComplete);
  }
}

class OperatorFlowable<C, N> extends Flowable<N> {
  flowable: Flowable<C>;
  operatorFunc: funcOperator<N, C>;

  constructor(flowable: Flowable<C>, operatorFunc: funcOperator<N, C>) {
    super();
    this.flowable = flowable;
    this.operatorFunc = operatorFunc;
  }

  listen(onNext: funcOnNext<N>, onError: funcOnNext<Error>, onComplete: funcOnNext<void>) {
    this.flowable.listen(this.operatorFunc(onNext), onError, onComplete);
  }
}

export default <T>(listenFunc: funcListen<T>): Flowable<T> => {
  return new Flowable(listenFunc);
};
