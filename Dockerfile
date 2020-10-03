FROM alpine:3.7 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build --no-daemon 

FROM openjdk:8-jre-slim

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-Xms128m", "-Xmx128m", "-jar", "app/app.jar"]
