#!/bin/sh -e

./build-jar-to-bin.sh
./build-function-zip.sh
sam local start-api -t template.yaml
