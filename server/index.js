"use strict";

const net = require("net");
const clients = [];

const encode = (message) => {
  return Buffer.from(message).toString("base64");
};

const decode = (message) => {
  return Buffer.from(message, "base64").toString();
};

const bundle = (message) => {
  return encode(message) + "\n";
};

const write = (socket, message) => {
  console.log("Sending message: " + message);
  console.log("result: " + socket.write(bundle(message)));
};

net.createServer((socket) => {
  socket.on("data", (chunk) => {
    console.log(decode(chunk.toString()));
  });
  socket.name = socket.remoteAddress + ":" + socket.remotePort;
  clients.push(socket);
  write(socket, "Connected to Persisto");
  console.log("Connected with " + socket.name);
}).listen(5000);


console.log("Server running on 5000");
