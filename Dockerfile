# Stage 1: Build the JAR file using Gradle
FROM openjdk:21-jdk-slim AS build

# Set the working directory in the container
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts

# Copy the source code into the container
COPY src src

# Make gradlew executable
RUN chmod +x gradlew

# Run the tests first
# RUN ./gradlew test --no-daemon

# Run the Gradle build to create the JAR file
RUN ./gradlew build --no-daemon

# Stage 2: Create the actual image using the JAR file
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/RepoScorer-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app will run on (Spring Boot default is 8080)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
