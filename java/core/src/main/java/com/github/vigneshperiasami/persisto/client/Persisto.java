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

  public Subject<String> writeSubject() throws IOException {
    return new Subject<String>() {
      final BufferedWriter writer = writer();

      @Override
      public void push(String message) {
        try {
          writer.write(message);
          writer.flush();
        } catch (Exception ignored) {

        }
      }
    };
  }

  public Flowable<String> readFlowable() throws IOException {
    return FlowableFactory.untilAlive(new FlowableFactory.PullFuncGen<String>() {
      @Override
      public FlowableFactory.PullFunc<String> create() throws Exception {
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
