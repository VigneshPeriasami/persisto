"use strict";

const net = require("net");

const Persisto = {};

Persisto.createReader = (socket, decodeFunc) => {
  const reader = (onNext) => {
    socket.on("data", (chunk) => {
      onNext(decodeFunc(chunk.toString()));
    });
  };
  return Object.assign({}, { listen: reader });
};

Persisto.createWriter = (socket, encodeFunc) => {
  const writer = (message) => {
    socket.write(encodeFunc(message));
  };
  return Object.assign({}, { write: writer });
};

Persisto.createInstance = (socket) => {
  const createWriter = (encodeFunc) => {
    return Persisto.createWriter(socket, encodeFunc);
  };
  const createReader = (decodeFunc) => {
    return Persisto.createReader(socket, decodeFunc);
  };

  return Object.assign({},
    { createWriter: createWriter, createReader: createReader },
    { socket: socket });
};

Persisto.connect = (port, onConnect) => {
  net.createServer((socket) => {
    onConnect(Persisto.createInstance(socket));
  }).listen(port);
};

module.exports = Persisto;
