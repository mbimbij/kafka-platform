# kafka platform

# Polyglot Microservices, #1

:fr: Sommaire / :gb: Table of Contents
=================

<!--ts-->

- [:fr: Description du projet](#fr-description-du-projet)
- [:gb: Project Description](#gb-project-description)

---

# :fr: Description du projet

Le but de ce projet est de s'entraîner au "platform engineering", appliquée à Kafka: mettre en place une API permettant 
à des développeurs de créer, monitorer et administrer leurs topics en "self-service", sur le modèle d'une plate-forme 
similaire que j'ai eu l'occasion d'utiliser dans un contexte professionnel.

D'autres mini-projets persos sur cette thématique à venir.

## TLS - autentification du serveur uniquement

### création de la paire de clé faisant office de ca root
```shell
openssl req -x509 -config openssl-ca.cnf -newkey rsa:4096 -sha256 -nodes -out cacert.pem -outform PEM
```

### création des truststore pour le serveur et le client
```shell
keytool -keystore client.truststore.jks -alias caroot -import -file cacert.pem -storepass changeit
keytool -keystore server.truststore.jks -alias caroot -import -file cacert.pem -storepass changeit
```

### création du keystore du serveur
```shell
keytool -keystore server.keystore.jks -alias localhost -validity 365 -genkey -keyalg RSA -storetype pkcs12 -storepass changeit
```

### création d'un csr pour le serveur
```shell
keytool -certreq -alias localhost -file server.key.csr -keystore server.keystore.jks -storepass changeit -ext SAN=DNS:localhost,DNS:$(hostname),IP:127.0.0.1
```

### signature du csr du serveur par le root ca
```shell
echo 01 > serial.txt
touch index.txt
openssl ca -config openssl-ca.cnf -policy signing_policy -extensions signing_req -out server.key.signed.cer -infiles server.key.csr
```

### update du keystore du serveur avec le ca root et la clé signée
```shell
keytool -keystore server.keystore.jks -alias caroot -import -file cacert.pem -storepass changeit
keytool -keystore server.keystore.jks -alias localhost -import -file server.key.signed.cer -storepass changeit
```


# :gb: Project Description

The goal of this project is to train in so-called "platform engineering", applied to Kafka: set up an API allowing 
developpers to create, monitor and manage their topics in a self service fashion, inspired by a similar platform i 
had the opportunity to use at work.

More pet projects on that topic to come.