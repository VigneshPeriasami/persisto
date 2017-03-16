"use strict";

const net = require("net");
const flowable = require("./flowable");
const subject = require("./subject");

const Persisto = {};

Persisto.readFlowable = (socket) => {
  return flowable((onNext) => {
    socket.on("data", (chunk) => {
      onNext(chunk.toString());
    });
  });
};

Persisto.writeSubject = (socket) => {
  return subject((message) => {
    socket.write(message);
  });
};

Persisto.create = (socket) => {
  const writeSubject = () => {
    return Persisto.writeSubject(socket);
  };
  const readFlowable = () => {
    return Persisto.readFlowable(socket);
  };

  return Object.assign({},
    { writeSubject, readFlowable },
    { socket });
};

Persisto.connect = (port, onConnect) => {
  net.createServer((socket) => {
    onConnect(Persisto.create(socket));
  }).listen(port);
};

module.exports = Persisto;
