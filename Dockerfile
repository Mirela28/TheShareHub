FROM gradle:8.6-jdk17 AS builder
WORKDIR /home/gradle/project

COPY gradlew .
COPY gradle gradle
COPY settings.gradle build.gradle ./

COPY src ./src

RUN chmod +x ./gradlew && ./gradlew bootJar -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
