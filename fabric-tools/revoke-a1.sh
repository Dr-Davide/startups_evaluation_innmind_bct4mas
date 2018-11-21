docker exec ca.example.com fabric-ca-client enroll -u http://admin:adminpw@ca.example.com:7054

docker exec ca.example.com fabric-ca-client revoke -e a1

docker exec ca.example.com fabric-ca-client gencrl

docker cp ca.example.com:/etc/hyperledger/fabric-ca-server/msp/crls/crl.pem $PWD/fabric-tools/tmp/crl.pem

docker exec cli peer channel fetch config config_block.pb -c servicech -o orderer.example.com:7050 

docker cp cli:/opt/gopath/src/github.com/hyperledger/fabric/peer/config_block.pb $PWD/fabric-tools/tmp/config_block.pb


# Update config block payload file path
CONFIG_UPDATE_ENVELOPE_FILE=$PWD/fabric-tools/tmp/config_update_as_envelope.pb

CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp

# Start the configtxlator
configtxlator start & configtxlator_pid=$!

sleep 5

cd $PWD/fabric-tools/tmp

CTLURL=http://127.0.0.1:7059

# Convert the config block protobuf to JSON
curl -X POST --data-binary @config_block.pb $CTLURL/protolator/decode/common.Block > config_block.json


# Extract the config from the config block
jq .data.data[0].payload.data.config config_block.json > config.json

# Update crl in the config json
crl=$(cat crl*.pem | base64 | tr -d '\n')
cat config.json | jq '.channel_group.groups.Application.groups.Org1MSP.values.MSP.value.config.revocation_list = ["'"${crl}"'"]' > updated_config.json

# Create the config diff protobuf
curl -X POST --data-binary @config.json $CTLURL/protolator/encode/common.Config > config.pb
curl -X POST --data-binary @updated_config.json $CTLURL/protolator/encode/common.Config > updated_config.pb
curl -X POST -F original=@config.pb -F updated=@updated_config.pb $CTLURL/configtxlator/compute/update-from-configs -F channel=servicech > config_update.pb

# Convert the config diff protobuf to JSON
curl -X POST --data-binary @config_update.pb $CTLURL/protolator/decode/common.ConfigUpdate > config_update.json

# Create envelope protobuf container config diff to be used in the "peer channel update" command to update the channel configuration block
echo '{"payload":{"header":{"channel_header":{"channel_id":"servicech", "type":2}},"data":{"config_update":'$(cat config_update.json)'}}}' > config_update_as_envelope.json
curl -X POST --data-binary @config_update_as_envelope.json $CTLURL/protolator/encode/common.Envelope > $CONFIG_UPDATE_ENVELOPE_FILE


# Stop configtxlator
kill $configtxlator_pid

cd ..

docker cp $PWD/tmp/config_update_as_envelope.pb cli:/opt/gopath/src/github.com/hyperledger/fabric/peer/config_update_as_envelope.pb

docker cp $PWD/tmp/crl.pem cli:/opt/gopath/src/github.com/hyperledger/fabric/clr.pem

docker exec cli peer channel update -f config_update_as_envelope.pb -c servicech -o orderer.example.com:7050