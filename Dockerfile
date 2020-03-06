FROM amazonlinux:2017.03.1.20170812 as base
ENV LANG=en_US.UTF-8
# Dependencies
RUN yum install -y \
    gcc \
    gcc-c++ \
    libc6-dev \
    zlib1g-dev \
    curl \
    bash \
    zlib \
    zlib-devel \
    zip \
    && rm -rf /var/cache/yum

FROM base as graalvm
WORKDIR /tmp
ENV GRAAL_VERSION 20.0.0
ENV GRAAL_TAR_FILENAME graalvm-ce-java8-linux-amd64-${GRAAL_VERSION}.tar.gz
ENV GRAAL_EXTRACTED_DIR graalvm-ce-java8-${GRAAL_VERSION}
# Download, extract and move Graal community edition
RUN curl --ipv4 -OL https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAAL_VERSION}/${GRAAL_TAR_FILENAME}
RUN tar -zxvf ${GRAAL_TAR_FILENAME} -C . \
    && mv ${GRAAL_EXTRACTED_DIR} /usr/lib/graalvm \
    && rm -f ${GRAAL_TAR_FILENAME}
ENV GRAALVM_HOME=/usr/lib/graalvm
ENV PATH $GRAALVM_HOME/bin:$PATH

FROM graalvm
# Install native-image https://www.graalvm.org/docs/reference-manual/native-image/
RUN gu install native-image
VOLUME ["/func"]
WORKDIR /func
ENTRYPOINT ["/usr/lib/graalvm/bin/native-image"]
CMD ["--help"]
