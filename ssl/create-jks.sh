#!/bin/bash

if [[ $# != 1 ]]; then
  echo -e "usage:\n./create-keystore.sh \$ENTITY_NAME"
  exit 1
fi

ENTITY_NAME=$1
PASSWORD=changeit
CSR_NAME=$ENTITY_NAME.key.csr
SIGNED_CSR_NAME=$ENTITY_NAME.key.signed.csr
KEYSTORE_NAME=$ENTITY_NAME.keystore.jks
TRUSTSTORE_NAME=$ENTITY_NAME.truststore.jks

echo "##################################################################################################"
echo "# création du keystore du client"
echo "##################################################################################################"
keytool -keystore $ENTITY_NAME.keystore.jks \
  -alias $ENTITY_NAME \
  -validity 365 \
  -genkey -keyalg RSA -storetype pkcs12 -storepass $PASSWORD \
  -dname "CN=$ENTITY_NAME, OU=TEST, O=SOME_ORG, L=SOME_PLACE, S=SOME_STATE, C=FR"

echo "##################################################################################################"
echo "# création d'un csr pour le client"
echo "##################################################################################################"
keytool -certreq -alias $ENTITY_NAME -file $CSR_NAME -keystore $KEYSTORE_NAME -storepass $PASSWORD -ext SAN=DNS:localhost,DNS:$(hostname),DNS:$ENTITY_NAME,IP:127.0.0.1

echo "##################################################################################################"
echo "# signature du csr du client par le root ca"
echo "##################################################################################################"
openssl ca -batch -config openssl-ca.cnf -policy signing_policy -extensions signing_req -out $SIGNED_CSR_NAME -infiles $CSR_NAME

echo "##################################################################################################"
echo "# création du truststore"
echo "##################################################################################################"
keytool -keystore $TRUSTSTORE_NAME -alias caroot -import -file cacert.pem -noprompt -storepass $PASSWORD

echo "##################################################################################################"
echo "# update du keystore du client avec le ca root et la clé signée"
echo "##################################################################################################"
keytool -keystore $KEYSTORE_NAME -alias caroot -import -file cacert.pem -noprompt -storepass $PASSWORD
keytool -keystore $KEYSTORE_NAME -alias $ENTITY_NAME -import -file $SIGNED_CSR_NAME -noprompt -storepass $PASSWORD

rm $CSR_NAME
rm $SIGNED_CSR_NAME
