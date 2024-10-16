# Step 1: Build stage
# Use an official Maven image to build the application
FROM maven:3.8.6-openjdk-17 as build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source files to the container
COPY pom.xml .
COPY src ./src

# Run the Maven build to create the JAR file (skip tests for now)
RUN mvn clean package -DskipTests

# Step 2: Run stage
# Use a lightweight OpenJDK image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage to the run stage
COPY --from=build /app/target/Heroku_Application.jar app.jar

# Expose port 8080 (default port for Spring Boot applications)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
