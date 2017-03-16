package com.github.vigneshperiasami.persisto.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Persisto implements AutoCloseable {
  private final Socket socket;
  private final BufferedReader reader;
  private final BufferedWriter writer;

  private Persisto(Socket socket) throws IOException {
    this.socket = socket;
    this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.writer =  new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }

  public static Persisto connect(String host, int port) throws IOException {
    return new Persisto(new Socket(host, port));
  }

  private BufferedReader reader() throws IOException {
    return reader;
  }

  private BufferedWriter writer() throws IOException {
    return writer;
  }

  @Override
  public void close() throws IOException {
    reader.close();
    writer.close();
    socket.close();
  }

  public Subject<String> writeSubject() throws IOException {
    return new Subject<String>() {
      final BufferedWriter writer = writer();

      @Override
      public void push(String message) throws Exception {
        writer.write(message);
        writer.flush();
      }
    };
  }

  public Flowable<String> readFlowable() throws IOException {
    return FlowableFactory.untilAlive(new Callable<FlowableFactory.PullFunc<String>>() {
      @Override
      public FlowableFactory.PullFunc<String> call() throws Exception {
        final BufferedReader reader = reader();

        return new FlowableFactory.PullFunc<String>() {
          @Override
          public String next() throws Exception {
            return reader.readLine();
          }

          @Override
          public void stop() {
            try {
              reader.close();
            } catch (IOException ignored) {
            }
          }
        };
      }
    });
  }
}