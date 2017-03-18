// automatically generated by the FlatBuffers compiler, do not modify

package persisto.proto.message;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Message extends Table {
  public static Message getRootAsMessage(ByteBuffer _bb) { return getRootAsMessage(_bb, new Message()); }
  public static Message getRootAsMessage(ByteBuffer _bb, Message obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public Message __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String content() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer contentAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public int receiver() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int sender() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createMessage(FlatBufferBuilder builder,
      int contentOffset,
      int receiver,
      int sender) {
    builder.startObject(3);
    Message.addSender(builder, sender);
    Message.addReceiver(builder, receiver);
    Message.addContent(builder, contentOffset);
    return Message.endMessage(builder);
  }

  public static void startMessage(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addContent(FlatBufferBuilder builder, int contentOffset) { builder.addOffset(0, contentOffset, 0); }
  public static void addReceiver(FlatBufferBuilder builder, int receiver) { builder.addInt(1, receiver, 0); }
  public static void addSender(FlatBufferBuilder builder, int sender) { builder.addInt(2, sender, 0); }
  public static int endMessage(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishMessageBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
}
