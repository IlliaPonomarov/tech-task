FROM openjdk:17-slim AS builder

WORKDIR /app

COPY pom.xml ./
COPY  . .

RUN mvn clean package

FROM openjdk:17-slim

WORKDIR /app

COPY --from=builder /app/target/*.jar biostorage.jar
EXPOSE 7777


ENTRYPOINT ["java", "-jar", "biostorage.jar"]


