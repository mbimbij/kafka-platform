#!/bin/bash

if [ -z $1 ]; then
  echo -e "usage:\n./delete-all.sh \$APPLICATION_NAME"
  exit 1
fi

APPLICATION_NAME=$1
KAFKA_STACK_NAME=$APPLICATION_NAME-kafka
NETWORKING_STACK_NAME=$APPLICATION_NAME-network
source infra/infra.env

echo "##############################################################################"
echo "Deleting lambda authorizer pipeline and sam stack"
echo -e "##############################################################################\n"
aws cloudformation delete-stack --stack-name $APPLICATION_NAME-lambda-authorizer-sam-stack
./scripts/empty-s3-bucket.sh $AWS_REGION-$ACCOUNT_ID-$APPLICATION_NAME-lambda-authorizer-pipeline-bucket
sleep 2
aws cloudformation delete-stack --stack-name $APPLICATION_NAME-lambda-authorizer-pipeline

echo "##############################################################################"
echo "Deleting lambda authorizer pipeline and sam stack"
echo -e "##############################################################################\n"
aws cloudformation delete-stack --stack-name $APPLICATION_NAME-topic-actions-sam-stack
sleep 2
./scripts/empty-s3-bucket.sh $AWS_REGION-$ACCOUNT_ID-$APPLICATION_NAME-topic-actions-pipeline-bucket
aws cloudformation delete-stack --stack-name $APPLICATION_NAME-topic-actions-pipeline

# delete kafka stack
aws cloudformation delete-stack --stack-name $KAFKA_STACK_NAME

# delete networking stack
aws cloudformation delete-stack --stack-name $NETWORKING_STACK_NAME