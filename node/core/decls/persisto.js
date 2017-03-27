declare function funcOnNext<T>(message: T): void;
declare function funcListen<T>(onNext: funcOnNext<T>): void;

declare function funcOperator<T, O>(onNext: funcOnNext<T>): funcOnNext<O>;
declare function funcFilter<T>(message: T): boolean;

// Subject

declare function funcPush<T>(message: T): void;
declare function funcSubjectOperator<T, N>(message: N): T;

declare type Persisto = {
  writeSubject: () => Subject<Buffer>,
  readFlowable: () => Flowable<string>,
  readFlowableBuffer: () => Flowable<Buffer>,
  socket: Socket
}
