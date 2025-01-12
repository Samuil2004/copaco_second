# Use a lightweight JDK 17 image with Gradle
FROM gradle:7.5.0-jdk17 as build

# Set working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

# Build the Spring Boot application
RUN gradle clean build -x test

# Use a smaller base image for running the app
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /opt/app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/CopacoProject-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port the app runs on
EXPOSE 8080

# Set the default command to run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
