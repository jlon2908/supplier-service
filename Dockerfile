# Etapa 1: construir el jar (opcional si ya lo haces localmente)
# FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
# WORKDIR /app
# COPY . .
# RUN mvn clean package -DskipTests

# Etapa 2: crear imagen final con el jar
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/catalog-service-0.0.1-SNAPSHOT.jar

COPY global-bundle.pem /tmp/global-bundle.pem
RUN keytool -import -trustcacerts -alias amazon-rds-root -file /tmp/global-bundle.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt




COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
