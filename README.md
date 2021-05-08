# kafka platform

# kafka platform

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

## zookeeper cluster vs zookeeper client - mutual TLS

Générer un keystore / truststore pour le cluster zookeeper avec le script `create-jks.sh`

Ensuite, définir les properties suivantes dans `config/zookeeper.properties` :

```properties
secureClientPort=2182
authProvider.x509=org.apache.zookeeper.server.auth.X509AuthenticationProvider
serverCnxnFactory=org.apache.zookeeper.server.NettyServerCnxnFactory
ssl.trustStore.location=/home/joseph/workspace/kafka-platform/ssl/zookeeper-server.truststore.jks
ssl.trustStore.password=changeit
ssl.keyStore.location=/home/joseph/workspace/kafka-platform/ssl/zookeeper-server.keystore.jks
ssl.keyStore.password=changeit
ssh.clientAuth=need
```

Dans les configs du client shell, rajouter les props suivantes:

```properties
zookeeper.clientCnxnSocket=org.apache.zookeeper.ClientCnxnSocketNetty
zookeeper.ssl.client.enable=true
zookeeper.ssl.protocol=TLSv1.3
zookeeper.ssl.truststore.location=/home/joseph/workspace/kafka-platform/ssl/zookeeper-client.truststore.jks
zookeeper.ssl.truststore.password=changeit
zookeeper.ssl.keystore.location=/home/joseph/workspace/kafka-platform/ssl/zookeeper-client.keystore.jks
zookeeper.ssl.keystore.password=changeit
```

Dans la config du broker Kafka, rajouter la config:

```properties
zookeeper.connect=localhost:2182
zookeeper.ssl.client.enable=true
zookeeper.clientCnxnSocket=org.apache.zookeeper.ClientCnxnSocketNetty
zookeeper.ssl.keystore.location=/home/joseph/workspace/kafka-platform/ssl/broker.keystore.jks
zookeeper.ssl.keystore.password=changeit
zookeeper.ssl.truststore.location=/home/joseph/workspace/kafka-platform/ssl/broker.truststore.jks
zookeeper.ssl.truststore.password=changeit
zookeeper.set.acl=true
```

## kafka broker vs kafka client - TLS - autentification du serveur uniquement

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

## kafka broker vs kafka client - multual TLS

### Création d'un truststore / keystore pour le client

Utilisation du script `create-jks.sh $CLIENT_NAME_AND_HOSTNAME`
En argument de ce script, le nom du client, qui sera utilisé:

- dans le nom des fichiers
- comme alias dans le keystore
- comme extension "SAN", soit un hostname supplémentaire, pour la vérification du hostname (qu'il est déconseillé de
  désactiver)

Rajouter les props suivantes au niveau du broker, dans `server.properties`

```properties
ssl.truststore.location=$PATH_TO_TRUSTSTORE
ssl.truststore.password=$TRUSTSTORE_PASSWORD
ssl.client.auth=required
```

## SASL/SCRAM - autentification des clients par username / password

### Création du username / password dans Zookeeper

Kafka Broker - Création du user `broker-admin` avec le mot de passe `pass`:

```shell
$KAFKA_HOME/bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type users --entity-name broker-admin --alter --add-config 'SCRAM-SHA-512=[password=pass]'
```

Kafka Producer - Création du user `sasl-producer` avec le mot de passe `producer-pass`:

```shell
./bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type users --entity-name sasl-producer --alter --add-config 'SCRAM-SHA-512=[password="producer-pass"]'
```

Kafka Consumer - Création du user `sasl-consumer` avec le mot de passe `consumer-pass`:

```shell
./bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type users --entity-name sasl-consumer --alter --add-config 'SCRAM-SHA-512=[password="consumer-pass"]'
```

Lister les utilisateurs

```shell
./bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type users --describe
```

## test en local

### invocation des lambdas:
1. lancer kafka et dynamodb via `docker-compose`
2. créer la table `topic-info` dans le dynamoDB local: 
```shell
aws dynamodb create-table \
    --table-name topic-info \
    --attribute-definitions AttributeName=topicName,AttributeType=S \
    --key-schema AttributeName=topicName,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --endpoint-url http://localhost:8000
```
3. invoquer la lambda via `sam-local`: 
  - créer un topic: `sam local invoke -t sam-template.yml ApplicationName-create-topic --docker-network kafka-platform_default --env-vars sam-local-env-vars.json -e src/test/resources/createTopic.json`  
  - récupérer les détails d'un topic: `sam local invoke -t sam-template.yml ApplicationName-topic-details --docker-network kafka-platform_default --env-vars sam-local-env-vars.json -e src/test/resources/getTopicDetails.json`
  - supprimer un topic: ``

4. invoquer l'API Gateway:
  - créer un topic: 
