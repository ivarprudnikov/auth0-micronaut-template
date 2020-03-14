#!/bin/sh -e

NAME="${LAMBDA_APP_NAME:?Unique app name required}"

./build-function-zip.sh

# Create S3 bucket used for deployment if one does not yet exist
if aws s3 ls s3://${NAME} 2>&1 | grep -q 'NoSuchBucket'; then
    aws s3 mb s3://${NAME}
fi

sam package --output-template-file packaged.yaml --s3-bucket ${NAME}
sam deploy --template-file packaged.yaml \
        --stack-name ${NAME} \
        --capabilities CAPABILITY_IAM
