#!/bin/sh -e

./build-function-zip.sh

BUCKET_NAME="${LAMBDA_S3_BUCKET_NAME:?S3 bucket name required}"

# Create S3 bucket used for deployment if one does not yet exist
if aws s3 ls s3://${BUCKET_NAME} 2>&1 | grep -q 'NoSuchBucket'; then
    aws s3 mb s3://${BUCKET_NAME}
fi

sam package --output-template-file packaged.yaml --s3-bucket ${BUCKET_NAME}
sam deploy --template-file packaged.yaml \
        --stack-name ${BUCKET_NAME} \
        --capabilities CAPABILITY_IAM
