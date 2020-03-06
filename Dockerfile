FROM oracle/graalvm-ce:20.0.0-java8 as graalvm
# Install native-image https://www.graalvm.org/docs/reference-manual/native-image/
RUN gu install native-image
VOLUME ["/func"]
WORKDIR /func
# main command to run when running the image
ENTRYPOINT ["native-image"]
# default args to entrypoint will print help
CMD ["--help"]

# Build it
# $ docker build . -t graalvm
#
# Convert app to native image in current dir
# $ docker run --rm -it -v $(pwd):/func graalvm \
#    -H:+TraceClassInitialization \
#    -H:+ReportExceptionStackTraces \
#    -H:Name=mygeneratedbin \
#    -H:Class=com.foo.bar.Application \
#    -H:IncludeResources=logback.xml\|application.yml \
#    --no-server \
#    --no-fallback \
#    -cp build/libs/foo-bar-1.0-all.jar
#
# Run image and use it through bash prompt
# $ docker run -it --entrypoint=/bin/bash -v $(pwd):/func graalvm
# bash-4.2#
