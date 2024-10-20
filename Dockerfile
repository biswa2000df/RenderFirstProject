
# Stage 1: Build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-alpine

# Install required packages
RUN apt-get update && apt-get install -y \
    chromium \
    chromium-driver \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables for Chrome
ENV CHROME_BIN=/usr/bin/chromium
ENV CHROME_DRIVER=/usr/bin/chromedriver
ENV DISPLAY=:99

WORKDIR /app
COPY --from=build /app/target/Heroku_Application.jar /app/app.jar
COPY BiswajitJARSeleniumDockerIsworkingorNot.jar /app/BiswajitJARSeleniumDockerIsworkingorNot.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
