// @flow
"use strict";

import net, {Socket} from "net";
import flowable, {Flowable} from "./flowable";
import subject, {Subject} from "./subject";

export type PersistoType = {
  writeSubject: () => Subject<Buffer>,
  readFlowable: () => Flowable<string>,
  readFlowableBuffer: () => Flowable<Buffer>,
  socket: Socket
};

const PersistoFactory = {};

PersistoFactory.readFlowable = (socket: Socket): Flowable<string> => {
  return flowable((onNext: funcOnNext<string>) => {
    // todo: access to stop the subscription
    socket.on("data", (chunk: Buffer) => {
      onNext(chunk.toString());
    });
  });
};

PersistoFactory.readFlowableBuffer = (socket: Socket): Flowable<Buffer> => {
  return flowable((onNext: funcOnNext<Buffer>) => {
    socket.on("data", (chunk: Buffer) => {
      onNext(chunk);
    });
  });
};

PersistoFactory.writeSubject = (socket: Socket): Subject<Buffer> => {
  return subject((message: Buffer) => {
    socket.write(message);
  });
};

PersistoFactory.create = (socket: Socket): PersistoType => {
  return {
    writeSubject: PersistoFactory.writeSubject.bind(null, socket),
    readFlowable: PersistoFactory.readFlowable.bind(null, socket),
    readFlowableBuffer: PersistoFactory.readFlowableBuffer.bind(null, socket),
    socket: socket
  };
};

PersistoFactory.connect = (port: number, onConnect: (obj: PersistoType) => void) => {
  net.createServer((socket: Socket) => {
    onConnect(PersistoFactory.create(socket));
  }).listen(port);
};

export default PersistoFactory;
