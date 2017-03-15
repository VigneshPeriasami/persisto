package com.github.vigneshperiasami.persisto.sample;

import com.github.vigneshperiasami.persisto.client.ROperator;
import com.github.vigneshperiasami.persisto.client.Flowable;
import com.github.vigneshperiasami.persisto.client.Operator;
import com.github.vigneshperiasami.persisto.client.Persisto;
import com.github.vigneshperiasami.persisto.client.Subject;
import com.github.vigneshperiasami.persisto.client.Subscriber;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class SimpleClient {
  public static void main(String[] args) throws IOException {
    Operator<String, String> decoder = subscriber -> new Subscriber<String>() {
      @Override
      public void onNext(String data) {
        subscriber.onNext(new String(DatatypeConverter.parseBase64Binary(data)));
      }
    };

    ROperator<String, String> encoder = s -> String.format("%s", DatatypeConverter.printBase64Binary(s.getBytes()));
    ROperator<Integer, String> intToString = String::valueOf;

    Persisto persisto = Persisto.connect("127.0.0.1", 5000);
    Flowable<String> reader = persisto.readFlowable();

    final Subject<String> writer = persisto.writeSubject()
        .lift(encoder);

    final Subject<Integer> numberWriter = writer.lift(intToString);

    numberWriter.push(100);

    reader.lift(decoder).listen(new Subscriber<String>() {
      @Override
      public void onNext(String data) {
        System.out.println(data);
        write(writer, "Roger that!!");
      }
    });
  }

  private static <T> void write(Subject<T> writer, T message) {
    try {
      writer.push(message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
