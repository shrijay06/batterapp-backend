# Build stage
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and config files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew build --no-daemon

# Package stage
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/build/libs/batterapp-backend-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
