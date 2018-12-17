#!/bin/bash

if [ -d hfc-key-store ]; then rm -Rf hfc-key-store; fi
	
docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q) && docker network prune && docker rmi $(docker images dev-* -q)
