FROM gradle:8.14.0-jdk21-ubi-minimal AS build
ENV HOME=/app
WORKDIR $HOME
COPY . $HOME
RUN gradle build

FROM amazoncorretto:21.0.6-alpine3.21
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
