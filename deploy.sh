#!/bin/sh -e

BUCKET_NAME="${LAMBDA_S3_BUCKET_NAME:?S3 bucket name required}"

if aws s3 ls s3://${BUCKET_NAME} 2>&1 | grep -q 'NoSuchBucket'; then
    aws s3 mb s3://${BUCKET_NAME}
fi

sam package --output-template-file packaged.yaml --s3-bucket ${BUCKET_NAME}

sam deploy --template-file packaged.yaml \
        --stack-name auth0-micronaut-template \
        --capabilities CAPABILITY_IAM
