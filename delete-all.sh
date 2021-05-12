#!/bin/bash

if [ -z $1 ]; then
  echo -e "usage:\n./delete-all.sh \$APPLICATION_NAME"
  exit 1
fi

APPLICATION_NAME=$1
KAFKA_STACK_NAME=$APPLICATION_NAME-kafka
NETWORKING_STACK_NAME=$APPLICATION_NAME-network
source infra/infra.env

echo -e "##############################################################################"
echo -e "Deleting sam stack"
echo -e "##############################################################################\n"
aws cloudformation delete-stack --stack-name $APPLICATION_NAME-topic-actions-sam-stack
aws cloudformation wait stack-delete-complete --stack-name $APPLICATION_NAME-topic-actions-sam-stack

echo -e "##############################################################################"
echo -e "Deleting pipeline"
echo -e "##############################################################################\n"
./scripts/empty-s3-bucket.sh $AWS_REGION-$ACCOUNT_ID-$APPLICATION_NAME-topic-actions-pipeline-bucket
aws cloudformation delete-stack --stack-name $APPLICATION_NAME-topic-actions-pipeline
aws cloudformation wait stack-delete-complete --stack-name $APPLICATION_NAME-topic-actions-pipeline

echo -e "##############################################################################"
echo -e "Deleting kafka cluster"
echo -e "##############################################################################\n"
# delete kafka stack
aws cloudformation delete-stack --stack-name $KAFKA_STACK_NAME
aws cloudformation wait stack-delete-complete --stack-name $KAFKA_STACK_NAME

echo -e "##############################################################################"
echo -e "Deleting network"
echo -e "##############################################################################\n"
# delete networking stack
aws cloudformation delete-stack --stack-name $NETWORKING_STACK_NAME
aws cloudformation wait stack-delete-complete --stack-name $NETWORKING_STACK_NAME