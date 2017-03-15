package com.github.vigneshperiasami.persisto.client;

public interface ROperator<I, O> {
  O call(I i);
}