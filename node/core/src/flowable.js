// @flow
"use strict";

const funcStub = () => {};
const errStub = (err: Error) => { throw err };

const safeWrapOnNext = <T>(onNext: funcOnNext<T>, onError: funcOnNext<Error>): funcOnNext<T> => {
  return (d: T, unsubscribe: () => void) => {
    try {
      onNext(d, unsubscribe);
    } catch (err) {
      onError(err);
      unsubscribe();
    }
  };
};

export class Subscription {
  unsubscribed: boolean;
  onUnsubscribe: () => void;

  constructor(onUnsubscribe: () => void = funcStub) {
    this.unsubscribed = false;
    this.onUnsubscribe = onUnsubscribe;
  }

  unsubscribeThunk(): () => void {
    return () => {
      this.unsubscribe();
    };
  }

  unsubscribe() {
    this.unsubscribed = true;
    this.onUnsubscribe();
  }
}

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

  listen(onNext: funcOnNext<T>, onError: funcOnNext<Error> = errStub,
      onComplete: funcOnNext<void> = funcStub): Subscription {
    return this.listenFunc(safeWrapOnNext(onNext, onError), onError, onComplete);
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

  listen(onNext: funcOnNext<N>, onError: funcOnNext<Error>,
      onComplete: funcOnNext<void>): Subscription {
    return this.flowable.listen(this.operatorFunc(onNext), onError, onComplete);
  }
}

export default <T>(listenFunc: funcListen<T>): Flowable<T> => {
  return new Flowable(listenFunc);
};
