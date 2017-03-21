#!/bin/bash
flatc -o ../java/sample-proto/src/main/java/ -j message.fbs
flatc -o ../node/sample-proto/ -s message.fbs
