FROM 653528873951.dkr.ecr.us-west-2.amazonaws.com/docker-hub/library/eclipse-temurin:17-jammy AS build
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN ./gradlew build --no-daemon

FROM 653528873951.dkr.ecr.us-west-2.amazonaws.com/docker-hub/library/eclipse-temurin:17-jre-jammy

RUN mkdir /app

COPY --from=build /app/build/libs/*-all.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app/app.jar"]