```shell
curl -H "Content-Type: application/json" -H "Authorization: eyJraWQiOiJRbitoOXNVTjVQWU1PVWt3SlZKR0RtZHd4YmpSbitNakhDN21rMHRnS3UwPSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoibGVzNHdfb0poVURvSzB3Ni1jWTEydyIsInN1YiI6IjcxOTM5YTA2LTdiMzktNGVhZC1hYjRhLWQwZDIxOWVlMDU4ZCIsImNvZ25pdG86Z3JvdXBzIjpbImRldiJdLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LXdlc3QtMy5hbWF6b25hd3MuY29tXC9ldS13ZXN0LTNfSE5KcllSaWoxIiwiY29nbml0bzp1c2VybmFtZSI6Impvc2VwaCIsImF1ZCI6IjV2MmpqbWl0ZmRybm9wbmJubWlzczVpc2VsIiwiZXZlbnRfaWQiOiIwOGQxY2ZhNi1lOGZlLTQ0ZmUtOGI4Ni02Yjg5MDMzOTIxNGIiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTYyMDEyMTMyMCwiZXhwIjoxNjIwMTI0OTIwLCJpYXQiOjE2MjAxMjEzMjAsImVtYWlsIjoiam9zZXBoLm1iaW1iaUBnbWFpbC5jb20ifQ.LgUr2GYY1lxj5Dfle5QXZKrQQUdcr9RX8qeCPPFMvpRk8o4qFPbnqNExVaDu7uPAzqUS9a9-shFL-ZKSa-k0REVsiTLFBohm2qto8JmD1sJCrFRGD4tCULUxN-_WaIp3zJK_nznPXRksRHvPHUv8rZycUcaI3guJx4oBaWDZUsmxoBH_aqmYLy2E9iStrg33DW6PIZziDed11ikEYYi_2LpE9gcvDZZgwN1kpddqtwan2yRO7086WbDDHXtyMG-AnrW8paOYkVR7T1jgCpW0-H8VkvjmtvAZjFOAwspiqqg1lv4_HyvDQnAY4U6XW5Y_vfSdaDwuzkYVNKjvaVQXjg" -X POST localhost:3000/topics -d "@src/test/resources/sam-local/createTopic.json" -w "\n"
```
  - récupérer les infos d'un topic:
```shell
curl -H "Authorization: eyJraWQiOiJRbitoOXNVTjVQWU1PVWt3SlZKR0RtZHd4YmpSbitNakhDN21rMHRnS3UwPSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoibGVzNHdfb0poVURvSzB3Ni1jWTEydyIsInN1YiI6IjcxOTM5YTA2LTdiMzktNGVhZC1hYjRhLWQwZDIxOWVlMDU4ZCIsImNvZ25pdG86Z3JvdXBzIjpbImRldiJdLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LXdlc3QtMy5hbWF6b25hd3MuY29tXC9ldS13ZXN0LTNfSE5KcllSaWoxIiwiY29nbml0bzp1c2VybmFtZSI6Impvc2VwaCIsImF1ZCI6IjV2MmpqbWl0ZmRybm9wbmJubWlzczVpc2VsIiwiZXZlbnRfaWQiOiIwOGQxY2ZhNi1lOGZlLTQ0ZmUtOGI4Ni02Yjg5MDMzOTIxNGIiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTYyMDEyMTMyMCwiZXhwIjoxNjIwMTI0OTIwLCJpYXQiOjE2MjAxMjEzMjAsImVtYWlsIjoiam9zZXBoLm1iaW1iaUBnbWFpbC5jb20ifQ.LgUr2GYY1lxj5Dfle5QXZKrQQUdcr9RX8qeCPPFMvpRk8o4qFPbnqNExVaDu7uPAzqUS9a9-shFL-ZKSa-k0REVsiTLFBohm2qto8JmD1sJCrFRGD4tCULUxN-_WaIp3zJK_nznPXRksRHvPHUv8rZycUcaI3guJx4oBaWDZUsmxoBH_aqmYLy2E9iStrg33DW6PIZziDed11ikEYYi_2LpE9gcvDZZgwN1kpddqtwan2yRO7086WbDDHXtyMG-AnrW8paOYkVR7T1jgCpW0-H8VkvjmtvAZjFOAwspiqqg1lv4_HyvDQnAY4U6XW5Y_vfSdaDwuzkYVNKjvaVQXjg" -X GET localhost:3000/topics/someTopic -d "@src/test/resources/sam-local/createTopic.json" -w "\n"
```

# :gb: Project Description

The goal of this project is to train in so-called "platform engineering", applied to Kafka: set up an API allowing
developpers to create, monitor and manage their topics in a self service fashion, inspired by a similar platform i had
the opportunity to use at work.

More pet projects on that topicDatabaseInfo to come.