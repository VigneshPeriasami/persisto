// @flow
import flowable, {Flowable, Subscription} from "../src/flowable";

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
  return flowable((onNext: funcOnNext<T>): Subscription => {
    const subscription = new Subscription();
    for (let i = 0; i < array.length; i++) {
      if (subscription.unsubscribed) {
        break;
      }
      onNext(array[i], subscription.unsubscribeThunk());
    }
    return subscription;
  });
};
