package com.github.vigneshperiasami.persisto.sample;

import com.github.vigneshperiasami.persisto.client.Flowable;
import com.github.vigneshperiasami.persisto.client.Operator;
import com.github.vigneshperiasami.persisto.client.Persisto;
import com.github.vigneshperiasami.persisto.client.ROperator;
import com.github.vigneshperiasami.persisto.client.Subject;
import com.github.vigneshperiasami.persisto.client.Subscriber;
import com.google.flatbuffers.FlatBufferBuilder;
import persisto.proto.message.Message;

import java.nio.ByteBuffer;

import static com.github.vigneshperiasami.persisto.client.Subscriber.create;

public class FlatBufferSample {

  public static void main(String[] args) throws Exception {
    Persisto persisto = Persisto.connect("127.0.0.1", 5000);

    Subject<String> messageWriter = persisto.writeSubjectByte().lift(messageWriter());
    Flowable<String> messageReader = persisto.readFlowableFixedLength().lift(messageReader());
    messageWriter.push("Hello jerry");

    messageReader.listen(create(System.out::println));
  }

  public static Operator<String, ByteBuffer> messageReader() {
    return stringSubscriber -> new Subscriber<ByteBuffer>() {
      @Override
      public void onNext(ByteBuffer data) {
        stringSubscriber.onNext(Message.getRootAsMessage(data).content());
      }

      @Override
      public void onError(Throwable err) {
        err.printStackTrace();
      }
    };
  }

  public static ROperator<String, byte[]> messageWriter() {
    return FlatBufferSample::flatMessage;
  }

  public static byte[] flatMessage(String message) {
    FlatBufferBuilder builder = new FlatBufferBuilder(1024);
    int content = builder.createString(message);
    Message.startMessage(builder);
    Message.addContent(builder, content);
    Message.addReceiver(builder, 2);
    Message.addSender(builder, 1);
    int tomMessagePayload = Message.endMessage(builder);

    builder.finish(tomMessagePayload);

    return builder.sizedByteArray();
  }
}
