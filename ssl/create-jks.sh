#!/bin/bash

if [[ $# != 2 ]]; then
  echo -e "usage:\n./create-keystore.sh \$ENTITY_NAME \$HOSTNAME"
  exit 1
fi

PASSWORD=changeit
CSR_NAME=$1.key.csr
SIGNED_CSR_NAME=$1.key.signed.csr
KEYSTORE_NAME=$1.keystore.jks
TRUSTSTORE_NAME=$1.truststore.jks
SUPPLEMENT_HOSTNAME=$2

echo "##################################################################################################"
echo "# création du keystore du client"
echo "##################################################################################################"
keytool -keystore $1.keystore.jks \
  -alias $1 \
  -validity 365 \
  -genkey -keyalg RSA -storetype pkcs12 -storepass $PASSWORD \
  -dname "CN=$1, OU=TEST, O=SOME_ORG, L=SOME_PLACE, S=SOME_STATE, C=FR"

echo "##################################################################################################"
echo "# création d'un csr pour le client"
echo "##################################################################################################"
keytool -certreq -alias $1 -file $CSR_NAME -keystore $KEYSTORE_NAME -storepass $PASSWORD -ext SAN=DNS:localhost,DNS:$(hostname),DNS:$SUPPLEMENT_HOSTNAME,IP:127.0.0.1

echo "##################################################################################################"
echo "# signature du csr du client par le root ca"
echo "##################################################################################################"
if [ ! -f serial.txt ]; then echo 01 > serial.txt;  else echo "serial.txt already present" ; fi
if [ ! -f index.txt ]; then touch index.txt;  else echo "index.txt already present" ; fi
openssl ca -batch -config openssl-ca.cnf -policy signing_policy -extensions signing_req -out $SIGNED_CSR_NAME -infiles $CSR_NAME

echo "##################################################################################################"
echo "# création du truststore"
echo "##################################################################################################"
keytool -keystore $TRUSTSTORE_NAME -alias caroot -import -file cacert.pem -noprompt -storepass $PASSWORD

echo "##################################################################################################"
echo "# update du keystore du client avec le ca root et la clé signée"
echo "##################################################################################################"
keytool -keystore $KEYSTORE_NAME -alias caroot -import -file cacert.pem -noprompt -storepass $PASSWORD
keytool -keystore $KEYSTORE_NAME -alias $1 -import -file $SIGNED_CSR_NAME -noprompt -storepass $PASSWORD

rm $CSR_NAME
rm $SIGNED_CSR_NAME
