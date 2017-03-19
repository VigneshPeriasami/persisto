const flatbuffers = require("../flatbuffers/flatbuffers").flatbuffers;
const proto = require("../sample-proto/message_generated").persisto.proto.message;
const Persisto = require("../core");

const decode = (byteArray) => {
  const buf = new flatbuffers.ByteBuffer(byteArray);
  return proto.Message.getRootAsMessage(buf);
};

const decodeOperatorFunc = (onNext) => {
  return (message) => {
    onNext(decode(message));
  };
};

const encode = (message) => {
  const builder = new flatbuffers.Builder(1024);
  const content = builder.createString(message);

  proto.Message.startMessage(builder);
  proto.Message.addContent(builder, content);
  proto.Message.addReceiver(builder, 1);
  proto.Message.addSender(builder, 2);
  const offset = proto.Message.endMessage(builder);
  builder.finish(offset);
  return builder.asUint8Array();
};

const encodeOperatorFunc = (message) => {
  message = encode(message);
  const payloadLength = Buffer.allocUnsafe(4);
  payloadLength.writeInt32BE(message.length, 0);
  return Buffer.concat([payloadLength, Buffer.from(message)]);
};

const onConnection = (persisto) => {
  const reader = persisto.readFlowableBuffer().lift(decodeOperatorFunc);
  reader.listen((message) => console.log(message.content()));

  const writer = persisto.writeSubject().lift(encodeOperatorFunc);
  writer.push("Connected to persisto!!!");
  writer.push("Hello tom!!");
  writer.push("Bye for now!!!");
};

Persisto.connect(5000, onConnection);
console.log("Listening on 5000");
