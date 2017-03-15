package com.github.vigneshperiasami.persisto.client;

import java.util.concurrent.Callable;

class FlowableFactory {

  static void safeStop(PullFunc pullFunc) {
    try {
      pullFunc.stop();
    } catch (Exception ignored) {
    }
  }

  static <T> Flowable<T> untilAlive(final Callable<PullFunc<T>> pullFuncGen) {
    return new Flowable<T>() {
      @Override
      public void listen(Subscriber<T> subscriber) {
        PullFunc<T> pullFunc = null;
        try {
          pullFunc = pullFuncGen.call();

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

  interface PullFunc<T> {
    T next() throws Exception;
    void stop();
  }
}
