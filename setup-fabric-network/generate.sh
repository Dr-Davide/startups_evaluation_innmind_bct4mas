#!/bin/sh
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
export PATH=$GOPATH/src/github.com/hyperledger/fabric/build/bin:${PWD}/../bin:${PWD}:$PATH
export FABRIC_CFG_PATH=${PWD}
CHANNEL_ONE=servicech
CHANNEL_TWO=transch

# remove previous crypto material and config transactions
rm -fr config/*
rm -fr crypto-config/*

# generate crypto material
cryptogen generate --config=./crypto-config.yaml
if [ "$?" -ne 0 ]; then
  echo "Failed to generate crypto material..."
  exit 1
fi

# generate genesis block for orderer
configtxgen -profile OneOrgOrdererGenesis -outputBlock ./config/genesis.block
if [ "$?" -ne 0 ]; then
  echo "Failed to generate orderer genesis block..."
  exit 1
fi

# generate channel servicech configuration transaction
configtxgen -profile OneOrgChannel -outputCreateChannelTx ./config/channelone.tx -channelID $CHANNEL_ONE
if [ "$?" -ne 0 ]; then
  echo "Failed to generate channel configuration transaction..."
  exit 1
fi

# generate channel transch configuration transaction
configtxgen -profile OneOrgChannel -outputCreateChannelTx ./config/channeltwo.tx -channelID $CHANNEL_TWO
if [ "$?" -ne 0 ]; then
  echo "Failed to generate channel configuration transaction..."
  exit 1
fi

# generate anchor peer transaction for servicech
configtxgen -profile OneOrgChannel -outputAnchorPeersUpdate ./config/Org1MSPanchorsone.tx -channelID $CHANNEL_ONE -asOrg Org1MSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for Org1MSP..."
  exit 1
fi

# generate anchor peer transaction for transch
configtxgen -profile OneOrgChannel -outputAnchorPeersUpdate ./config/Org1MSPanchorstwo.tx -channelID $CHANNEL_TWO -asOrg Org1MSP
if [ "$?" -ne 0 ]; then
  echo "Failed to generate anchor peer update for Org1MSP..."
  exit 1
fi
