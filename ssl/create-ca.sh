#!/bin/bash
openssl req -x509 -config openssl-ca.cnf -newkey rsa:4096 -sha256 -nodes -out cacert.pem -outform PEM
if [ ! -f serial.txt ]; then echo 01 > serial.txt;  else echo "serial.txt already present" ; fi
if [ ! -f index.txt ]; then touch index.txt;  else echo "index.txt already present" ; fi