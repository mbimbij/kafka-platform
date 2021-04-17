#!/bin/bash

if [ -z $1 ]; then
  echo -e "usage:\n./delete-keystore.sh \$ENTITY_NAME"
  exit 1
fi

KEYSTORE_NAME=$1.keystore.jks
TRUSTSTORE_NAME=$1.truststore.jks

rm $TRUSTSTORE_NAME
rm $KEYSTORE_NAME
rm index.txt*
rm serial.txt*
ls *.pem | grep -Ev '(cacert|cakey)' | xargs rm