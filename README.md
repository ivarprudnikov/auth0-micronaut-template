Auth0 Micronaut template
======================== 

[![CI](https://github.com/ivarprudnikov/auth0-micronaut-template/actions/workflows/deploy.yml/badge.svg)](https://github.com/ivarprudnikov/auth0-micronaut-template/actions/workflows/deploy.yml) 
[![GitHub issues](https://img.shields.io/github/issues/ivarprudnikov/auth0-micronaut-template.svg)](https://github.com/ivarprudnikov/auth0-micronaut-template/issues)
[![GitHub last commit](https://img.shields.io/github/last-commit/ivarprudnikov/auth0-micronaut-template.svg)](https://github.com/ivarprudnikov/auth0-micronaut-template/commits/master)

## Live

App is deployed to AWS Lambda:

|      | JAR with StreamLambdaHandler | Graal VM native binary with custom runtime |
| ---- | ---------------------------- | ------------------------------------------ |
| Branch | `master`                     | `aws-lambda-graalvm`                       |
| URL | https://te60oj36jd.execute-api.eu-west-1.amazonaws.com/Prod/ | https://995oz2jt04.execute-api.eu-west-1.amazonaws.com/Prod/ |

Client application using this API: 

| Website URL | Github repository |
| ---------------------------- | ------------------------------------------ |
| https://ivarprudnikov.github.io/react-auth0-template/ | https://github.com/ivarprudnikov/react-auth0-template |


## About

Example should serve as a template when necessary to write a small service which uses JWT tokens signed by Auth0 server.

Blog post explaining implementation - [ivarprudnikov.com/micronaut-kotlin-jwt-secured-api-aws-lambda](https://www.ivarprudnikov.com/micronaut-kotlin-jwt-secured-api-aws-lambda/)

JWT config (by using JWKS) is visible in [application.yml](/src/main/resources/application.yml#L7). Controller endpoint then uses [`@Secured` annotation](src/main/kotlin/com/ivarprudnikov/auth0/IndexController.kt#L29).

### API endpoints

- `/` - returns status `UP` in plain text
- `/me` - returns user `io.micronaut.security.authentication.Authentication` in  JSON response

## Development

### Running locally

Start [Micronaut server](https://docs.micronaut.io/latest/guide/index.html#creatingServer)

```shell script
./gradlew run
```

Then to check status

```shell script
curl http://localhost:8080
UP%
```

### Running with SAM

This requires Docker to be running locally and `sam` CLI installed. 
https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-cli-command-reference-sam-local-start-api.html

```shell script
sam local start-api --template template.yaml --port 8080

....

Mounting ExampleFunction at http://127.0.0.1:8080/ [DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT]
Mounting ExampleFunction at http://127.0.0.1:8080/{proxy+} [DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT]
You can now browse to the above endpoints to invoke your functions. You do not need to restart/reload SAM CLI while working on your functions, changes will be reflected instantly/automatically. You only need to restart SAM CLI if you update your AWS SAM template
2020-03-02 22:16:20  * Running on http://127.0.0.1:8080/ (Press CTRL+C to quit)
```

### Invoking function with events

Instead of running the application and then trying to invoke API in the browser you could
just invoke Lambda function with pre-built events:
```
sam local invoke --template template.yaml --event aws-test-events/options-me.json --skip-pull-image
```

## Packaging and deployment

Utilizing AWS Lambda to make sure I'm not paying for idle time.

Example micronaut implementations: 
- https://github.com/micronaut-projects/micronaut-aws/tree/master/examples/api-gateway-example
- https://github.com/micronaut-guides/micronaut-function-aws-lambda
Example micronaut implementations using Graal VM (native binaries): 
- https://github.com/awslabs/aws-serverless-java-container/tree/master/samples/micronaut/pet-store
- https://github.com/micronaut-guides/micronaut-creating-first-graal-app

### Steps

1. Build jar

    ```shell script
    ./gradlew clean build --info
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
        --stack-name STACK_NAME \
        --capabilities CAPABILITY_IAM
    ```

5. After deployment is complete you can run the following command to retrieve the API Gateway Endpoint URL:

    ```shell script
    aws cloudformation describe-stacks \
        --stack-name STACK_NAME \
        --query 'Stacks[].Outputs[?OutputKey==`ApiUrl`]' \
        --output table
    ```

### Simpler way

Below `sam validate` requires AWS Credentials to be set up.

```shell script
sam validate && LAMBDA_APP_NAME=auth0-micronaut-template bash build-deploy.sh
```

### Cloudformation cleanup

In order to delete our Serverless Application recently deployed you can use the following AWS CLI Command:

```shell script
aws cloudformation delete-stack --stack-name STACK_NAME
```
