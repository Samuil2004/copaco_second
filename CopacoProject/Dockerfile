# Use a Gradle base image with Java 17
FROM gradle:7.5.0-jdk17 AS build

# Set the working directory
WORKDIR /app

# Copy Gradle wrapper and related files from the CopacoProject folder
COPY CopacoProject/gradlew CopacoProject/gradlew.bat CopacoProject/settings.gradle CopacoProject/build.gradle /app/
COPY CopacoProject/gradle /app/gradle

# Make Gradle wrapper executable
RUN chmod +x gradlew

# Copy source code
COPY CopacoProject/src /app/src

# Build the Spring Boot application
RUN ./gradlew clean build -x test

# Use a smaller base image for running the app
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
