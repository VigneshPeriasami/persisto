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

public class Persisto implements AutoCloseable {
  private final Socket socket;
  private final DataInputStream inputStream;
  private final BufferedWriter writer;

  private Persisto(Socket socket) throws IOException {
    this.socket = socket;
    this.inputStream = new DataInputStream(socket.getInputStream());
    this.writer =  new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }

  public static Persisto connect(String host, int port) throws IOException {
    return new Persisto(new Socket(host, port));
  }

  private BufferedWriter writer() throws IOException {
    return writer;
  }

  @Override
  public void close() throws IOException {
    inputStream.close();
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

  public Flowable<DataInputStream> readFlowableInputStream() throws IOException {
    return new Flowable<DataInputStream>() {
      @Override
      public void listen(Subscriber<DataInputStream> subscriber) {
        subscriber.onNext(inputStream);
      }
    };
  }

  public Flowable<ByteBuffer> readFlowableFixedLength() throws IOException {
    return readFlowableInputStream().lift(fixedLengthCodec());
  }

  public Flowable<String> readFlowableLine() throws IOException {
    return readFlowableInputStream().lift(newLineCodec());
  }

  private static void safeClose(AutoCloseable closeable) {
    try {
      closeable.close();
    } catch (Exception ignored) {
    }
  }

  private static Operator<String, DataInputStream> newLineCodec() {
    return new Operator<String, DataInputStream>() {
      @Override
      public Subscriber<DataInputStream> call(final Subscriber<String> stringSubscriber) {
        return Subscriber.create(new Func<DataInputStream>() {
          @Override
          public void call(DataInputStream data) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(data));
            try {
              while(stringSubscriber.isAlive()) {
                stringSubscriber.onNext(reader.readLine());
              }
            } catch (Exception e) {
              stringSubscriber.onError(e);
            } finally {
              safeClose(reader);
            }
          }
        });
      }
    };
  }

  private static Operator<ByteBuffer, DataInputStream> fixedLengthCodec() {
    return new Operator<ByteBuffer, DataInputStream>() {
      @Override
      public Subscriber<DataInputStream> call(final Subscriber<ByteBuffer> byteBufferSubscriber) {
        return Subscriber.create(new Func<DataInputStream>() {
          @Override
          public void call(DataInputStream data) {
            try {
              while (byteBufferSubscriber.isAlive()) {
                int readLen = data.readInt();
                if (readLen == -1) {
                  throw new RuntimeException("End of Stream");
                }
                final byte[] full = new byte[readLen];
                data.readFully(full);
                byteBufferSubscriber.onNext(ByteBuffer.wrap(full));
              }
            } catch (Exception e) {
              byteBufferSubscriber.onError(e);
            } finally {
              safeClose(data);
            }
          }
        });
      }
    };
  }
}
