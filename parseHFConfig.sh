#!/bin/bash
#
# Parser of JSON Configuration file of the HF network
#
# Author: Valerio Mattioli (valeriomattioli580@gmail.com)

#CONFIG_HF_PATH=configHF.json
CONFIG_HF_PATH=src/main/resources/configHF.json

get_all_config_file () {
	cat $CONFIG_HF_PATH | jq '.'
}

get_org_name () {
	cat $CONFIG_HF_PATH | jq -r '.["orgName"]'
}

get_domain () {
	cat $CONFIG_HF_PATH | jq -r '.["domain"]'
}

get_fabric_user_path () {
	cat $CONFIG_HF_PATH | jq -r '.["fabricUserPath"]'
}

get_ca_name () {
	cat $CONFIG_HF_PATH | jq -r '.["certificationAuthorityName"]'
}

get_service_channel_name () {
	cat $CONFIG_HF_PATH | jq -r '.["channelNames"][0]'
}

get_transaction_channel_name () {
	cat $CONFIG_HF_PATH | jq -r '.["channelNames"][1]'
}

get_service_chaincode () {
	cat $CONFIG_HF_PATH | jq -r '.["chaincodeNames"][0]'
}

get_transaction_chaincode () {
	cat $CONFIG_HF_PATH | jq  -r '.["chaincodeNames"][1]'
}

get_servicemarbles_chaincode () {
	cat $CONFIG_HF_PATH | jq  -r '.["chaincodeNames"][2]'
}

get_transactionmarbles_chaincode () {
	cat $CONFIG_HF_PATH | jq  -r '.["chaincodeNames"][3]'
}

get_trustreputationledger_chaincode () {
	cat $CONFIG_HF_PATH | jq  -r '.["chaincodeNames"][4]'
}

get_orderer_name () {
	cat $CONFIG_HF_PATH | jq -r '.["ordererNames"][0]'
}

get_orderer_grpc_url () {
	cat $CONFIG_HF_PATH | jq -r '.["ordererGrpcURLs"][0]'
}

get_peer_names () {
	cat $CONFIG_HF_PATH | jq -r '.["peerNames"]'
}

get_peer1_name () {
	cat $CONFIG_HF_PATH | jq -r '.["peerNames"][0]'
}
get_peer2_name () {
	cat $CONFIG_HF_PATH | jq -r '.["peerNames"][1]'
}
get_peer3_name () {
	cat $CONFIG_HF_PATH | jq -r '.["peerNames"][2]'
}

get_peer1_grpc_url () {
	cat $CONFIG_HF_PATH | jq -r '.["peerGrpcURLs"][0]'
}

get_peer2_grpc_url () {
	cat $CONFIG_HF_PATH | jq -r '.["peerGrpcURLs"][1]'
}

get_peer3_grpc_url () {
	cat $CONFIG_HF_PATH | jq -r '.["peerGrpcURLs"][2]'
}
case "$1" in
org) 
get_org_name
;;
domain) 
get_domain
;;
ca) 
get_ca_name
;;
orderer) 
get_orderer_name
;;
ordererUrl) 
get_orderer_grpc_url
;;
sc)
get_service_channel_name
;;
tc)
get_transaction_channel_name
;;
scc)
get_service_chaincode
;;
tcc)
get_transaction_chaincode
;;
smcc)
get_servicemarbles_chaincode
;;
tmcc)
get_transactionmarbles_chaincode
;;
trustreputation)
get_trustreputationledger_chaincode
;;
peers)
get_peer_names
;;
peer1)
get_peer1_name
;;
peer2)
get_peer2_name
;;
peer3)
get_peer3_name
;;
peerUrl1)
get_peer1_grpc_url
;;
peerUrl2)
get_peer2_grpc_url
;;
peerUrl3)
get_peer3_grpc_url
;;


*)
get_all_config_file
;;
esac
