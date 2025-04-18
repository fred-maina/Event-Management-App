# Dockerfile
FROM openjdk:21-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN apt-get update && apt-get install -y curl


ENTRYPOINT ["java", "-jar", "/app.jar"]
