FROM openjdk:14-alpine
COPY build/libs/auth0-micronaut-template-*-all.jar auth0-micronaut-template.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "auth0-micronaut-template.jar"]
