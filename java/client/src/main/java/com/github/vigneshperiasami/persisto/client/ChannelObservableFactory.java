package com.github.vigneshperiasami.persisto.client;

class ChannelObservableFactory {
  public static <T> ChannelObservable<T> untilAlive(final LoopCall<T> loopCall) {
    return new ChannelObservable<T>() {
      @Override
      public Subscription listen(Subscriber<T> subscriber) {
        while (subscriber.isAlive()) {
          try {
            subscriber.onNext(loopCall.next());
          } catch (Exception e) {
            subscriber.onError(e);
          }
        }
        return subscriber;
      }
    };
  }

  public interface LoopCall<T> {
    T next() throws Exception;
  }
}
