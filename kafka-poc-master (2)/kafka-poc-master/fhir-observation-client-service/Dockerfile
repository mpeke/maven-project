FROM openjdk:8-jdk-alpine
WORKDIR /tmp
COPY target/fhir-observation-client-service-0.0.1-SNAPSHOT.jar /tmp
EXPOSE 8982
ENTRYPOINT ["java","-jar","fhir-observation-client-service-0.0.1-SNAPSHOT.jar"]