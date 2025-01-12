# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the project JAR file into the container
COPY build/libs/*.jar app.jar

# Expose the port
EXPOSE 8080

# Set the environment variable for staging
ENV APP_PROFILE=staging

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]