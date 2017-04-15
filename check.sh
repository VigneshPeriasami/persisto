#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo $DIR
cd $DIR/java/
gradle :core:check

cd $DIR/node/core
npm install
gulp
