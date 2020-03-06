#!/bin/bash

DOCKER_IMAGE_NAME=graalvm
EXECUTABLE_NAME=serverbin
MAIN_CLASS=com.ivarprudnikov.auth0.Application
APP_JAR=build/libs/auth0-micronaut-template-1.0-all.jar

./gradlew clean build --info
if [[ $? -ne 0 ]]; then
    echo "Gradle build failed"
    exit 1
fi

if [[ "$(docker images -q ${DOCKER_IMAGE_NAME} 2> /dev/null)" == "" ]]; then
    docker build . -t ${DOCKER_IMAGE_NAME}
fi

docker run --rm -it -v $(pwd):/func ${DOCKER_IMAGE_NAME} \
  -H:+TraceClassInitialization \
  -H:+ReportExceptionStackTraces \
  -H:Name=${EXECUTABLE_NAME} \
  -H:Class=${MAIN_CLASS} \
  -H:IncludeResources=logback.xml\|application.yml \
  --no-server \
  --no-fallback \
  -cp ${APP_JAR}
