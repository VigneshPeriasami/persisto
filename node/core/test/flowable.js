// @flow
import type {Flowable} from "../src/flowable";
import flowable from "../src/flowable";

import {describe, it} from "mocha"
import {expect} from "chai";
import {recorder, fromArray} from "./helper";
import type {RecObjType} from "./helper";

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

  it("error pipeline", () => {
    const errorFlowable: Flowable<string> = flowable(
      (onNext: funcOnNext<string>, onError: funcOnNext<Error>) => {
        onError(new Error("fake error"));
      }
    );
    const resultRecorder: RecObjType<string> = recorder();
    const errRecorder: RecObjType<Error> = recorder();
    errorFlowable.listen(resultRecorder.onNext, errRecorder.onNext);

    expect(errRecorder.rec).to.have.lengthOf(1);
    expect(errRecorder.rec[0]).to.be.a("Error");
  });
});
