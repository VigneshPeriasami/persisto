"use strict";

class Subject {
  constructor(pushFunc) {
    this.pushFunc = pushFunc;
  }

  push(message) {
    this.pushFunc(message);
  }

  lift(operatorFunc) {
    return new InterSubject(this, operatorFunc);
  }
}

class InterSubject extends Subject {
  constructor(subject, operatorFunc) {
    super();
    this.subject = subject;
    this.operatorFunc = operatorFunc;
  }

  push(message) {
    this.subject.push(this.operatorFunc(message));
  }
}

module.exports = (pushFunc) => {
  return new Subject(pushFunc);
};
