// @flow
"use strict";

export class Subject<T> {
  pushFunc: funcPush<T>;

  constructor(pushFunc: funcPush<T>) {
    this.pushFunc = pushFunc;
  }

  push(message: T) {
    this.pushFunc(message);
  }

  lift<N>(operatorFunc: funcSubjectOperator<N, T>): Subject<N> {
    return new InterSubject(this, operatorFunc);
  }
}

class InterSubject<T, N> extends Subject<N> {
  subject: Subject<T>;
  operatorFunc: funcSubjectOperator<N, T>;

  constructor(subject: Subject<T>, operatorFunc: funcSubjectOperator<N, T>) {
    super();
    this.subject = subject;
    this.operatorFunc = operatorFunc;
  }

  push(message: N) {
    this.subject.push(this.operatorFunc(message));
  }
}

export default <T>(pushFunc: funcPush<T>): Subject<T> => {
  return new Subject(pushFunc);
};
