// @flow
import flowable, {Flowable} from "../src/flowable";

export type RecObjType<T> = {
  rec: Array<T>,
  onNext: funcOnNext<T>
};

export const recorder = <T>(): RecObjType<T> => {
  const rec: Array<T> = [];
  return {
    rec: rec,
    onNext: (data: T) => {
      rec.push(data);
    }
  };
};

export const fromArray = <T>(array: Array<T>): Flowable<T> => {
  return flowable((onNext: funcOnNext<T>) => {
    array.forEach((item: T) => {
      onNext(item);
    });
  });
};
