# Stage 1: Build React frontend
FROM node:22 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

# Stage 2: Build Spring Boot backend
FROM eclipse-temurin:24-jdk-alpine AS backend-build
WORKDIR /app/backend

# Copy Gradle wrapper and build files
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Make gradlew executable
RUN chmod +x gradlew

# Build backend jar
RUN ./gradlew bootJar -x test

# Stage 3: Combine frontend + backend
FROM eclipse-temurin:24-jdk-alpine
WORKDIR /app

# Copy backend jar
COPY --from=backend-build /app/backend/build/libs/*.jar app.jar

# Copy frontend build into Spring Boot static folder
COPY --from=frontend-build /app/frontend/build ./static

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
