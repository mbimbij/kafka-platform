#!/bin/bash

if [ $# != 0 ]; then
  echo -e "usage:\n./create-kafka-cluster.sh (no argument)"
  exit 1
fi

privateSubnetIds=$(aws cloudformation list-exports --region $AWS_REGION --query "Exports[?Name=='$NETWORKING_STACK_NAME::SubnetsPrivate'].Value" --output text)
numberOfBrokerNodes=$(echo $privateSubnetIds | tr ',' '\n' | wc -l)
volumeSize=8
kafkaKmsId=$(aws kms list-aliases --query "Aliases[?AliasName=='alias/aws/kafka'].TargetKeyId" --output text)
kafkaKmsArn=$(aws kms describe-key --key-id $kafkaKmsId --query "KeyMetadata.Arn" --output text)

echo "privateSubnetIds: $privateSubnetIds"
echo "numberOfBrokerNodes: $numberOfBrokerNodes"
echo "volumeSize: $volumeSize"
echo "kafkaKmsArn: $kafkaKmsArn"

aws cloudformation deploy   \
  --stack-name $KAFKA_STACK_NAME  \
  --template-file infra/kafka/kafka-cfn.yml \
  --capabilities CAPABILITY_NAMED_IAM   \
  --parameter-overrides \
    NetworkingStackName=$NETWORKING_STACK_NAME \
    KMSKafkaArn=$kafkaKmsArn \
    NumberOfBrokerNodes=$numberOfBrokerNodes \
    KakfaClusterName=$KAFKA_CLUSTER_NAME \
    VolumeSize=$volumeSize \
    BastionHostKeyName=$BASTION_HOST_KEY_NAME \
    AddEksSecurityGroup=$ADD_EKS_SECURITY_GROUP
