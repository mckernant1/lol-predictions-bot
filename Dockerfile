FROM openjdk:8-jdk-alpine

COPY build/libs/lol-predictions-bot-1.0-SNAPSHOT-all.jar app.jar

ENTRYPOINT ["java", "-Xmx64m", "-jar", "app.jar"]
