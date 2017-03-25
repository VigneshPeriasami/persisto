package com.github.vigneshperiasami.persisto;

import com.github.vigneshperiasami.persisto.client.Flowable;
import com.github.vigneshperiasami.persisto.client.Func;
import com.github.vigneshperiasami.persisto.client.Operator;
import com.github.vigneshperiasami.persisto.client.Subscriber;
import org.junit.Test;

import static com.github.vigneshperiasami.persisto.client.Subscriber.create;
import static org.junit.Assert.assertEquals;

public class OperatorFlowableTest {

  @Test
  public void testOperatorChaining() {

    Flowable<String> reader = new Flowable<Integer>() {
      @Override
      public void listen(Subscriber<Integer> subscriber) {
        subscriber.onNext(1);
        subscriber.onNext(2);
      }
    }.lift(new Operator<String, Integer>() {
      @Override
      public Subscriber<Integer> call(final Subscriber<String> subscriber) {
        return new Subscriber<Integer>() {
          @Override
          public void onNext(Integer data) {
            subscriber.onNext(String.valueOf(data));
          }

          @Override
          public void onError(Throwable err) {
            subscriber.onError(err);
          }
        };
      }
    });

    final StringBuilder result = new StringBuilder();
    reader.listen(create(new Func<String>() {
      @Override
      public void call(String data) {
        result.append(data);
      }
    }));
    assertEquals("12", result.toString());
  }

  @Test
  public void testSameType() {
    Flowable<String> reader = new Flowable<String>() {
      @Override
      public void listen(Subscriber<String> subscriber) {
        subscriber.onNext("hello");
      }
    }.lift(new Operator<String, String>() {
      @Override
      public Subscriber<String> call(final Subscriber<String> subscriber) {
        return new Subscriber<String>() {
          @Override
          public void onNext(String data) {
            subscriber.onNext(data + " ,");
          }

          @Override
          public void onError(Throwable err) {
            subscriber.onError(err);
          }
        };
      }
    });
  }
}
