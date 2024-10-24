# Stage 1: Build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Install dependencies for Chrome
RUN apk update && apk add --no-cache bash curl chromium chromium-chromedriver

# Set ChromeDriver path
ENV CHROMEDRIVER_PATH=/usr/bin/chromedriver
ENV CHROME_PATH=/usr/lib/chromium/chrome



# Copy the app files
COPY --from=build /app/target/Heroku_Application.jar /app/app.jar
COPY BiswajitJARSeleniumDockerIsworkingorNot.jar /app/BiswajitJARSeleniumDockerIsworkingorNot.jar
COPY Main_Controller.xlsx /app/Main_Controller.xlsx
COPY /DataSheet /app/DataSheet

# Print Chrome and ChromeDriver version for debugging
RUN chromium-browser --version && chromedriver --version

# Expose the port and run the app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
