package com.github.vigneshperiasami.persisto.sample;

import com.github.vigneshperiasami.persisto.client.Decoder;
import com.github.vigneshperiasami.persisto.client.Persisto;
import com.github.vigneshperiasami.persisto.client.Subscriber;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleClient {
  public static void main(String[] args) throws IOException {
    Decoder<String> decoder = bytes -> new String(DatatypeConverter.parseBase64Binary(new String(bytes)));

    Persisto.from(new InetSocketAddress("127.0.0.1", 5000), decoder).listen(new Subscriber<String>() {
      @Override
      protected void onNext(String data) {
        System.out.println(data);
      }
    });
  }
}
