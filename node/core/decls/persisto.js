declare function funcOnNext<T>(message: T, unsubscribe?: () => void): void;
declare function funcListen<T>(onNext: funcOnNext<T>, onError: funcOnNext<Error>, onComplete: funcOnNext<void>): void;

declare function funcOperator<T, O>(onNext: funcOnNext<T>): funcOnNext<O>;
declare function funcFilter<T>(message: T): boolean;

// Subject

declare function funcPush<T>(message: T): void;
declare function funcSubjectOperator<T, N>(message: N): T;

declare type SubscriptionType = {
  unsubscribed: boolean;
  unsubscribe: () => void;
};
