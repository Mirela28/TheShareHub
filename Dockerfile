# Stage 1: Build React frontend
FROM node:22 AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

# Stage 2: Build Spring Boot backend
FROM eclipse-temurin:24-jdk-alpine AS backend-build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src/ ./src
RUN ./gradlew bootJar -x test

# Stage 3: Combine frontend + backend
FROM eclipse-temurin:24-jdk-alpine
WORKDIR /app

COPY --from=backend-build /app/build/libs/*.jar app.jar
COPY --from=frontend-build /app/build ./static

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
