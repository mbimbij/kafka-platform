#!/bin/bash

if [ -z $1 ]; then
  echo -e "usage:\n./create-all.sh \$APPLICATION_NAME"
  exit 1
fi

export APPLICATION_NAME=$1
export NETWORKING_STACK_NAME=$APPLICATION_NAME-network
export KAFKA_CLUSTER_NAME=$APPLICATION_NAME-kafka-cluster
export KAFKA_STACK_NAME=$APPLICATION_NAME-kafka
source infra/infra.env

# create vpc and networking
aws cloudformation deploy \
  --stack-name $NETWORKING_STACK_NAME \
  --template-file infra/networking/networking-cfn-template.yml \
  --capabilities CAPABILITY_NAMED_IAM

# create kafka cluster
infra/kafka/create-kafka-cluster.sh
