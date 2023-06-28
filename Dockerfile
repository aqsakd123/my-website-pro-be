FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ./target/website-pro-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/static/privateKey.json /src/main/resources/static/privateKey.json
ENTRYPOINT ["java", "-jar", "/app.jar"]