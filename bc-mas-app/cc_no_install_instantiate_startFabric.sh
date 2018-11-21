#!/bin/bash
#
# Valerio Mattioli (valeriomattioli580@gmail.com) 
#
# Exit on first error print all commands
#set -ev

# Exit on first error 
set -e

# REMOVE OLD CERTIFICATES
# start fresh!
if [ -d hfc-key-store ]; then rm -Rf hfc-key-store; fi
# rm -r hfc-key-store
mkdir hfc-key-store

# don't rewrite paths for Windows Git Bash users
export MSYS_NO_PATHCONV=1

starttime=$(date +%s)


# GET VALUES FROM JSON CONFIG FILE
cd ..
SERV_CHAN="$(./parseHFConfig.sh sc)"
TRANS_CHAN="$(./parseHFConfig.sh tc)"
CHAINCODE_SERV=$(./parseHFConfig.sh scc)
CHAINCODE_TRANS="$(./parseHFConfig.sh tcc)"
CHAINCODE_SMARBLES=$(./parseHFConfig.sh smcc)
CHAINCODE_TMARBLES=$(./parseHFConfig.sh tmcc)
CHAINCODE_TRUSTREPUTATION=$(./parseHFConfig.sh trustreputation)
cd bc-mas-app


# LAUNCH THE NETWORK, CREATE CHANNELS AND JOIN PEERS TO CHANNELS
cd ../setup-fabric-network
./start.sh

# FROM HERE STARTS THE HF SDK (cli, java, js)

# Now launch the CLI container in order to install, instantiate chaincode (-d : Detached mode (background))
docker-compose -f ./docker-compose.yml up -d cli

printf "\nTotal setup execution time : $(($(date +%s) - starttime)) secs ...\n\n\n"
printf "If ther is no folder called node_modules, then run 'npm install'\n otherwise ignore this message\n"
printf "Then you can run the JaDE application'\n"