#!/bin/bash

if [ -d hfc-key-store ]; then rm -Rf hfc-key-store; fi


cd ../basic-network-test
./stop.sh