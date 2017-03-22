"use strict";

class Flowable {
  constructor(listenFunc) {
    this.listenFunc = listenFunc;
  }

  lift(operatorFunc) {
    return new OperatorFlowable(this, operatorFunc);
  }

  filter(filterFunc) {
    return new OperatorFlowable(this, (onNext) => {
      return (data) => {
        if (filterFunc(data)) {
          onNext(data)
        }
      }
    })
  }

  listen(onNext) {
    this.listenFunc(onNext);
  }
}

class OperatorFlowable extends Flowable {
  constructor(flowable, operatorFunc) {
    super();
    this.flowable = flowable;
    this.operatorFunc = operatorFunc;
  }

  listen(onNext) {
    this.flowable.listen(this.operatorFunc(onNext));
  }
}

module.exports = (listenFunc) => {
  return new Flowable(listenFunc);
};
