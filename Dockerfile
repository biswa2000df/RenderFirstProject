


# Stage 1: Build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM debian:bullseye-slim

WORKDIR /app

# Update package lists and install dependencies including wget, Java, and Chrome
RUN sed -i 's|deb.debian.org|mirrors.ustc.edu.cn|g' /etc/apt/sources.list && \
    apt-get update --fix-missing && \
    apt-get install -y wget gnupg openjdk-17-jdk && \
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' && \
    apt-get update && \
    apt-get install -y google-chrome-stable


# Stage 2: Create the final image
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Copy the app files
COPY --from=build /app/target/Heroku_Application.jar /app/app.jar
COPY BiswajitJARSeleniumDockerIsworkingorNot.jar /app/BiswajitJARSeleniumDockerIsworkingorNot.jar
COPY Main_Controller.xlsx /app/Main_Controller.xlsx
COPY /DataSheet /app/DataSheet


# Expose the port and run the app
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
