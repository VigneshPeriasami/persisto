"use strict";

const net = require("net");

const Persisto = {};

Persisto.createReader = (socket, decodeFunc) => {
  const listen = (onNext) => {
    socket.on("data", (chunk) => {
      onNext(decodeFunc(chunk.toString()));
    });
  };
  return Object.assign({}, { listen });
};

Persisto.createWriter = (socket, encodeFunc) => {
  const write = (message) => {
    socket.write(encodeFunc(message));
  };
  return Object.assign({}, { write });
};

Persisto.createInstance = (socket) => {
  const createWriter = (encodeFunc) => {
    return Persisto.createWriter(socket, encodeFunc);
  };
  const createReader = (decodeFunc) => {
    return Persisto.createReader(socket, decodeFunc);
  };

  return Object.assign({},
    { createWriter, createReader },
    { socket });
};

Persisto.connect = (port, onConnect) => {
  net.createServer((socket) => {
    onConnect(Persisto.createInstance(socket));
  }).listen(port);
};

module.exports = Persisto;
