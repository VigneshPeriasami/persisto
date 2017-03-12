package com.github.vigneshperiasami.persisto.client;

class ChannelReaderFactory {
  public static <T> ChannelReader<T> untilAlive(final LoopCall<T> loopCall) {
    return new ChannelReader<T>() {
      @Override
      public Subscription listen(Subscriber<T> subscriber) {
        while (subscriber.isAlive()) {
          try {
            subscriber.onNext(loopCall.next());
          } catch (Exception e) {
            subscriber.onError(e);
            subscriber.unsubscribe();
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
