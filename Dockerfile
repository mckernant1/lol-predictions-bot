FROM openjdk:8-jdk-alpine

COPY build/libs/*all.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
