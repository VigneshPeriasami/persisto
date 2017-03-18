package com.github.vigneshperiasami.persisto.sample;

import com.google.flatbuffers.FlatBufferBuilder;
import persisto.proto.message.Message;

public class FlatBufferSample {

  public static void main(String[] args) {
    FlatBufferBuilder builder = new FlatBufferBuilder(1024);
    int content = builder.createString("Hello jerry");
    int tomMessage = Message.createMessage(builder, content, 2, 1);
    builder.finish(tomMessage);
    System.out.println(tomMessage);
    Message message = Message.getRootAsMessage(builder.dataBuffer());
    System.out.println(message.content());
  }
}
