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

echo -e  "\n##############################################################################"
echo "creating networking: VPC, etc."
echo -e "##############################################################################\n"
# create vpc and networking
aws cloudformation deploy \
  --stack-name $NETWORKING_STACK_NAME \
  --template-file infra/networking/networking-cfn-template.yml \
  --capabilities CAPABILITY_NAMED_IAM

echo -e  "\n##############################################################################"
echo "creating cicd pipeline for lambda authorizer platform"
echo -e "##############################################################################\n"
# create cicd pipeline for topics crud actions
aws cloudformation deploy    \
  --stack-name $APPLICATION_NAME-lambda-authorizer-pipeline   \
  --template-file lambda-authorizer/pipeline-stack.yml    \
  --capabilities CAPABILITY_NAMED_IAM   \
  --parameter-overrides     \
    ApplicationName=$APPLICATION_NAME-lambda-authorizer     \
    ApplicationDirectoryName=lambda-authorizer \
    GithubRepo=$GITHUB_REPO     \
    SamStackName=$APPLICATION_NAME-lambda-authorizer-sam-stack

echo -e  "\n##############################################################################"
echo "creating cicd pipeline for topic CRUD actions platform"
echo -e "##############################################################################\n"
# create cicd pipeline for topics crud actions
aws cloudformation deploy    \
  --stack-name $APPLICATION_NAME-topic-actions-pipeline   \
  --template-file topic-actions/pipeline-stack.yml    \
  --capabilities CAPABILITY_NAMED_IAM   \
  --parameter-overrides     \
    ApplicationName=$APPLICATION_NAME-topic-actions     \
    ApplicationDirectoryName=topic-actions \
    GithubRepo=$GITHUB_REPO     \
    SamStackName=$APPLICATION_NAME-topic-actions-sam-stack \
    KakfaClusterName=$KAFKA_CLUSTER_NAME

#echo-e  "\n##############################################################################"
#echo "creating kafka cluster"
#echo -e "##############################################################################\n"
## create kafka cluster
#infra/kafka/create-kafka-cluster.sh

echo -e  "\n##############################################################################"
echo "kafka cluster, kafka topic CRUD actions platform pipeline, lambda authorizer pipeline creation done"
echo -e "##############################################################################\n"

#echo "##############################################################################"
#echo "creating kafka topic 'test'"
#echo "##############################################################################"
#kafkaClusterArn=$(aws kafka list-clusters --query "ClusterInfoList[?ClusterName=='$KAFKA_CLUSTER_NAME'].ClusterArn" --output text)
#kafkaClusterBootstrapBrokers=$(aws kafka get-bootstrap-brokers --cluster-arn $kafkaClusterArn --query "BootstrapBrokerString" --output text | awk -F ',' '{print $1}')
#bastionHostPublicDnsName=$(aws cloudformation describe-stacks --stack-name $KAFKA_STACK_NAME --query "Stacks[].Outputs[?OutputKey=='BastionHostPublicDnsName'][].OutputValue" --output text)
#ssh -o StrictHostKeyChecking=no ubuntu@$bastionHostPublicDnsName "./kafka_2.13-2.7.0/bin/kafka-topics.sh --bootstrap-server $kafkaClusterBootstrapBrokers --create --topic test --partitions 4"
