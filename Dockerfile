## Build stage
#FROM gradle:7.5.1-jdk17 AS build
#
## Set the working directory
#WORKDIR /app
#
## Copy Gradle wrapper and build files (dependency caching optimization)
#COPY CopacoProject/gradlew CopacoProject/gradlew.bat CopacoProject/settings.gradle CopacoProject/build.gradle /app/
#COPY CopacoProject/gradle /app/gradle
#
## Pre-download dependencies (useful for caching)
#RUN ./gradlew dependencies --no-daemon
#
## Copy the rest of the source code
#COPY CopacoProject/src /app/src
#
## Build the application
#RUN ./gradlew clean build -x test --no-daemon
#
## Runtime stage
#FROM openjdk:17-jdk-slim
#
## Set the working directory
#WORKDIR /app
#
## Add a non-root user for security
#RUN useradd -ms /bin/bash appuser
#USER appuser
#
## Copy the built JAR file from the build stage
#COPY --from=build /app/build/libs/*.jar app.jar
#
## Expose the application port
#EXPOSE 8080
#
## Run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Build stage
FROM gradle:7.5.1-jdk17 AS build

# Set the working directory
WORKDIR /app

# Copy Gradle wrapper and build files (dependency caching optimization)
COPY CopacoProject/gradlew CopacoProject/gradlew.bat CopacoProject/settings.gradle CopacoProject/build.gradle /app/
COPY CopacoProject/gradle /app/gradle

# Pre-download dependencies (useful for caching)
RUN ./gradlew dependencies --no-daemon

# Copy the rest of the source code
COPY CopacoProject/src /app/src

# Build the application
RUN ./gradlew clean build -x test --no-daemon

# Runtime stage
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Add a non-root user for security
RUN useradd -ms /bin/bash appuser
USER appuser

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
