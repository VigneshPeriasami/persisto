const Persisto = require("../core");

const decodeFunc = (message) => {
  return Buffer.from(message, "base64").toString();
};

const encodeFunc = (message) => {
  return Buffer.from(message).toString("base64") + "\n";
};

const onConnection = (persisto) => {
  const reader = persisto.createReader(decodeFunc);
  const writer = persisto.createWriter(encodeFunc);
  reader.listen(console.log);
  writer.write("Connected to persisto");
};

Persisto.connect(5000, onConnection);
console.log("Listening on 5000");
