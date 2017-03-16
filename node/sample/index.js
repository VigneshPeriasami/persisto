const Persisto = require("../core");

const decodeOperatorFunc = (onNext) => {
  return (message) => {
    onNext(Buffer.from(message, "base64").toString());
  };
};

const encodeOperatorFunc = (message) => {
  return Buffer.from(message).toString("base64") + "\n";
};

const onConnection = (persisto) => {
  const reader = persisto.readFlowable().lift(decodeOperatorFunc);
  reader.listen(console.log);

  const writer = persisto.writeSubject().lift(encodeOperatorFunc);
  writer.push("Connected to persisto");
};

Persisto.connect(5000, onConnection);
console.log("Listening on 5000");
