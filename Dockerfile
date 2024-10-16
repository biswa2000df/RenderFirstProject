# Step 1: Build stage
# Use an official Maven image to build the application
FROM maven:3.8.6-openjdk-17-slim as build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .

# Download the dependencies (faster)
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src /app/src

# Package the application
RUN mvn clean install

# Step 2: Run stage
# Use an OpenJDK base image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/Heroku_Application.jar app.jar

# Expose the port the app will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
