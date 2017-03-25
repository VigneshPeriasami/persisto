"use strict";

const net = require("net");
const flowable = require("./flowable");
const subject = require("./subject");

const Persisto = {};

Persisto.readFlowable = (socket) => {
  return flowable((onNext) => {
    // todo: access to stop the subscription
    socket.on("data", (chunk) => {
      onNext(chunk.toString());
    });
  });
};

Persisto.readFlowableBuffer = (socket) => {
  return flowable((onNext) => {
    socket.on("data", (chunk) => {
      onNext(chunk);
    });
  });
};

Persisto.writeSubject = (socket) => {
  return subject((message) => {
    socket.write(message);
  });
};

Persisto.create = (socket) => {
  return Object.assign({},
    {
      writeSubject: Persisto.writeSubject.bind(null, socket),
      readFlowable: Persisto.readFlowable.bind(null, socket),
      readFlowableBuffer: Persisto.readFlowableBuffer.bind(null, socket)
    },
    { socket });
};

Persisto.connect = (port, onConnect) => {
  net.createServer((socket) => {
    onConnect(Persisto.create(socket));
  }).listen(port);
};

module.exports = Persisto;
