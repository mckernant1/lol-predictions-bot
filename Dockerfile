FROM openjdk:8 AS build
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN ./gradlew build --no-daemon

FROM openjdk:8-jre-slim

RUN mkdir /app

COPY --from=build /app/build/libs/*-all.jar /app/app.jar

ENTRYPOINT ["java", "-Xms128m", "-Xmx128m", "-jar", "app/app.jar"]
