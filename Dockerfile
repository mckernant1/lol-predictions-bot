FROM openjdk:8-jdk-alpine

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Xmx64m", "-jar", "app.jar"]
