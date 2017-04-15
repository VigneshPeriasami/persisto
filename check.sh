#!/bin/bash
(cd node/core; npm install; gulp)
(cd java/; gradle check)
