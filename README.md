Auth0 Micronaut template
======================== 

[IN PROGRESS]

## About

Example should serve as a template when necessary to write a small service which uses JWT tokens signed by Auth0 server.

## Packaging and deployment

Utilizing AWS Lambda to make sure I'm not paying for idle time.

Example micronaut implementation: https://github.com/micronaut-projects/micronaut-aws/tree/master/examples/api-gateway-example

### Steps

1. Build jar

    ```shell script
    ./gradlew build
    ```

2. Create `S3 bucket` where application version is going to be uploaded before deployed to Cloudformation:

    ```shell script
    aws s3 mb s3://BUCKET_NAME
    ```

3. Package Lambda (uploads to S3):

    ```shell script
    sam package \
        --output-template-file packaged.yaml \
        --s3-bucket BUCKET_NAME
    ```

4. Create Cloudformation Stack and deploy your SAM resources.

    ```shell script
    sam deploy \
        --template-file packaged.yaml \
        --stack-name auth0-micronaut-template \
        --capabilities CAPABILITY_IAM
    ```

5. After deployment is complete you can run the following command to retrieve the API Gateway Endpoint URL:

    ```shell script
    aws cloudformation describe-stacks \
        --stack-name auth0-micronaut-template \
        --query 'Stacks[].Outputs[?OutputKey==`ApiUrl`]' \
        --output table
    ```

### Simpler way

Below `sam validate` requires AWS Credentials to be set up.

```shell script
sam validate && LAMBDA_S3_BUCKET_NAME=auth0-micronaut-template ./deploy
```

### Cloudformation cleanup

In order to delete our Serverless Application recently deployed you can use the following AWS CLI Command:

```shell script
aws cloudformation delete-stack --stack-name auth0-micronaut-template
```
