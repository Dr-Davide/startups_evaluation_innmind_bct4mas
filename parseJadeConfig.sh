#!/bin/bash
#
# Author: Valerio Mattioli (valeriomattioli580@gmail.com)

#CONFIG_JADE_PATH=configJade.json
CONFIG_JADE_PATH=src/main/resources/configJade.json

get_all_config_file () {
	cat $CONFIG_JADE_PATH | jq '.'
}

get_org_name () {
	cat $CONFIG_JADE_PATH | jq '.["org"]'
}

if [[ $1 == org ]]
	then get_org_name
else get_all_config_file
fi

#for a in ${BASH_ARGV[*]} ; do
#	echo -n "$a"
#done
#echo "finito"

