Auth0 Micronaut template
======================== 

[![Build Status](https://travis-ci.org/ivarprudnikov/auth0-micronaut-template.svg?branch=master)](https://travis-ci.org/ivarprudnikov/auth0-micronaut-template)

## Live

App is deployed to AWS Lambda: https://995oz2jt04.execute-api.eu-west-1.amazonaws.com/Prod/

## About

Example should serve as a template when necessary to write a small service which uses JWT tokens signed by Auth0 server.

JWT config (by using JWKS) is visible in [application.yml](/src/main/resources/application.yml#L7). Controller endpoint then uses [`@Secured` annotation](src/main/kotlin/com/ivarprudnikov/auth0/IndexController.kt#L29).

### API endpoints

- `/` - returns status `UP` in plain text
- `/me` - returns user `io.micronaut.security.authentication.Authentication` in  JSON response

## Development

### Running locally

Start [Micronaut server](https://docs.micronaut.io/latest/guide/index.html#creatingServer)

```shell script
./geadlew run
```

Then to check status

```shell script
curl http://localhost:8080
UP%
```

### Running with SAM

This requires Docker to be running locally and `sam` CLI installed.

```shell script
./build-sam-local.sh

....

Mounting ExampleFunction at http://127.0.0.1:3000/ [DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT]
Mounting ExampleFunction at http://127.0.0.1:3000/{proxy+} [DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT]
You can now browse to the above endpoints to invoke your functions. You do not need to restart/reload SAM CLI while working on your functions, changes will be reflected instantly/automatically. You only need to restart SAM CLI if you update your AWS SAM template
2020-03-02 22:16:20  * Running on http://127.0.0.1:3000/ (Press CTRL+C to quit)
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
    ./gradlew build
    ```

2. Convert jar to native binary image to reduce cold startup times.

    - First build a docker image that contains GraalVM `native-image` installed:

    ```shell script
    docker build . -t MY_IMAGE_TAG
    ```

    - Convert jar file to native image:

    ```shell script
    docker run --rm -it -v $(pwd):/func MY_IMAGE_TAG \
      -H:+TraceClassInitialization \
      -H:+ReportExceptionStackTraces \
      -H:-AllowVMInspection \
      -H:Name=serverbin \
      -H:Class=io.micronaut.function.aws.runtime.MicronautLambdaRuntime \
      -H:IncludeResources=logback.xml\|application.yml \
      --no-server \
      --no-fallback \
      -cp build/libs/auth0-micronaut-template-1.0-all.jar
    ```

3. Create `S3 bucket` where application version is going to be uploaded before deployed to Cloudformation:

    ```shell script
    aws s3 mb s3://BUCKET_NAME
    ```

4. Package Lambda (uploads to S3):

    ```shell script
    sam package \
        --output-template-file packaged.yaml \
        --s3-bucket BUCKET_NAME
    ```

5. Create Cloudformation Stack and deploy your SAM resources.

    ```shell script
    sam deploy \
        --template-file packaged.yaml \
        --stack-name STACK_NAME \
        --capabilities CAPABILITY_IAM
    ```

6. After deployment is complete you can run the following command to retrieve the API Gateway Endpoint URL:

    ```shell script
    aws cloudformation describe-stacks \
        --stack-name STACK_NAME \
        --query 'Stacks[].Outputs[?OutputKey==`ApiUrl`]' \
        --output table
    ```

### Simpler way

Below `sam validate` requires AWS Credentials to be set up.

```shell script
sam validate && LAMBDA_APP_NAME=auth0-micronaut-graal-template bash build-deploy.sh
```

### Cloudformation cleanup

In order to delete our Serverless Application recently deployed you can use the following AWS CLI Command:

```shell script
aws cloudformation delete-stack --stack-name STACK_NAME
```
