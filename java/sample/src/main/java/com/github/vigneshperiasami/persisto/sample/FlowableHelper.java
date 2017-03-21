package com.github.vigneshperiasami.persisto.sample;

import com.github.vigneshperiasami.persisto.client.Flowable;
import com.github.vigneshperiasami.persisto.client.Subscriber;

import java.util.concurrent.Executor;

public class FlowableHelper {

  public static Scheduler scheduler(Executor executor) {
    return new Scheduler(executor);
  }

  public static class Scheduler {
    private Executor executor;

    public Scheduler(Executor executor) {
      this.executor = executor;
    }

    public <T> void subscribe(Flowable<T> flowable, Subscriber<T> subscriber) {
      executor.execute(() -> flowable.listen(subscriber));
    }
  }
}
