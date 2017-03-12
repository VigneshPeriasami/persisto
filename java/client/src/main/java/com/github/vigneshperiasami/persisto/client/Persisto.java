package com.github.vigneshperiasami.persisto.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Persisto {
  private final Socket socket;

  private Persisto(Socket socket) {
    this.socket = socket;
  }

  public static Persisto connect(String host, int port) throws IOException {
    return new Persisto(new Socket(host, port));
  }

  private BufferedReader reader() throws IOException {
    return new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  private BufferedWriter writer() throws IOException {
    return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }

  public <T> ChannelWriter<T> createWriter(final Encoder<T> encoder) throws IOException {
    final BufferedWriter writer = writer();
    return new ChannelWriter<T>() {
      @Override
      public void write(T message) throws IOException {
        writer.write(encoder.encode(message));
        writer.flush();
      }
    };
  }

  public <T> ChannelReader<T> createReader(final Decoder<T> decoder) throws IOException {
    final BufferedReader reader = reader();

    return ChannelReaderFactory.untilAlive(new ChannelReaderFactory.LoopCall<T>() {
      @Override
      public T next() throws Exception {
        return decoder.decode(reader.readLine());
      }
    });
  }
}
