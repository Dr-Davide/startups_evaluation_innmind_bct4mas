#!/bin/bash
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
# Exit on first error, print all commands.
set -ev

# don't rewrite paths for Windows Git Bash users
export MSYS_NO_PATHCONV=1

# GET VALUES FROM JSON CONFIG FILE
cd ..
ORG_NAME="$(./parseHFConfig.sh org)"
DOMAIN="$(./parseHFConfig.sh domain)"

CA_NAME="$(./parseHFConfig.sh ca)"

ORDER_NAME="$(./parseHFConfig.sh orderer)"
ORDER_URL="$(./parseHFConfig.sh ordererUrl)"

SERV_CHAN="$(./parseHFConfig.sh sc)"
TRANS_CHAN="$(./parseHFConfig.sh tc)"

PEER_NAME_1="$(./parseHFConfig.sh peer1)"
PEER_NAME_2="$(./parseHFConfig.sh peer2)"
PEER_NAME_3="$(./parseHFConfig.sh peer3)"

PEER_URL_1="$(./parseHFConfig.sh peerUrl1)"
PEER_URL_2="$(./parseHFConfig.sh peerUrl2)"
PEER_URL_3="$(./parseHFConfig.sh peerUrl3)"


cd setup-fabric-network
ls

# DOWN THE (IF EXIST) OLD NETWORK
docker-compose -f docker-compose.yml down

# TO REFRESH THE CHAINCODE CONTAINERS
# use when change chaincode
# docker rmi $(docker images -q)

# UP THE NEW NETWORK
# Network Topology:
# - FABRIC-CA
# - ORDERER
# - PEER 0
# - PEER 1
# - PEER 2
# - CLI
docker-compose -f docker-compose.yml up -d ca.example.com orderer.example.com peer0.org1.example.com peer1.org1.example.com peer2.org1.example.com


# don't works:
#docker-compose -f docker-compose.yml up -d ${CA_NAME}.${DOMAIN} $ORDER_NAME.$DOMAIN $PEER_NAME_1.$ORG_NAME.$DOMAIN $PEER_NAME_2.$ORG_NAME.$DOMAIN $PEER_NAME_3.$ORG_NAME.$DOMAIN
# -d = Detached mode: Run containers in the background
#docker-compose -f docker-compose.yml up 

# wait for Hyperledger Fabric to start
# in case of errors when running later commands, issue export FABRIC_START_TIMEOUT=<larger number>
export FABRIC_START_TIMEOUT=10
#echo ${FABRIC_START_TIMEOUT}
sleep ${FABRIC_START_TIMEOUT}

# PEER 0 CREATE THE TWO CHANNELS
# Create the channel channelone (peer0 create the channel)
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel create -o orderer.example.com:7050 -c servicech -f /etc/hyperledger/configtx/channelone.tx
# Join peer0.org1.example.com to the channel.
#docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel join -b servicech.block


# Create the channel channeltwo
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel create -o orderer.example.com:7050 -c transch -f /etc/hyperledger/configtx/channeltwo.tx
# Join peer0.org1.example.com to the channel.
#docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel join -b transch.block


# Copy channel channel.blocks from inside peer0 to (remove the old value first) temp directory in the project  ($PWD)
rm -rf ~/.temp/*
docker cp peer0.org1.example.com:/opt/gopath/src/github.com/hyperledger/fabric/ $PWD/temp/

# Copy channel channel.blocks from temp to peer1 and peer 2, then peer1 and peer2 can join the channels
docker cp $PWD/temp/fabric/ peer1.org1.example.com:/opt/gopath/src/github.com/hyperledger/
docker cp $PWD/temp/fabric/ peer2.org1.example.com:/opt/gopath/src/github.com/hyperledger/

# JOIN PEERS TO SERVICE CHANNEL
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel join -b servicech.block
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer1.org1.example.com peer channel join -b servicech.block
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer2.org1.example.com peer channel join -b servicech.block

# JOIN PEERS TO TRANSACTION CHANNEL
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer channel join -b transch.block
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer1.org1.example.com peer channel join -b transch.block
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer2.org1.example.com peer channel join -b transch.block
