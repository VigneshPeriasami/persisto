package com.github.vigneshperiasami.persisto.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Persisto {

  public static <T> ChannelObservable<T> from(InetSocketAddress addr, final Decoder<T> decoder) throws IOException {
    Socket socket = new Socket();
    socket.connect(addr);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    return ChannelObservableFactory.untilAlive(new ChannelObservableFactory.LoopCall<T>() {
      @Override
      public T next() throws Exception {
        return decoder.decode(reader.readLine().getBytes());
      }
    });
  }
}
