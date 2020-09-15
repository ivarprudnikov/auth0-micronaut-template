FROM openjdk:14-alpine
COPY build/libs/*-all.jar server.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "server.jar"]
