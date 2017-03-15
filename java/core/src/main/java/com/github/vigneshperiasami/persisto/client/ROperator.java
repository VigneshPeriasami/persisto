package com.github.vigneshperiasami.persisto.client;

public interface ROperator<I, O> {
  O encode(I i);
}