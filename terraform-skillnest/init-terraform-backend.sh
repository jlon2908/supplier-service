#!/bin/bash

# Crear el bucket para los states si no existe
aws s3 mb s3://skillnest-terraform-states --region us-east-1 || true

# Crear la tabla de DynamoDB para locks
aws dynamodb create-table \
  --table-name terraform-locks \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region us-east-1 || true

# Habilitar versionamiento en el bucket
aws s3api put-bucket-versioning \
  --bucket skillnest-terraform-states \
  --versioning-configuration Status=Enabled \
  --region us-east-1