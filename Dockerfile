FROM openjdk:8-jdk-alpine

COPY build/libs/*all.jar app.jar

ENTRYPOINT ["java", "-Xms128m", "-Xmx128m", "-jar", "app.jar"]
