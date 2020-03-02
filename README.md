Auth0 Micronaut template
======================== 

[IN PROGRESS]

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
sam local start-api
Mounting ExampleFunction at http://127.0.0.1:3000/ [DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT]
Mounting ExampleFunction at http://127.0.0.1:3000/{proxy+} [DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT]
You can now browse to the above endpoints to invoke your functions. You do not need to restart/reload SAM CLI while working on your functions, changes will be reflected instantly/automatically. You only need to restart SAM CLI if you update your AWS SAM template
2020-03-02 22:16:20  * Running on http://127.0.0.1:3000/ (Press CTRL+C to quit)
```

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
