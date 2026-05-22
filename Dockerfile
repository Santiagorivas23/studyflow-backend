# Dockerfile para StudyFlow backend

FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar pom y fuentes para build
COPY pom.xml ./
COPY src ./src

# Build con maven (usa la imagen oficial, no requiere mvnw en el repo)
RUN mvn -B -DskipTests clean package

# Ejecutable run stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Puerto configurable
ARG PORT=8080
ENV SERVER_PORT=$PORT
EXPOSE $PORT

CMD ["java","-jar","/app/app.jar"]
