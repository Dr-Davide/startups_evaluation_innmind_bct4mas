#!/bin/bash
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
# Exit on first error
set -e

# start fresh!
if [ -d hfc-key-store ]; then rm -Rf hfc-key-store; fi
# rm -r hfc-key-store

# don't rewrite paths for Windows Git Bash users
export MSYS_NO_PATHCONV=1

starttime=$(date +%s)
CHAINCODE_SERV=service-cc
CHAINCODE_TRANS=transaction-cc

SERV_CHAN=servicech
TRANS_CHAN=transch


# docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)
# docker rmi $(docker images dev-* -q)
# launch network; create channel and join peer to channel
cd ../basic-network-test
./start.sh

# Now launch the CLI container in order to install, instantiate chaincode
docker-compose -f ./docker-compose.yml up -d cli

# cd ../basic-network-test
# docker-compose -f ./docker-compose.yml down -d cli
# docker-compose -f ./docker-compose.yml up -d cli

## FOR SERVICE-CC CHAINCODE INSTALL AND INSTANTIATE
docker exec -e "CORE_PEER_ADDRESS=peer0.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode install -n $CHAINCODE_SERV -v 1.0 -p github.com/$CHAINCODE_SERV
docker exec -e "CORE_PEER_ADDRESS=peer0.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode instantiate -o orderer.example.com:7050 -C $SERV_CHAN -n $CHAINCODE_SERV -v 1.0 -c '{"Args":[""]}' -P "OR ('Org1MSP.member','Org2MSP.member')"

docker exec -e "CORE_PEER_ADDRESS=peer1.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode install -n $CHAINCODE_SERV -v 1.0 -p github.com/$CHAINCODE_SERV
docker exec -e "CORE_PEER_ADDRESS=peer1.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode instantiate -o orderer.example.com:7050 -C $SERV_CHAN -n $CHAINCODE_SERV -v 1.0 -c '{"Args":[""]}' -P "OR ('Org1MSP.member','Org2MSP.member')"

docker exec -e "CORE_PEER_ADDRESS=peer2.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode install -n $CHAINCODE_SERV -v 1.0 -p github.com/$CHAINCODE_SERV
sleep 1
#docker exec -e "CORE_PEER_ADDRESS=peer2.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode instantiate -o orderer.example.com:7050 -C $SERV_CHAN -n $CHAINCODE_SERV -v 1.0 -c '{"Args":[""]}' -P "OR ('Org1MSP.member','Org2MSP.member')"

## FOR TRANSACTION-CC CHAINCODE INSTALL AND INSTANTIATE
docker exec -e "CORE_PEER_ADDRESS=peer0.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode install -n $CHAINCODE_TRANS -v 1.0 -p github.com/$CHAINCODE_TRANS
docker exec -e "CORE_PEER_ADDRESS=peer0.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode instantiate -o orderer.example.com:7050 -C $TRANS_CHAN -n $CHAINCODE_TRANS -v 1.0 -c '{"Args":[""]}' -P "OR ('Org1MSP.member','Org2MSP.member')"

docker exec -e "CORE_PEER_ADDRESS=peer1.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode install -n $CHAINCODE_TRANS -v 1.0 -p github.com/$CHAINCODE_TRANS
docker exec -e "CORE_PEER_ADDRESS=peer1.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode instantiate -o orderer.example.com:7050 -C $TRANS_CHAN -n $CHAINCODE_TRANS -v 1.0 -c '{"Args":[""]}' -P "OR ('Org1MSP.member','Org2MSP.member')"

docker exec -e "CORE_PEER_ADDRESS=peer2.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode install -n $CHAINCODE_TRANS -v 1.0 -p github.com/$CHAINCODE_TRANS
sleep 1
#docker exec -e "CORE_PEER_ADDRESS=peer2.org1.example.com:7051" -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode instantiate -o orderer.example.com:7050 -C $TRANS_CHAN -n $CHAINCODE_TRANS -v 1.0 -c '{"Args":[""]}' -P "OR ('Org1MSP.member','Org2MSP.member')"

sleep 10
# docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp" cli peer chaincode invoke -o orderer.example.com:7050 -C servicech -n fabcar -c '{"function":"initLedger","Args":[""]}'


# INVOKATION ON SERVICECH
docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer chaincode invoke -o orderer.example.com:7050 -C $SERV_CHAN -n $CHAINCODE_SERV -v 1.0 -c '{"Args":["initAgents"]}'
# sleep 5
# docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer chaincode invoke -o orderer.example.com:7050 -C $SERV_CHAN -n $CHAINCODE_SERV -v 1.0 -c '{"Args":["queryService","service3"]}'

#sleep 5
#docker exec -e "CORE_PEER_LOCALMSPID=Org1MSP" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@org1.example.com/msp" peer0.org1.example.com peer chaincode invoke -o orderer.example.com:7050 -C servicech -n bc-mas-cc -v 1.0 -c '{"Args":["createService","AGENT3","address3","service3","description3"]}'

printf "\nTotal setup execution time : $(($(date +%s) - starttime)) secs ...\n\n\n"
printf "If ther is no folder called node_modules, then run 'npm install'\n otherwise ignore this message\n"
printf "Then you can run the JaDE application'\n"
