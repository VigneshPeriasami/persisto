package com.github.vigneshperiasami.persisto;

import com.github.vigneshperiasami.persisto.client.Flowable;
import com.github.vigneshperiasami.persisto.client.Subscriber;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.vigneshperiasami.persisto.client.Subscriber.create;
import static com.google.common.truth.Truth.assertThat;

public class FlowableTest {

  public static <T> Flowable<T> from(final List<T> list) {
    return new Flowable<>(subscriber -> {
      for (T data : list) {
        if (!subscriber.isAlive()) {
          return;
        }
        subscriber.onNext(data);
      }
    });
  }

  public static <T> Flowable<T> from(T... data) {
    return from(Arrays.asList(data));
  }

  @Test
  public void basicFlowable() {
    Flowable<String> flowable = from("one", "two", "three");
    SubscriberRecord<String> subscriber = new SubscriberRecord<>();
    flowable.listen(subscriber);

    assertThat(subscriber.size()).isEqualTo(3);
    assertThat(subscriber.take(0)).isEqualTo("one");
    assertThat(subscriber.take(1)).isEqualTo("two");
    assertThat(subscriber.take(2)).isEqualTo("three");
  }

  @Test
  public void handleSubscriberUnHandledError() {
    Flowable<String> flowable = from("one", "two");
    AtomicReference<Throwable> ref = new AtomicReference<>();

    List<String> all = new LinkedList<>();
    flowable.listen(create(data -> {
      all.add(data);
      throw new RuntimeException(data);
    }, ref::set));

    assertThat(ref).isNotNull();
    assertThat(all).hasSize(1);
  }
}
