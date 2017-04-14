// @flow
import type {Flowable, Subscription} from "../src/flowable";
import flowable from "../src/flowable";

import {describe, it} from "mocha"
import {expect} from "chai";
import {recorder, fromArray} from "./helper";
import type {RecObjType} from "./helper";

const throwError = (err: Error) => { throw err; };

describe("Flowable", () => {
  it("listen all emitted flowable values", () => {
    const stringFlowable: Flowable<string> = fromArray(["one", "two"]);
    const recorderObj: RecObjType<string> = recorder();
    stringFlowable.listen(recorderObj.onNext);

    expect(recorderObj.rec).to.have.lengthOf(2);
    expect(recorderObj.rec).to.eql(["one", "two"]);
  });

  it("listen always from the begining", () => {
    const stringFlowable: Flowable<string> = fromArray(["one", "two"]);
    const recorderOne: RecObjType<string> = recorder();
    const recorderTwo: RecObjType<string> = recorder();
    stringFlowable.listen(recorderOne.onNext);
    stringFlowable.listen(recorderTwo.onNext);

    expect(recorderOne.rec).to.have.lengthOf(2);
    expect(recorderOne.rec).to.eql(recorderTwo.rec);
  });

  it("functional immutable operator chaining", () => {
    const intFlowable: Flowable<number> = fromArray([1, 2]);
    const recorderObj: RecObjType<string> = recorder();
    const intRecorderObj: RecObjType<number> = recorder();
    const stringFlowable = intFlowable.lift((onNext: funcOnNext<string>): funcOnNext<number> => {
      return (data: number) => {
        onNext(data.toString());
      };
    });
    intFlowable.listen(intRecorderObj.onNext);
    stringFlowable.listen(recorderObj.onNext);

    expect(intRecorderObj.rec).to.have.lengthOf(2);
    expect(recorderObj.rec).to.have.lengthOf(2);
    expect(intRecorderObj.rec).to.eql([1, 2]);
    expect(recorderObj.rec).to.eql(["1", "2"]);
  });

  it("check filter operator", () => {
    const recorderObj: RecObjType<number> = recorder();
    fromArray([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]).filter((data: number): boolean => {
      return data % 2 === 0;
    }).listen(recorderObj.onNext);

    expect(recorderObj.rec).to.have.lengthOf(5);
    expect(recorderObj.rec).to.eql([2, 4, 6, 8, 10]);
  });

  describe("Error handling", () => {
    const fakeErrorFlow: funcListen<string> = (onNext: funcOnNext<string>,
        onError: funcOnNext<Error>) => {
      try {
        throw Error("fake exception");
      } catch (err) {
        onError(err);
      }
    };

    it("receive error", () => {
      const resultRecorder: RecObjType<string> = recorder();
      const errRecorder: RecObjType<Error> = recorder();
      flowable(fakeErrorFlow).listen(resultRecorder.onNext, errRecorder.onNext);

      expect(errRecorder.rec).to.have.lengthOf(1);
      expect(errRecorder.rec[0]).to.be.a("Error");
      expect(errRecorder.rec[0].message).to.equal("fake exception");
    });

    it("do not ignore if no error callback", () => {
      const resultRecorder: RecObjType<string> = recorder();
      const flowThunk = () => {
        flowable(fakeErrorFlow).listen(resultRecorder);
      };
      expect(flowThunk).to.throw(Error);
      expect(resultRecorder.rec).to.have.lengthOf(0);
    });

    it("operator chaining error flow", () => {
      const resultRecorder: RecObjType<string> = recorder();
      const errRecorder: RecObjType<Error> = recorder();
      flowable(fakeErrorFlow).lift((onNext: funcOnNext<string>): funcOnNext<string> => {
        return (data: string) => {
          onNext("msg: " + data);
        };
      }).listen(resultRecorder.onNext, errRecorder.onNext);

      expect(errRecorder.rec).to.have.lengthOf(1);
      expect(errRecorder.rec[0]).to.be.a("Error");
    });

    it("catch onNext errors", () => {
      const errRecorder: RecObjType<Error> = recorder();
      fromArray(["one", "two"]).listen(() => {
        throw Error("unhandled onNext exception");
      }, errRecorder.onNext);

      expect(errRecorder.rec).to.have.lengthOf(1);
      expect(errRecorder.rec[0]).to.be.a("Error");
      expect(errRecorder.rec[0].message).to.equal("unhandled onNext exception");
    });
  });

  describe("Subscription", () => {
    it("receive subscription with onNext", () => {
      fromArray(["one", "two"]).listen((data: string, unsubscribe: () => void) => {
        expect(unsubscribe).to.not.be.undefined;
        expect(unsubscribe).to.be.a("Function");
      }, throwError);
    });

    it("stop after unsubscribe call", () => {
      const result: Array<string> = [];
      fromArray(["one", "two", "done", "none"]).listen((data: string, unsubscribe: () => void) => {
        if (data == "done") {
          unsubscribe();
        }
        result.push(data);
      });
      expect(result).to.have.lengthOf(3);
    });

    it("unsubscribe on error", () => {
      const result: Array<string> = [];
      const errRecorder: RecObjType<Error> = recorder();
      const subscription: Subscription = fromArray(["one", "two", "done", "none"])
        .listen((data: string) => {
          result.push(data);
          if (data == "done") {
            throw Error("fake exception");
          }
        }, errRecorder.onNext);

      expect(result).to.have.lengthOf(3);
      expect(errRecorder.rec).to.have.lengthOf(1);
      expect(errRecorder.rec[0]).to.be.a("Error");
      expect(subscription.unsubscribed).to.be.true;
    });
  });
});
