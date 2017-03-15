package com.github.vigneshperiasami.persisto.client;

class FlowableFactory {

  static void safeStop(PullFunc pullFunc) {
    try {
      pullFunc.stop();
    } catch (Exception ignored) {
    }
  }

  static <T> Flowable<T> untilAlive(final PullFuncGen<T> pullFuncGen) {
    return new Flowable<T>() {
      @Override
      public void listen(Subscriber<T> subscriber) {
        PullFunc<T> pullFunc = null;
        try {
          pullFunc = pullFuncGen.create();

          while (subscriber.isAlive()) {
              subscriber.onNext(pullFunc.next());
          }
        } catch (Exception e) {
          subscriber.onError(e);
          subscriber.unsubscribe();
        } finally {
          safeStop(pullFunc);
        }
      }
    };
  }

  interface PullFuncGen<T> {
    PullFunc<T> create() throws Exception;
  }

  interface PullFunc<T> {
    T next() throws Exception;
    void stop();
  }
}
