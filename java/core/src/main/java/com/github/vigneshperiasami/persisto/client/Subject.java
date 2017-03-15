package com.github.vigneshperiasami.persisto.client;

public abstract class Subject<T> {
  public abstract void push(T message) throws Exception;

  public <N> Subject<N> lift(ROperator<N, T> ROperator) {
    return new InterSubject<>(this, ROperator);
  }

  static class InterSubject<C, N> extends Subject<C> {
    private final Subject<N> subject;
    private final ROperator<C, N> ROperator;

    public InterSubject(Subject<N> subject, ROperator<C, N> ROperator) {
      this.subject = subject;
      this.ROperator = ROperator;
    }

    @Override
    public void push(C message) throws Exception {
      subject.push(ROperator.call(message));
    }
  }

}
