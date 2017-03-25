package com.github.vigneshperiasami.persisto;

import com.github.vigneshperiasami.persisto.client.Subscriber;

import java.util.LinkedList;
import java.util.List;

public class SubscriberRecord<T> extends Subscriber<T> {
  List<T> dataQueue = new LinkedList<>();
  Throwable error;

  @Override
  public void onNext(T data) {
    dataQueue.add(data);
  }

  public T take(int position) {
    return dataQueue.get(position);
  }

  public int size() {
    return dataQueue.size();
  }

  public boolean hasError() {
    return error != null;
  }

  @Override
  public void onError(Throwable err) {
    if (hasError())
      throw new IllegalStateException("Error thrown twice");
    error = err;
  }
}
