package com.github.vigneshperiasami.persisto.sample;

import com.github.vigneshperiasami.persisto.client.ChannelReader;
import com.github.vigneshperiasami.persisto.client.ChannelWriter;
import com.github.vigneshperiasami.persisto.client.Decoder;
import com.github.vigneshperiasami.persisto.client.Encoder;
import com.github.vigneshperiasami.persisto.client.Persisto;
import com.github.vigneshperiasami.persisto.client.Subscriber;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class SimpleClient {
  public static void main(String[] args) throws IOException {
    Decoder<String> decoder = raw -> new String(DatatypeConverter.parseBase64Binary(raw));
    Encoder<String> encoder = data -> String.format("%s", DatatypeConverter.printBase64Binary(data.getBytes()));

    Persisto persisto = Persisto.connect("127.0.0.1", 5000);
    ChannelReader<String> reader = persisto.createReader(decoder);
    ChannelWriter<String> writer = persisto.createWriter(encoder);

    reader.listen(new Subscriber<String>() {
      @Override
      protected void onNext(String data) {
        System.out.println(data);
        write(writer, "Roger that!!");
      }
    });
  }

  private static <T> void write(ChannelWriter<T> writer, T message) {
    try {
      writer.write(message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
