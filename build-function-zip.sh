#!/bin/bash

chmod 755 ./serverbin
zip -j function.zip aws/bootstrap serverbin
