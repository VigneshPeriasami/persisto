"use strict";

class Flowable {
  constructor(listenFunc) {
    this.listenFunc = listenFunc;
  }

  lift(operatorFunc) {
    return new OperatorFlowable(this, operatorFunc);
  }

  listen(subscriber) {
    this.listenFunc(subscriber);
  }
}

class OperatorFlowable extends Flowable {
  constructor(flowable, operatorFunc) {
    super();
    this.flowable = flowable;
    this.operatorFunc = operatorFunc;
  }

  listen(subscriber) {
    this.flowable.listen(this.operatorFunc(subscriber));
  }
}

module.exports = (listenFunc) => {
  return new Flowable(listenFunc);
};
