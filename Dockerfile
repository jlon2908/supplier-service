# Etapa 1: construir el jar (opcional si ya lo haces localmente)
# FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
# WORKDIR /app
# COPY . .
# RUN mvn clean package -DskipTests

# Etapa 2: crear imagen final con el jar
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/supplier-service-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
