package com.github.vigneshperiasami.persisto.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
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

  public Subject<byte[]> writeSubjectByte() throws IOException {
    return new Subject<byte[]>() {
      final OutputStream outputStream = socket.getOutputStream();

      @Override
      public void push(byte[] message) throws Exception {
        outputStream.write(message);
        outputStream.flush();
      }
    };
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

  public Flowable<ByteBuffer> readFlowableByte() throws IOException {
    return FlowableFactory.untilAlive(new Callable<FlowableFactory.PullFunc<ByteBuffer>>() {
      @Override
      public FlowableFactory.PullFunc<ByteBuffer> call() throws Exception {

        return new FlowableFactory.PullFunc<ByteBuffer>() {
          final DataInputStream inputStream = new DataInputStream(socket.getInputStream());

          @Override
          public ByteBuffer next() throws Exception {
            int readLen = inputStream.readInt();
            if (readLen == -1) {
              throw new RuntimeException("End of stream");
            }
            final byte[] full = new byte[readLen];
            inputStream.readFully(full);
            return ByteBuffer.wrap(full);
          }

          @Override
          public void stop() {
          }
        };
      }
    });
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
